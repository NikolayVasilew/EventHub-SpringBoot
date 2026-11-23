package bg.university.eventhub.dto;

import jakarta.validation.constraints.*;

public class SignupForm {
  @NotBlank
  @Size(min = 2, max = 80)
  private String fullName;

  @NotBlank @Email private String email;

  @NotBlank
  @Size(min = 8, max = 72)
  private String password;

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String v) {
    fullName = v;
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
}
