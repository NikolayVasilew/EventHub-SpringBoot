package bg.university.eventhub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 120)
  @Column(nullable = false)
  private String title;

  @NotBlank
  @Size(max = 2000)
  @Column(nullable = false, length = 2000)
  private String description;

  @NotBlank
  @Size(max = 120)
  @Column(nullable = false)
  private String location;

  @NotNull
  @Future
  @Column(nullable = false)
  private LocalDateTime startsAt;

  @Min(1)
  @Max(100000)
  @Column(nullable = false)
  private int capacity = 30;

  public Long getId() {
    return id;
  }

  public void setId(Long v) {
    id = v;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String v) {
    title = v;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String v) {
    description = v;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String v) {
    location = v;
  }

  public LocalDateTime getStartsAt() {
    return startsAt;
  }

  public void setStartsAt(LocalDateTime v) {
    startsAt = v;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int v) {
    capacity = v;
  }
}
