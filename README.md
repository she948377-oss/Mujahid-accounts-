# Mujahid Accounts - Business Ledger & Khata Management 📊

A modern Android application for Pakistani and international businesses, retailers, and wholesalers to manage credit/debit khatas (گاہک / سپلائر کھاتہ), daily cashbook (روکڑ), stock inventory (اسٹاک), and financial balance sheets (رپورٹس).

---

## 🚀 Automated GitHub Actions CI/CD

This repository includes automated GitHub Actions workflows located in `.github/workflows/android-build.yml`.

### How it works:
1. **Automatic Build**: Whenever you `push` code or open a `pull_request` on the `main` or `master` branch, GitHub Actions will automatically:
   - Check out the repository.
   - Configure Java 17 and Gradle environment.
   - Run tests and build the application.
   - Compile the installable Android APK (`.apk`).
2. **Download APK on Mobile or Desktop**:
   - Go to your repository on GitHub.
   - Tap/click the **"Actions"** tab.
   - Select the latest workflow run.
   - Under the **"Artifacts"** section, download **`MujahidAccounts-Debug-APK`**.
   - Install the APK directly on any Android phone!

---

## 🛠 Features

- **Khata Management (گاہک اور سپلائر کھاتہ)**:
  - Track customer and supplier balances with instant Gave (دیے) / Got (لیے) entries.
  - One-tap WhatsApp, SMS, and Call reminders with automated Urdu/English ledger text.
- **Daily Cashbook (روکڑ)**:
  - Daily In/Out cash recording categorized by sales, rent, utilities, bills, and custom expenses.
- **Inventory & Stock Tracking (اسٹاک انوینٹری)**:
  - Manage stock quantities, purchase/sale prices, low-stock warnings, and inventory valuation.
- **Reports & Balance Sheet (مالیاتی رپورٹس)**:
  - Real-time receivables, payables, stock assets, net business worth, and shareable PDF/text reports.
- **Local Persistence**:
  - Offline-first architecture built with Jetpack Compose and Room Database.
