package eu.codingschool.homeautomation.controllers;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import eu.codingschool.homeautomation.HomeAutomationApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK, 
		classes = HomeAutomationApplication.class
)
// We could have @Autowired MockMvc if Spring security was absent and we didn't configure that via setUp().
//@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@SqlGroup({
	@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/test-data-population.sql"),
	@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/test-data-cleanup.sql")
})
public class IndexControllerIntegrationTest {
	
	@Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    
    private static final String ENDPOINT_ROOT = "/";
    private static final String VIEW_INDEX = "index";
    private static final String ENDPOINT_INDEX = ENDPOINT_ROOT + VIEW_INDEX;
    private static final String VIEW_LOGIN = "login";
    private static final String ENDPOINT_LOGIN = ENDPOINT_ROOT + VIEW_LOGIN;
    private static final String VIEW_REGISTRATION = "registration";
    private static final String ENDPOINT_REGISTRATION = ENDPOINT_ROOT + VIEW_REGISTRATION;
    
    // User inserted in database via test/resources/test-data-population.sql
    private static final String MOCK_USER = "testuser1@foo.com";
	
	@Before
    public void setUp() {
		// explicitly configure the filter chain
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
								 .apply(springSecurity())
								 .build();
    }
	
	@Test
	@WithMockUser(username = MOCK_USER)
	public void home_shouldLoadIndexPage_whenRequestingRootPage() throws Exception {
		mockMvc.perform(get(ENDPOINT_ROOT))
			   .andExpect(status().isOk())
			   .andExpect(view().name(VIEW_INDEX));
	}
	
	@Test
	@WithMockUser(username = MOCK_USER)
	public void home_shouldLoadIndexPage_whenRequestingIndexPage() throws Exception {
		mockMvc.perform(get(ENDPOINT_INDEX))
		   	   .andExpect(status().isOk())
		   	   .andExpect(view().name(VIEW_INDEX));
	}

	@Test
	@WithMockUser(username = MOCK_USER)
	public void login_shouldLoadLoginPage_whenRequestingLoginPage() throws Exception {
		mockMvc.perform(get(ENDPOINT_LOGIN))
		   	   .andExpect(status().isOk())
		   	   .andExpect(view().name(VIEW_LOGIN));
	}
	
	@Test
	public void registration_shouldLoadRegistrationPage_whenRequestingRegistrationPage() throws Exception {
		mockMvc.perform(get(ENDPOINT_REGISTRATION))
		   	   .andExpect(status().isOk())
		   	   .andExpect(view().name(VIEW_REGISTRATION));
	}
	
	@Test
	public void registration_shouldLoadIndexPage_whenSubmittingRegistrationPageSucceeds() throws Exception {
		mockMvc.perform(post(ENDPOINT_REGISTRATION)
							.param("name", "TestUser")
							.param("surname", "TestUserSurname")
							.param("email", "testuser@ha.com")
							.param("password", "***")
					   )
					   .andExpect(status().is3xxRedirection())
					   .andExpect(view().name("redirect:" + ENDPOINT_INDEX));
	}
	
	@Test
	public void registration_shouldLoadRegistrationPage_whenSubmittingRegistrationPageFails() throws Exception {
		mockMvc.perform(post(ENDPOINT_REGISTRATION)
							.param("name", "TestUser")
							.param("surname", "TestUserSurname")
							.param("email", "not_a_valid_email")
							.param("password", "***")
					   )
					   .andExpect(status().isOk())
					   .andExpect(view().name(VIEW_REGISTRATION));
	}
}
