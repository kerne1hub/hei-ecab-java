package kernel.heiecab.security;

import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtSigner jwtSigner;

    @Autowired
    public JwtAuthenticationManager(JwtSigner jwtSigner) {
        this.jwtSigner = jwtSigner;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .map(auth -> jwtSigner.validate(auth.getCredentials().toString()))
                .onErrorMap(SignatureException.class, error -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, error.getMessage()))
                .map(claims -> new UsernamePasswordAuthenticationToken(
                        claims.getBody().getSubject(),
                        authentication.getCredentials().toString(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                ));
    }
}
