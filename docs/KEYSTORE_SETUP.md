# Keystore Generation Guide

## CRITICAL WARNING
> If you lose your keystore file or password, you **cannot update your app on the Play Store**.
> There is NO way to recover it. **Back up your keystore in at least 3 separate secure locations.**

## Generate Keystore (run once, keep forever)

```bash
keytool -genkey -v \
  -keystore period-saathi-release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias period-saathi-key \
  -dname "CN=Period Saathi, OU=Dev, O=YourName, L=City, S=State, C=IN"
```

When prompted:
- Keystore password: use a strong, unique password (20+ chars)
- Key password: can be the same as keystore password
- Save both passwords in a password manager

## Verify Keystore
```bash
keytool -list -v -keystore period-saathi-release.jks
```

## Configure local.properties
```properties
keystore.path=../period-saathi-release.jks
keystore.password=YOUR_KEYSTORE_PASSWORD
key.alias=period-saathi-key
key.password=YOUR_KEY_PASSWORD
```

## Backup Checklist
- [ ] Keystore saved to encrypted USB drive
- [ ] Keystore uploaded to personal Google Drive (encrypted ZIP)
- [ ] Passwords saved in password manager
- [ ] local.properties NEVER committed to git

## For Play App Signing (Recommended)
Upload your keystore to Google Play App Signing.
Google will manage signing for you after initial upload.

## Build Release AAB
```bash
./gradlew bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
```