package com.paymybuddy.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.backend.dto.FriendDTO;
import com.paymybuddy.backend.dto.ProfilDTO;
import com.paymybuddy.backend.dto.UpdateProfilDTO;
import com.paymybuddy.backend.dto.UpdateProfilResponseDTO;
import com.paymybuddy.backend.model.User;
import com.paymybuddy.backend.repository.UserRepository;
import com.paymybuddy.backend.security.JwtService;
import com.paymybuddy.backend.security.PasswordUtils;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordUtils passwordUtils;

	@Mock
	private JwtService jwtService;
	
	@InjectMocks
	private UserService userService;

	@Test
	public void testAddFriend() {
		User user = new User();
		user.setId(1);
		user.setUsername("username");
		user.setEmail("user@gmail.com");
		user.setFriends(new ArrayList<>());

		User friend = new User();
		friend.setId(2);
		friend.setEmail("friend@gmail.com");
		friend.setFriends(new ArrayList<>());

		when(userRepository.findByUsernameIgnoreCase("username")).thenReturn(Optional.of(user));
		when(userRepository.findByEmailIgnoreCase("friend@gmail.com")).thenReturn(Optional.of(friend));
		when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

		FriendDTO result = userService.addFriends("username", "friend@gmail.com");

		assertNotNull(result);
		assertEquals("friend@gmail.com", result.getEmail());
		assertTrue(user.getFriends().contains(friend));

		verify(userRepository).save(user);
	}

	@Test
	void testAddFriends_shouldThrowWhenUserNotFound() {
		when(userRepository.findByUsernameIgnoreCase("username")).thenReturn(Optional.empty());

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> userService.addFriends("username", "friend@example.com"));

		assertEquals("Utilisateur non trouvé", ex.getMessage());
	}

	@Test
	void testAddFriends_shouldThrowWhenAddingSelf() {
		User user = new User();
		user.setId(1);
		user.setUsername("username");
		user.setEmail("user@example.com");

		when(userRepository.findByUsernameIgnoreCase("username")).thenReturn(Optional.of(user));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> userService.addFriends("username", "user@example.com"));

		assertEquals("Vous ne pouvez pas vous ajouter vous même", ex.getMessage());
	}

	@Test
	void testAddFriends_shouldThrowWhenFriendNotFound() {
		User user = new User();
		user.setId(1);
		user.setUsername("username");
		user.setEmail("user@example.com");

		when(userRepository.findByUsernameIgnoreCase("username")).thenReturn(Optional.of(user));
		when(userRepository.findByEmailIgnoreCase("friend@example.com")).thenReturn(Optional.empty());

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> userService.addFriends("username", "friend@example.com"));

		assertEquals("Aucun utilisateur trouvé", ex.getMessage());
	}

	@Test
	void testAddFriends_shouldThrowWhenFriendAlreadyAdded() {
		User user = new User();
		user.setId(1);
		user.setUsername("username");
		user.setEmail("user@example.com");

		User friend = new User();
		friend.setId(2);
		friend.setEmail("friend@example.com");

		user.setFriends(new ArrayList<>());
		user.getFriends().add(friend);

		when(userRepository.findByUsernameIgnoreCase("username")).thenReturn(Optional.of(user));
		when(userRepository.findByEmailIgnoreCase("friend@example.com")).thenReturn(Optional.of(friend));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> userService.addFriends("username", "friend@example.com"));

		assertEquals("Cet utilisateur est déjà dans votre liste d'amis", ex.getMessage());
	}

	@Test
	public void testGetAccountBalance() {
		User user = new User();
		user.setId(1);
		user.setUsername("john");
		user.setAccountBalance(BigDecimal.valueOf(50.00));

		when(userRepository.findByUsernameIgnoreCase("john")).thenReturn(Optional.of(user));

		BigDecimal currentBalance = userService.getAccountBalance("john");

		assertEquals(BigDecimal.valueOf(50.00), currentBalance);
	}

	@Test
	public void testGetAccountBalance_shouldWrongArgument() {
		User user = new User();
		user.setId(1);
		user.setUsername("username");
		user.setAccountBalance(BigDecimal.valueOf(50.00));

		when(userRepository.findByUsernameIgnoreCase("username")).thenReturn(Optional.empty());

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> userService.getAccountBalance("username"));

		assertEquals("Utilisateur non trouvé", ex.getMessage());
	}

	@Test
	public void testAddFixedAmountToAccount() {
		String username = "Harry";
		User user = new User();
		user.setId(1);
		user.setUsername(username);
		user.setAccountBalance(BigDecimal.valueOf(50.00));

		when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(user));

		userService.addFixedAmountToAccount(username);

		BigDecimal expected = new BigDecimal("100.00");

		assertEquals(expected, user.getAccountBalance());

	}

	@Test
	public void testAddFixedAmountToAccount_WithNonExistantUser() {
		String username = "NonExistantUser";
		when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> {
			userService.addFixedAmountToAccount(username);
		});
		verify(userRepository, never()).save(any());
	}

	@Test
	public void testGetUserProfil() {

		User user = new User();
		user.setUsername("Albus");
		user.setEmail("dumbledor@poudlard.com");

		when(userRepository.findByUsernameIgnoreCase("Albus")).thenReturn(Optional.of(user));

		ProfilDTO getUser = userService.getUserProfil("Albus");

		assertEquals("Albus", getUser.getUsername());
		assertEquals("dumbledor@poudlard.com", getUser.getEmail());
	}

	@Test
	public void testGetUserProfil_withNonExistantUser() {

		when(userRepository.findByUsernameIgnoreCase("NonExistantUser"))
				.thenThrow(new IllegalArgumentException("Utilisateur non trouvé"));

		assertThrows(IllegalArgumentException.class, () -> {
			userService.getUserProfil("NonExistantUser");
		});
	}
	
	@Test
	public void testUpdateUserProfil() {
		
		String connectedUser = "Harry";
		
		UpdateProfilDTO updateProfil = new UpdateProfilDTO("newHarry", "newhpotter@gryffondor.com", "newPassword");
		
		User user = new User();
		user.setUsername("Harry");
		user.setEmail("hpotter@gryffondor.com");
		user.setPassword("oldPassword");
		
		when(userRepository.findByUsernameIgnoreCase(connectedUser)).thenReturn(Optional.of(user));
		
		when(userRepository.findByUsernameIgnoreCase("newHarry")).thenReturn(Optional.empty());
		
	when(userRepository.findByEmailIgnoreCase("newhpotter@gryffondor.com")).thenReturn(Optional.empty());
		
		when(passwordUtils.hashPassword("newPassword")).thenReturn("hashedNewPassword");
		
		when(jwtService.generateJwtToken("newHarry")).thenReturn("newToken");
		
		UpdateProfilResponseDTO response = userService.updateUserProfil(connectedUser, updateProfil);
		
		assertEquals("newHarry", response.getUsername());
		assertEquals("newhpotter@gryffondor.com", response.getEmail() );
		assertEquals("newToken", response.getToken());
		
		verify(userRepository).save(user);
		verify(jwtService).generateJwtToken("newHarry");
	}
	
	

}
