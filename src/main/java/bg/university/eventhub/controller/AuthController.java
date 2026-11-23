package bg.university.eventhub.controller;

import bg.university.eventhub.dto.SignupForm;
import bg.university.eventhub.model.AppUser;
import bg.university.eventhub.model.Role;
import bg.university.eventhub.repository.UserRepository;
import bg.university.eventhub.service.PasswordValidator;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

  private final UserRepository users;
  private final PasswordEncoder encoder;

  public AuthController(UserRepository users, PasswordEncoder encoder) {

    this.users = users;
    this.encoder = encoder;
  }

  // Страница за вход
  @GetMapping("/login")
  public String login() {
    return "login";
  }

  // Страница за регистрация
  @GetMapping("/signup")
  public String signup(Model model) {

    model.addAttribute("form", new SignupForm());

    return "signup";
  }

  // Обработка на регистрацията
  @PostMapping("/signup")
  public String signupPost(@Valid @ModelAttribute("form") SignupForm form, BindingResult errors) {

    // Нормализиране на имейла
    String email = form.getEmail() == null ? "" : form.getEmail().trim().toLowerCase(Locale.ROOT);

    // Проверка дали имейлът вече съществува
    if (users.existsByEmail(email)) {

      errors.rejectValue("email", "duplicate", "Имейлът вече се използва.");
    }

    // Проверка за сложност на паролата
    if (!PasswordValidator.isValid(form.getPassword())) {

      errors.rejectValue(
          "password",
          "invalidPassword",
          "Паролата трябва да съдържа минимум 8 символа, "
              + "поне една главна буква, една малка буква, "
              + "една цифра и специален символ (@#$%^&).");
    }

    // Ако има грешки, връщаме регистрационната форма
    if (errors.hasErrors()) {
      return "signup";
    }

    // Създаване на нов потребител
    AppUser user = new AppUser();

    user.setEmail(email);
    user.setFullName(form.getFullName().trim());

    // Криптиране на валидната парола
    user.setPassword(encoder.encode(form.getPassword()));

    // Задаване на потребителска роля
    user.setRole(Role.USER);

    // Записване в базата данни
    users.save(user);

    // Пренасочване към страницата за вход
    return "redirect:/login?registered";
  }
}
