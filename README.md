# Capital Commute

**An Advance Taxi Booking and Management System**

## 1. Project Overview

Capital Commute is a taxi booking and management system designed to support advanced taxi reservations and the management of clients, drivers, vehicles, bookings, rides and payments.

The system is intended to allow clients to arrange taxi journeys ahead of time, while supporting driver and vehicle allocation and administrative oversight.

Unlike on-demand ride-hailing platforms, Capital Commute is centred on scheduled taxi bookings rather than continuously matching passengers with nearby drivers.

## 2. Core Objectives

- Support client registration and authentication.
- Manage client and driver accounts.
- Support advance taxi bookings.
- Maintain vehicle and driver information.
- Manage booking and ride-related records.
- Support payment-related operations.
- Provide administrative functionality for system management.

## 3. Technology Stack

| Component | Technology |
|---|---|
| Programming language | Java |
| User interface | JavaFX |
| Backend framework | Spring Boot |
| Persistence | Spring Data JPA |
| Data access | Repository pattern |
| Security | Spring Security configuration |
| Build and dependency management | To be verified |

## 4. System Architecture

The recovered implementation follows a layered architecture.

```text
             JavaFX User Interface
                       |
                       v
                  Controllers
                       |
                       v
                    Services
                       |
                       v
                  Repositories
                       |
                       v
                  JPA / Database
```

Additional components include:

- **DTOs:** Objects used to transfer data between application layers.
- **Configuration:** Application and security configuration.
- **Client:** API communication functionality.
- **Utilities:** Supporting functionality such as CAPTCHA generation and session-related operations.

## 5. Main Domain Entities

The recovered source includes the following principal domain models:

- Person
- Client
- Driver
- Admin
- AdminProfile
- Vehicle
- Booking
- Ride
- Payment

These entities represent the main participants and operational records within the taxi booking system.

## 6. Authentication

The recovered implementation includes authentication and registration endpoints under:

`/api/auth`

Available endpoints include:

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/login` | Authenticate a user |
| POST | `/api/auth/register` | Register a client or driver |
| GET | `/api/auth/captcha/generate` | Generate a CAPTCHA |
| POST | `/api/auth/captcha/verify` | Verify CAPTCHA input |

The authentication service uses a password encoder to verify passwords and stores encoded passwords during registration.

The login response includes the user's role, user ID, national ID and a status message.

## 7. User Roles

The recovered authentication logic recognises:

- Client
- Driver
- Admin

The application contains separate user-facing functionality for different roles. The exact dashboard navigation and permissions will be documented after tracing the relevant UI and controller flows.

## 8. Project Structure

The recovered source is organised into the following principal packages:

```text
CapitalCommute/
└── commute/
    ├── Controller/
    ├── DTO/
    ├── Service/
    ├── model/
    ├── repository/
    ├── UI/
    ├── client/
    ├── config/
    ├── util/
    ├── CapitalCommuteApp.java
    └── CommuteApplication.java

```

## 9. Testing and Verification

The recovered backend/API was tested using **Postman** to exercise its available endpoints and inspect request and response behaviour.

The API testing process formed part of the development and verification of the recovered implementation.

Testing evidence should be interpreted within its actual scope: Postman API testing does not, by itself, establish that every user workflow, database interaction, security control or end-to-end application scenario has been validated.

Further testing and verification will cover:

- Authentication and registration.
- Role-specific access and permissions.
- Booking creation and management.
- Driver and vehicle allocation.
- Ride and payment workflows.
- Input validation and error handling.
- Security and privacy controls.

## 10. Current Project Status

This repository documents a recovered implementation undergoing reverse engineering and technical rehabilitation.

The source has been exercised through Postman API testing and is being examined to establish:

- Existing functionality.
- Application architecture and component responsibilities.
- Data relationships and persistence behaviour.
- Authentication and authorisation flows.
- Implemented booking, ride and payment workflows.
- Existing limitations and incomplete functionality.

Features should not be assumed to be complete merely because corresponding classes or packages exist.

## 11. Future Version: Privacy, Data Protection and Security

The next version of Capital Commute must incorporate **privacy by design and by default** throughout its architecture, implementation and operational processes.

The system will need to consider:

- **Kenya Data Protection Act, 2019 (KDPA):** Applicable data protection principles, obligations and safeguards.
- **Data Protection Impact Assessment (DPIA):** Assess processing activities that are likely to result in high risks to individuals before implementing those activities.
- **Lawful processing:** Establish and document appropriate lawful bases and purposes for collecting and processing personal data.
- **Data minimisation:** Collect only the information necessary for defined system functions.
- **Transparency:** Explain how personal information is collected, used, stored and shared.
- **Data subject rights:** Provide appropriate mechanisms for handling requests from individuals.
- **Retention and deletion:** Define retention periods and secure deletion procedures.
- **Security safeguards:** Apply appropriate technical and organisational measures to protect personal information.
- **Third-party processing:** Assess payment providers, hosting services, messaging providers and other data processors.
- **Access control:** Restrict access to personal information according to roles and responsibilities.

Particular attention will be required for personal information such as national identification details, contact information, location and journey records, authentication credentials and payment-related information.

The DPIA will be informed by the actual data flows, processing purposes, categories of personal data, potential risks and proposed safeguards. It should not be treated as a substitute for implementing the safeguards it identifies.

**Important:** These are requirements and design considerations for the future version. Their inclusion here does not claim that the recovered implementation already satisfies KDPA 2019 or has undergone a DPIA.

## 12. Future Technical Documentation

The following documentation will be developed separately:

- Domain model and entity relationships.
- Existing system architecture.
- System design gap analysis
- Security assessment.
- Data flow and privacy assessment.
- DPIA.
- KDPA compliance mapping.
- Target system architecture.


