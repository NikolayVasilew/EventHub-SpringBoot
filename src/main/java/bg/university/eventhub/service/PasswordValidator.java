package bg.university.eventhub.service;

import java.util.regex.Pattern;

public final class PasswordValidator {

  private static final Pattern PASSWORD_PATTERN =
      Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)" + "(?=.*[@#$%^&]).{8,}$");

  private PasswordValidator() {}

  public static boolean isValid(String password) {
    return password != null && PASSWORD_PATTERN.matcher(password).matches();
  }

  public static void validate(String password) {
    if (!isValid(password)) {
      throw new IllegalArgumentException(
          "Паролата трябва да съдържа минимум "
              + "8 символа, главна буква, малка буква, "
              + "цифра и специален символ (@#$%^&).");
    }
  }
}
