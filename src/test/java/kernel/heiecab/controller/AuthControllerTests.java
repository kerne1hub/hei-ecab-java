package kernel.heiecab.controller;

import kernel.heiecab.domain.User;
import kernel.heiecab.dto.mapper.UserMapper;
import kernel.heiecab.dto.request.AuthRequest;
import kernel.heiecab.dto.request.RegisterRequest;
import kernel.heiecab.dto.response.UserResponse;
import kernel.heiecab.dto.response.Response;
import kernel.heiecab.security.JwtAuthenticationManager;
import kernel.heiecab.security.JwtServerAuthenticationConverter;
import kernel.heiecab.security.JwtSigner;
import kernel.heiecab.security.SecurityConfiguration;
import kernel.heiecab.service.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static kernel.heiecab.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@WebFluxTest(AuthController.class)
@Import({SecurityConfiguration.class, JwtAuthenticationManager.class, JwtServerAuthenticationConverter.class, JwtSigner.class})
class AuthControllerTests {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    AuthService authService;

    @Test
    void testRegisterNewUser() {
        User user = mockUser();
        RegisterRequest request = mockRegisterRequest();

        final ResponseEntity<UserResponse> response = ResponseEntity.ok(UserMapper.INSTANCE.toDTO(user));

        Mockito.when(authService.register(any(RegisterRequest.class)))
                .thenReturn(Mono.just(response));

        webClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .value(responseBody -> {
                    assertNotNull(responseBody);
                    Assertions.assertEquals(user.getId(), responseBody.getId());
                    Assertions.assertEquals(user.getEmail(), responseBody.getEmail());
                });
    }

    @Test
    void testLogin() {
        User user = mockUser();
        AuthRequest request = mockAuthRequest();

        final ResponseEntity<Response> response = ResponseEntity.ok(UserMapper.INSTANCE.toDTO(user));

        Mockito.when(authService.login(any(AuthRequest.class)))
                .thenReturn(Mono.just(response));

        webClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .value(responseBody -> {
                    assertNotNull(responseBody);
                    Assertions.assertEquals(user.getId(), responseBody.getId());
                    Assertions.assertEquals(user.getEmail(), responseBody.getEmail());
                });
    }

    @Test
    @WithMockUser
    void testGetProfile() {
        User user = mockUser();

        final ResponseEntity<UserResponse> response = ResponseEntity.ok(UserMapper.INSTANCE.toDTO(user));

        Mockito.when(authService.getProfile(anyString()))
                .thenReturn(Mono.just(response));

        webClient.get()
                .uri("/auth/user")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .value(responseBody -> {
                    assertNotNull(responseBody);
                    Assertions.assertEquals(user.getId(), responseBody.getId());
                    Assertions.assertEquals(user.getEmail(), responseBody.getEmail());
                });
    }
}
