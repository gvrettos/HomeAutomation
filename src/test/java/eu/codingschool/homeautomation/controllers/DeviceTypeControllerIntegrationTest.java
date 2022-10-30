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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.repositories.DeviceTypeRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = HomeAutomationApplication.class)
// We can @Autowire MockMvc if Spring security was absent and we didn't configure that via setUp().
// @AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@SqlGroup({ 
	@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/test-data-population.sql"),
	@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/test-data-cleanup.sql") 
})
public class DeviceTypeControllerIntegrationTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private DeviceTypeRepository deviceTypeRepository;

	private MockMvc mockMvc;

	private static final String ENDPOINT_DEVICE_TYPE_LIST = "/admin/deviceType/list";
	private static final String ENDPOINT_DEVICE_TYPE_NEW = "/admin/deviceType/new";
	private static final String ENDPOINT_DEVICE_TYPE_EDIT = "/admin/deviceType/{id}/edit";
	private static final String ENDPOINT_DEVICE_TYPE_DELETE = "/admin/deviceType/{id}/delete";
	
	private static final String LAYOUT_DEVICE_TYPE_LIST = "deviceType/list";
	private static final String LAYOUT_ERROR_403 = "/error/403";
	private static final String LAYOUT_ERROR_422 = "/error/422";
	
	private static final String MODAL_DEVICE_TYPE_NEW_OR_EDIT = "deviceType/modals :: modalNewOrEdit";
	private static final String REDIRECT = "redirect:";
	
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
	public void getDeviceTypes_shouldReturnAllDevicesTypes_whenAdminUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_DEVICE_TYPE_LIST))
			   .andDo(print())
			   .andExpect(view().name(LAYOUT_DEVICE_TYPE_LIST));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void getDeviceTypes_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_DEVICE_TYPE_LIST)).andDo(print()).andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewNewDeviceTypeForm_shouldDisplayForm_whenAdminUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_DEVICE_TYPE_NEW))
			   .andDo(print())
			   .andExpect(view().name(MODAL_DEVICE_TYPE_NEW_OR_EDIT));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewNewDeviceTypeForm_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_DEVICE_TYPE_NEW))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void createNewDeviceType_shouldDisplayDevicesList_whenSubmittingFormSucceeds() throws Exception {
		long deviceTypesCountBefore = deviceTypeRepository.count();

		mockMvc.perform(post(ENDPOINT_DEVICE_TYPE_NEW)
							.param("type", "Thermostat")
							.param("informationType", "Target temp.")
				)
				.andDo(print())
				.andExpect(view().name(REDIRECT + ENDPOINT_DEVICE_TYPE_LIST));

		long deviceTypesCountAfter = deviceTypeRepository.count();
		assertEquals(deviceTypesCountBefore + 1, deviceTypesCountAfter);
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void createNewDeviceType_shouldDisplayForm_whenSubmittingFormFails() throws Exception {
		mockMvc.perform(post(ENDPOINT_DEVICE_TYPE_NEW)
					.param("type", "Thermostat")
					.param("informationType", "")
			   )
			   .andDo(print())
			   .andExpect(view().name(MODAL_DEVICE_TYPE_NEW_OR_EDIT));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void createNewDeviceType_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(post(ENDPOINT_DEVICE_TYPE_NEW))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewEditDeviceTypeForm_shouldDisplayForm_whenAdminUser() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(get(ENDPOINT_DEVICE_TYPE_EDIT, deviceTypeId))
			   .andDo(print())
			   .andExpect(view().name(MODAL_DEVICE_TYPE_NEW_OR_EDIT))
			   .andExpect(model().size(3))
			   .andExpect(model().attribute("deviceType", isA(DeviceType.class)))
			   .andExpect(model().attribute("deviceType", hasProperty("id", equalTo(deviceTypeId))))
			   .andExpect(model().attribute("deviceType", hasProperty("type", equalTo("Door Lock"))))
			   .andExpect(model().attribute("actionUrl", String.format("/admin/deviceType/%s/edit", deviceTypeId)))
			   .andExpect(model().attribute("modalTitle", "Edit"));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewEditDeviceTypeForm_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(get(ENDPOINT_DEVICE_TYPE_EDIT, deviceTypeId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editDeviceType_shouldDisplayDevicesList_whenSubmittingFormSucceeds() throws Exception {
		Integer deviceTypeId = 3;
		DeviceType deviceTypeBeforeEdit = deviceTypeRepository.findById(deviceTypeId).get();
		String typeBeforeEdit = deviceTypeBeforeEdit.getType();
		String informationTypeBeforeEdit = deviceTypeBeforeEdit.getInformationType();

		mockMvc.perform(post(ENDPOINT_DEVICE_TYPE_EDIT, deviceTypeId)
					.param("type", "Thermostat")
					.param("informationType", "Target temp.")
				)
				.andDo(print())
				.andExpect(view().name(REDIRECT + ENDPOINT_DEVICE_TYPE_LIST));

		// Check that the device type was edited
		DeviceType deviceTypeAfterEdit = deviceTypeRepository.findById(deviceTypeId).get();
		String typeAfterEdit = deviceTypeAfterEdit.getType();
		String informationTypeAfterEdit = deviceTypeAfterEdit.getInformationType();

		assertNotEquals(typeBeforeEdit, typeAfterEdit);
		assertNotEquals(informationTypeBeforeEdit, informationTypeAfterEdit);
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editDeviceType_shouldDisplayForm_whenSubmittingFormFails() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(post(ENDPOINT_DEVICE_TYPE_EDIT, deviceTypeId)
					.param("type", "Thermostat")
					.param("informationType", "")
				)
				.andDo(print())
				.andExpect(view().name(MODAL_DEVICE_TYPE_NEW_OR_EDIT));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editDeviceType_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(post(ENDPOINT_DEVICE_TYPE_EDIT, deviceTypeId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void selectDeleteDeviceType_shouldDisplayConfirmationDialog_whenAdminUser() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(get(ENDPOINT_DEVICE_TYPE_DELETE, deviceTypeId))
			   .andDo(print())
			   .andExpect(view().name("deviceType/modals :: modalDelete"))
			   .andExpect(model().size(2))
			   .andExpect(model().attribute("deviceType", hasProperty("id", equalTo(deviceTypeId))))
			   .andExpect(model().attribute("deviceType", hasProperty("type", equalTo("Door Lock"))))
			   .andExpect(model().attribute("actionUrl", String.format("/admin/deviceType/%s/delete", deviceTypeId)));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void selectDeleteDeviceType_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(get(ENDPOINT_DEVICE_TYPE_DELETE, deviceTypeId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeleteDeviceType_shouldDisplayDevicesList_whenDeviceTypeUnused() throws Exception {
		DeviceType savedDeviceType = deviceTypeRepository.save(new DeviceType());
		long devicesCountBefore = deviceTypeRepository.count();
		Integer deviceTypeId = savedDeviceType.getId();
		mockMvc.perform(post(ENDPOINT_DEVICE_TYPE_DELETE, deviceTypeId))
			   .andDo(print())
			   .andExpect(view().name(REDIRECT + ENDPOINT_DEVICE_TYPE_LIST));

		// Check that the selected device type was deleted
		long devicesCountAfter = deviceTypeRepository.count();
		assertEquals(devicesCountBefore - 1, devicesCountAfter);
		assertFalse(deviceTypeRepository.findById(deviceTypeId).isPresent());
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeleteDeviceType_shouldFail_whenDeviceTypeUsedByDevice() throws Exception {
		Integer deviceId = 3;
		mockMvc.perform(post(ENDPOINT_DEVICE_TYPE_DELETE, deviceId))
			   .andDo(print())
			   .andExpect(view().name(LAYOUT_ERROR_422))
			   .andExpect(model().attribute("action", "delete device type"))
			   .andExpect(model().attribute("entityName", "Door Lock"))
			   .andExpect(model().attribute("additionalMessage", notNullValue()));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeleteDeviceType_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(post(ENDPOINT_DEVICE_TYPE_DELETE, deviceTypeId))
			   .andDo(print())
			   .andExpect(forwardedUrl(LAYOUT_ERROR_403));
	}

}
