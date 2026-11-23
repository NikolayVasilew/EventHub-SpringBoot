package bg.university.eventhub.repository;

import bg.university.eventhub.model.Registration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

  long countByEventId(Long eventId);

  boolean existsByUserIdAndEventId(Long userId, Long eventId);

  Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);

  // Зарежда регистрациите заедно със събитията.
  // Предотвратява LazyInitializationException.
  @Query(
      """
            SELECT r
            FROM Registration r
            JOIN FETCH r.event
            WHERE r.user.id = :userId
            ORDER BY r.event.startsAt ASC
            """)
  List<Registration> findByUserIdOrderByEventStartsAtAsc(@Param("userId") Long userId);

  @Transactional
  void deleteByEventId(Long eventId);
}
