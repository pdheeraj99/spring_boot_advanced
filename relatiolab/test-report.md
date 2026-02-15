# Test Report

## Automated Backend
Command:
- `mvnd test`

Result:
- Status: PASS
- Tests: 5 passed
- Coverage highlights:
  - OneToOne orphan removal
  - OneToMany orphan removal
  - Duplicate enrollment conflict (409)
  - Debug SQL endpoint request-id check
  - N+1 comparison endpoint basic metrics response

## Automated Frontend
Commands:
- `npm run test`
- `npm run lint`
- `npm run build`

Result:
- `npm run test`: PASS (1 test)
- `npm run lint`: PASS
- `npm run build`: PASS

## Manual Scenario Checklist
1. Student + profile create flow: READY
2. Same student multiple courses enroll: READY
3. Duplicate enrollment -> 409: READY (automated)
4. Mentor-course and mentor-skill link: READY
5. N+1 bad vs optimized modes: READY via `/api/v1/debug/nplus1/*`
6. SQL monitor shows request-correlated SQL: READY via `/api/v1/debug/sql/recent`
7. Delete profile orphan removal: READY
8. Unlink mentor-course join row only: READY via Course unlink endpoint

## MySQL Runtime Smoke
Command style:
- Start app: `mvnd spring-boot:run`
- Hit APIs using PowerShell `Invoke-RestMethod`

Verified:
- Student create
- Course create
- Mentor + Skill create
- Student profile upsert
- Enrollment create
- Course-mentor link
- Mentor-skill link
- N+1 endpoint (`join-fetch`) response
- SQL trace endpoint response

Observed output:
- `SMOKE_OK student=1 course=1 enrollment=1 nplus1_queries=2 sql_count=20`
