package nl.novi.eindprojectbackend.config;

import jakarta.servlet.http.HttpServletResponse;
import nl.novi.eindprojectbackend.filters.JwtRequestFilter;
import nl.novi.eindprojectbackend.services.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SpringSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SpringSecurityConfig.class);

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    public SpringSecurityConfig(CustomUserDetailsService customUserDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(customUserDetailsService);
        return new ProviderManager(List.of(authProvider));
    }

    @Bean
    protected SecurityFilterChain filter(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {
                })
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC ENDPOINTS
                        .requestMatchers(HttpMethod.POST, "/authenticate").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()

                        // USER MANAGEMENT
                        .requestMatchers(HttpMethod.POST, "/api/users/create-user").hasRole("MEDEWERKER")

                        // REPAIR TYPES
                        .requestMatchers(HttpMethod.GET, "/api/repair-types/**").hasAnyRole("MONTEUR", "MEDEWERKER")
                        .requestMatchers(HttpMethod.POST, "/api/repair-types/**").hasRole("MONTEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/repair-types/**").hasRole("MONTEUR")
                        .requestMatchers(HttpMethod.PATCH, "/api/repair-types/**").hasRole("MONTEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/repair-types/**").hasRole("MONTEUR")

                        // REPAIRS
                        .requestMatchers(HttpMethod.POST, "/api/cars/*/repairs").hasRole("MONTEUR")
                        .requestMatchers(HttpMethod.PATCH, "/api/cars/*/repairs/*").hasRole("MONTEUR")
                        .requestMatchers(HttpMethod.GET, "/api/cars/*/repairs").hasRole("MONTEUR")
                        .requestMatchers(HttpMethod.GET, "/api/cars/*/repairs/*").hasRole("MONTEUR")

                        // PDFS
                        .requestMatchers(HttpMethod.GET, "/api/pdfs/download/**").hasAnyRole("MONTEUR", "KLANT", "MEDEWERKER")
                        .requestMatchers(HttpMethod.GET, "/api/pdfs/{carId}").hasAnyRole("MONTEUR", "KLANT", "MEDEWERKER")
                        .requestMatchers(HttpMethod.POST, "/api/pdfs/**").hasRole("KLANT")
                        .requestMatchers(HttpMethod.DELETE, "/api/pdfs/**").hasAnyRole("MONTEUR", "KLANT", "MEDEWERKER")

                        // PARTS
                        .requestMatchers(HttpMethod.GET, "/api/parts/**").hasAnyRole("MONTEUR", "MEDEWERKER")
                        .requestMatchers(HttpMethod.POST, "/api/parts/**").hasAnyRole("MONTEUR", "MEDEWERKER")
                        .requestMatchers(HttpMethod.PUT, "/api/parts/**").hasAnyRole("MONTEUR", "MEDEWERKER")
                        .requestMatchers(HttpMethod.PATCH, "/api/parts/**").hasAnyRole("MONTEUR", "MEDEWERKER")
                        .requestMatchers(HttpMethod.DELETE, "/api/parts/**").hasAnyRole("MONTEUR", "MEDEWERKER")

                        // CARS

                        .requestMatchers(HttpMethod.GET, "/api/cars").hasAnyRole("MONTEUR", "MEDEWERKER")
                        .requestMatchers(HttpMethod.GET, "/api/cars/{id}").hasAnyRole("KLANT", "MONTEUR", "MEDEWERKER")

                        .requestMatchers(HttpMethod.POST, "/api/cars/**").hasAnyRole("MONTEUR", "MEDEWERKER")
                        .requestMatchers(HttpMethod.PATCH, "/api/cars/**").hasAnyRole("MONTEUR", "MEDEWERKER")
                        .requestMatchers(HttpMethod.DELETE, "/api/cars/**").hasAnyRole("MONTEUR", "MEDEWERKER")



                                .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            logger.warn("Unauthorized request to: {}", request.getRequestURI());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized request!");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            logger.warn("Access denied for user {} on: {}",
                                    request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Unknown",
                                    request.getRequestURI());
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied!");
                        })
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
