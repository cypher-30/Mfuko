# Mfuko — Backend (Ktor Server)

> Backend project location: `C:\Users\Alvin\IdeaProjects\MfukoServer`

The Mfuko Android app works fully offline (Phase 3+). The Ktor backend is **optional** and
enables real multi-device sync, M-Pesa Daraja integration, and cloud push notifications.

---

## Backend tech stack

| Component | Library |
|---|---|
| Server framework | Ktor 3.2.3 (Netty engine) |
| Database ORM | Jetbrains Exposed 0.49.0 |
| Database | H2 file (local, zero-setup) |
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

> `inviteCode` in the request/response bodies maps to `NestsTable.joinCode` server-side.

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

> App and server both use `GET /api/loans/nest/{nestId}` — the earlier path mismatch is fixed.

### Dashboard (JWT required)
| Method | Path | Response |
|---|---|---|
| GET | `/api/me/dashboard` | `{contributionStatus?, loanStatus?, penaltyStatus?, userRole?}` |

---

## Running locally (current — zero external services)

```bash
cd C:\Users\Alvin\IdeaProjects\MfukoServer
./gradlew run
# Server starts at http://0.0.0.0:8081
# DB is an H2 file at ./build/mfuko_db — created automatically, no setup needed.
```

To point the app at a physical device instead of an emulator, set
`BuildConfig.BASE_URL` in `app/build.gradle.kts` to your machine's LAN IP
(e.g. `http://192.168.x.x:8081/`) — the emulator loopback `10.0.2.2` only
works from the Android emulator, not a real device on the same Wi-Fi.

---

## Fixed in Phase 7

The bugs previously tracked here are resolved:

1. ~~Ktor version conflict~~ — JWT auth now pulled from the same Ktor 3.2.3
   version catalog entries (`libs.ktor.server.auth`, `libs.ktor.server.auth.jwt`)
   as the rest of the server.
2. ~~Routing.kt referenced non-existent table columns~~ — create-nest now
   inserts `joinCode`, `managerId`, and `createdAt` correctly.
3. ~~`NestsTable.managerId` never set~~ — fixed, set from the JWT principal.
4. ~~`CyclesTable.startDate`/`endDate` type mismatch~~ — route now inserts
   `LocalDate.now()`, matching the column type.
5. ~~Loan list path mismatch~~ — app and server agree on `/api/loans/nest/{nestId}`.
6. ~~`DashboardResponse` missing `userRole`~~ — added; manager-only UI can now activate.
7. ~~MySQL requirement~~ — replaced with a file-based H2 DB; `./gradlew run` needs no external services.

## Still open

- **JWT secret is hardcoded** in `application.conf` (`jwt.secret`) — fine for
  local dev, must move to an environment variable before any real deployment.
- **`BuildConfig.USE_REMOTE` defaults to `false`.** The DI layer (`AppModule`)
  now supports switching between local (Room) and network repo impls via this
  flag, but several screens (e.g. `NestSettingsScreen`) still read Room
  directly regardless of the flag, and demo login only works offline. Don't
  flip it to `true` until those screens are migrated.
- (Optional) Deploy to Railway/Render with Postgres + real M-Pesa Daraja + FCM push.
