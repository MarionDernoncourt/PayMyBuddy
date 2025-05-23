package com.paymybuddy.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import com.paymybuddy.backend.model.User;
import com.paymybuddy.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private UserService userService;

	

	@Test
	public void testAddFriend() {
		User user = new User() ;
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
		
		FriendDTO result = userService.addFriends("username","friend@gmail.com");
		
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
	    
}
