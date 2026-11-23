package bg.university.eventhub.repository;

import bg.university.eventhub.model.Event;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {
  List<Event> findAllByOrderByStartsAtAsc();

  List<Event> findByTitleContainingIgnoreCaseOrderByStartsAtAsc(String query);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select e from Event e where e.id=:id")
  Optional<Event> lockById(@Param("id") Long id);
}
