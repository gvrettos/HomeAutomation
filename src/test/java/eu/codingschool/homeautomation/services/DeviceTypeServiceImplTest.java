package eu.codingschool.homeautomation.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import eu.codingschool.homeautomation.model.DeviceType;
import eu.codingschool.homeautomation.repositories.DeviceTypeRepository;

@RunWith(SpringRunner.class)
public class DeviceTypeServiceImplTest {

	@TestConfiguration
	static class DeviceTypeServiceImplTestContextConfiguration {
		@Bean
		public DeviceTypeService deviceTypeService() {
			return new DeviceTypeServiceImpl();
		}
	}

	@Autowired
	private DeviceTypeService deviceTypeService;

	@MockBean
	private DeviceTypeRepository deviceTypeRepository;
	
	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private DeviceType deviceType1;
	private DeviceType deviceType2;

	@Before
	public void setUp() {
		deviceType1 = new DeviceType();
		deviceType1.setId(1);
		
		deviceType2 = new DeviceType();
		deviceType2.setId(2);
	}
	
	@Test
	public void findAll_Should_ReturnDeviceTypes_When_DeviceTypesExist() {
		// given
		List<DeviceType> deviceTypesSaved = Arrays.asList(deviceType1, deviceType2);
		Mockito.when(deviceTypeRepository.findAll()).thenReturn(deviceTypesSaved);
		
		// when
		List<DeviceType> deviceTypesFound = deviceTypeService.findAll();
		
		// then
		assertNotNull(deviceTypesFound);
		assertThat(deviceTypesFound.size()).isEqualTo(deviceTypesSaved.size());
	}
	
	@Test
	public void findAll_Should_NotReturnDeviceTypes_When_DeviceTypesNotExist() {
		// given
		Mockito.when(deviceTypeRepository.findAll()).thenReturn(Arrays.asList());
		
		// when
		List<DeviceType> deviceTypes = deviceTypeService.findAll();
		
		// then
		assertNotNull(deviceTypes);
		assertThat(deviceTypes).isEmpty();
	}
	
	@Test
	public void findById_Should_ReturnDeviceType_When_DeviceTypeWithIdExists() {
		// given
		Mockito.when(deviceTypeRepository.findById(deviceType1.getId())).thenReturn(Optional.of(deviceType1));
		Mockito.when(deviceTypeRepository.findById(deviceType2.getId())).thenReturn(Optional.of(deviceType2));
		
		// when
		DeviceType deviceType = deviceTypeService.findById(deviceType1.getId());
		
		// then
		assertNotNull(deviceType);
		assertThat(deviceType.getId()).isEqualTo(deviceType1.getId());
	}
	
	@Test
	public void findById_Should_NotReturnDeviceType_When_DeviceTypeWithIdNotExists() {
		// when
		DeviceType deviceType = deviceTypeService.findById(3);
		
		// then
		assertNull(deviceType);
	}
	
	@Test
	public void save_ShouldPersistDeviceType_When_Called() {
		// given
		DeviceType deviceTypeToPersist = new DeviceType();
		deviceTypeToPersist.setMaxValue(100);
		Mockito.when(deviceTypeRepository.save(deviceTypeToPersist)).thenReturn(deviceTypeToPersist);
		
		// when
		DeviceType deviceTypePersisted = deviceTypeService.save(deviceTypeToPersist);
		
		// then
		assertNotNull(deviceTypePersisted);
		assertThat(deviceTypePersisted.getMaxValue()).isEqualTo(100);
	}
	
	@Test
	public void delete_ShouldCallRepositoryDeleteOnce_When_Called() {
		// given
		DeviceType deviceType = new DeviceType();
		
		// when
		deviceTypeService.delete(deviceType);
		
		// then
		verify(deviceTypeRepository).delete(any());
	}
}