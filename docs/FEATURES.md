# Mfuko — Feature Specification

> Transcribed from the hand-drawn spec PDF (`app/Money nest.pdf`).
> This is the single source of truth for features. The PDF is no longer needed as a reference.

---

## App overview (PDF p.2)

**Core purpose:** Group savings and lending (chama) manager.

| Feature | Description |
|---|---|
| Contribute funds | Members contribute on a specific day each cycle |
| Track contributions | See who has paid and who hasn't |
| Issue & record loans | Loans come from the collective pool |
| Auto interest | Interest is calculated automatically |
| Track repayments | Track outstanding balance per loan |
| Customisable interest rate | Managers set the rate |
| Contribution reminders | Automatic notifications |
| Loan tracking | Full history per member |
| Contribution history | Per-member, per-cycle |
| Manager panel | Add/remove members, approve loans, view financial health |
| Repayment tracking | How fast members repay |
| Custom interest rate | Set by manager, agreed by group |
| Exportable reports | PDF export |
| Secure cloud backup | (Phase 7) |

---

## Roles (PDF p.3)

### Admin / Manager
- Create group (Nest)
- Add or remove members
- Set contribution schedule
- Set interest rate (agreed on by members)
- Approve or reject loans *(auto-approval by system based on financial health also planned)*
- Export reports
- Send announcements
- Can also do everything a member can do

### Member
- Contribute, request loan, repay
- View report and contribution history
- Receive notifications

> *Loans are funded from the collective contribution pool — think of it like an internal bank.*

> *A "moderator" role for delegating tasks is a future consideration.*

---

## User diagram (PDF p.4)

### Admin (Manager)
- Sign up / Log in / Reset password
- Create Nest
- Add / remove members
- Set contribution schedule
- Set interest rate
- Approve / reject loan requests *(after system approval)*
- Send announcements
- View and export reports
- View group financial dashboard

### Members
- Join Nest (invite code or link)
- Make contribution + view history
- View financial health
- Request loan
- View loan status
- Make repayment
- View group dashboard
- Receive notifications

### Automatic System
- Send contribution reminder
- Send loan repayment alert
- Calculate loan interest
- Trigger overdue notifications
- Auto-approve / reject a loan request based on financial health

---

## Graphical interface wireframes (PDF p.5)

### Sign Up
- Email address *(Note: actual implementation uses phone number — see auth DTOs)*
- Phone number
- Password / PIN
- Sign up with Google *(future)*

### Log In
- Email address or phone
- Password / PIN
- Login with Google *(future)*
- Sign up link if no account

### Home Page
- Nest name (e.g. "Imara Nest")
- Welcome message
- Your contribution this month
- Outstanding loans
- Amount due
- Next contribution date
- Financial health score

#### Bottom nav: Home | Activity | Notifications

### Activity Page
- Make a contribution
  - Amount
  - Pay now (M-Pesa) *(simulated in Phase 5, real in Phase 7)*
- Loan reason button *(optional)*
  - Loan amount + reason
- Contribution schedule
  - Upcoming date + due amounts
- Transaction history
  - List: contributions + loans taken

#### Bottom nav: Home | Activity | Notifications

---

## Notifications screen (PDF p.6)

- Reminder: contribution in two days
- "You've successfully contributed"
- Loan was approved
- Missed payment
- Read / unread indicator

### Profile button (accessible from top bar)
- Profile info
- Join or create Nest
- Switch Nest *(future)*
- Settings:
  - Notifications on/off
  - Change password / PIN
- Log out

---

## Manager screens (PDF p.6–7)

### Manager home (extra sections vs member home)
- Members (sort / filter)
  - Add members
  - Remove members
- Contribution Overview
  - Total funds
  - Who has paid and who hasn't
  - Export data (PDF)
  - Send reminder to members (unpaid) *(can be done by admin and system)*

#### Bottom nav: Home | Members | Activity | Notification

### Manager — Loan Management section
- Loan queue
  - Approve / Reject
  - Set interest rate (default)
- Ongoing loans
  - Member, loan amount, interest, due date *(maybe a table)*
- Nest settings
  - Set default interest rate
  - Set contribution schedule
  - Enable / disable auto reminder
  - Export full nest report

---

## Dialogs / smaller screens (PDF p.7)

### Contribution dialog
- Amount input
- Payment method
- Confirmation screen
- Success / failure
- Receipt

### Loan request dialog
- Loan request
- Approval status: Approval | Pending | Denied
- Repayment tracking view
- Progress bar + due date

### Empty states
- No joined nest
- No loans yet
- No contributions

---

## Technology notes (PDF p.8)

- Entity relationship diagram: https://dbdiagram.io/d/681104b31ca52373f5db8e5f
- Integration targets: M-Pesa (Safaricom Daraja API)
- Financial health chart (future)

---

## Implementation status

| Feature | Phase | Status |
|---|---|---|
| Register / login (local, demo) | 3 | 🔜 |
| Create & join Nest | 0–2 | ✅ Working (remote) |
| Dashboard | 0–2 | ✅ Working (remote) |
| Invite code display on nest creation | 2 | ✅ Fixed |
| Manager approve / reject loans | 0–2 | ✅ Fixed (Snackbar on error) |
| Contribution recording (manager) | 0–2 | ✅ Working |
| nestId propagation (no hardcoded 1L) | 2 | ✅ Fixed |
| Divide-by-zero in progress circle | 2 | ✅ Fixed |
| Member empty state | 2 | ✅ Fixed |
| Offline-first Room database | 3 | 🔜 |
| Local auth + demo mode | 3 | 🔜 |
| Mfuko branding / theme | 0, 4 | ✅ Applied |
| Money formatter (KES) | 2, 4 | ✅ Applied |
| Contribution schedule & cycles | 5 | 🔜 |
| Auto interest calculation | 5 | 🔜 |
| In-app + push notifications | 5 | 🔜 |
| Financial health score + chart | 5 | 🔜 |
| PDF export | 5 | 🔜 |
| M-Pesa simulated flow | 5 | 🔜 |
| Manager panel (full) | 5 | 🔜 |
| Profile / settings screen | 5 | 🔜 |
| Announcements | 5 | 🔜 |
| Unit tests | 6 | 🔜 |
| Backend fix + H2 | 7 | 🔜 |
| Cloud sync + real M-Pesa | 7 | 🔜 |
