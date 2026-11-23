package bg.university.eventhub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class AppUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, length = 80)
  private String fullName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role = Role.USER;

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String v) {
    email = v;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String v) {
    password = v;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String v) {
    fullName = v;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role v) {
    role = v;
  }
}
