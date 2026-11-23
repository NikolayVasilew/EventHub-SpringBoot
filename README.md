# EventHub — Spring Boot университетски проект

Цялостна уеб система за публикуване на събития и регистрация на участници.

## Технологии
Java 25 · Spring Boot 3.5.7 · Spring MVC · Spring Security · Thymeleaf · Spring Data JPA · H2 · Maven.

## Стартиране
1. Инсталирайте **JDK 25** и **Apache Maven 3.9+**.
2. Отворете папката в VS Code (Extensions: Extension Pack for Java и Spring Boot Extension Pack) или IntelliJ IDEA.
3. За да създадете администратор, задайте две environment променливи **преди първото стартиране**:

   **Windows PowerShell**:
   ```powershell
   $env:EVENTHUB_ADMIN_EMAIL="admin@example.com"
   $env:EVENTHUB_ADMIN_PASSWORD="ChooseYourOwnStrongPassword123!"
   mvn spring-boot:run
   ```
   **Linux/macOS**:
   ```bash
   export EVENTHUB_ADMIN_EMAIL=admin@example.com
   export EVENTHUB_ADMIN_PASSWORD='ChooseYourOwnStrongPassword123!'
   mvn spring-boot:run
   ```
4. Отворете **http://localhost:8080**.
5. Регистрирайте нормален потребител от **/signup**. За администратор влезте с избраните environment credentials.

Ако администраторът вече е създаден, промяната на environment паролата **не сменя** записаната парола. Базата се пази в `data/` и оцелява след рестарт. За чиста демо база спрете приложението и изтрийте папката `data/` (това изтрива всички данни).

## Тестове
```bash
mvn test
```

## Структура
- `model/` — JPA entities: AppUser, Event, Registration
- `repository/` — Spring Data JPA repositories
- `service/` — бизнес правила, транзакции и защита от надвишаване на капацитета
- `controller/` — MVC маршрути, форми, валидация
- `config/` — Spring Security и демо данни
- `templates/` — Thymeleaf страници
- `static/css/` — адаптивен интерфейс

## Security
- BCrypt hash на паролите; никога plaintext в базата.
- Роли USER и ADMIN; `/admin/**` изисква ADMIN.
- Spring Security login с HTTP сесия; POST logout.
- CSRF защита е включена по подразбиране и Thymeleaf добавя CSRF hidden field към POST формите.
- Валидация на форми чрез Jakarta Bean Validation.
- Потребителят се определя от автентикираната сесия, не от параметър в URL.
- Песимистично заключване на Event при регистрация срещу race condition.
- Админ профил се създава само ако са подадени environment credentials.

## Основни URL
| URL | Действие |
|---|---|
| `/` | Каталог и търсене |
| `/signup` | Регистрация |
| `/login` | Вход |
| `/events/{id}` | Детайли |
| `/events/{id}/join` | POST записване |
| `/events/{id}/leave` | POST отказ |
| `/my-events` | Моите събития |
| `/admin` | Админ панел |
| `/admin/events/new` | Ново събитие |

## Модел на данните
`AppUser (1) ── (N) Registration (N) ── (1) Event`

## Ограничения и развитие
Това е локален демонстрационен проект, **не production deployment**. За продукционна употреба: PostgreSQL, Flyway миграции, HTTPS, rate limiting, имейл потвърждение, reset password, централизирано логване и допълнителни интеграционни тестове. H2 не е предназначена за мащабируем production режим. Събитията се показват в локалната часова зона на сървъра.
"# EventHub-SpringBoot" 
