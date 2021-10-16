package kernel.heiecab.service;

import kernel.heiecab.domain.User;
import kernel.heiecab.dto.request.AuthRequest;
import kernel.heiecab.dto.request.RegisterRequest;
import kernel.heiecab.dto.response.UserResponse;
import kernel.heiecab.dto.response.Response;
import kernel.heiecab.repository.UserRepository;
import kernel.heiecab.security.JwtSigner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static kernel.heiecab.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthServiceTests {
    @Mock
    UserRepository userRepository;
    AuthService authService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepository, new JwtSigner());
    }

    @BeforeEach
    public void reset() {
        BDDMockito.reset(userRepository);
    }

    @Test
    void registerUserSuccess() {
        User user = mockUser();
        RegisterRequest request = mockRegisterRequest();

        BDDMockito.given(userRepository.save(any(User.class))).willReturn(Mono.just(user));
        BDDMockito.given(userRepository.findByUsername(anyString())).willReturn(Mono.empty());

        final Mono<ResponseEntity<UserResponse>> response = authService.register(request);

        StepVerifier.create(response)
                .thenConsumeWhile(result -> {
                    assertEquals(OK.value(), result.getStatusCodeValue());
                    assertNotNull(result.getBody());
                    assertEquals(user.getId(), result.getBody().getId());
                    assertEquals(user.getEmail(), result.getBody().getEmail());
                    return true;
                })
                .verifyComplete();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUserAlreadyExists() {
        User user = mockUser();
        RegisterRequest request = mockRegisterRequest();

        BDDMockito.given(userRepository.save(any(User.class))).willReturn(Mono.just(user));
        BDDMockito.given(userRepository.findByUsername(anyString())).willReturn(Mono.just(user));

        final Mono<ResponseEntity<UserResponse>> response = authService.register(request);

        StepVerifier.create(response)
                .thenConsumeWhile((result) -> {
                    assertEquals(BAD_REQUEST.value(), result.getStatusCodeValue());
                    return true;
                })
                .expectErrorMatches(error -> error instanceof ResponseStatusException &&
                        Objects.equals(((ResponseStatusException) error).getReason(), "User already exist!"))
                .verify();

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void loginUserSuccess() {
        User user = mockUser();

        BDDMockito.given(userRepository.findByUsername(anyString())).willReturn(Mono.just(user));

        AuthRequest authRequest = mockAuthRequest();

        final Mono<ResponseEntity<Response>> authResponse = authService.login(authRequest);

        StepVerifier.create(authResponse)
                .thenConsumeWhile(result -> {
                    final UserResponse response = (UserResponse) result.getBody();
                    assertEquals(OK.value(), result.getStatusCodeValue());
                    assertNotNull(response);
                    assertEquals(user.getId(), response.getId());
                    assertEquals(user.getEmail(), response.getEmail());
                    assertTrue(Objects.requireNonNull(result.getHeaders()
                            .get(HttpHeaders.SET_COOKIE))
                            .get(0)
                            .startsWith("X-Auth"));
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void loginUserInvalidPassword() {
        User user = mockUser();

        BDDMockito.given(userRepository.save(any(User.class))).willReturn(Mono.just(user));
        BDDMockito.given(userRepository.findByUsername(anyString())).willReturn(Mono.just(user));

        AuthRequest authRequest = mockAuthRequest();
        authRequest.setPassword("wrong");

        final Mono<ResponseEntity<Response>> authResponse = authService.login(authRequest);

        StepVerifier.create(authResponse)
                .thenConsumeWhile((result) -> {
                    assertEquals(BAD_REQUEST.value(), result.getStatusCodeValue());
                    return true;
                })
                .expectErrorMatches(error -> error instanceof ResponseStatusException &&
                        Objects.equals(((ResponseStatusException) error).getReason(), "Invalid password or login"))
                .verify();
    }

    @Test
    void loginUserNotFound() {
        BDDMockito.given(userRepository.findByUsername(anyString())).willReturn(Mono.empty());

        AuthRequest authRequest = mockAuthRequest();

        final Mono<ResponseEntity<Response>> authResponse = authService.login(authRequest);

        StepVerifier.create(authResponse)
                .thenConsumeWhile((result) -> {
                    assertEquals(BAD_REQUEST.value(), result.getStatusCodeValue());
                    return true;
                })
                .expectErrorMatches(error -> error instanceof ResponseStatusException &&
                        Objects.equals(((ResponseStatusException) error).getReason(), "User not found"))
                .verify();
    }

    @Test
    void getProfileSuccess() {
        User user = mockUser();

        BDDMockito.given(userRepository.findByUsername(anyString())).willReturn(Mono.just(user));

        final Mono<ResponseEntity<UserResponse>> profileResponse = authService.getProfile(user.getUsername());

        StepVerifier.create(profileResponse)
                .thenConsumeWhile(result -> {
                    assertEquals(OK.value(), result.getStatusCodeValue());
                    assertNotNull(result.getBody());
                    assertEquals(user.getId(), result.getBody().getId());
                    assertEquals(user.getEmail(), result.getBody().getEmail());
                    return true;
                })
                .verifyComplete();
    }
}
