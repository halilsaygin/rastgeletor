#!/bin/bash
# build_appimage.sh - AppImage build script for Rastgeletör / ETAP App
set -e

PROJ_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CONTROL_FILE="${PROJ_DIR}/packaging/deb/DEBIAN/control"

# DEBIAN/control dosyasından verileri dinamik çekme
RAW_NAME="$(grep -i '^Package:' "$CONTROL_FILE" | awk '{print $2}')"
# İlk harfi büyüt (rastgeletor -> Rastgeletor)
APP_NAME="$(tr '[:lower:]' '[:upper:]' <<< ${RAW_NAME:0:1})${RAW_NAME:1}"
VERSION="$(grep -i '^Version:' "$CONTROL_FILE" | awk '{print $2}')"
APP_DIR="${PROJ_DIR}/build/AppDir"
OUTPUT_NAME="${APP_NAME}-${VERSION}-x86_64.AppImage"

echo "=== [1/3] AppDir Hazırlanıyor ==="
rm -rf "$APP_DIR"
mkdir -p "${APP_DIR}/usr/bin"
mkdir -p "${APP_DIR}/usr/share/rastgeletor/app"
mkdir -p "${APP_DIR}/usr/share/applications"
mkdir -p "${APP_DIR}/usr/share/icons/hicolor/256x256/apps"
mkdir -p "${APP_DIR}/usr/share/icons/hicolor/512x512/apps"
mkdir -p "${APP_DIR}/usr/share/pixmaps"

# AppRun
cp "${PROJ_DIR}/packaging/appimage/AppRun" "${APP_DIR}/AppRun"
chmod 0755 "${APP_DIR}/AppRun"

# Python uygulama kaynakları
cp -r "${PROJ_DIR}/src/app/"* "${APP_DIR}/usr/share/rastgeletor/app/"
rm -rf "${APP_DIR}/usr/share/rastgeletor/app"/**/__pycache__ 2>/dev/null || true

# Masaüstü dosyası ve simgeler (AppDir köküne ve usr/share'e)
cp "${PROJ_DIR}/data/rastgeletor.desktop" "${APP_DIR}/rastgeletor.desktop"
cp "${PROJ_DIR}/data/rastgeletor.desktop" "${APP_DIR}/usr/share/applications/rastgeletor.desktop"

ICON_SRC="${PROJ_DIR}/src/commonMain/composeResources/drawable/app_icon.png"
if [ ! -f "$ICON_SRC" ]; then
    ICON_SRC="${PROJ_DIR}/data/icons/rastgeletor.png"
fi

cp "$ICON_SRC" "${APP_DIR}/rastgeletor.png"
cp "$ICON_SRC" "${APP_DIR}/.DirIcon"
cp "$ICON_SRC" "${APP_DIR}/usr/share/pixmaps/rastgeletor.png"

echo "=== [2/3] appimagetool Kontrolü ==="
APPIMAGETOOL=""
if command -v appimagetool >/dev/null 2>&1; then
    APPIMAGETOOL="appimagetool"
elif [ -f "${PROJ_DIR}/appimagetool-x86_64.AppImage" ]; then
    chmod +x "${PROJ_DIR}/appimagetool-x86_64.AppImage"
    APPIMAGETOOL="${PROJ_DIR}/appimagetool-x86_64.AppImage"
else
    echo "appimagetool bulunamadı. İndiriliyor..."
    if command -v curl >/dev/null 2>&1; then
        curl -L -o "${PROJ_DIR}/appimagetool-x86_64.AppImage" "https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-x86_64.AppImage"
        chmod +x "${PROJ_DIR}/appimagetool-x86_64.AppImage"
        APPIMAGETOOL="${PROJ_DIR}/appimagetool-x86_64.AppImage"
    elif command -v wget >/dev/null 2>&1; then
        wget -q -O "${PROJ_DIR}/appimagetool-x86_64.AppImage" "https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-x86_64.AppImage"
        chmod +x "${PROJ_DIR}/appimagetool-x86_64.AppImage"
        APPIMAGETOOL="${PROJ_DIR}/appimagetool-x86_64.AppImage"
    else
        echo "UYARI: curl veya wget bulunamadı, AppDir hazırlandı fakat .AppImage dosyası oluşturulamadı."
        echo "Lütfen appimagetool kurun: pacman -S appimagetool veya https://appimage.github.io/"
        exit 1
    fi
fi

echo "=== [3/3] AppImage Paketleniyor ==="
export ARCH="x86_64"

# Eğer FUSE desteklenmiyorsa appimagetool'u extract edip çalıştır
if [ -n "$APPIMAGETOOL" ]; then
    if ! "$APPIMAGETOOL" --version >/dev/null 2>&1; then
        export APPIMAGE_EXTRACT_AND_RUN=1
    fi
    "$APPIMAGETOOL" --no-appstream "$APP_DIR" "${PROJ_DIR}/${OUTPUT_NAME}"
fi

echo ""
echo "✓ AppImage başarıyla üretildi: ${PROJ_DIR}/${OUTPUT_NAME}"
echo "Çalıştırmak için:"
echo "  ./${OUTPUT_NAME}"
echo ""
echo "FUSE yüklü olmayan sistemler için:"
echo "  ./${OUTPUT_NAME} --appimage-extract-and-run"
