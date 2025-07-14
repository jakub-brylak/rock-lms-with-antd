# Rock LMS - Learning Management System

Rock Learning Management System is a full-stack demo project showcasing modern web development practices with React frontend and Spring Boot backend, focused on rock music education.

## 🎸 Business Overview

Rock LMS is designed for music educators and students who want to create, manage, and deliver rock music courses. The platform enables:

- **Course Creation**: Instructors can create comprehensive rock music courses
- **Content Management**: Organize course materials with titles, descriptions, and duration
- **Publishing Workflow**: Draft → Published → Archived lifecycle management

### Course Lifecycle

Courses in Rock LMS follow a structured three-state lifecycle:

1. **DRAFT** - Initial state when course is created
   - Editable content (title, description, duration)
   - Can be published or archived

2. **PUBLISHED** - Course available to students
   - Still editable for updates
   - Visible in course catalog
   - Tracks publication timestamp
   - Can be archived

3. **ARCHIVED** - Retired course
   - Read-only state (cannot be edited)
   - Preserves historical data
   - Cannot be published again

### Business Rules

- **Publication Requirements**: Courses must have a valid title and duration ≥ 1 minute
- **Edit Restrictions**: Archived courses cannot be modified
- **Status Filtering**: Users can filter courses by status for better organization
- **Data Integrity**: Publication timestamps are preserved during archiving

## 🏗️ Architecture

### Frontend (React + Ant Design)
- **Framework**: React 18 with TypeScript
- **UI Library**: Ant Design for consistent component library
- **Routing**: React Router for SPA navigation
- **API Integration**: TypeScript-generated client from OpenAPI spec
- **Build Tool**: Vite for fast development and building

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.5.3 with Java 24
- **Database**: PostgreSQL with Flyway migrations
- **API**: OpenAPI 3.0 specification with code generation
- **Architecture**: Layered architecture (Controller → Service → Repository)
- **Testing**: JUnit 5 with Mockito and integration tests

### Database
- **Primary**: PostgreSQL 17
- **Test**: H2 in-memory database
- **Migrations**: Flyway for version control
- **Seeding**: Development data for demonstration

## 🚀 Quick Start

### Prerequisites
- Java 24+
- Node.js 22+
- Docker

### 1. Database Setup

Start PostgreSQL database:
```bash
docker-compose up -d
```

This creates:
- PostgreSQL database `rocklms`
- User: `rocklms-user`
- Password: `rocklms-pass`
- Port: `5432`

### 2. Backend Setup

Navigate to backend directory:
```bash
cd backend
```

Build and run Spring Boot application:
```bash
mvn clean install
mvn spring-boot:run
```

The backend will:
- Generate API classes from OpenAPI spec
- Run Flyway migrations
- Seed development data
- Start on `http://localhost:8080`

**API Documentation**: http://localhost:8080/swagger-ui.html

### 3. Frontend Setup

Navigate to frontend directory:
```bash
cd frontend
```

Install dependencies and start development server:
```bash
npm install
npm run dev
```

The frontend will:
- Generate TypeScript API client from OpenAPI spec
- Start development server on `http://localhost:5173`
- Enable hot reloading for development

### 4. Verify Setup

1. **Database**: Check PostgreSQL is running on port 5432
2. **Backend**: Visit http://localhost:8080/api/courses
3. **Frontend**: Visit http://localhost:5173
4. **Integration**: Create, publish, and archive courses through the UI

## 🔧 Development

### API Code Generation

Backend (automatic on build):
```bash
mvn clean compile
```

Frontend (manual):
```bash
npm run generate:api
```

Integration tests with in memorydatabase interaction and API endpoint testing.
