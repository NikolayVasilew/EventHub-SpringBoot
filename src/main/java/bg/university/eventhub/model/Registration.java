package bg.university.eventhub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "registrations",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "event_id"})})
public class Registration {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser user;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  protected Registration() {}

  public Registration(AppUser user, Event event) {
    this.user = user;
    this.event = event;
  }

  public Long getId() {
    return id;
  }

  public AppUser getUser() {
    return user;
  }

  public Event getEvent() {
    return event;
  }
}
