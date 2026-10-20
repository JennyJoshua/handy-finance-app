# Wrapping a website or a GitHub web project

## A. A live website you may use

1. Confirm it is served over https and that you have the right to wrap it.
2. Set `LOAD_MODE=REMOTE`, `START_URL=https://yoursite/`.
3. If it uses a separate login or payment host, add it to `ALLOWED_HOSTS` (and the main host too).
4. Run the workflow, install, and test login, back button, and uploads.

Some sites refuse to run in embedded WebViews (certain Google/Facebook logins, some payment pages).
Those need the site owner's help or a different approach.

## B. A static website (HTML/CSS/JS) from GitHub

1. Check its licence; keep required notices.
2. Delete the sample files in `app/src/main/assets/www/` and copy the site's built files there
   (the folder that contains `index.html`). Keep the same folder structure.
3. `LOAD_MODE=BUNDLED`, `BUNDLED_ENTRY=index.html`.
4. Make links relative. A single-page app using `/route` paths or the History API may need hash routing
   or a base path of `/assets/www/`.
5. Remove or replace anything loaded from CDNs if you want it fully offline.

## C. A project that needs building first (React, Vue, Next export…)

Build it elsewhere (its own GitHub Action or a computer) to produce static files, then follow B.
This template does not run Node builds.

## D. A frontend with a backend (HYBRID)

1. Bundle the frontend as in B, with `LOAD_MODE=HYBRID`.
2. Point its API calls at the backend's https URL.
3. Add the backend/login hosts to `ALLOWED_HOSTS`.
4. Configure backend CORS for `https://appassets.androidplatform.net` (see WEBVIEW_CONFIGURATION.md).
5. Document internet and backend requirements for your users. The app cannot work without the backend.

## Not possible with this template

Desktop programs, server software, and apps that need hardware access (camera, bluetooth, GPS) from the web page.
