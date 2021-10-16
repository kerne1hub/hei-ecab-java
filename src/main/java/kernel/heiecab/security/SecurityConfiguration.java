package kernel.heiecab.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfiguration {
    private final NegatedServerWebExchangeMatcher negateWhiteList =
            new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers(
                    "/auth/login",
                    "/auth/register"
            ));

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveAuthenticationManager jwtAuthManager,
                                                         ServerAuthenticationConverter jwtAuthConverter) {



        final AuthenticationWebFilter authWebFilter = new AuthenticationWebFilter(jwtAuthManager);
        authWebFilter.setServerAuthenticationConverter(jwtAuthConverter);
        authWebFilter.setRequiresAuthenticationMatcher(negateWhiteList);

        return http.exceptionHandling()
                .authenticationEntryPoint((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .accessDeniedHandler((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                .and()
                .authorizeExchange()
                .pathMatchers("/auth/**").permitAll()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(authWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .build();
    }
}
