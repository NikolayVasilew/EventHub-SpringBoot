package bg.university.eventhub.config;

import bg.university.eventhub.model.AppUser;
import bg.university.eventhub.model.Event;
import bg.university.eventhub.model.Role;
import bg.university.eventhub.repository.EventRepository;
import bg.university.eventhub.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

  @Bean
  CommandLineRunner seed(UserRepository users, EventRepository events, PasswordEncoder encoder) {

    return args -> {
      String adminEmail = System.getenv("EVENTHUB_ADMIN_EMAIL");

      String adminPassword = System.getenv("EVENTHUB_ADMIN_PASSWORD");

      if (adminEmail != null
          && !adminEmail.isBlank()
          && adminPassword != null
          && !adminPassword.isBlank()) {

        String email = adminEmail.toLowerCase(Locale.ROOT);

        if (!users.existsByEmail(email)) {
          AppUser admin = new AppUser();
          admin.setFullName("Administrator");
          admin.setEmail(email);
          admin.setPassword(encoder.encode(adminPassword));
          admin.setRole(Role.ADMIN);

          users.save(admin);
        }
      }

      // Оригиналните 3 събития
      if (events.count() == 0) {
        add(
            events,
            "Java Spring Boot Workshop",
            "Практическо обучение по Spring Boot и MVC.",
            "София",
            14,
            35);

        add(events, "Tech Meetup", "Среща на разработчици и студенти.", "Пловдив", 21, 80);

        add(
            events,
            "Cybersecurity Basics",
            "Въведение в сигурността на уеб приложенията.",
            "Онлайн",
            28,
            50);
      }

      // 10 нови събития
      addIfMissing(
          events,
          "EventHub Demo - Само 1 място",
          "Демонстрационно събитие с капацитет 1 участник.",
          "София",
          7,
          1);

      addIfMissing(
          events,
          "Git и GitHub Практикум",
          "Работа с Git, branches, commits и GitHub.",
          "София",
          9,
          2);

      addIfMissing(
          events,
          "Spring Security Workshop",
          "Практическо обучение по автентикация и роли.",
          "Онлайн",
          11,
          3);

      addIfMissing(
          events,
          "Docker за начинаещи",
          "Създаване и стартиране на Docker контейнери.",
          "София",
          13,
          5);

      addIfMissing(
          events,
          "AI и машинно обучение",
          "Въведение в изкуствения интелект и ML.",
          "Пловдив",
          16,
          8);

      addIfMissing(
          events,
          "Web Development Bootcamp",
          "HTML, CSS, JavaScript и уеб приложения.",
          "Варна",
          18,
          12);

      addIfMissing(
          events, "MongoDB Практикум", "Работа с MongoDB, заявки и агрегации.", "Онлайн", 20, 15);

      addIfMissing(
          events,
          "Java Programming Challenge",
          "Състезание по програмиране с Java.",
          "София",
          23,
          20);

      addIfMissing(
          events,
          "Cybersecurity Conference",
          "Лекции за киберсигурност и защита на данните.",
          "Бургас",
          26,
          40);

      addIfMissing(
          events,
          "Technology Innovation Forum",
          "Форум за нови технологии и иновации.",
          "София",
          30,
          100);
    };
  }

  private static void addIfMissing(
      EventRepository repository,
      String title,
      String description,
      String location,
      int daysFromNow,
      int capacity) {

    // Използваме съществуващия метод за търсене.
    if (repository.findByTitleContainingIgnoreCaseOrderByStartsAtAsc(title).stream()
        .noneMatch(e -> e.getTitle().equalsIgnoreCase(title))) {

      add(repository, title, description, location, daysFromNow, capacity);
    }
  }

  private static void add(
      EventRepository repository,
      String title,
      String description,
      String location,
      int daysFromNow,
      int capacity) {

    Event event = new Event();

    event.setTitle(title);
    event.setDescription(description);
    event.setLocation(location);
    event.setStartsAt(LocalDateTime.now().plusDays(daysFromNow));
    event.setCapacity(capacity);

    repository.save(event);
  }
}
