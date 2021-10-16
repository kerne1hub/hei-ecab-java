package kernel.heiecab.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveAuthenticationManager jwtAuthManager,
                                                         ServerAuthenticationConverter jwtAuthConverter) {
        final AuthenticationWebFilter authWebFilter = new AuthenticationWebFilter(jwtAuthManager);
        authWebFilter.setServerAuthenticationConverter(jwtAuthConverter);

        return http.authorizeExchange()
                .pathMatchers("/auth/**").permitAll()
                .pathMatchers("/auth/user").authenticated()
                .and()
                .addFilterAt(authWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .build();
    }
}
