# Failed ZIP Imports

GitHub Actions moves ZIP files that fail validation or extraction
into this directory and creates an accompanying error report.

## How to troubleshoot

1. Open the failed ZIP's `.error.txt` report.
2. Read the reason for failure.
3. Correct the original ZIP contents or filename.
4. Upload the corrected ZIP into `_inbox/`.
5. Check the Actions tab for the new result.

## Common problems

- ZIP filename does not follow the required naming convention.
- Archive contains unsafe paths or symbolic links.
- Archive exceeds the configured size or file-count limits.
- Archive contains protected directories or generated files.
- ZIP archive is corrupted.

## Security

Never upload passwords, API keys, signing keys, or other secrets.

Keep failed archives only as long as needed for troubleshooting.
