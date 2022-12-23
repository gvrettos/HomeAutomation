package eu.codingschool.homeautomation.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import eu.codingschool.homeautomation.HomeAutomationApplication;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.repositories.PersonRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = HomeAutomationApplication.class)
// We can @Autowire MockMvc if Spring security was absent and we didn't configure that via setUp().
// @AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@SqlGroup({ 
	@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/test-data-population.sql"),
	@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/test-data-cleanup.sql") 
})
public class PersonControllerIntegrationTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private PersonRepository personRepository;

	private MockMvc mockMvc;

	private static final String ENDPOINT_ADMIN_PERSONS_BASE_URL = "/admin/people";
	private static final String ENDPOINT_ADMIN_PERSONS_EDIT_OR_DELETE_BASE_URL = ENDPOINT_ADMIN_PERSONS_BASE_URL + "/{id}";
	private static final String REDIRECT_ENDPOINT_ADMIN_PERSONS_BASE_URL = "redirect:" + ENDPOINT_ADMIN_PERSONS_BASE_URL;

	private static final String VIEW_PERSON_LIST = "person/list";
	private static final String VIEW_ERROR_403 = "/error/403";
	
	private static final String MODAL_PERSON_NEW_OR_EDIT = "person/modals :: modalNewOrEdit";
	private static final String MODAL_PERSON_DELETE = "person/modals :: modalDelete";
	
	private static final String USER_DETAILS_SERVICE = "userDetailsService";

	// User inserted in database via test/resources/test-data-population.sql
	private static final String USER_ADMIN = "testadmin@foo.com";
	private static final String USER_SIMPLE = "testuser1@foo.com";

	@Before
	public void setup() {
		// explicitly configure the filter chain
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void getPeople_shouldReturnAllPeople_whenAdminUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_ADMIN_PERSONS_BASE_URL))
			   .andDo(print())
			   .andExpect(view().name(VIEW_PERSON_LIST));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void getPeople_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_ADMIN_PERSONS_BASE_URL))
			   .andDo(print())
			   .andExpect(forwardedUrl(VIEW_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewEditPersonForm_shouldDisplayForm_whenAdminUser() throws Exception {
		Integer personId = 103;
		mockMvc.perform(put(ENDPOINT_ADMIN_PERSONS_EDIT_OR_DELETE_BASE_URL + "/form", personId))
			   .andDo(print())
			   .andExpect(view().name(MODAL_PERSON_NEW_OR_EDIT))
			   .andExpect(model().size(6))
			   .andExpect(model().attribute("person", isA(Person.class)))
			   .andExpect(model().attribute("person", hasProperty("id", equalTo(personId))))
			   .andExpect(model().attribute("person", hasProperty("name", equalTo("User2"))))
			   .andExpect(model().attribute("person", hasProperty("surname", equalTo("UserSurname"))))
			   .andExpect(model().attribute("person", hasProperty("email", equalTo("testuser2@foo.com"))))
			   .andExpect(model().attribute("person", hasProperty("password", equalTo("***"))))
			   .andExpect(model().attribute("person", hasProperty("role", equalTo("USER"))))
			   .andExpect(model().attribute("actionUrl", String.format(ENDPOINT_ADMIN_PERSONS_BASE_URL + "/%s", personId)))
			   .andExpect(model().attribute("actionType", "PUT"))
			   .andExpect(model().attribute("modalTitle", "Edit"));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewEditPersonForm_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer personId = 103;
		mockMvc.perform(put(ENDPOINT_ADMIN_PERSONS_EDIT_OR_DELETE_BASE_URL, personId))
			   .andDo(print())
			   .andExpect(forwardedUrl(VIEW_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editPerson_shouldDisplayPeopleList_whenSubmittingFormSucceeds() throws Exception {
		Integer personId = 103;
		Person personBeforeEdit = personRepository.findById(personId).get();
		String nameBeforeEdit = personBeforeEdit.getName();

		mockMvc.perform(put(ENDPOINT_ADMIN_PERSONS_EDIT_OR_DELETE_BASE_URL, personId)
							.param("name", "Another person")
							.param("surname", "UserSurname")
							.param("email", "testuser2@foo.com")
				)
				.andDo(print())
				.andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_PERSONS_BASE_URL));

		// Check that the person was edited
		Person personAfterEdit = personRepository.findById(personId).get();
		String nameAfterEdit = personAfterEdit.getName();

		assertNotEquals(nameBeforeEdit, nameAfterEdit);
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editPerson_shouldDisplayForm_whenSubmittingFormFails() throws Exception {
		Integer personId = 103;
		mockMvc.perform(put(ENDPOINT_ADMIN_PERSONS_EDIT_OR_DELETE_BASE_URL, personId)
							.param("name", "User2")
							.param("surname", "UserSurname")
							.param("email", "testuser2")
				)
				.andDo(print())
				.andExpect(view().name(MODAL_PERSON_NEW_OR_EDIT));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editPerson_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer personId = 103;
		mockMvc.perform(put(ENDPOINT_ADMIN_PERSONS_EDIT_OR_DELETE_BASE_URL, personId))
			   .andDo(print())
			   .andExpect(forwardedUrl(VIEW_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void confirmDeletePerson_shouldDisplayConfirmationDialog_whenAdminUser() throws Exception {
		Integer personId = 103;
		mockMvc.perform(delete(ENDPOINT_ADMIN_PERSONS_EDIT_OR_DELETE_BASE_URL + "/confirmation", personId))
			   .andDo(print())
			   .andExpect(view().name(MODAL_PERSON_DELETE))
			   .andExpect(model().size(3))
			   .andExpect(model().attribute("person", hasProperty("id", equalTo(personId))))
			   .andExpect(model().attribute("person", hasProperty("name", equalTo("User2"))))
			   .andExpect(model().attribute("person", hasProperty("surname", equalTo("UserSurname"))))
			   .andExpect(model().attribute("person", hasProperty("email", equalTo("testuser2@foo.com"))))
			   .andExpect(model().attribute("actionUrl", String.format(ENDPOINT_ADMIN_PERSONS_BASE_URL + "/%s", personId)))
			   .andExpect(model().attribute("actionType", "DELETE"));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void confirmDeletePerson_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer personId = 103;
		mockMvc.perform(delete(ENDPOINT_ADMIN_PERSONS_EDIT_OR_DELETE_BASE_URL, personId))
			   .andDo(print())
			   .andExpect(forwardedUrl(VIEW_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeletePerson_shouldDisplayPeopleList_whenNewPerson() throws Exception {
		Person savedPerson = personRepository.save(new Person());
		long peopleCountBefore = personRepository.count();
		Integer personId = savedPerson.getId();
		mockMvc.perform(delete(ENDPOINT_ADMIN_PERSONS_EDIT_OR_DELETE_BASE_URL, personId))
			   .andDo(print())
			   .andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_PERSONS_BASE_URL));

		// Check that the new person was deleted
		long peopleCountAfter = personRepository.count();
		assertEquals(peopleCountBefore - 1, peopleCountAfter);
		assertFalse(personRepository.findById(personId).isPresent());
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeletePerson_shouldUnassignAllDevicesAndDisplayPeopleList_whenExistingPerson() throws Exception {
		Integer personId = 103;
		long peopleCountBefore = personRepository.count();
		mockMvc.perform(delete(ENDPOINT_ADMIN_PERSONS_EDIT_OR_DELETE_BASE_URL, personId))
		   	   .andDo(print())
		   	   .andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_PERSONS_BASE_URL));
		
		// Check that the existing person was deleted
		long peopleCountAfter = personRepository.count();
		assertEquals(peopleCountBefore - 1, peopleCountAfter);
		assertFalse(personRepository.findById(personId).isPresent());
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeletePerson_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer personId = 103;
		mockMvc.perform(delete(ENDPOINT_ADMIN_PERSONS_EDIT_OR_DELETE_BASE_URL, personId))
			   .andDo(print())
			   .andExpect(forwardedUrl(VIEW_ERROR_403));
	}

}
