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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

	private static final String ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL = "/admin/deviceTypes";
	private static final String ENDPOINT_ADMIN_DEVICE_TYPES_EDIT_OR_DELETE_BASE_URL = ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL + "/{id}";
	private static final String REDIRECT_ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL = "redirect:" + ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL;

	private static final String MODAL_DEVICE_TYPE_NEW_OR_EDIT = "deviceType/modals :: modalNewOrEdit";
	private static final String MODAL_DEVICE_TYPE_DELETE = "deviceType/modals :: modalDelete";

	private static final String VIEW_DEVICE_TYPE_LIST = "deviceType/list";
	private static final String VIEW_ERROR_403 = "/error/403";
	private static final String VIEW_ERROR_422 = "/error/422";
	
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
		mockMvc.perform(get(ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL))
			   .andExpect(status().isOk())
			   .andExpect(view().name(VIEW_DEVICE_TYPE_LIST));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void getDeviceTypes_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(get(ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl(VIEW_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewNewDeviceTypeForm_shouldDisplayForm_whenAdminUser() throws Exception {
		mockMvc.perform(post(ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL))
			   .andExpect(status().isOk())
			   .andExpect(view().name(MODAL_DEVICE_TYPE_NEW_OR_EDIT));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewNewDeviceTypeForm_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(post(ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl(VIEW_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void createNewDeviceType_shouldDisplayDevicesList_whenSubmittingFormSucceeds() throws Exception {
		long deviceTypesCountBefore = deviceTypeRepository.count();

		mockMvc.perform(post(ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL)
							.param("type", "Thermostat")
							.param("informationType", "Target temp.")
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL));

		long deviceTypesCountAfter = deviceTypeRepository.count();
		assertEquals(deviceTypesCountBefore + 1, deviceTypesCountAfter);
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void createNewDeviceType_shouldDisplayForm_whenSubmittingFormFails() throws Exception {
		mockMvc.perform(post(ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL)
					.param("type", "Thermostat")
					.param("informationType", "")
			   )
			   .andExpect(status().isOk())
			   .andExpect(view().name(MODAL_DEVICE_TYPE_NEW_OR_EDIT));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void createNewDeviceType_shouldBeForbidden_whenSimpleUser() throws Exception {
		mockMvc.perform(post(ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl(VIEW_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewEditDeviceTypeForm_shouldDisplayForm_whenAdminUser() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(put(ENDPOINT_ADMIN_DEVICE_TYPES_EDIT_OR_DELETE_BASE_URL + "/form", deviceTypeId))
			   .andExpect(status().isOk())
			   .andExpect(view().name(MODAL_DEVICE_TYPE_NEW_OR_EDIT))
			   .andExpect(model().size(4))
			   .andExpect(model().attribute("deviceType", isA(DeviceType.class)))
			   .andExpect(model().attribute("deviceType", hasProperty("id", equalTo(deviceTypeId))))
			   .andExpect(model().attribute("deviceType", hasProperty("type", equalTo("Door Lock"))))
			   .andExpect(model().attribute("actionUrl", String.format(ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL + "/%s", deviceTypeId)))
			   .andExpect(model().attribute("actionType", "PUT"))
			   .andExpect(model().attribute("modalTitle", "Edit"));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void viewEditDeviceTypeForm_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(put(ENDPOINT_ADMIN_DEVICE_TYPES_EDIT_OR_DELETE_BASE_URL, deviceTypeId))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl(VIEW_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editDeviceType_shouldDisplayDevicesList_whenSubmittingFormSucceeds() throws Exception {
		Integer deviceTypeId = 3;
		DeviceType deviceTypeBeforeEdit = deviceTypeRepository.findById(deviceTypeId).get();
		String typeBeforeEdit = deviceTypeBeforeEdit.getType();
		String informationTypeBeforeEdit = deviceTypeBeforeEdit.getInformationType();

		mockMvc.perform(put(ENDPOINT_ADMIN_DEVICE_TYPES_EDIT_OR_DELETE_BASE_URL, deviceTypeId)
					.param("type", "Thermostat")
					.param("informationType", "Target temp.")
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL));

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
		mockMvc.perform(put(ENDPOINT_ADMIN_DEVICE_TYPES_EDIT_OR_DELETE_BASE_URL, deviceTypeId)
					.param("type", "Thermostat")
					.param("informationType", "")
				)
				.andExpect(status().isOk())
				.andExpect(view().name(MODAL_DEVICE_TYPE_NEW_OR_EDIT));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void editDeviceType_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(put(ENDPOINT_ADMIN_DEVICE_TYPES_EDIT_OR_DELETE_BASE_URL, deviceTypeId))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl(VIEW_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void confirmDeleteDeviceType_shouldDisplayConfirmationDialog_whenAdminUser() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(delete(ENDPOINT_ADMIN_DEVICE_TYPES_EDIT_OR_DELETE_BASE_URL + "/confirmation", deviceTypeId))
			   .andExpect(status().isOk())
			   .andExpect(view().name(MODAL_DEVICE_TYPE_DELETE))
			   .andExpect(model().size(3))
			   .andExpect(model().attribute("deviceType", hasProperty("id", equalTo(deviceTypeId))))
			   .andExpect(model().attribute("deviceType", hasProperty("type", equalTo("Door Lock"))))
			   .andExpect(model().attribute("actionUrl", String.format(ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL + "/%s", deviceTypeId)))
			   .andExpect(model().attribute("actionType", "DELETE"));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void confirmDeleteDeviceType_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(delete(ENDPOINT_ADMIN_DEVICE_TYPES_EDIT_OR_DELETE_BASE_URL + "/confirmation", deviceTypeId))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl(VIEW_ERROR_403));
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeleteDeviceType_shouldDisplayDevicesList_whenDeviceTypeUnused() throws Exception {
		DeviceType savedDeviceType = deviceTypeRepository.save(new DeviceType());
		long devicesCountBefore = deviceTypeRepository.count();
		Integer deviceTypeId = savedDeviceType.getId();
		mockMvc.perform(delete(ENDPOINT_ADMIN_DEVICE_TYPES_EDIT_OR_DELETE_BASE_URL, deviceTypeId))
			   .andExpect(status().is3xxRedirection())
			   .andExpect(view().name(REDIRECT_ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL));

		// Check that the selected device type was deleted
		long devicesCountAfter = deviceTypeRepository.count();
		assertEquals(devicesCountBefore - 1, devicesCountAfter);
		assertFalse(deviceTypeRepository.findById(deviceTypeId).isPresent());
	}

	@Test
	@WithUserDetails(value = USER_ADMIN, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeleteDeviceType_shouldFail_whenDeviceTypeUsedByDevice() throws Exception {
		Integer deviceId = 3;
		mockMvc.perform(delete(ENDPOINT_ADMIN_DEVICE_TYPES_EDIT_OR_DELETE_BASE_URL, deviceId))
			   .andExpect(status().isOk())
			   .andExpect(view().name(VIEW_ERROR_422))
			   .andExpect(model().attribute("action", "delete device type"))
			   .andExpect(model().attribute("entityName", "Door Lock"))
			   .andExpect(model().attribute("additionalMessage", notNullValue()));
	}

	@Test
	@WithUserDetails(value = USER_SIMPLE, userDetailsServiceBeanName = USER_DETAILS_SERVICE)
	public void doDeleteDeviceType_shouldBeForbidden_whenSimpleUser() throws Exception {
		Integer deviceTypeId = 3;
		mockMvc.perform(delete(ENDPOINT_ADMIN_DEVICE_TYPES_EDIT_OR_DELETE_BASE_URL, deviceTypeId))
			   .andExpect(status().isForbidden())
			   .andExpect(forwardedUrl(VIEW_ERROR_403));
	}

}
