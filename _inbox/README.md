# ZIP Inbox

Upload ZIP archives containing project files into this directory.

## Instructions

1. Upload a ZIP file into `_inbox/`.
2. Use a lowercase filename with letters, numbers, and hyphens.

Example:

`android-webview-template-v1.zip`

3. Commit the uploaded ZIP to the `main` branch.
4. GitHub Actions will validate and extract the archive.
5. Check the Actions tab for the workflow result.
6. If processing fails, inspect `_failed/` and its error report.

## Important

- Upload only project files you trust.
- Do not include passwords, API keys, signing keys, or other secrets.
- Do not include generated build directories or caches.
- Existing repository files with matching paths may be overwritten.
- Files not included in the ZIP will not automatically be deleted.
