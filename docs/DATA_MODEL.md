# Mfuko — Data Model

ERD (online): https://dbdiagram.io/d/681104b31ca52373f5db8e5f

---

## Entities

### User
| Column | Type | Notes |
|---|---|---|
| id | Long PK | Auto-increment |
| name | String | Full name |
| phone | String UNIQUE | Login credential |
| passwordHash | String | BCrypt hash |
| createdAt | Timestamp | |

### Nest (Group)
| Column | Type | Notes |
|---|---|---|
| id | Long PK | |
| name | String | Group display name |
| joinCode / inviteCode | String UNIQUE | 6-digit code members use to join |
| managerId | Long FK → User | Creator / manager |
| configuration | Text (JSON) | Settings: interest rate, schedule, etc. |
| createdAt | Timestamp | |

> Note: The Ktor backend table uses `joinCode`; the Android DTO uses `inviteCode`. These will be reconciled in Phase 7.

### Membership (many-to-many User ↔ Nest)
| Column | Type | Notes |
|---|---|---|
| userId | Long FK → User | |
| nestId | Long FK → Nest | |
| role | String | "manager" or "member" |
| createdAt | Timestamp | |
| PRIMARY KEY | (userId, nestId) | A user can only be in a nest once |

### Cycle (contribution period)
| Column | Type | Notes |
|---|---|---|
| id | Long PK | |
| nestId | Long FK → Nest | |
| name | String | e.g. "January 2025" |
| startDate | Date | |
| endDate | Date | |
| amountDuePerMember | Decimal(12,2) | |
| status | String | "open" or "closed" |

### Contribution
| Column | Type | Notes |
|---|---|---|
| id | Long PK | |
| cycleId | Long FK → Cycle | Which cycle this payment belongs to |
| userId | Long FK → User | Who paid |
| amountPaid | Decimal(12,2) | Cumulative (upserted, not multiple rows) |
| datePaid | DateTime | Last payment timestamp |
| status | String | "paid" / "partial" / "unpaid" |

### Loan
| Column | Type | Notes |
|---|---|---|
| id | Long PK | |
| nestId | Long FK → Nest | Which group's pool is being borrowed from |
| userId | Long FK → User | Borrower |
| principalAmount | Decimal(12,2) | |
| interestRate | Decimal(6,4) | Percentage (e.g. 10.0000 = 10%) |
| interestType | String | "flat" or "reducing" |
| termMonths | Int | Loan duration |
| status | String | "pending" / "active" / "paid" / "rejected" |
| totalInterestAmount | Decimal(12,2)? | Computed on approval |
| totalRepayableAmount | Decimal(12,2)? | principal + interest |
| outstandingBalance | Decimal(12,2) | Decreases on repayment |
| requestDate | DateTime | |
| approvalDate | DateTime? | |
| disbursementDate | Date? | |

### LoanRepayment *(planned for Phase 5)*
| Column | Type | Notes |
|---|---|---|
| id | Long PK | |
| loanId | Long FK → Loan | |
| amount | Decimal(12,2) | |
| paidAt | DateTime | |

### Penalty
| Column | Type | Notes |
|---|---|---|
| id | Long PK | |
| userId | Long FK → User | |
| nestId | Long FK → Nest | |
| amount | Decimal(12,2) | |
| reason | String | |
| status | String | "unpaid" / "paid" |
| createdAt | DateTime | |

### Notification *(Phase 5)*
| Column | Type | Notes |
|---|---|---|
| id | Long PK | |
| userId | Long FK → User | Recipient |
| title | String | |
| body | String | |
| type | String | "contribution_reminder" / "loan_approved" / "overdue" / etc. |
| isRead | Boolean | |
| createdAt | DateTime | |

### Announcement *(Phase 5)*
| Column | Type | Notes |
|---|---|---|
| id | Long PK | |
| nestId | Long FK → Nest | |
| authorId | Long FK → User | Manager who sent it |
| message | String | |
| createdAt | DateTime | |

---

## Room entities (Phase 3)

Room entities will be added in `data/local/entities/` and mirror the table structure above.
Each entity gets a corresponding DAO in `data/local/dao/` exposing `Flow<List<T>>` for
reactive UI updates.

The `MfukoDatabase` will:
1. Auto-create all tables on first launch.
2. Run a `RoomDatabase.Callback.onCreate` to seed demo data when the user taps "Continue as demo".

---

## Financial health score (Phase 5)

Computed per member based on:
- Repayment speed (days from due date to actual payment)
- Number of missed payments
- Penalty history

Score: 0–100, displayed as a chart on the dashboard.
Auto-approval threshold: configurable per nest (e.g. score ≥ 70 = auto-approve).
