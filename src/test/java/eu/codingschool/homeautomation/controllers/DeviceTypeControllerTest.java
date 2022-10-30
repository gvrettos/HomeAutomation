package eu.codingschool.homeautomation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.services.DeviceTypeService;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.validators.DeviceTypeValidator;

@RunWith(SpringRunner.class)
@WebMvcTest(DeviceTypeController.class)
public class DeviceTypeControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DeviceTypeValidator deviceTypeValidator;
	
	@MockBean
	@Qualifier("personServiceImpl")
	private PersonService personService;
	
	@MockBean
	@Qualifier("deviceTypeServiceImpl")
	private DeviceTypeService deviceTypeService;
	
	private Person admin;
	private List<DeviceType> allDeviceTypes;
	
	@Before
    public void setUp() throws Exception {
		admin = new Person();
		admin.setRole("ADMIN");
				
		DeviceType deviceType1 = new DeviceType();
		deviceType1.setId(1);
		DeviceType deviceType2 = new DeviceType();
		deviceType1.setId(2);
		
		allDeviceTypes = Arrays.asList(deviceType1, deviceType2);
		
		when(deviceTypeService.findAll()).thenReturn(allDeviceTypes);
		when(deviceTypeService.findById(1)).thenReturn(deviceType1);
		when(deviceTypeService.findById(2)).thenReturn(deviceType2);
    }
	
	@Test
	@WithMockUser
	public void getDeviceTypes_Should_LoadAllDeviceTypes_When_AdminLoggedInAndRequestingDeviceTypes() throws Exception {
		// given
		when(personService.findByEmail(any())).thenReturn(admin);
		
		// when - then
		this.mockMvc.perform(get("/admin/deviceType/list"))
					.andExpect(status().isOk()) 
					.andExpect(model().attribute("loggedInUser", admin))
					.andExpect(model().attribute("deviceTypes", allDeviceTypes))
					.andExpect(view().name("deviceType/list"));
	}
	
	@Test
	@WithMockUser
	public void newDeviceType_Should_OpenModal_When_Requested() throws Exception {
		// when - then
		performHttpGetAction("new", -1, "modalNewOrEdit");
	}
	
	@Test
	@WithMockUser
	public void addDeviceType_Should_SaveDeviceType_When_Provided() throws Exception {
		// when - then
		performHttpPostAction("new", -1, "/admin/deviceType/list");
		
		verify(deviceTypeService, times(1)).save(any());
	}
	
	@Test
	@WithMockUser
	public void addDeviceType_Should_NotSaveDeviceType_When_NameNotProvided() throws Exception {
		// given 
		// mock the void method to return validation errors
		doAnswer(validator -> {
			((BeanPropertyBindingResult)validator.getArguments()[1]).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(deviceTypeValidator).validate(any(), any());
		
		// when - then
		this.mockMvc.perform(post("/admin/deviceType/new")
								.with(csrf())
					)
					.andExpect(status().isOk())
					.andExpect(view().name("deviceType/modals :: modalNewOrEdit"));
		
		verifyZeroInteractions(deviceTypeService);
	}
	
	@Test
	@WithMockUser
	public void viewDeviceType_Should_OpenModal_When_Requested() throws Exception {
		// given
		Integer deviceTypeId = 2;
		
		// when - then
		performHttpGetAction("edit", deviceTypeId, "modalNewOrEdit");
		
		verify(deviceTypeService, times(1)).findById(deviceTypeId);
		verify(deviceTypeService, times(0)).findById(deviceTypeId + 1);
		verify(deviceTypeService, times(0)).findById(deviceTypeId - 1);
	}
	
	@Test
	@WithMockUser
	public void editDeviceType_Should_SaveDeviceType_When_Provided() throws Exception {
		// given
		Integer deviceTypeId = 2;
				
		// when - then
		performHttpPostAction("edit", deviceTypeId, "/admin/deviceType/list");
		
		verify(deviceTypeService, times(1)).save(any());
	}
	
	@Test
	@WithMockUser
	public void editDeviceType_Should_NotSaveDeviceType_When_NameNotProvided() throws Exception {
		// given 
		Integer deviceTypeId = 2;
		
		// mock the void method to return validation errors
		doAnswer(validator -> {
			((BeanPropertyBindingResult)validator.getArguments()[1]).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(deviceTypeValidator).validate(any(), any());
		
		// when - then
		performHttpPostActionWithValidationErrors("edit", deviceTypeId, "modalNewOrEdit");
		
		verifyZeroInteractions(deviceTypeService);
	}
	
	@Test
	@WithMockUser
	public void confrimDeleteDeviceType_Should_OpenModal_When_Requested() throws Exception {
		// given
		Integer deviceTypeId = 2;
		
		// when - then
		performHttpGetAction("delete", deviceTypeId, "modalDelete");
		
		verify(deviceTypeService, times(1)).findById(deviceTypeId);
		verify(deviceTypeService, times(0)).findById(deviceTypeId + 1);
		verify(deviceTypeService, times(0)).findById(deviceTypeId - 1);
	}
	
	@Test
	@WithMockUser
	public void doDeleteDeviceType_Should_CallDelete_When_Exists() throws Exception {
		// given
		Integer deviceTypeId = 2;
				
		// when - then
		performHttpPostAction("delete", deviceTypeId, "/admin/deviceType/list");
		
		verify(deviceTypeService, times(1)).delete(any());
	}
	
	@Test
	@WithMockUser
	public void doDeleteDeviceType_Should_CallDelete_When_NotExists() throws Exception {
		// given
		Integer deviceTypeId = 3; // this room does not exist
				
		// when - then
		performHttpPostAction("delete", deviceTypeId, "/admin/deviceType/list");
		
		verify(deviceTypeService, times(1)).delete(any());
	}
	
	private void performHttpGetAction(String actionType, Integer deviceTypeId, String modalName) throws Exception {
		this.mockMvc.perform(get("/admin/deviceType/" + (deviceTypeId != -1 ? deviceTypeId : "") + "/" + actionType))
					.andExpect(status().isOk())
					.andExpect(view().name("deviceType/modals :: " + modalName));
	}
	
	private void performHttpPostAction(String actionType, Integer deviceTypeId, String expectedUrl) throws Exception {
		this.mockMvc.perform(post("/admin/deviceType/" + (deviceTypeId != -1 ? deviceTypeId : "") + "/" + actionType)
								.with(csrf())
					)
					.andExpect(redirectedUrl(expectedUrl))
					.andExpect(view().name("redirect:" + expectedUrl));
	}
	
	private void performHttpPostActionWithValidationErrors(String actionType, Integer deviceTypeId, String modalName) 
			throws Exception {
		
		this.mockMvc.perform(post("/admin/deviceType/" + deviceTypeId + "/" + actionType)
							.with(csrf())
					)
					.andExpect(status().isOk())
					.andExpect(view().name("deviceType/modals :: " + modalName));
	}
	
}
