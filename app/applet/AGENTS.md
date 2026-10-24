# MONETA MINING APP - MASTER ARCHITECTURE & SECURITY MANDATES

These instructions represent the foundational architectural rules for the Moneta Mining App. All future code modifications and feature additions must strictly adhere to these principles.

## 1. CORE ARCHITECTURE BOUNDARY
*   **ANDROID APK = DISPLAY + USER INTERACTION**
*   **BACKEND = BUSINESS LOGIC + AUTHORIZATION + ACCOUNT STATE**
*   **DATABASE = PERSISTENT APPLICATION STATE**
*   **BLOCKCHAIN = BLOCKCHAIN TRANSACTION TRUTH**
*   **LEDGER = MINT ACCOUNTING TRUTH**

The Android client is untrusted. It must **never** be responsible for determining or finalizing:
*   User identity or UUIDs
*   MINT balance or accounting
*   Mining rewards, rates, or duration logic
*   Payment confirmation or subscription entitlements
*   Transaction validity

## 2. MINING SYSTEM SECURITY
*   The client only sends intent (`START MINING: 6 HOURS`). 
*   The backend records the `started_at` and `scheduled_end_at` timestamps using **server time**.
*   The client must never calculate the final reward. It displays estimated progress.
*   When a session ends, the client displays a `VERIFYING` state until the backend explicitly returns a `CREDITED` status with the finalized reward.

## 3. PAYMENT & WALLET SECURITY
*   **Wallet Integration**: Support only public address connection (e.g., via Trust Wallet intent).
*   **NEVER** request, input, or store Seed Phrases, Recovery Phrases, or Private Keys.
*   **Payment Intents**: Payments require a backend-generated Payment Intent (`CREATED` -> `PENDING` -> `CONFIRMED`).
*   The client detects the transaction hash and submits it, but the **backend** verifies the blockchain confirmations, network ID, receiver address, and exact amount before issuing entitlements (e.g., `AUTO_MINING_30D`).

## 4. IDENTITY & SYNCHRONIZATION
*   There is exactly **one** authoritative MINT balance. It is synchronized globally across Miner, Boost, Friends, and Wallet screens.
*   Users are identified publicly by a backend-generated `MONETA ID` (e.g., `MNT-8K4P7Q2X`). Internal database operations use a secure UUID.

## 5. ANDROID APP DEVELOPMENT RULES
*   **No local shortcuts**: Do not bypass the simulated backend logic. If a feature requires backend authority (like claiming a referral), ensure the UI reflects a "loading/verifying" state before showing success.
*   **Idempotency & Replay**: Ensure the UI does not allow spamming of critical actions (disable buttons during network requests).
*   **Secrets**: Never embed backend database credentials, private master keys, or internal security configs into the APK source code.
