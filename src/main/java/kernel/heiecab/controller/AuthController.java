package kernel.heiecab.controller;

import kernel.heiecab.dto.request.AuthRequest;
import kernel.heiecab.dto.request.RegisterRequest;
import kernel.heiecab.dto.response.RegisterResponse;
import kernel.heiecab.dto.response.Response;
import kernel.heiecab.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public Mono<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Response>> login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @GetMapping("/user")
    public Mono<ResponseEntity<RegisterResponse>> profile(Mono<Principal> principal) {
        return principal
                .map(Principal::getName)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password")))
                .flatMap(authService::getProfile);
    }
}
