# Task Board — Frontend

Angular 21 application for the Task Board — a Kanban-style board with search, task creation, and a details/edit modal.

## Requirements

- Node.js `^20.19.0` or `^22.12.0` or `^24.0.0` (required by Angular 21)
- npm `10+`

## Running the application

1. Install dependencies:
   ```bash
   cd frontend
   npm install
   ```

2. Start the dev server:
   ```bash
   npm start
   ```
   The app will be available at `http://localhost:4200`.

> **The backend must be running first**, at `http://localhost:8080` — the frontend expects the API to be reachable there (see the base URL in `src/app/services/task.service.ts`). See the [backend README](../backend/README.md) for setup instructions.

## Features

- **Board view**: 4 columns (Backlog, On Hold, In Progress, Done), each showing up to 5 tasks before scrolling.
- **Search**: real-time search by task ID (numeric input) or by title (prefix match), with debounce to avoid unnecessary requests.
- **Create task**: modal form with required fields, character limits, and validation (Reactive Forms).
- **Task details / edit**: click a card to view full details; edit mode enables editable fields (id and timestamps remain read-only); includes a delete confirmation step.
- **Move between columns**: arrows on each card move the task to the previous/next status via the API.

## Project structure

```
src/app/
├── models/          Task interface
├── services/        TaskService (HTTP calls)
└── pages/
    ├── task-list/     Main board component
    ├── create-task/   Task creation modal
    └── modal-task/     Task details/edit modal
```

## Running Tests

```bash
npm test
```

Angular 21 uses **Vitest** as the default test runner (replacing Jasmine/Karma from previous versions).

> **Note:** automated frontend tests were not implemented in this project due to time constraints — testing effort was focused on the backend (business logic and API layer), which concentrates the application's core rules. Adding component-level tests (e.g. `TaskList`, `CreateTask` form validation) would be a natural next step. See [technical-decisions.md](../docs/technical-decisions.md) for more details.