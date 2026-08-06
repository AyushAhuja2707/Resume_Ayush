# Office Issue Bot

Internal Spring Boot service for production support teams to:

- paste or upload logs and get a first-pass issue summary
- ask chatbot-style troubleshooting questions
- inspect database metadata such as schemas, tables, columns, and procedures
- stay inside a controlled read-only access model

## Suggested architecture

1. `logs` module analyzes pasted log lines and flags likely problem signatures.
2. `chat` module combines the user question with log findings and database context.
3. `db` module exposes safe metadata exploration against IT environments.
4. Future AI integration can call an LLM after these internal signals are assembled.

## Current APIs

### Analyze logs

`POST /api/logs/analyze`

```json
{
  "incidentId": "INC-1023",
  "systemName": "payments",
  "environment": "prod",
  "userQuestion": "Why are payment retries failing?",
  "logLines": [
    "2026-04-06 10:10:11 ERROR java.net.SocketTimeoutException timeout calling auth service",
    "2026-04-06 10:10:12 WARN retry failed for transaction 123"
  ]
}
```

### Chat endpoint

`POST /api/chat`

```json
{
  "sessionId": "chat-1",
  "userMessage": "Check if this is DB or network issue",
  "pastedLogs": [
    "ORA-00060 deadlock detected",
    "Procedure process_payment failed"
  ],
  "schemaName": "public",
  "objectName": "payment",
  "includeProcedures": true
}
```

### Database metadata

`GET /api/db/metadata?schemaName=public&objectName=payment&includeProcedures=false`

## Important security guidance

- Keep the datasource account read-only.
- Start with metadata access only; do not allow row reads until audited.
- Restrict schemas and blocked objects in `application.yml`.
- Add authentication before exposing this inside office networks.
- Route all procedure execution requests through an approval workflow instead of direct bot execution.

## Recommended next steps

1. Add file upload support and parse `.log`, `.txt`, and zipped archives.
2. Store incidents and conversation history in a database.
3. Integrate with an internal LLM or API for richer natural-language answers.
4. Add Spring Security with SSO or LDAP.
5. Add vendor-specific metadata queries for Oracle, SQL Server, or PostgreSQL.

## Run locally

```bash
mvn spring-boot:run
```
