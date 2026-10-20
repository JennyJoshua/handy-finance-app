#!/usr/bin/env bash
# Checks that every required project file exists and is sane. Used locally and by GitHub Actions.
set -u
cd "$(dirname "$0")/.."
fail=0
err() { echo "ERROR: $*"; fail=1; }

required=(
  settings.gradle.kts build.gradle.kts gradle.properties app-config.properties
  gradlew gradlew.bat gradle/wrapper/gradle-wrapper.properties gradle/wrapper/gradle-wrapper.jar
  app/build.gradle.kts app/src/main/AndroidManifest.xml
  app/src/main/java/com/webviewtemplate/app/MainActivity.java
  app/src/main/java/com/webviewtemplate/app/UrlPolicy.java
  app/src/main/java/com/webviewtemplate/app/AppConfig.java
  app/src/main/res/layout/activity_main.xml app/src/main/res/values/strings.xml
  app/src/main/res/values/themes.xml app/src/main/res/xml/network_security_config.xml
  app/src/main/res/drawable/ic_launcher_foreground.xml app/src/main/res/mipmap-anydpi/ic_launcher.xml
  app/src/main/assets/www/index.html
  app/src/test/java/com/webviewtemplate/app/UrlPolicyTest.java
  .github/workflows/android-build.yml
  README.md PROJECT_REQUIREMENTS.md DECISIONS.md CHANGELOG.md REVIEW.md handoff.md
  docs/SETUP.md docs/WEBVIEW_CONFIGURATION.md docs/WEBSITE_IMPORT_GUIDE.md docs/TROUBLESHOOTING.md
)
for f in "${required[@]}"; do [ -f "$f" ] || err "missing required file: $f"; done

jar=gradle/wrapper/gradle-wrapper.jar
if [ -f "$jar" ]; then
  size=$(wc -c < "$jar")
  [ "$size" -gt 30000 ] || err "$jar is only $size bytes - looks like a placeholder"
  [ "$(head -c2 "$jar")" = "PK" ] || err "$jar is not a valid JAR/ZIP file"
fi
[ -f gradlew ] && { head -1 gradlew | grep -q '^#!' || err "gradlew has no shebang line"; }
[ -f gradle/wrapper/gradle-wrapper.properties ] && {
  grep -q '^distributionUrl=' gradle/wrapper/gradle-wrapper.properties || err "wrapper properties has no distributionUrl"; }

# configuration sanity
get() { grep -E "^$1=" app-config.properties | tail -1 | cut -d= -f2-; }
mode=$(get LOAD_MODE); url=$(get START_URL)
case "$mode" in REMOTE|BUNDLED|HYBRID) ;; *) err "LOAD_MODE must be REMOTE, BUNDLED or HYBRID (got '$mode')";; esac
if [ "$mode" = "REMOTE" ]; then case "$url" in https://*) ;; *) err "START_URL must start with https:// (got '$url')";; esac; fi
if [ "$mode" != "REMOTE" ]; then
  entry=$(get BUNDLED_ENTRY); [ -f "app/src/main/assets/www/${entry:-index.html}" ] || err "bundled entry page not found in assets/www/"
fi

# XML well-formedness
python3 - <<'PY' || fail=1
import glob, sys, xml.etree.ElementTree as ET
bad = 0
for p in glob.glob('app/src/main/**/*.xml', recursive=True):
    try: ET.parse(p)
    except Exception as e: print(f"ERROR: malformed XML {p}: {e}"); bad = 1
sys.exit(bad)
PY

# secrets / forbidden files
while IFS= read -r f; do err "forbidden file committed: $f"; done < <(find . -path ./.git -prune -o -type f \( -name '*.jks' -o -name '*.keystore' -o -name '.env' -o -name 'local.properties' -o -name 'google-services.json' \) -print)
if grep -rIEn --exclude-dir=.git --exclude-dir=scripts --exclude-dir=docs --exclude='*.md' '(AKIA[0-9A-Z]{16}|-----BEGIN [A-Z ]*PRIVATE KEY-----|storePassword|keyPassword)' . ; then
  err "possible secret found (see lines above)"
fi

if [ "$fail" -eq 0 ]; then echo "validate-project: OK"; else echo "validate-project: FAILED"; fi
exit "$fail"
