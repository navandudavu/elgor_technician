# ELGOR Technician — Android app

A native Android app for ELGOR technicians to work their assigned
ServiceHub jobs: view assigned jobs, change status, log hours, add notes,
and upload photos. This is a separate app from the ELGOR customer app
(elgor1999) — that one's for customers browsing/buying, this one's an
internal tool for the people doing the repairs, talking to the same
ServiceHub backend the web dashboard uses.

## Status: never compiled

Same honest disclosure as the original ELGOR customer app's own status
doc: there's no Android SDK or emulator available in the environment this
was built in, so this has **never been compiled**. Every file was checked
by hand — package declarations match their folder, braces balance, no
corrupted characters (a real one was caught and fixed during development,
see below), Material3 APIs checked against the specific compose-bom
version this project pins — but none of that is a substitute for an
actual `./gradlew assembleDebug`. That's the first thing to run once this
is open in Android Studio.

## What it does

- Login with a ServiceHub account (technician or admin credentials both
  work, but the app itself is scoped to technician actions only)
- See your assigned jobs
- Open a job: change its status (pending → assigned → in_progress →
  finished), log hours worked, add notes, take/pick a photo and upload it,
  delete photos you uploaded
- First-run screen to enter the ServiceHub server address, since this
  points at a server that isn't deployed anywhere fixed yet — see below

## Before you can actually use it: server address

This app has no backend URL baked in. On first launch it asks for one -
during development that's your computer's local network IP and the
ServiceHub port, e.g. `http://192.168.1.100:4000` (find your computer's
LAN IP, don't use `localhost` - that would point the phone at itself, not
your computer). Once ServiceHub is deployed somewhere real, that becomes
a fixed public URL instead.

The address is saved on the phone (DataStore) so this is only needed once
per install.

## Cleartext HTTP is allowed (for now)

`app/src/main/res/xml/network_security_config.xml` permits plain `http://`
connections, unlike the ELGOR customer app which blocks it. This is
intentional and necessary while ServiceHub runs on local HTTP without a
certificate — but it should be tightened once ServiceHub has a real HTTPS
deployment. The file has a comment explaining exactly what to change.

## Stack

Matches the existing ELGOR customer app's choices where they made sense
to reuse: Kotlin, Jetpack Compose + Material3, Navigation Compose,
Retrofit + Moshi for the API, Coil (available, though photo loading here
uses a manual authenticated-fetch approach instead - see below),
coroutines + ViewModel for state. DataStore added for persisting the JWT
and server URL between app launches (the customer app didn't need this,
it has no login).

compose-bom is pinned to `2024.06.00` to match the customer app's version.
One Material3 API (`PullToRefreshBox`) needed Material3 1.3.0+ and isn't
available at this BOM version - the job list uses a manual refresh button
instead rather than bumping the BOM (which would cascade into a
`compileSdk` bump too, not worth it for one nice-to-have).

## Why photos don't use plain Coil image loading

ServiceHub's photo endpoint requires a JWT in the Authorization header
(`GET /api/jobs/photos/:filename`, see the backend's `requireAuth`
middleware) - a normal image loader has no way to attach that header, so
a plain Coil `AsyncImage` pointed at the URL would just get a 401. Same
root problem the web app hit and solved the same way: `JobPhoto.kt`
fetches the raw bytes through the same authenticated Retrofit client
every other request uses, then decodes them into a bitmap manually.

## Setup in Android Studio

1. Open this folder as a project in Android Studio (it'll regenerate the
   Gradle wrapper jar automatically on sync - that binary file can't be
   generated in this environment, no network path to Gradle's servers,
   same limitation the customer app's build had).
2. Let Gradle sync, resolve dependencies.
3. Build → Make Project. This is the real first test - nothing here has
   compiled yet.
4. Run on a device or emulator on the same network as your ServiceHub
   backend (or use `10.0.2.2` instead of `localhost` if testing against a
   backend running on the same machine as an Android emulator - that's
   the emulator's special alias for the host machine).
5. Enter the server address, log in with a technician account seeded in
   ServiceHub (see the ServiceHub README's seed script).

## Known gaps / things to check first

- **Never compiled** - see above, this is the big one.
- **No app icon adaptive/foreground layers** - uses plain PNG launcher
  icons (generated from the real ELGOR logo at all 5 density buckets),
  not the adaptive-icon XML format the customer app uses. Simpler, works
  fine, just less fancy (no separate background/foreground parallax
  layer). Fine for an internal tool; revisit if it matters to you.
- **No offline handling** - if a technician is in a basement with no
  signal (a real scenario for appliance repair), every action just fails
  with a network error. No local queue/retry. Worth adding later if this
  becomes a real pain point in the field.
- **No photo compression before upload** - uploads the picked image at
  whatever size the phone's photo picker returns it at, capped by the
  backend's 8MB limit (which will just reject anything bigger with a
  clear error, it won't silently fail).
- **Admin-only ServiceHub features aren't here** - job assignment,
  scheduling, CSV export, team management. Out of scope on purpose - this
  app is for technicians, see the ServiceHub web app for admin functions.
- **Settings screen for changing the server URL doesn't exist yet** - the
  login screen's first-run flow mentions "you can change it later in
  Settings" but that screen isn't built. If you need to change the server
  address after first setup, clearing app data is currently the only way.

## A bug that was caught and fixed during development

While writing the Job Detail screen, one import line ended up with
Cyrillic characters mixed into what should have been the plain-ASCII
identifier `horizontalScroll` (`горizontalScroll`) - a corrupted,
uncompilable import that also wasn't even used anywhere in the file. It
was caught by a targeted sweep for non-ASCII characters in import/package
lines across every file, not by a compiler (since nothing here compiles).
Fixed by removing the broken, unused import entirely. Worth knowing this
class of error is possible and worth spot-checking if you hand-edit
anything here yourself.
