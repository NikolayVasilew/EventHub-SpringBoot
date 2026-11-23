package bg.university.eventhub;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTests {
  @Autowired MockMvc mvc;

  @Test
  void publicHome() throws Exception {
    mvc.perform(get("/")).andExpect(status().isOk());
  }

  @Test
  void adminRedirectsAnonymous() throws Exception {
    mvc.perform(get("/admin")).andExpect(status().is3xxRedirection());
  }

  @Test
  void signupAccessible() throws Exception {
    mvc.perform(get("/signup")).andExpect(status().isOk());
  }
}
