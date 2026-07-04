# UAT checklist

Manual acceptance pass, one session per role. Run against a fresh `docker compose up --build` so seed data is predictable. Check off as you go; anything that fails becomes a defect via the [template](defect-report-template.md).

## Session 1 — Officer (`officer` / `officer123`)

- [ ] Login with wrong password shows an error, doesn't sign you in
- [ ] Login with correct credentials lands on the dashboard
- [ ] Dashboard cards show plausible numbers (lanes counts match the lanes page)
- [ ] "Log inspection" is visible in the nav
- [ ] Logging a PASS inspection works, flash message appears, record is at the top of the list
- [ ] Logging a FAIL inspection with notes works, notes visible in the list
- [ ] Submitting the form with nothing selected shows field errors, nothing is saved
- [ ] Inspections list filters: All / Passed / Failed each show only matching rows
- [ ] Lanes page is visible, but no "Manage" column, no status dropdowns
- [ ] Sign out returns to login with a "signed out" message
- [ ] After sign-out, the back button / direct URL to `/inspections` bounces to login

## Session 2 — Supervisor (`supervisor` / `supervisor123`)

- [ ] Everything an officer can do (spot-check: log one inspection)
- [ ] Lanes page shows the Manage column with status dropdowns
- [ ] Changing a lane to MAINTENANCE updates the badge and the dashboard count
- [ ] Changing it back works, "last updated" timestamp moves

## Session 3 — Auditor (`auditor` / `auditor123`)

- [ ] Login works, dashboard visible
- [ ] No "Log inspection" in the nav
- [ ] Typing `/inspections/new` directly in the address bar is denied (403), not a broken page
- [ ] Inspections and lanes are readable
- [ ] No Manage column on lanes

## Session 4 — Cross-cutting

- [ ] Session expires after 30 minutes idle → next click bounces to login with "expired" note (long one — okay to verify with a shortened timeout in config)
- [ ] Keyboard-only pass: tab order on login and the inspection form is sane, everything reachable, focus visible
- [ ] Form labels are attached to inputs (click a label, its field focuses)
- [ ] Swagger UI loads at `/swagger-ui.html` and "Try it out" works with basic auth
