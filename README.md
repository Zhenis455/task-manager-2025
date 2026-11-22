# Taskflow — Task Manager (2025 project, completed edition)

A Java 21 / Spring Boot 3.5.6 / Thymeleaf / Bootstrap 5 task manager with file-backed H2, a Kanban board, dashboards, search, task CRUD, due dates, CSV export, and a JSON REST API.

**Windows:** double-click `START_WINDOWS.bat`, then open `http://localhost:8081/tasks`. Java 21 is required; on the first run Gradle and dependencies may download. Run `TEST_WINDOWS.bat` to execute automated tests. Source lives under `src/`.

**Other platforms:** run `START_MAC_LINUX.sh` or use an installed Gradle with `gradle bootRun`. API is under `/api/tasks` and Kanban under `/board`.

Read **README_RU.md** for full instructions and GitHub commands.

**Local demo only:** no authentication; only listens on `127.0.0.1`. Database in `.data/` (excluded from Git). Reconstructed from the original project's requirements; not a copy of the older team source.
