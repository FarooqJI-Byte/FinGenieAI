package com.fingenie.ai.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.fingenie.ai.dto.LoginRequest;
import com.fingenie.ai.dto.LoginResponse;
import com.fingenie.ai.dto.RegisterRequest;
import com.fingenie.ai.dto.VerifyOtpRequest;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.enums.Role;
import com.fingenie.ai.exception.BusinessException;
import com.fingenie.ai.exception.ResourceNotFoundException;
import com.fingenie.ai.repository.UserRepository;
import com.fingenie.ai.security.JwtUtil;

/**
 * ✅ Unit Test for AuthServiceImpl
 *
 * Covers:
 * - Register flow (with OTP + Email)
 * - Login flow (JWT generation)
 * - OTP verification
 * - Validation + error scenarios
 */
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * ✅ TEST: register SUCCESS
     */
    @Test
    void register_success() {

        RegisterRequest request = new RegisterRequest();
        request.setName("John");
        request.setEmail("john@gmail.com");
        request.setPassword("1234");

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("1234"))
                .thenReturn("encoded_password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setUserId(1L);
                    return user;
                });

        doNothing().when(emailService)
                .sendEmail(eq("john@gmail.com"), anyString());

        var response = authService.register(request);

        assertNotNull(response);
        assertEquals("John", response.getName());
        assertEquals("john@gmail.com", response.getEmail());

        verify(emailService, times(1))
                .sendEmail(eq("john@gmail.com"), anyString());
    }

    /**
     * ❌ TEST: register FAIL - email already exists
     */
    @Test
    void register_shouldThrow_whenEmailExists() {

        RegisterRequest request = new RegisterRequest();
        request.setName("John");
        request.setEmail("john@gmail.com");
        request.setPassword("1234");

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(BusinessException.class,
                () -> authService.register(request));
    }

    /**
     * ❌ TEST: register FAIL - invalid email
     */
    @Test
    void register_shouldThrow_whenInvalidEmail() {

        RegisterRequest request = new RegisterRequest();
        request.setName("John");
        request.setEmail("invalid-email");
        request.setPassword("1234");

        assertThrows(BusinessException.class,
                () -> authService.register(request));
    }

    /**
     * ❌ TEST: register FAIL - email sending fails
     */
    @Test
    void register_shouldThrow_whenEmailFails() {

        RegisterRequest request = new RegisterRequest();
        request.setName("John");
        request.setEmail("john@gmail.com");
        request.setPassword("1234");

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doThrow(new RuntimeException("Mail failed"))
                .when(emailService)
                .sendEmail(anyString(), anyString());

        assertThrows(BusinessException.class,
                () -> authService.register(request));
    }

    /**
     * ✅ TEST: login SUCCESS
     */
    @Test
    void login_success() {

        LoginRequest request = new LoginRequest();
        request.setEmail("john@gmail.com");
        request.setPassword("1234");

        User user = User.builder()
                .userId(1L)
                .email("john@gmail.com")
                .password("encoded")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("1234", "encoded"))
                .thenReturn(true);

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("CUSTOMER", response.getRole());
        assertEquals(1L, response.getUserId());
    }

    /**
     * ❌ TEST: login FAIL - user not found
     */
    @Test
    void login_shouldThrow_whenUserNotFound() {

        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@gmail.com");
        request.setPassword("1234");

        when(userRepository.findByEmail("notfound@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> authService.login(request));
    }

    /**
     * ❌ TEST: login FAIL - wrong password
     */
    @Test
    void login_shouldThrow_whenInvalidPassword() {

        LoginRequest request = new LoginRequest();
        request.setEmail("john@gmail.com");
        request.setPassword("wrong");

        User user = User.builder()
                .email("john@gmail.com")
                .password("encoded")
                .build();

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        assertThrows(BusinessException.class,
                () -> authService.login(request));
    }

    /**
     * ✅ TEST: verifyOtp SUCCESS
     */
    @Test
    void verifyOtp_success() {

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("john@gmail.com");
        request.setOtp("123456");

        User user = User.builder()
                .email("john@gmail.com")
                .otp("123456")
                .verified(false)
                .build();

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        authService.verifyOtp(request);

        assertTrue(user.isVerified());
        assertNull(user.getOtp());

        verify(userRepository).save(user);
    }

    /**
     * ❌ TEST: verifyOtp FAIL - user not found
     */
    @Test
    void verifyOtp_shouldThrow_whenUserNotFound() {

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("notfound@gmail.com");

        when(userRepository.findByEmail("notfound@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authService.verifyOtp(request));
    }

    /**
     * ❌ TEST: verifyOtp FAIL - already verified
     */
    @Test
    void verifyOtp_shouldThrow_whenAlreadyVerified() {

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("john@gmail.com");
        request.setOtp("123456");

        User user = User.builder()
                .email("john@gmail.com")
                .otp("123456")
                .verified(true)
                .build();

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(BusinessException.class,
                () -> authService.verifyOtp(request));
    }

    /**
     * ❌ TEST: verifyOtp FAIL - wrong OTP
     */
    @Test
    void verifyOtp_shouldThrow_whenInvalidOtp() {

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("john@gmail.com");
        request.setOtp("000000");

        User user = User.builder()
                .email("john@gmail.com")
                .otp("123456")
                .verified(false)
                .build();

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(BusinessException.class,
                () -> authService.verifyOtp(request));
    }

    /**
     * ❌ TEST: verifyOtp FAIL - OTP already used/null
     */
    @Test
    void verifyOtp_shouldThrow_whenOtpNull() {

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("john@gmail.com");
        request.setOtp("123456");

        User user = User.builder()
                .email("john@gmail.com")
                .otp(null)
                .verified(false)
                .build();

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(BusinessException.class,
                () -> authService.verifyOtp(request));
    }
}