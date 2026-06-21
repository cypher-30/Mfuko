# Mfuko — Backend (Ktor Server)

> Backend project location: `C:\Users\Alvin\IdeaProjects\GroupMoneyManagerServer`

The Mfuko Android app works fully offline (Phase 3+). The Ktor backend is **optional** and
enables real multi-device sync, M-Pesa Daraja integration, and cloud push notifications.

---

## Backend tech stack

| Component | Library |
|---|---|
| Server framework | Ktor 3.2.3 (Netty engine) |
| Database ORM | Jetbrains Exposed 0.49.0 |
| Database | MySQL 8.4 (local) → H2 file (Phase 7 fix) |
| Auth | JWT (auth0 java-jwt 4.4.0) |
| Password hashing | BCrypt (jbcrypt 0.4) |
| Connection pool | HikariCP 5.1.0 |
| Serialisation | Gson (ktor-serialization-gson) |

---

## API endpoints (current)

### Auth (no JWT required)
| Method | Path | Body | Response |
|---|---|---|---|
| POST | `/api/auth/register` | `{name, phone, password}` | `{userId, name, phone, token}` |
| POST | `/api/auth/login` | `{phone, password}` | `{userId, name, phone, token}` |

> Login uses **phone number** (not email). Password minimum 8 characters.

### Nests (JWT required)
| Method | Path | Body | Response |
|---|---|---|---|
| POST | `/api/nests/create` | `{name, contributionAmount}` | `{nestId, nestName, inviteCode}` |
| POST | `/api/nests/join` | `{inviteCode}` | `{nestId, nestName, inviteCode}` |
| GET | `/api/nests/{nestId}/members` | — | `[{userId, name, role, amountPaid, totalDue, status}]` |

### Contributions (JWT required)
| Method | Path | Body | Response |
|---|---|---|---|
| POST | `/api/contributions/record` | `{nestId, userId, amount}` | 200 OK |

> Manager only. Records or updates a member's contribution in the current open cycle.

### Loans (JWT required)
| Method | Path | Body | Response |
|---|---|---|---|
| POST | `/api/loans/request` | `{nestId, amount, termMonths}` | `{loanId, nestId, userId, amount, status}` |
| POST | `/api/loans/{loanId}/approve` | — | 200 OK |
| POST | `/api/loans/{loanId}/reject` | — | 200 OK |
| POST | `/api/loans/{loanId}/repay` | `{amount}` | 200 OK |
| GET | `/api/loans/nest/{nestId}` | — | `[{loanId, userId, principalAmount, termMonths, status}]` |

> **App ↔ server path mismatch (to fix in Phase 7):** the app calls
> `GET /api/nests/{nestId}/loans` but the server serves `GET /api/loans/nest/{nestId}`.

### Dashboard (JWT required)
| Method | Path | Response |
|---|---|---|
| GET | `/api/me/dashboard` | `{contributionStatus?, loanStatus?, penaltyStatus?}` |

> **Missing field (to add in Phase 7):** `userRole` is not in the server response.

---

## Running locally (current — requires MySQL)

### Prerequisites
1. MySQL 8.x installed and running.
2. Create database: `CREATE DATABASE mfuko_db;`
3. Verify `src/main/resources/application.conf` has your MySQL password.

```bash
cd C:\Users\Alvin\IdeaProjects\GroupMoneyManagerServer
./gradlew run
# Server starts at http://0.0.0.0:8081
```

---

## Known backend bugs (to fix in Phase 7)

### 1. Ktor version conflict
The `build.gradle.kts` mixes Ktor 3.2.3 (most deps) with Ktor 2.3.12 (JWT auth).
These are binary-incompatible. Fix: align everything to Ktor 3.x.
```
# Current (broken):
implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.12")   # Ktor 2 !
implementation(libs.ktor.server.core)                         # Ktor 3

# Fix:
implementation("io.ktor:ktor-server-auth-jwt-jvm:3.2.3")
```

### 2. Routing.kt references non-existent table columns
`/api/nests/create` references `NestsTable.contributionAmount` and `NestsTable.inviteCode`
which don't exist on `NestsTable`. The table has `joinCode` (not `inviteCode`) and no
`contributionAmount` column. The contribution amount belongs on `CyclesTable`.

### 3. NestsTable has `managerId` (required) but route never sets it
`NestsTable.managerId` is a non-nullable `long` column. The create nest route never
inserts it — the server will crash with a constraint violation on every create-nest call.
Fix: `it[NestsTable.managerId] = userId` in the insert block.

### 4. CyclesTable.startDate / endDate type mismatch
`CyclesTable` uses `date("start_date")` (java `LocalDate`) but the route inserts
`LocalDateTime.now()` — a type error. Fix: use `LocalDate.now()`.

### 5. Loan list path mismatch
- App calls: `GET /api/nests/{nestId}/loans`
- Server serves: `GET /api/loans/nest/{nestId}`

### 6. DashboardResponse missing `userRole`
The app's DTO (`DashboardResponse`) expects a `userRole: String?` field but the server
never sends it — manager-only UI never activates. Fix: add `userRole` to the server response.

---

## Phase 7 plan

1. Align Ktor to 3.x throughout.
2. Fix all routing and table bugs listed above.
3. Replace MySQL with **H2 file database** so `./gradlew run` needs zero external tools.
4. Add `userRole` to the dashboard response.
5. Align the loan-list path.
6. Externalise the JWT secret to an env var.
7. Set `BuildConfig.USE_REMOTE = true` in the app + wire `RemoteSync` layer.
8. (Optional) Deploy to Railway/Render with Postgres + real M-Pesa Daraja + FCM push.
