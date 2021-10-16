package kernel.heiecab.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange)
                .flatMap(e -> Mono.justOrEmpty(e.getRequest().getCookies().get("X-Auth")))
                .filter(cookie -> !cookie.isEmpty())
                .map(value -> value.get(0).getValue())
                .map(value -> new UsernamePasswordAuthenticationToken(value, value));
    }
}
