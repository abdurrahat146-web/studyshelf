# StudyShelf — Native Android App

A native Kotlin/Jetpack Compose rewrite of the StudyShelf web app, sharing the
same Supabase backend (`users`, `shared_books`, `admins` tables) so accounts
and shared books work across the web app and this native app.

## Free / Premium separation

This is **one Gradle project, two build variants** — the standard native-Android
way to split a free and premium app, mirroring your web app's two HTML files
but as proper Gradle **product flavors** instead of duplicated code:

| | `free` flavor | `premium` flavor |
|---|---|---|
| Application ID | `com.studyshelf.app.free` | `com.studyshelf.app.premium` |
| App name | StudyShelf | StudyShelf Premium |
| Daily AI credits | 10 | Unlimited |
| Ads | Shown | None |
| Premium unlock | Key redemption screen (bKash) | Already unlocked |
| Launcher icon | Orange accent | Gold accent |

Both variants install **side-by-side on the same device** (different
application IDs) — exactly like having both `studyshelf-free-updated.html`
and `studyshelf-premium.html` open at once, but as installable apps.

**All shared logic — auth, book sharing, notifications, routines — lives once**
in `app/src/main/` and is identical in both builds. Only the tier-specific
behavior (ads, credit count, key gate) differs, isolated behind one interface:

```
app/src/main/.../premium/PremiumGate.kt      ← shared interface, both flavors implement it
app/src/free/.../premium/PremiumGateImpl.kt      ← free: shows the key-redemption gate
app/src/premium/.../premium/PremiumGateImpl.kt   ← premium: always unlocked, no gate
```

Gradle compiles whichever `PremiumGateImpl` matches the active flavor — same
fully-qualified class name in each source set, so `main/` code (like
`PremiumScreen.kt`) just calls `app.premiumGate.isPremiumBuild` without ever
needing an if/else on which flavor it's running as.

### Building each variant

```bash
./gradlew assembleFreeDebug       # StudyShelf (free) debug APK
./gradlew assemblePremiumDebug    # StudyShelf Premium debug APK
./gradlew assembleFreeRelease     # StudyShelf (free) release APK
./gradlew assemblePremiumRelease  # StudyShelf Premium release APK
./gradlew assembleDebug           # builds BOTH debug APKs in one go
```

APKs land in `app/build/outputs/apk/free/debug/` and
`app/build/outputs/apk/premium/debug/` respectively. In Android Studio, use
the **Build Variants** panel (bottom-left) to switch which one runs when you
hit ▶️.

### Free flavor's premium keys

`app/src/free/.../PremiumGateImpl.kt` ships the **same obfuscated key set**
(base64 + reversed) as the web app's Premium Gate — a key sold for the web
app also unlocks the free Android app. If you rotate keys on the web app,
update this file to match.

## What's included

- **Jetpack Compose UI** — Auth (login/signup), Library, Shared Inbox, Study
  Routine, and Premium screens, dark theme matching the web app's palette.
- **Supabase integration** — via the official Kotlin `supabase-kt` client
  (Postgrest + Realtime), pointed at the same project as the web app.
- **Real Android notifications**, two kinds:
  1. **Shared book notifications** — a `WorkManager` periodic job
     (`SharedBookPollWorker`) checks the `shared_books` table every 15
     minutes (Android's minimum periodic interval) for new books shared
     with the logged-in user and fires a notification for each.
  2. **Study routine reminders** — `AlarmManager.setExactAndAllowWhileIdle`
     schedules an exact alarm per routine entry per selected day of the
     week. `RoutineAlarmReceiver` fires the notification and reschedules
     itself for next week. `BootReceiver` re-arms the shared-book poll
     worker after a device restart (routine alarms are re-armed when the
     app is next opened, since `AlarmManager` alarms don't survive reboot).
- **GitHub Actions CI** (`.github/workflows/android.yml`) — runs
  `./gradlew lintDebug`, `assembleDebug` (both flavors), and
  `testDebugUnitTest` on every push/PR, uploading lint reports and both
  debug APKs as build artifacts.

## ⚠️ One manual step before this builds

The `gradle-wrapper.jar` binary itself isn't included (binary jars can't be
generated as text). Before running `./gradlew` for the first time, either:

**Option A — let Android Studio do it:**
Open the project in Android Studio; it will detect the missing wrapper jar
and offer to regenerate it automatically.

**Option B — regenerate manually:**
```bash
gradle wrapper --gradle-version 8.7
```
(requires a local Gradle install just for this one-time step)

**Option C — download it directly:**
```bash
curl -L -o gradle/wrapper/gradle-wrapper.jar \
  https://raw.githubusercontent.com/gradle/gradle/v8.7.0/gradle/wrapper/gradle-wrapper.jar
```

Once `gradle-wrapper.jar` is present, `./gradlew build` and the CI workflow
will both work without any further setup.

## Project structure

```
app/src/main/java/com/studyshelf/app/
├── MainActivity.kt              Nav host, permission requests
├── StudyShelfApp.kt              Application class — sets up channels + polling
├── data/
│   ├── model/Models.kt            Supabase table models (User, Admin, SharedBook, Book, RoutineEntry)
│   ├── remote/SupabaseClient.kt   Shared Supabase client singleton
│   └── repository/StudyShelfRepository.kt   Login/signup/share/inbox logic
├── notifications/
│   ├── NotificationChannels.kt        Creates the two notification channels
│   ├── NotificationHelper.kt          Builds + fires notifications
│   ├── SharedBookPollWorker.kt        WorkManager job — polls for new shares
│   ├── RoutineAlarmScheduler.kt       Schedules/cancels AlarmManager alarms
│   ├── RoutineAlarmReceiver.kt        Fires when a routine alarm goes off
│   ├── BootReceiver.kt                Re-arms polling after device reboot
│   └── NotificationScheduler.kt       Central WorkManager scheduling entrypoint
└── ui/
    ├── theme/                     Colors, typography, theme matching the web app
    └── screens/
        ├── AuthScreen.kt          Login / signup
        ├── LibraryScreen.kt       Book list + share dialog
        ├── InboxScreen.kt         Shared-with-you list (notification deep link target)
        └── RoutineScreen.kt       Study reminders — creates real scheduled notifications
```

## Supabase tables required

Same as the web app — see the web project's README for the exact SQL, or:

```sql
create table users (
  id text primary key, name text, username text unique,
  password text, created_at timestamp default now()
);
create table admins (
  id text primary key, name text, username text unique,
  password text, created_at timestamp default now()
);
create table shared_books (
  id serial primary key, book_id text, book_title text, book_emoji text,
  from_username text, to_user_id text, shared_at timestamp default now()
);

alter table users enable row level security;
alter table admins enable row level security;
alter table shared_books enable row level security;

create policy "Allow all" on users for all using (true) with check (true);
create policy "Allow all" on admins for all using (true) with check (true);
create policy "Allow all" on shared_books for all using (true) with check (true);
```

## Notification permission notes

- **Android 13+ (API 33+)**: requires runtime `POST_NOTIFICATIONS` permission,
  requested on first launch in `MainActivity`.
- **Android 12+ (API 31+)**: exact alarms require the user to grant
  "Alarms & reminders" in system settings — `MainActivity` opens that
  settings screen automatically if not yet granted.
- If notification or exact-alarm permission is denied, the app **degrades
  gracefully** — it just won't fire local notifications; all other features
  keep working.

## Known limitations / next steps

- The book catalog in `LibraryScreen` is a small hardcoded sample list —
  wire it up to your actual book source (Supabase table or bundled JSON)
  to replace `sampleBooks`.
- No PDF reader screen yet (the web app uses PDF.js in-browser; native
  would use something like `AndroidPdfViewer` or `PdfRenderer`).
- No premium/admin panel screens yet — only the underlying Supabase admin
  table and repository hooks are there to build on.
- WorkManager's minimum periodic interval is 15 minutes — for near-instant
  shared-book notifications you'd want to move to Supabase Realtime
  (`postgres_changes` subscription), which the `realtime-kt` dependency is
  already included for but not yet wired into a long-running listener.
