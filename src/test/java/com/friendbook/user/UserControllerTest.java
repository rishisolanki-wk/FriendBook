package com.friendbook.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.friendbook.auth.AuthUtils;
import com.friendbook.exception.ResourceAlreadyExistsException;
import com.friendbook.posts.PostResponseDTO;

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

	@Test
	void registerUser_shouldReturn409_whenRegistrationFails() throws Exception {

		doThrow(new ResourceAlreadyExistsException("User Already Exists!")).when(userService)
				.registerUser(any(UserRegisterRequestDTO.class));

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
				""")).andExpect(status().isConflict())
				.andExpect(MockMvcResultMatchers.content().string("User not registered : User Already Exists!"));
	}

	@Test
	void getProfile_shouldReturn200_whenProfileExists() throws Exception {
		User user = new User();
		user.setUserName("rishi");

		UserProfileResponseDTO profileResponseDTO = new UserProfileResponseDTO();
		profileResponseDTO.setUserName("rishi");
		profileResponseDTO.setEmail("rishi@gmail.com");

		when(userService.getUserByUserName("rishi")).thenReturn(profileResponseDTO);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);

		mockMvc.perform(get("/api/profile").principal(authentication)).andExpect(status().isOk())
				.andExpect(jsonPath("$.userName").value("rishi"))
				.andExpect(jsonPath("$.email").value("rishi@gmail.com"));

		verify(userService).getUserByUserName("rishi");

	}

	@Test
	void getProfile_shouldReturn400_whenServiceReturnsNull() throws Exception {
		User user = new User();
		user.setUserName("rishi");

		Authentication authentication = mock(Authentication.class);

		when(authentication.getPrincipal()).thenReturn(user);

		when(userService.getUserByUserName("rishi")).thenReturn(null);

		mockMvc.perform(get("/api/profile").principal(authentication)).andExpect(status().isBadRequest());
	}

	@Test
	void searchUsers_shouldReturn200_withSearchResults() throws Exception {

		UserProfileSearchResponseDTO userDto = new UserProfileSearchResponseDTO(1L, "rishi", "Rishi", "Solanki",
				"profile.jpg");

		when(userService.searchUsers("ri")).thenReturn(List.of(userDto));

		mockMvc.perform(get("/api/search").param("keyword", "ri")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].userId").value(1)).andExpect(jsonPath("$[0].userName").value("rishi"))
				.andExpect(jsonPath("$[0].firstName").value("Rishi"));

		verify(userService).searchUsers("ri");

	}

	@Test
	void publicProfileView_shouldReturn200_whenProfileExists() throws Exception {

		PublicProfileViewDTO responseDTO = new PublicProfileViewDTO();

		responseDTO.setUserId(1L);
		responseDTO.setUserName("rishi");

		when(userService.getPublicProfileViewById(1L)).thenReturn(responseDTO);

		mockMvc.perform(get("/api/profile/public-view/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value(1)).andExpect(jsonPath("$.userName").value("rishi"));

		verify(userService).getPublicProfileViewById(1L);
	}

	@Test
	void getUserFeedPosts_shouldReturn200_withPosts() throws Exception {

		User user = new User();
		user.setUserId(1L);
		user.setUserName("rishi");

		Page<PostResponseDTO> page = Page.empty();

		when(userService.getFeedPosts(user, 0, 10)).thenReturn(page);

		Authentication authentication = mock(Authentication.class);

		when(authentication.getPrincipal()).thenReturn(user);

		mockMvc.perform(get("/api/home/0/10").principal(authentication)).andExpect(status().isOk());

		verify(userService).getFeedPosts(user, 0, 10);
	}

	@Test
	void editProfile_shouldReturn200_whenProfileIsUpdated() throws Exception {

		User user = new User();
		user.setUserId(1L);
		user.setUserName("rishi");

		Authentication authentication = mock(Authentication.class);

		when(authentication.getPrincipal()).thenReturn(user);

		mockMvc.perform(put("/api/profile/edit").principal(authentication).param("profileBio", "New bio")
				.param("accountStatus", "PUBLIC")).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Profile Edited!"));

		verify(userService).editProfile(any(UserProfileEditRequestDTO.class), any(User.class));
	}

}