# API Documentation

This document provides details about the API endpoints available in the Financial File Parser application.

---

## Health Check

- **Endpoint:** `/health-check`
- **Method:** `GET`
- **Description:** Checks the health of the server. Useful for monitoring and uptime checks.
- **Success Response:**
  - **Code:** `200 OK`
  - **Content:** `Up and running!`

---

## Process Financial File

- **Endpoint:** `/api/v1/file/process`
- **Method:** `POST`
- **Description:** Processes an uploaded financial file (e.g., a bank statement or credit card bill) and extracts transaction data.
- **Headers:**
  - `Institution` (Required): The name of the financial institution.
    - **Possible Values:** `NUBANK`, `ITAU`, `C6_BANK`, `BRADESCO`, `WISE`, `ANY`.
  - `File-Type` (Required): The type of file being uploaded (e.g., `PDF`, `CSV`).
  - `Invoice-Type` (Required): The type of invoice.
    - **Possible Values:** `CREDIT_INVOICE`, `ACCOUNT_STATEMENT`.
  - `CSV-Separator` (Optional): The separator character for CSV files. Defaults to `,`.
  - `DateTime-Pattern` (Optional): The date/time pattern for parsing dates. Defaults to `dd/MM/yyyy HH:mm:ss`.
- **Request Body:** The raw file content to be processed.
- **Success Response:**
  - **Code:** `200 OK`
  - **Content:** A JSON array of `FinancialTransactionResponse` objects. Each object has the following structure:
    ```json
    [
      {
        "value": 123.45,
        "description": "Transaction Description",
        "transactionTime": "dd/MM/yyyy HH:mm:ss",
        "institutionUUID": "unique-id-of-the-transaction",
        "transactionType": "DEBIT",
        "institution": "NUBANK",
        "cardType": "CREDIT",
        "currency": "BRL"
      }
    ]
    ```
  - **Field Descriptions:**
    - `value` (number): The monetary value of the transaction.
    - `description` (string): A description of the transaction.
    - `transactionTime` (string): The date and time of the transaction, formatted according to the `DateTime-Pattern` header.
    - `institutionUUID` (string, optional): A unique identifier for the transaction, if available.
    - `transactionType` (string): The type of transaction (e.g., `DEBIT`, `CREDIT`).
    - `institution` (string): The financial institution associated with the transaction.
    - `cardType` (string, optional): The type of card used. Possible values are `CREDIT`, `DEBIT`, or `NONE`.
    - `currency` (string): The currency of the transaction (defaults to `BRL`).
- **Error Responses:**
  - **Code:** `204 No Content` - If no transactions are found in the file.
  - **Code:** `400 Bad Request` - If required headers are missing.
  - **Code:** `415 Unsupported Media Type` - If the `File-Type` is invalid.
  - **Code:** `500 Internal Server Error` - For unexpected server errors.
