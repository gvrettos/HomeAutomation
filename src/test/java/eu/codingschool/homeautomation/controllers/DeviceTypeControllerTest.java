package eu.codingschool.homeautomation.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;

import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.model.Person;
import eu.codingschool.homeautomation.services.DeviceTypeService;
import eu.codingschool.homeautomation.services.PersonService;
import eu.codingschool.homeautomation.validators.DeviceTypeValidator;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@RunWith(SpringRunner.class)
public class DeviceTypeControllerTest {

	@Mock
	private DeviceTypeValidator deviceTypeValidator;
	
	@Mock
	private PersonService personService;
	
	@Mock
	private DeviceTypeService deviceTypeService;

	@InjectMocks
	private DeviceTypeController deviceTypeController;

	private static final String ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL = "/admin/deviceTypes";
	private static final String REDIRECT_ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL = "redirect:" + ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL;

	private static final String MODAL_DEVICE_TYPE_NEW_OR_EDIT = "deviceType/modals :: modalNewOrEdit";
	private static final String MODAL_DEVICE_TYPE_DELETE = "deviceType/modals :: modalDelete";

	private static final String VIEW_DEVICE_TYPE_LIST = "deviceType/list";
	
	private Person admin;
	private List<DeviceType> allDeviceTypes;
	
	@Before
    public void setUp() {
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
	public void getDeviceTypes_shouldLoadAllDeviceTypes_whenRequestingDeviceTypes() {
		// when
		String returnedView = deviceTypeController.getDeviceTypes(new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(VIEW_DEVICE_TYPE_LIST);
	}

	@Test
	@WithMockUser
	public void newDeviceType_shouldOpenModal_whenRequested() {
		// when
		String returnedView = deviceTypeController.newDeviceType(new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(MODAL_DEVICE_TYPE_NEW_OR_EDIT);
	}

	@Test
	@WithMockUser
	public void addDeviceType_shouldSaveDeviceType_whenProvided() {
		// when
		String returnedView = deviceTypeController.addDeviceType(
				new DeviceType(),
				new MapBindingResult(new HashMap<>(), "foo")
		);

		// then
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL);
		verify(deviceTypeService, times(1)).save(any());
	}

	@Test
	@WithMockUser
	public void addDeviceType_shouldNotSaveDeviceType_whenNameNotProvided() {
		// given
		// mock the void method to return validation errors
		doAnswer(validator -> {
			((BeanPropertyBindingResult)validator.getArguments()[1]).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(deviceTypeValidator).validate(any(), any());

		// when
		String returnedView = deviceTypeController.addDeviceType(
				new DeviceType(),
				new BeanPropertyBindingResult(new HashMap<>(), "foo")
		);

		// then
		assertThat(returnedView).isEqualTo(MODAL_DEVICE_TYPE_NEW_OR_EDIT);
		verifyZeroInteractions(deviceTypeService);
	}

	@Test
	@WithMockUser
	public void viewDeviceType_shouldOpenModal_whenRequested() {
		// given
		int deviceTypeId = 2;

		// when
		String returnedView = deviceTypeController.viewDeviceType(deviceTypeId, new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(MODAL_DEVICE_TYPE_NEW_OR_EDIT);
		verify(deviceTypeService, times(1)).findById(deviceTypeId);
		verify(deviceTypeService, times(0)).findById(deviceTypeId + 1);
		verify(deviceTypeService, times(0)).findById(deviceTypeId - 1);
	}

	@Test
	@WithMockUser
	public void editDeviceType_shouldSaveDeviceType_whenProvided() {
		// given
		Integer deviceTypeId = 2;

		// when
		String returnedView = deviceTypeController.editDeviceType(
				deviceTypeService.findById(deviceTypeId),
				new MapBindingResult(new HashMap<>(), "foo")
		);

		// then
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL);
		verify(deviceTypeService, times(1)).save(any());
	}

	@Test
	@WithMockUser
	public void editDeviceType_shouldNotSaveDeviceType_whenNameNotProvided() {
		// given
		int deviceTypeId = 2;

		// mock the void method to return validation errors
		doAnswer(validator -> {
			((BeanPropertyBindingResult)validator.getArguments()[1]).addError(new ObjectError("name", "NotEmpty"));
			return null;
		}).when(deviceTypeValidator).validate(any(), any());

		// when
		String returnedView = deviceTypeController.editDeviceType(
				allDeviceTypes.get(deviceTypeId - 1),
				new BeanPropertyBindingResult(new HashMap<>(), "foo")
		);

		// then
		assertThat(returnedView).isEqualTo(MODAL_DEVICE_TYPE_NEW_OR_EDIT);
		verifyZeroInteractions(deviceTypeService);
	}

	@Test
	@WithMockUser
	public void confirmDeleteDeviceType_shouldOpenModal_whenRequested() {
		// given
		int deviceTypeId = 2;

		// when
		String returnedView = deviceTypeController.confirmDeleteDeviceType(
				deviceTypeId,
				new RedirectAttributesModelMap()
		);

		// then
		assertThat(returnedView).isEqualTo(MODAL_DEVICE_TYPE_DELETE);
		verify(deviceTypeService, times(1)).findById(deviceTypeId);
		verify(deviceTypeService, times(0)).findById(deviceTypeId + 1);
		verify(deviceTypeService, times(0)).findById(deviceTypeId - 1);
	}

	@Test
	@WithMockUser
	public void doDeleteDeviceType_shouldCallDelete_whenExists() {
		// given
		Integer deviceTypeId = 2;

		// when
		String returnedView = deviceTypeController.doDeleteDeviceType(
				deviceTypeService.findById(deviceTypeId),
				new RedirectAttributesModelMap()
		);

		// then
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL);
		verify(deviceTypeService, times(1)).delete(any());
	}

	@Test
	@WithMockUser
	public void doDeleteDeviceType_shouldCallDelete_whenNotExists() {
		// given
		int deviceTypeId = 3; // this deviceType does not exist
		DeviceType deviceType = new DeviceType();
		deviceType.setId(deviceTypeId);

		// when
		String returnedView = deviceTypeController.doDeleteDeviceType(deviceType, new RedirectAttributesModelMap());

		// then
		assertThat(returnedView).isEqualTo(REDIRECT_ENDPOINT_ADMIN_DEVICE_TYPES_BASE_URL);
		verify(deviceTypeService, times(1)).delete(any());
	}
	
}
