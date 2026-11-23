package bg.university.eventhub.service;

import bg.university.eventhub.model.*;
import bg.university.eventhub.repository.*;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {
  private final EventRepository events;
  private final RegistrationRepository registrations;
  private final UserRepository users;

  public EventService(
      EventRepository events, RegistrationRepository registrations, UserRepository users) {
    this.events = events;
    this.registrations = registrations;
    this.users = users;
  }

  @Transactional
  public void join(Long eventId, String email) {
    Event event =
        events
            .lockById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Събитието не е намерено."));
    AppUser user = users.findByEmail(email).orElseThrow();
    if (!event.getStartsAt().isAfter(LocalDateTime.now()))
      throw new IllegalStateException("Събитието вече е започнало.");
    if (registrations.existsByUserIdAndEventId(user.getId(), eventId))
      throw new IllegalStateException("Вече сте записани.");
    if (registrations.countByEventId(eventId) >= event.getCapacity())
      throw new IllegalStateException("Няма свободни места.");
    registrations.save(new Registration(user, event));
  }

  @Transactional
  public void leave(Long eventId, String email) {
    AppUser user = users.findByEmail(email).orElseThrow();
    registrations.findByUserIdAndEventId(user.getId(), eventId).ifPresent(registrations::delete);
  }

  @Transactional
  public void deleteEvent(Long eventId) {
    Event e = events.lockById(eventId).orElseThrow();
    registrations.deleteByEventId(eventId);
    registrations.flush();
    events.delete(e);
  }

  @Transactional
  public void updateEvent(Long id, Event form) {
    Event e = events.lockById(id).orElseThrow();
    if (form.getCapacity() < registrations.countByEventId(id))
      throw new IllegalStateException("Капацитетът е под броя на записаните.");
    e.setTitle(form.getTitle());
    e.setDescription(form.getDescription());
    e.setLocation(form.getLocation());
    e.setStartsAt(form.getStartsAt());
    e.setCapacity(form.getCapacity());
  }
}
