package bg.university.eventhub.config;

import bg.university.eventhub.model.AppUser;
import bg.university.eventhub.repository.UserRepository;
import bg.university.eventhub.service.JwtService;
import jakarta.servlet.http.Cookie;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.util.WebUtils;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService userDetailsService(UserRepository users) {
    return email -> {
      AppUser user =
          users
              .findByEmail(email.toLowerCase(Locale.ROOT))
              .orElseThrow(() -> new UsernameNotFoundException("Unknown user"));

      return new User(
          user.getEmail(),
          user.getPassword(),
          java.util.List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    };
  }

  @Bean
  AuthenticationManager authenticationManager(
      UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

    provider.setPasswordEncoder(passwordEncoder);

    return new ProviderManager(provider);
  }

  @Bean
  BearerTokenResolver bearerTokenResolver() {
    return request -> {
      String authorization = request.getHeader("Authorization");

      if (authorization != null && authorization.startsWith("Bearer ")) {
        return authorization.substring(7);
      }

      Cookie cookie = WebUtils.getCookie(request, JwtService.COOKIE_NAME);

      return cookie == null ? null : cookie.getValue();
    };
  }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter() {

    JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();

    authorities.setAuthoritiesClaimName("roles");
    authorities.setAuthorityPrefix("");

    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

    converter.setJwtGrantedAuthoritiesConverter(authorities);

    return converter;
  }

  @Bean
  SecurityFilterChain filterChain(
      HttpSecurity http,
      BearerTokenResolver bearerTokenResolver,
      JwtAuthenticationConverter jwtAuthenticationConverter)
      throws Exception {

    CookieCsrfTokenRepository csrfRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

    csrfRepository.setCookiePath("/");

    CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();

    http.csrf(csrf -> csrf.csrfTokenRepository(csrfRepository).csrfTokenRequestHandler(csrfHandler))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .requestCache(cache -> cache.disable())
        .authorizeHttpRequests(
            auth ->
                auth

                    // Записването и отказът изискват вход.
                    .requestMatchers(HttpMethod.POST, "/events/*/join", "/events/*/leave")
                    .authenticated()

                    // Административни страници.
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                    // Публични страници.
                    .requestMatchers(
                        "/",
                        "/login",
                        "/signup",
                        "/events/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/error",
                        "/favicon.ico")
                    .permitAll()

                    // Всичко останало изисква JWT.
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth ->
                oauth
                    .bearerTokenResolver(bearerTokenResolver)
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                    (request, response, exception) -> {
                      if (request.getRequestURI().startsWith("/api/")) {

                        response.sendError(401);
                        return;
                      }

                      response.sendRedirect(request.getContextPath() + "/login");
                    }))
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout")
                    .deleteCookies(JwtService.COOKIE_NAME, "XSRF-TOKEN")
                    .logoutSuccessUrl("/login?logout"));

    return http.build();
  }
}
