# Security

## Files intentionally excluded from git

This repository does not commit local Firebase, signing, or machine-specific files:

- `app/google-services.json`
- `local.properties`
- `.idea/`
- `.gradle/`
- `build/` and module build directories
- `*.keystore`
- `*.jks`

Keep these files local or store them in a secrets manager. Do not share release signing keys or Firebase configuration through pull requests.

## Local Firebase setup

1. Create or open a Firebase project.
2. Add an Android app with package name `com.example.findme_shahar_ofek`.
3. Download `google-services.json`.
4. Place it at `app/google-services.json`.
5. Enable Email/Password authentication in Firebase Authentication.
6. Deploy `firestore.rules` and `storage.rules` before using a shared Firebase project.

## Contributor practices

- Do not commit API keys, service account files, keystores, tokens, passwords, or generated Firebase config files.
- Keep debug-only credentials in `local.properties`, user-level Gradle properties, or environment variables.
- Use the checked-in Firebase rules as the baseline for any shared project.
- Review rule changes for anonymous access, public writes, oversized uploads, and cross-user data access.
- Rotate any credential that was committed or shared outside trusted channels.
