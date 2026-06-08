# Employee Management System (EMS Pro)

EMS Pro is a role-based Employee Management System built with Spring Boot, Thymeleaf, Spring Security, Spring Data JPA, and MySQL. The project centralizes employee administration, recruitment, attendance, leave workflows, payroll processing, department management, HR analytics, and account security into a single web application.

The application is designed around real organizational roles: public candidates can browse and apply for jobs, employees can manage their own work information, department managers can review team activity, HR can operate core employee processes, HR managers can approve and monitor workforce operations, and administrators can manage the organizational structure and system access.

## Key Features

### Public Career Portal

- Company home page, public about page, contact form, and job listing pages.
- Job filtering by department and detailed job information.
- Candidate application flow with CV upload support.
- Candidate application tracking by token.
- Public company information and configurable home content from the database.

### Authentication and Account Security

- Spring Security form login with role-based access control.
- Separate access areas for Admin, HR, HR Manager, Department Manager, and Employee users.
- Password reset flow using email OTP.
- Account activation flow for inactive users using email OTP.
- Admin account actions: lock, unlock, activate, deactivate, and reset password.
- Brute-force login protection with temporary account lock and scheduled auto-unlock.
- Audit logging for important system and workflow actions.

### Employee Self-Service

- Employee dashboard with leave, attendance, payroll, performance, and recent activity summaries.
- Profile viewing and profile update.
- Leave balance overview and leave request submission.
- Attendance history with clock-in, clock-out, and CSV export.
- Payroll history, payslip summary, and payroll CSV export.
- Performance summary and review history.

### Department Manager Workspace

- Department dashboard and team overview.
- Department detail page with team-level insights.
- Leave approval workflow at department level.
- Attendance review by week.
- Performance review creation and update.
- Team member add/remove request creation.

### HR Operations

- HR dashboard with employee and recruitment metrics.
- Employee directory with search, filters, pagination, and employee detail API.
- Daily attendance review with date, department, status, and search filters.
- Leave management with pending requests, HR Manager escalation view, history, statistics, leave balance summaries, bulk approve/reject, calendar API, and CSV export.
- Recruitment management including job posts, candidate applications, application stages, CV viewing/downloading, interviewer assignment, interview scheduling, feedback, and job request approval.
- Performance review monitoring with search and filters.
- Payroll period management, payroll preview, draft payslip generation, payroll review, draft export, and bank export.
- Employee bank detail management with VietQR-supported bank list integration.

### HR Manager Workspace

- Executive HR dashboard with KPI cards, hiring and attrition trends, upcoming events, and recent activity center.
- Request management with approve, reject, bulk approve, bulk reject, revert within an allowed window, and critical-item notification support.
- Payroll approval by department.
- HR analytics for workforce, department, salary, diversity, training, and policy review data.
- Calendar management with event create, update, and delete operations.

### Administration

- Admin dashboard with staff status statistics, department distribution, headcount trends, recent users, and recent logs.
- Department management with hierarchy support, manager assignment, filtering, sorting, pagination, and member lookup.
- User management with keyword, role, status, department filters, sorting, pagination, and create/update support.
- Company information management for public-facing content.
- System log viewing, deletion, and clearing.

### Payroll and Finance Controls

- Timesheet period creation and locking.
- Payroll preview based on active employees in the selected period.
- Prorated salary calculation using payable days.
- Overtime calculation using 1.5x rate.
- Bonus and deduction aggregation.
- Gross-to-net draft payslip generation.
- Payslip line items for payroll transparency and auditability.
- Approved payroll bank export as CSV.
- AES encryption converter for sensitive bank account data.

## Tech Stack

- Backend: Java 17, Spring Boot 4, Spring MVC, Spring Security
- Persistence: Spring Data JPA, Hibernate, MySQL
- View Layer: Thymeleaf, Tailwind CSS, vanilla JavaScript
- Infrastructure: Maven Wrapper, Spring Mail, Spring Cache, Spring Scheduling
- Integrations: VietQR bank API, SMTP email delivery
- Data: SQL schema and seed scripts in `sql/`

## Project Structure

```text
.
+-- pom.xml
+-- mvnw / mvnw.cmd
+-- sql/
|   +-- final_script.sql
+-- src/main/
    +-- java/com/group5/ems/
    |   +-- config/          # Security, startup, migration, crypto, scheduling
    |   +-- controller/      # MVC controllers by role/module
    |   +-- dto/             # Request and response DTOs
    |   +-- entity/          # JPA entities
    |   +-- repository/      # Spring Data repositories
    |   +-- service/         # Business services by domain/role
    |   +-- util/            # Shared utilities
    +-- resources/
        +-- static/          # CSS, JS, icons
        +-- templates/       # Thymeleaf pages and fragments
```

## Main Application Areas

| Area | Base Route | Primary Users |
| --- | --- | --- |
| Public portal | `/home` | Guests, candidates |
| Authentication | `/login`, `/forgot-password`, `/activate` | All users |
| Employee portal | `/employee` | Employees, managers, HR users |
| Department manager portal | `/dept-manager` | Department Managers |
| HR portal | `/hr` | HR staff |
| HR Manager portal | `/hrmanager` | HR Managers |
| Admin portal | `/admin` | Administrators |

## Database Overview

The main schema covers the following domains:

- Account and role-based access control: `users`, `roles`, `user_roles`, OTP verification, system settings.
- Organization structure: departments, positions, employees, account requests.
- HR operations: contracts, attendance, salaries, bonuses, benefits, leave balances.
- Performance: skills, employee skills, KPIs, reviews, rewards, discipline records, tasks.
- Workflow: request types, requests, approval history.
- Recruitment: candidates, CVs, job posts, applications, stages, interviews, offers.
- Communication and audit: email templates, email logs, audit logs.
- Payroll: timesheet periods, payslips, pay components, payslip line items, employee bank details.

Database scripts are located in `sql/`:

1. `sql/final_script.sql` initializes the schema and seed data for the current application version.

Note: the SQL script creates the `erm_system` database, while local Spring datasource configuration may use a different database name. Make sure `spring.datasource.url` points to the schema you actually import.

## Getting Started

### Prerequisites

- JDK 17+
- MySQL 8+
- Maven Wrapper included in the repository

### 1. Clone the repository

```bash
git clone <repository-url>
cd employee-management-system
```

### 2. Prepare the database

Import the SQL script:

```bash
mysql -u root -p < sql/final_script.sql
```

### 3. Configure the application

Copy `src/main/resources/application.properties.example` to `src/main/resources/application.properties`, then update it with your local settings:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/erm_system
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

server.port=8080

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

crypto.secret=base64_encoded_16_byte_key
```

Do not commit real database credentials, SMTP app passwords, or encryption keys.

### 4. Run the application

On Windows:

```bash
mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

Then open:

```text
http://localhost:8080
```

The root route redirects to the public career portal at `/home`.

## Seeded Access

The SQL seed data creates initial roles and sample users. Review the `INSERT INTO users` statements in `sql/final_script.sql` for the current sample accounts and adjust them for your local environment.

## Development Notes

- The project uses server-rendered Thymeleaf templates grouped by role under `src/main/resources/templates/`.
- Shared navigation is implemented through Thymeleaf fragments.
- Some views use Tailwind via CDN and page-level JavaScript.
- Application-level migrations in `DatabaseMigrationRunner` add security-related fields and enforce supported user status values on startup.
- Bank account numbers are encrypted and decrypted through a JPA `AttributeConverter`.
- Payroll generation is intentionally guarded by locked timesheet periods and existing-payslip checks to avoid duplicate runs.

## Repository About Text

Short description:

```text
Role-based Employee Management System built with Spring Boot, Thymeleaf, MySQL, and Spring Security, covering HR operations, recruitment, attendance, leave workflows, payroll, analytics, and admin management.
```

Suggested topics:

```text
spring-boot, thymeleaf, mysql, spring-security, employee-management, hr-management, payroll, recruitment, attendance, leave-management, java
```

Suggested website URL:

```text
http://localhost:8080/home
```

For a public GitHub repository, replace the website URL with a deployed demo link when available.
