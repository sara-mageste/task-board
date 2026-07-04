# Technical Notes

Brief notes on the main decisions, trade-offs, and possible future improvements for this project.

## Architecture

- **Backend**: standard layered architecture (`controller` → `service` → `repository`), with DTOs for the `create` flow (`TaskRequestDTO` / `TaskResponseDTO`) and centralized error handling via `@RestControllerAdvice`.


- **Frontend**: Angular 21 standalone components (no `NgModule`), organized by feature (`pages/task/task-list`, `create-task`, `modal-task`), using **signals** for state instead of plain class properties.
    - This choice turned out to matter more than expected: Angular 21 defaults to **zoneless change detection**, so plain property reassignment (`this.tasks = data`) doesn't reliably trigger a re-render after an async response. Signals (`signal()` / `.set()` / `.update()`) notify the framework directly, independent of zone.js — this is now the recommended pattern for any state coming from HTTP or other async sources.

## Key decisions & trade-offs

- **Frontend model uses string union types instead of TypeScript enums** (`priority: 'LOW' | 'MEDIUM' | 'HIGH'`) — simpler, and maps directly to the backend's `@Enumerated(EnumType.STRING)` serialization without an extra translation layer.


- **Validation exists on both layers, but asymmetrically**: the `POST /tasks` endpoint uses a DTO with Bean Validation (`@NotBlank`, `@Size`, `@NotNull`), enforced via `@Valid`. The `PUT /tasks/{id}` endpoint still receives the raw `Task` entity, with no server-side validation — only the frontend form validates on update. This was a conscious trade-off: introducing a `TaskUpdateDTO` at this stage would touch a working, tested contract with limited time left. Documented here instead of risking a late change.


- **Database credentials are hardcoded** in `application.properties` (`root`/`root`). Acceptable for a local technical assessment; in a real deployment these would be externalized via environment variables or a secrets manager, and never committed to version control.


- **Search (frontend)** uses `debounceTime` + `switchMap` (RxJS) instead of firing a request per keystroke — avoids both unnecessary calls and race conditions where an older response could overwrite a newer one.


- **Delete confirmation** is implemented as an inline overlay inside `ModalTask`, rather than as a separate reusable component. Given the scope (only one place uses it), a dedicated component would have added indirection without real benefit yet.


- **Column scroll**: each Kanban column shows up to 5 cards before scrolling, via a `max-height` on `.column-body` combined with `overflow-y: auto`. The height is an approximation of card size rather than a measured/dynamic value — simple and effective for this dataset size, but would need revisiting if card content becomes more variable in height.


- **Logging** uses SLF4J (`@Slf4j`) with deliberate levels: `INFO` for business operations that change state (create/update/delete/status change), `DEBUG` for read/query operations, `WARN` for expected-but-abnormal situations (validation failures, resource not found), and `ERROR` with full stack trace for anything unhandled. A generic `Exception` handler was added specifically so no error goes unlogged — before this, any exception outside `ResourceNotFoundException` fell through to Spring's default (unlogged) error response.

## Testing scope

Automated testing was concentrated on the **backend** (`TaskServiceTest` — unit, Mockito; `TaskControllerTest` — integration, MockMvc), since it's where the core business rules live and where regressions are costliest. Frontend component tests (e.g. form validation in `CreateTask`, board filtering in `TaskList`) were not implemented due to time constraints. This is a deliberate prioritization, not an oversight.

## Possible future improvements

- Add `TaskUpdateDTO` with validation for `PUT /tasks/{id}`, matching the `create` flow.
- Externalize database credentials via environment variables / `.env` file.
- Extract shared CSS (inputs, custom-select, error states) currently duplicated between `create-task.css` and `modal-task.css` into a shared stylesheet or design tokens.
- Add frontend component tests (Vitest, Angular 21's default test runner).
- Replace the fixed `max-height` estimate on columns with a value based on measured card height, or a virtualized list if task volume grows significantly.
- Consider drag-and-drop between columns as an alternative/complement to the arrow-based status navigation.
- Add a `docker-compose` service for the backend itself (currently only MySQL is containerized), for a true one-command startup.
- Add a CI pipeline (e.g. GitHub Actions) running `mvn test` and `npm test` on every push/PR.

## Author
Sara Mageste.