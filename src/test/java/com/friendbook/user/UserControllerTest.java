package com.friendbook.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.friendbook.auth.AuthUtils;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private AuthUtils authUtils;

	@Test
	void registerUser_shouldReturn201_whenRegistrationIsSuccessful() throws Exception {

		mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "userName": "rishi",
				    "firstName": "Rishi",
				    "lastName": "Solanki",
				    "email": "rishi@gmail.com",
				    "mobile": "9192939495",
				    "password": "1234",
				    "gender": "MALE"
				}
				""")).andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.content().string("Registered Successfuly"));

		verify(userService).registerUser(any(UserRegisterRequestDTO.class));
	}

}