# Keystore Setup for Play Store Release

## Prerequisites

- Java JDK 17+ installed
- `keytool` available in PATH (comes with JDK)

## Step 1: Generate the Keystore

Run the following command in the project root directory:

```bash
keytool -genkey -v -keystore period-saathi-key.jks \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -alias period-saathi
```

You will be prompted to enter:
- **Keystore password:** Choose a strong password
- **Key password:** Can be same as keystore password
- **Distinguished name fields:** Your name, organization, location

### Example Input

```
Keystore password: Paldeepak079@
Re-enter password: Paldeepak079@
First and Last Name: Paldeepak
Organizational Unit: Development
Organization: Period Saathi
City: Your City
State: Your State
Country Code: IN
```

## Step 2: Add Keystore Info to local.properties

Add these lines to `local.properties` (do NOT commit this file to git):

```properties
keystore.path=../period-saathi-key.jks
keystore.password=Paldeepak079@
key.alias=period-saathi
key.password=Paldeepak079@
```

## Step 3: Verify Signing

Run the release build to verify signing configuration:

```bash
./gradlew assembleRelease
```

## Important Security Notes

- ⚠️ **NEVER** commit `*.jks` or `local.properties` to version control
- ⚠️ **BACKUP** your keystore file. If lost, you cannot update your app on Play Store
- ⚠️ **SAVE** your passwords. Store them in a password manager
- The keystore has a 10000-day validity (~27 years)

## .gitignore

The `.gitignore` already excludes `local.properties`. To also exclude keystore files, add:

```
*.jks
*.keystore
```