package bg.university.eventhub.controller;

import bg.university.eventhub.repository.EventRepository;
import bg.university.eventhub.repository.RegistrationRepository;
import bg.university.eventhub.repository.UserRepository;
import bg.university.eventhub.service.EventService;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EventController {

  private final EventRepository events;
  private final RegistrationRepository registrations;
  private final UserRepository users;
  private final EventService service;

  public EventController(
      EventRepository events,
      RegistrationRepository registrations,
      UserRepository users,
      EventService service) {

    this.events = events;
    this.registrations = registrations;
    this.users = users;
    this.service = service;
  }

  // Начална страница - всички събития
  @GetMapping("/")
  public String index(@RequestParam(defaultValue = "") String q, Model model) {

    model.addAttribute(
        "events",
        q.isBlank()
            ? events.findAllByOrderByStartsAtAsc()
            : events.findByTitleContainingIgnoreCaseOrderByStartsAtAsc(q));

    model.addAttribute("q", q);

    return "index";
  }

  // Детайли за конкретно събитие
  @GetMapping("/events/{id}")
  public String details(@PathVariable Long id, Model model, Principal principal) {

    var event =
        events
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Събитието не е намерено."));

    model.addAttribute("event", event);

    model.addAttribute("count", registrations.countByEventId(id));

    boolean joined =
        principal != null
            && users
                .findByEmail(principal.getName())
                .map(user -> registrations.existsByUserIdAndEventId(user.getId(), id))
                .orElse(false);

    model.addAttribute("joined", joined);

    return "details";
  }

  // Записване за събитие
  @PostMapping("/events/{id}/join")
  public String join(@PathVariable Long id, Principal principal, RedirectAttributes flash) {

    if (principal == null) {
      return "redirect:/login";
    }

    try {
      service.join(id, principal.getName());

      flash.addFlashAttribute("success", "Успешно записване!");

    } catch (IllegalStateException | IllegalArgumentException ex) {

      flash.addFlashAttribute("error", ex.getMessage());
    }

    return "redirect:/events/" + id;
  }

  // Отказване от събитие
  @PostMapping("/events/{id}/leave")
  public String leave(@PathVariable Long id, Principal principal, RedirectAttributes flash) {

    if (principal == null) {
      return "redirect:/login";
    }

    service.leave(id, principal.getName());

    flash.addFlashAttribute("success", "Отказахте участието си.");

    return "redirect:/events/" + id;
  }

  // Моите записи - поправен метод
  @GetMapping("/my-events")
  public String myEvents(Principal principal, Model model) {

    if (principal == null) {
      return "redirect:/login";
    }

    var user =
        users
            .findByEmail(principal.getName())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Потребителят не е намерен."));

    model.addAttribute(
        "registrations", registrations.findByUserIdOrderByEventStartsAtAsc(user.getId()));

    return "my-events";
  }
}
