package kernel.heiecab.service;

import kernel.heiecab.dto.mapper.UserMapper;
import kernel.heiecab.dto.request.AuthRequest;
import kernel.heiecab.dto.request.RegisterRequest;
import kernel.heiecab.dto.response.RegisterResponse;
import kernel.heiecab.dto.response.Response;
import kernel.heiecab.repository.UserRepository;
import kernel.heiecab.security.JwtSigner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtSigner jwtSigner;

    public Mono<RegisterResponse> register(RegisterRequest request) {
        return isUserExist(request.getUsername())
                .filter(isExist -> !isExist)
                .switchIfEmpty(Mono.error(new RuntimeException("User already exist!")))
                .flatMap(isNotExist -> userRepository.save(UserMapper.INSTANCE.fromDto(request)))
                .flatMap(user -> Mono.just(UserMapper.INSTANCE.toDTO(user)))
                .log();
    }

    private Mono<Boolean> isUserExist(String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> Mono.just(true))
                .switchIfEmpty(Mono.just(false));
    }

    public Mono<ResponseEntity<RegisterResponse>> getProfile(String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> Mono.just(ResponseEntity.ok(UserMapper.INSTANCE.toDTO(user))))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()))
                .log();
    }

    public Mono<ResponseEntity<Response>> login(AuthRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found")))
                .filter(user -> user.getPassword().equals(request.getPassword()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password or login")))
                .map(user -> {
                    final String jwt = jwtSigner.createJwt(user.getUsername());
                    final ResponseCookie cookie = ResponseCookie.fromClientResponse("X-Auth", jwt)
                            .maxAge(3600)
                            .httpOnly(true)
                            .path("/")
                            .secure(false)
                            .build();

                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

                    return new ResponseEntity<>(UserMapper.INSTANCE.toDTO(user), headers, HttpStatus.OK);
                });
    }
}
