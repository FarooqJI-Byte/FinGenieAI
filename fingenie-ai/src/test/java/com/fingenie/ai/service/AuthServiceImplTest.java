package com.fingenie.ai.service;

import com.fingenie.ai.dto.*;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.enums.Role;
import com.fingenie.ai.exception.BusinessException;
import com.fingenie.ai.exception.ResourceNotFoundException;
import com.fingenie.ai.repository.UserRepository;
import com.fingenie.ai.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        // ✅ inject @Value
        ReflectionTestUtils.setField(authService, "otpExpirationMinutes", 5L);
    }

    // ✅ REGISTER SUCCESS
    @Test
    void register_shouldWork() {

        RegisterRequest request = new RegisterRequest();
        request.setName("Farooq");
        request.setEmail("test@gmail.com");
        request.setPassword("pass123");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("pass123"))
                .thenReturn("encoded");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setUserId(1L);
                    return u;
                });

        doNothing().when(emailService)
                .sendEmail(anyString(), anyString());

        UserResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("test@gmail.com", response.getEmail());
    }

    // ✅ REGISTER - EMAIL EXISTS
    @Test
    void register_shouldFail_ifEmailExists() {

        RegisterRequest request = new RegisterRequest();
        request.setName("Farooq");
        request.setEmail("test@gmail.com");
        request.setPassword("pass123");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(BusinessException.class,
                () -> authService.register(request));
    }

    // ✅ LOGIN SUCCESS
    @Test
    void login_shouldReturnToken() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("pass123");

        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@gmail.com");
        user.setPassword("encoded");
        user.setRole(Role.CUSTOMER);
        user.setVerified(true);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("pass123", "encoded"))
                .thenReturn(true);

        when(jwtUtil.generateToken("test@gmail.com", "CUSTOMER"))
                .thenReturn("token123");

        LoginResponse response = authService.login(request);

        assertEquals("token123", response.getToken());
        assertEquals("CUSTOMER", response.getRole());
        assertEquals(1L, response.getUserId());
    }

    // ✅ LOGIN - WRONG PASSWORD
    @Test
    void login_shouldFail_invalidPassword() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("wrong");

        User user = new User();
        user.setPassword("encoded");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        assertThrows(BusinessException.class,
                () -> authService.login(request));
    }

    // ✅ LOGIN - NOT VERIFIED
    @Test
    void login_shouldFail_notVerified() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("pass123");

        User user = new User();
        user.setPassword("encoded");
        user.setVerified(false);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        assertThrows(BusinessException.class,
                () -> authService.login(request));
    }

    // ✅ VERIFY OTP SUCCESS
    @Test
    void verifyOtp_shouldWork() {

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("test@gmail.com");
        request.setOtp("123456");

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setOtp("123456");
        user.setVerified(false);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        authService.verifyOtp(request);

        assertTrue(user.isVerified());
        assertNull(user.getOtp());
    }

    // ✅ VERIFY OTP - WRONG OTP
    @Test
    void verifyOtp_shouldFail_wrongOtp() {

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("test@gmail.com");
        request.setOtp("000000");

        User user = new User();
        user.setOtp("123456");
        user.setVerified(false);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(BusinessException.class,
                () -> authService.verifyOtp(request));
    }

    // ✅ VERIFY OTP - EXPIRED
    @Test
    void verifyOtp_shouldFail_expiredOtp() {

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("test@gmail.com");
        request.setOtp("123456");

        User user = new User();
        user.setOtp("123456");
        user.setVerified(false);
        user.setOtpExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(BusinessException.class,
                () -> authService.verifyOtp(request));
    }

    // ✅ VERIFY OTP - USER NOT FOUND
    @Test
    void verifyOtp_shouldFail_userNotFound() {

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authService.verifyOtp(request));
    }
}