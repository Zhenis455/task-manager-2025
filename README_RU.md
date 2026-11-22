# TASK MANAGER — COMPLETE EDITION (2025 / восстановлен в 2026)

> Java 21 · Spring Boot 3.5.6 · Thymeleaf · Bootstrap 5 · H2 (file) · REST API · Gradle

Это восстановленная и расширенная **Java-версия** исторического Task Manager: исходники совместного проекта 2025 года здесь не использованы. В ранней командной версии backend был отдельно на Go/GORM/PostgreSQL, а frontend — HTML + Bootstrap + Axios; данный ZIP сохраняет запрошенный стек **Java + Spring Boot + Bootstrap** и открывает REST API для отдельного клиента.

## Быстрый запуск в Windows 10 / 11

1. Распакуй всю ZIP-папку в `C:\Users\Acer\Downloads\Task_Manager_2025_COMPLETE`.
2. Убедись, что JDK 21 установлен (после Student Exam Results он уже установлен).
3. Двойной клик по `START_WINDOWS.bat` **из папки проекта**, или открой CMD и введи команды **по одной**:

   ```cmd
   cd /d C:\Users\Acer\Downloads\Task_Manager_2025_COMPLETE
   START_WINDOWS.bat
   ```

4. После строки `Started TaskManagerApplication` открой:

   - **Dashboard:** http://localhost:8081/tasks
   - **Kanban:** http://localhost:8081/board
   - **JSON API:** http://localhost:8081/api/tasks
   - **CSV:** http://localhost:8081/tasks/export.csv

Окно CMD оставь открытым. Для остановки: **Ctrl+C**. На первой загрузке нужен интернет для библиотек Spring Boot/Gradle. Скрипт по возможности повторно использует Gradle, установленный рядом с первым проектом Student Exam Results.

**Исправления запуска Windows:** исправлена попытка присвоить значение встроенной переменной PowerShell `$HOME` (она только для чтения); теперь используется `$jdkHomeDir`.

**Исправление ошибки старого ZIP:** старый `gradle-bootstrap.ps1` был UTF-8 без BOM и содержал русские строки; Windows PowerShell 5.1 прочитал его как ANSI и выдавал `Unexpected token ...` и `Missing closing }`. Новый файл полностью в ASCII, поэтому этой ошибки нет. Никаких изменений в настройках PowerShell не требуется.

## Функции

- Создание, просмотр, изменение и удаление задач (CRUD)
- Статусы: To do, In progress, Completed
- Приоритеты: Low, Medium, High
- Категории, описания, дедлайны, пометка просроченных задач
- Dashboard со статистикой + поиск/фильтры
- Kanban Board с переходами между статусами
- Экспорт задач CSV (Excel-compatible UTF-8)
- REST API для внешнего frontend
- Автоматические тесты и постоянное хранение задач в H2

## Данные и безопасность

База данных сохраняется локально: `.data/taskmanager.mv.db`. Она **не попадает на GitHub** благодаря `.gitignore`. Не удаляй папку `.data`, если нужно сохранить задачи. На первом запуске создаются 5 демонстрационных задач; они добавляются только однажды. Сервис слушает `127.0.0.1:8081` и доступен только на этом компьютере. **Авторизации нет — для публичного размещения потребуется добавить Spring Security и защищённые аккаунты**.

## Проверка

```cmd
TEST_WINDOWS.bat
```

Чтобы проверить HTTP-страницы и API, **не закрывая окно запущенного сервера**, дважды щёлкни `API_CHECK_WINDOWS.bat` в другом окне. Должно появиться `All HTTP smoke checks passed.`.

Затем убедись, что Gradle пишет `BUILD SUCCESSFUL`. Тесты проверяют CRUD, валидацию, статусы, статистику, API и страницы Kanban/CSV.

Для сборки исполняемого JAR: `BUILD_JAR_WINDOWS.bat`, результат — в `build\libs`.

## REST API

| Метод | URL | Описание |
|---|---|---|
| GET | `/api/tasks` | Список; фильтры `q`, `status`, `priority` |
| POST | `/api/tasks` | Создать задачу |
| GET | `/api/tasks/{id}` | Задача по ID |
| PUT | `/api/tasks/{id}` | Изменить задачу |
| PATCH | `/api/tasks/{id}/status` | Изменить статус |
| DELETE | `/api/tasks/{id}` | Удалить задачу |
| GET | `/api/tasks/stats` | Статистика |

Пример `POST /api/tasks` (JSON):

```json
{"title":"Prepare Java assignment","description":"Finish the UI and connect API","category":"Study","priority":"HIGH","status":"TODO","dueDate":"2026-12-01"}
```

Для `PATCH /api/tasks/1/status`:

```json
{"status":"DONE"}
```

Допускается `due_date` вместо `dueDate` при отправке API-запроса. Поля кроме `title` необязательные. Ответ включает ID и даты. Отдельные frontend на `localhost` (например VS Code Live Server) могут обращаться к API через CORS, но сервер привязан к localhost, для работы из другого устройства потребуется конфигурация сети и авторизации.

## Структура проекта

- `src/main/java/com/example/taskmanager/model` — Task, статусы и приоритеты
- `repository` — JPA, база H2
- `service` — бизнес-логика, статистика, сохранение
- `web` — страницы Thymeleaf и CSV
- `api` — JSON REST API, CORS для localhost
- `src/main/resources/templates` — HTML страницы
- `src/main/resources/static` — CSS, JS, иконка
- `src/test` — тесты
- `scripts/gradle-bootstrap.ps1` — запуск для Windows PowerShell 5.1

## GitHub: Zhenis455

Создай пустой репозиторий **task-manager-2025** в https://github.com/new без README и .gitignore через GitHub. Открой CMD и вводи **каждую команду отдельно**:

```cmd
cd /d C:\Users\Acer\Downloads\Task_Manager_2025_COMPLETE
git init
git add .
git commit -m "Task Manager complete - Spring Boot 2025"
git branch -M main
git remote add origin https://github.com/Zhenis455/task-manager-2025.git
git push -u origin main
```

Если `origin already exists`, используй `git remote set-url origin https://github.com/Zhenis455/task-manager-2025.git`, затем `git push -u origin main`.

## Открыть исходный код

В CMD из папки проекта: `code .` или **VS Code → File → Open Folder**.

## Важные сведения

Это самодостаточный локальный учебный веб-проект: нужен JDK 21, а Gradle и зависимости автоматически загружаются на первом запуске. Он рассчитан на портфолио, не на публичный SaaS без авторизации и резервных копий.

## Соответствие первоначальному заданию 2025 года

Старая модель `Task(id, name, deadlineDate, isCompleted)` расширена: `name` → `title`, `deadlineDate` → `dueDate`, а `isCompleted` → статус `DONE`/`TODO`. Добавлены описание, приоритет, категории, статистика, REST API и Kanban. Точный Go-код командного backend из 2025 года в архив не включён; это отдельная самостоятельная Java-реализация по восстановленному ТЗ.
