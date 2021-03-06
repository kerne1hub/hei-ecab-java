package kernel.heiecab.service;

import kernel.heiecab.dto.mapper.UserMapper;
import kernel.heiecab.dto.request.AuthRequest;
import kernel.heiecab.dto.request.RegisterRequest;
import kernel.heiecab.dto.response.UserResponse;
import kernel.heiecab.dto.response.Response;
import kernel.heiecab.repository.UserRepository;
import kernel.heiecab.security.JwtSigner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtSigner jwtSigner;

    public Mono<ResponseEntity<UserResponse>> register(RegisterRequest request) {
        return isUserExist(request.getUsername())
                .filter(isExist -> !isExist)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exist!")))
                .flatMap(isNotExist -> userRepository.save(UserMapper.INSTANCE.fromDto(request)))
                .map(UserMapper.INSTANCE::toDTO)
                .map(ResponseEntity::ok);
    }

    private Mono<Boolean> isUserExist(String username) {
        return userRepository.findByUsername(username)
                .map(user -> true)
                .switchIfEmpty(Mono.just(false));
    }

    public Mono<ResponseEntity<UserResponse>> getProfile(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper.INSTANCE::toDTO)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
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
