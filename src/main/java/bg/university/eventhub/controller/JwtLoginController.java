package bg.university.eventhub.controller;

import bg.university.eventhub.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JwtLoginController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public JwtLoginController(AuthenticationManager authenticationManager, JwtService jwtService) {

    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @PostMapping("/login")
  public String login(
      @RequestParam("username") String email,
      @RequestParam("password") String password,
      HttpServletRequest request,
      HttpServletResponse response) {

    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(email.trim(), password));

      String token = jwtService.generateToken(authentication);

      ResponseCookie cookie =
          ResponseCookie.from(JwtService.COOKIE_NAME, token)
              .httpOnly(true)
              .secure(request.isSecure())
              .sameSite("Lax")
              .path("/")
              .maxAge(jwtService.getLifetime())
              .build();

      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

      return "redirect:/";

    } catch (AuthenticationException ex) {
      return "redirect:/login?error";
    }
  }
}
