package bg.university.eventhub.controller;

import bg.university.eventhub.model.Event;
import bg.university.eventhub.repository.*;
import bg.university.eventhub.service.EventService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
  private final EventRepository events;
  private final RegistrationRepository registrations;
  private final EventService service;

  public AdminController(
      EventRepository events, RegistrationRepository registrations, EventService service) {
    this.events = events;
    this.registrations = registrations;
    this.service = service;
  }

  @GetMapping
  String dashboard(Model m) {
    m.addAttribute("events", events.findAllByOrderByStartsAtAsc());
    m.addAttribute(
        "registrationCounts",
        events.findAll().stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    Event::getId, e -> registrations.countByEventId(e.getId()))));
    return "admin/dashboard";
  }

  @GetMapping("/events/new")
  String create(Model m) {
    m.addAttribute("event", new Event());
    m.addAttribute("editing", false);
    return "admin/form";
  }

  @GetMapping("/events/{id}/edit")
  String edit(@PathVariable Long id, Model m) {
    m.addAttribute("event", events.findById(id).orElseThrow());
    m.addAttribute("editing", true);
    return "admin/form";
  }

  @PostMapping("/events/save")
  String save(
      @Valid @ModelAttribute Event event, BindingResult errors, Model m, RedirectAttributes flash) {
    boolean editing = event.getId() != null;
    if (errors.hasErrors()) {
      m.addAttribute("editing", editing);
      return "admin/form";
    }
    try {
      if (editing) service.updateEvent(event.getId(), event);
      else events.save(event);
    } catch (IllegalStateException ex) {
      errors.reject("capacity", ex.getMessage());
      m.addAttribute("editing", editing);
      return "admin/form";
    }
    flash.addFlashAttribute("success", "Събитието е запазено.");
    return "redirect:/admin";
  }

  @PostMapping("/events/{id}/delete")
  String delete(@PathVariable Long id, RedirectAttributes flash) {
    service.deleteEvent(id);
    flash.addFlashAttribute("success", "Събитието е изтрито.");
    return "redirect:/admin";
  }
}
