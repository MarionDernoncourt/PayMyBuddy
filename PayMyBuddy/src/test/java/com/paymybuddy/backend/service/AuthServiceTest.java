package com.paymybuddy.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.backend.dto.LoginUserDTO;
import com.paymybuddy.backend.dto.RegistrationUserDTO;
import com.paymybuddy.backend.dto.ValidLoginUserDTO;
import com.paymybuddy.backend.dto.ValidRegistrationUserDTO;
import com.paymybuddy.backend.model.User;
import com.paymybuddy.backend.repository.IUserRepository;
import com.paymybuddy.backend.security.JwtService;
import com.paymybuddy.backend.security.PasswordUtils;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	private IUserRepository userRepository;

	@Mock
	private PasswordUtils passwordUtils;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private AuthService authService;

	@Test
	public void testRegisterUser_Successfully() throws Exception {

		RegistrationUserDTO newUser = new RegistrationUserDTO("john", "john@mail.com", "password123");

		when(userRepository.findByEmailIgnoreCase("john@mail.com")).thenReturn(Optional.empty());
		when(userRepository.findByUsernameIgnoreCase("john")).thenReturn(Optional.empty());
		when(passwordUtils.hashPassword(anyString())).thenReturn("hashedPassword");

		User savedUser = new User();
		savedUser.setEmail("john@mail.com");
		savedUser.setUsername("john");
		savedUser.setPassword("hashedPassword");

		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		ValidRegistrationUserDTO result = authService.registerUser(newUser);

		assertEquals("john", result.getUsername());
		assertEquals("john@mail.com", result.getEmail());
	}

	@Test
	public void testRegisterUser_WhenEmailAlreadyUsed() throws Exception {

		RegistrationUserDTO newUser = new RegistrationUserDTO("john", "john@mail.com", "password123");

		when(userRepository.findByEmailIgnoreCase("john@mail.com")).thenReturn(Optional.of(new User()));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> authService.registerUser(newUser));

		assertEquals("Cet email est déjà utilisé par un autre utilisateur", exception.getMessage());
		verify(userRepository, never()).save(any());
	}

	@Test
	public void testRegisterUser_WhenUsernameAlreadyUsed() throws Exception {

		RegistrationUserDTO newUser = new RegistrationUserDTO("john", "john@mail.com", "password123");

		when(userRepository.findByUsernameIgnoreCase("john")).thenReturn(Optional.of(new User()));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> authService.registerUser(newUser));

		assertEquals("Ce pseudo est déjà pris, merci d'en choisir un autre", exception.getMessage());
		verify(userRepository, never()).save(any());
	}

	@Test
	public void testLoginUser() throws Exception {

		String username = "john";
		String email = "john@gmail.com";
		String password = "johnjohn";
		String hashedPassword = "$2a$10$Xv/xh8RaIfzjrC3D9ZDGWeUaBjgAv4y96vrwnSTtgc9dV5a2vLKby";
		String expectedToken = "mocked-jwt-token";

		User user = new User();
		user.setEmail(email);
		user.setUsername(username);
		user.setPassword(hashedPassword);

		when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
		when(passwordUtils.verifyPassword(password, hashedPassword)).thenReturn(true);
		when(jwtService.generateJwtToken(username)).thenReturn(expectedToken);

		ValidLoginUserDTO result = authService.login(new LoginUserDTO(email, password));

		// then
		assertEquals(username, result.getUsername());
		assertEquals(email, result.getEmail());
		assertEquals(expectedToken, result.getToken());
	}

	@Test
	public void testLoginUser_userNotFound() {

		String email = "notfound@mail.com";
		String password = "password";
		when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> {
			authService.login(new LoginUserDTO(email, password));
		});
	}

	@Test
	public void testLoginUser_wrongPassword() {
		String email = "john@gmail.com";
		String password = "password";
		String hashedPassword = "hashed12345";

		User user = new User();
		user.setUsername("John");
		user.setEmail(email);
		user.setPassword(hashedPassword);

		when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
		when(passwordUtils.verifyPassword(password, hashedPassword)).thenReturn(false);

		assertThrows(IllegalArgumentException.class, () -> {
			authService.login(new LoginUserDTO(email, password));
		});
	}

}
