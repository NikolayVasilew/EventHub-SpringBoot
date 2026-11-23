package bg.university.eventhub.repository;

import bg.university.eventhub.model.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByEmail(String email);

  boolean existsByEmail(String email);
}
