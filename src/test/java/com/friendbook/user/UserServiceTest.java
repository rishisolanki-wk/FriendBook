package com.friendbook.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.friendbook.exception.ResourceAlreadyExistsException;
import com.friendbook.exception.UserNotFoundException;
import com.friendbook.follow.FollowRepository;
import com.friendbook.follow.FollowStatus;
import com.friendbook.posts.CloudinaryService;
import com.friendbook.posts.CloudinaryUploadResult;
import com.friendbook.posts.PostResponseDTO;
import com.friendbook.posts.PostService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private PostService postService;

	@Mock
	private CloudinaryService cloudinaryService;

	@Mock
	private FollowRepository followRepository;

	@InjectMocks
	private UserService userService;

	@Test
	void registerUser_shouldSaveUser_whenEmailDoesNotExist() {

		UserRegisterRequestDTO request = new UserRegisterRequestDTO();
		request.setEmail("rishi@gmail.com");
		request.setPassword("1234");
		request.setUserName("rishi");
		request.setFirstName("Rishi");
		request.setLastName("Solanki");

		when(userRepository.findByEmail("rishi@gmail.com")).thenReturn(Optional.empty());

		when(passwordEncoder.encode("1234")).thenReturn("ENCODED_PASSWORD");

		userService.registerUser(request);

		verify(userRepository).findByEmail("rishi@gmail.com");
		verify(passwordEncoder).encode("1234");
		verify(userRepository).save(any(User.class));
	}

	@Test
	void registerUser_shouldThrowException_whenEmailAlreadyExists() {
		UserRegisterRequestDTO registerRequestDTO = new UserRegisterRequestDTO();
		registerRequestDTO.setEmail("rishi@gmail.com");

		User user = new User();

		when(userRepository.findByEmail("rishi@gmail.com")).thenReturn(Optional.of(user));

		assertThrows(ResourceAlreadyExistsException.class, () -> userService.registerUser(registerRequestDTO));

		verify(userRepository, never()).save(any(User.class));
		verify(passwordEncoder, never()).encode(anyString());

	}

	@Test
	void getUserByUserName_shouldReturnProfile_whenUserExists() {

		User user = new User();
		user.setUserId(1L);
		user.setUserName("rishi");
		user.setEmail("rishi@gmail.com");
		user.setFirstName("Rishi");
		user.setLastName("Solanki");

		when(userRepository.findByUserName("rishi")).thenReturn(Optional.of(user));

		List<PostResponseDTO> posts = List.of();

		when(postService.getPostByUserId(1L)).thenReturn(posts);

		when(followRepository.countByFollowing_UserIdAndStatus(1L, FollowStatus.ACCEPTED)).thenReturn(10L);

		when(followRepository.countByFollower_UserIdAndStatus(1L, FollowStatus.ACCEPTED)).thenReturn(5L);

		UserProfileResponseDTO result = userService.getUserByUserName("rishi");

		assertNotNull(result);
		assertEquals("rishi", result.getUserName());
		assertEquals("rishi@gmail.com", result.getEmail());
		assertEquals("Rishi", result.getFirstName());
		assertEquals(10L, result.getFollowers());
		assertEquals(5L, result.getFollowings());
		assertEquals(posts, result.getPosts());

		verify(userRepository).findByUserName("rishi");
		verify(postService).getPostByUserId(1L);
	}

	@Test
	void getUserByUserName_shouldThrowException_whenUserDoesNotExist() {

		when(userRepository.findByUserName("unknown")).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.getUserByUserName("unknown"));

		verify(postService, never()).getPostByUserId(anyLong());
	}

	@Test
	void getFeedPosts_shouldReturnPostsForFollowingUsers() {

		User user = new User();
		user.setUserId(1L);

		List<Long> followingIds = List.of(2L, 3L);

		Page<PostResponseDTO> expectedPage = Page.empty();

		when(userRepository.findFollowingUserIds(1L)).thenReturn(followingIds);

		when(postService.getFeedPosts(followingIds, 0, 10, 1L)).thenReturn(expectedPage);

		Page<PostResponseDTO> result = userService.getFeedPosts(user, 0, 10);

		assertEquals(expectedPage, result);

		verify(userRepository).findFollowingUserIds(1L);

		verify(postService).getFeedPosts(followingIds, 0, 10, 1L);
	}

	@Test
	void editProfile_shouldUpdateProfile_whenUserExists() {

		User user = new User();
		user.setUserId(1L);
		user.setProfileBio("Old bio");
		user.setAccountStatus(AccountStatus.PUBLIC);

		UserProfileEditRequestDTO request = new UserProfileEditRequestDTO();

		request.setProfileBio("New bio");
		request.setAccountStatus(AccountStatus.PRIVATE);
		request.setProfileImage(null);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		userService.editProfile(request, user);

		assertEquals("New bio", user.getProfileBio());
		assertEquals(AccountStatus.PRIVATE, user.getAccountStatus());

		verify(userRepository).save(user);
		verify(cloudinaryService, never()).uploadImage(any());
	}

	@Test
	void editProfile_shouldUploadImage_whenProfileImageProvided() {

		User user = new User();
		user.setUserId(1L);
		user.setAccountStatus(AccountStatus.PUBLIC);

		UserProfileEditRequestDTO request = new UserProfileEditRequestDTO();

		request.setAccountStatus(AccountStatus.PUBLIC);

		MultipartFile image = mock(MultipartFile.class);
		request.setProfileImage(image);

		CloudinaryUploadResult uploadResult = new CloudinaryUploadResult("https://cloudinary.com/image.jpg",
				"profile_123");

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(cloudinaryService.uploadImage(image)).thenReturn(uploadResult);

		userService.editProfile(request, user);

		assertEquals("https://cloudinary.com/image.jpg", user.getProfileImage());

		assertEquals("profile_123", user.getImagePublicId());

		verify(cloudinaryService).uploadImage(image);
		verify(userRepository).save(user);
	}

	@Test
	void editProfile_shouldThrowException_whenUserDoesNotExist() {

		User user = new User();
		user.setUserId(1L);

		UserProfileEditRequestDTO request = new UserProfileEditRequestDTO();

		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> userService.editProfile(request, user));

		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void searchUsers_shouldReturnMappedUsers() {

		User user1 = new User();
		user1.setUserId(1L);
		user1.setUserName("rishi");
		user1.setFirstName("Rishi");
		user1.setLastName("Solanki");
		user1.setProfileImage("image1");

		User user2 = new User();
		user2.setUserId(2L);
		user2.setUserName("john");
		user2.setFirstName("John");
		user2.setLastName("Doe");
		user2.setProfileImage("image2");

		when(userRepository.searchUsers("ri")).thenReturn(List.of(user1, user2));

		List<UserProfileSearchResponseDTO> result = userService.searchUsers("ri");

		assertEquals(2, result.size());

		assertEquals(1L, result.get(0).getUserId());
		assertEquals("rishi", result.get(0).getUserName());

		assertEquals(2L, result.get(1).getUserId());
		assertEquals("john", result.get(1).getUserName());

		verify(userRepository).searchUsers("ri");
	}

	@Test
	void getPublicProfileViewById_shouldReturnPosts_whenAccountIsPublic() {

		User user = new User();
		user.setUserId(1L);
		user.setUserName("rishi");
		user.setFirstName("Rishi");
		user.setLastName("Solanki");
		user.setActiveStatus(true);
		user.setAccountStatus(AccountStatus.PUBLIC);

		List<PostResponseDTO> posts = List.of();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(followRepository.countByFollower_UserIdAndStatus(1L, FollowStatus.ACCEPTED)).thenReturn(5L);

		when(followRepository.countByFollowing_UserIdAndStatus(1L, FollowStatus.ACCEPTED)).thenReturn(10L);

		when(postService.getPostByUserId(1L)).thenReturn(posts);

		PublicProfileViewDTO result = userService.getPublicProfileViewById(1L);

		assertNotNull(result);
		assertEquals("rishi", result.getUserName());
		assertEquals(AccountStatus.PUBLIC, result.getAccountStatus());
		assertEquals(posts, result.getPosts());
		assertEquals(10L, result.getFollowers());
		assertEquals(5L, result.getFollowings());

		verify(postService).getPostByUserId(1L);
	}

	@Test
	void getPublicProfileViewById_shouldNotReturnPosts_whenAccountIsPrivate() {

		User user = new User();
		user.setUserId(1L);
		user.setUserName("rishi");
		user.setActiveStatus(true);
		user.setAccountStatus(AccountStatus.PRIVATE);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(followRepository.countByFollower_UserIdAndStatus(1L, FollowStatus.ACCEPTED)).thenReturn(10L);

		when(followRepository.countByFollowing_UserIdAndStatus(1L, FollowStatus.ACCEPTED)).thenReturn(5L);

		PublicProfileViewDTO result = userService.getPublicProfileViewById(1L);

		assertNotNull(result);
		assertNull(result.getPosts());

		verify(postService, never()).getPostByUserId(anyLong());
	}
}