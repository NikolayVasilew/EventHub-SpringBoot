package bg.university.eventhub.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  public static final String COOKIE_NAME = "EVENTHUB_TOKEN";

  private final JwtEncoder encoder;
  private final Duration lifetime;

  public JwtService(
      JwtEncoder encoder,
      @Value("${eventhub.jwt.expiration-seconds:3600}") long expirationSeconds) {

    if (expirationSeconds <= 0) {
      throw new IllegalArgumentException("JWT expiration must be positive.");
    }

    this.encoder = encoder;
    this.lifetime = Duration.ofSeconds(expirationSeconds);
  }

  public String generateToken(Authentication authentication) {

    Instant now = Instant.now();

    List<String> roles =
        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer("eventhub")
            .subject(authentication.getName())
            .issuedAt(now)
            .expiresAt(now.plus(lifetime))
            .claim("roles", roles)
            .build();

    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

    return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
  }

  public Duration getLifetime() {
    return lifetime;
  }
}
