# Troubleshooting

## Concept
Run/setup issues fast ga diagnose cheyyadaniki quick fixes.

## Visual
```text
Symptom -> Check -> Fix
```

## Code Snippet
```properties
spring.datasource.password=7!X0rPX0H^hQnM#h8%K%
```
Annotation: wrong password unte startup fail with datasource auth error.

## Common Mistakes
1. MySQL service not running.
2. wrong Java version (not Java 21).
3. frontend proxy misconfig (`/api`).
4. CORS config missing for direct backend calls.
5. Massive startup seeding takes time; wait for completion logs.

## Interview Talking Points
- "I use integration tests + SQL monitor to validate mapping issues."
- "I verify DB constraints with dedicated conflict tests."

## Related Files
- `backend/src/main/resources/application.properties`
- `backend/src/main/java/com/relatiolab/bootstrap/MassiveDataSeeder.java`
- `backend/src/main/java/com/relatiolab/config/WebConfig.java`
- `frontend/vite.config.ts`
