# RelatioLab

Full-stack learning lab for JPA/Hibernate relationships with MySQL, Spring Boot, and React.

## Stack
- Backend: Spring Boot 3.2.12, Java 21, Spring Data JPA, Hibernate
- DB: MySQL 8 (`relatiolab` schema)
- Frontend: React 18, Vite, TypeScript

## Project Layout
- `backend/` Spring APIs, relationships, debug endpoints
- `frontend/` UI pages + SQL monitor + N+1 comparison
- `notes/` interview-ready concept docs
- `sample-data.sql` seed data
- `test-report.md` executed test evidence

## MySQL Config
`backend/src/main/resources/application.properties` already configured with:
- URL: `jdbc:mysql://localhost:3306/relatiolab...`
- Username: `root`
- Password: `7!X0rPX0H^hQnM#h8%K%`

## Run Backend
```powershell
cd backend
mvnd spring-boot:run
```

## Run Frontend
```powershell
cd frontend
npm install
npm run dev
```

## Key Endpoints
- `POST /api/v1/students`
- `POST /api/v1/students/{id}/profile`
- `POST /api/v1/enrollments`
- `POST /api/v1/courses/{courseId}/mentors/{mentorId}`
- `POST /api/v1/mentors/{mentorId}/skills/{skillId}`
- `GET /api/v1/debug/sql/recent`
- `GET /api/v1/debug/fetch-comparison?mode=join-fetch|entity-graph|batch`
- `GET /api/v1/debug/nplus1/students-with-enrollments?mode=bad|join-fetch|entity-graph|batch`

## Validation Commands
```powershell
cd backend
mvnd test

cd ../frontend
npm run test
npm run lint
npm run build
```

## Quick Manual Scenarios
1. Create student and profile from Students page.
2. Create mentor/skill and link from Mentors page.
3. Create course and assign mentor from Courses page.
4. Run N+1 modes from N+1 Lab and compare query count.
5. Open SQL Monitor and inspect generated SQL live.