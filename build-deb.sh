#!/bin/bash
# build-deb.sh - Rastgeletor .deb paketi oluşturma betiği
set -e

APP_NAME="rastgeletor"
VERSION="2.1.0"
ARCH="amd64"
# Staging dizinini WSL native fs'de oluştur (NTFS izin sorunundan kaçınmak için)
DEB_DIR="/tmp/rastgeletor-deb-staging"
PROJ_DIR="$(pwd)"

echo "==> Gradle ile uygulama derleniyor..."
# WSL ortamında gradle.properties içindeki Windows Java yolunu geçici olarak devre dışı bırak
PROPS_FILE="gradle.properties"
PROPS_BACKUP="gradle.properties.bak"

if grep -q "^org.gradle.java.home=" "$PROPS_FILE" 2>/dev/null; then
    cp "$PROPS_FILE" "$PROPS_BACKUP"
    grep -v "^org.gradle.java.home=" "$PROPS_FILE" > gradle.properties.tmp
    cp gradle.properties.tmp "$PROPS_FILE"
    rm -f gradle.properties.tmp
    GRADLE_JAVA_HOME_DISABLED=1
fi

./gradlew packageDeb
GRADLE_EXIT=$?

# gradle.properties'i geri yükle
if [ "${GRADLE_JAVA_HOME_DISABLED}" = "1" ]; then
    cp "$PROPS_BACKUP" "$PROPS_FILE"
    rm -f "$PROPS_BACKUP"
fi

[ $GRADLE_EXIT -ne 0 ] && exit $GRADLE_EXIT

GRADLE_DEB=$(find build/compose/binaries -name "*.deb" | head -1)
if [ -z "$GRADLE_DEB" ]; then
    echo "HATA: Gradle .deb çıktısı bulunamadı!" >&2
    exit 1
fi

echo "==> Gradle .deb bulundu: $GRADLE_DEB"
echo "==> Debian paket dizini hazırlanıyor..."

rm -rf "$DEB_DIR"

# Gradle .deb içeriğini WSL native /tmp altına çıkart
dpkg-deb -R "$GRADLE_DEB" "$DEB_DIR"

# --- DEBIAN/control dosyasını debian/ klasöründekiyle güncelle ---
cp "${PROJ_DIR}/debian/control" "${DEB_DIR}/DEBIAN/control"

# --- .desktop dosyasını yerleştir ---
mkdir -p "${DEB_DIR}/usr/share/applications"
cp "${PROJ_DIR}/debian/rastgeletor.desktop" "${DEB_DIR}/usr/share/applications/"

# --- İkonu yerleştir ---
mkdir -p "${DEB_DIR}/usr/share/icons/hicolor/256x256/apps"
cp "${PROJ_DIR}/src/commonMain/composeResources/drawable/app_icon.png" \
    "${DEB_DIR}/usr/share/icons/hicolor/256x256/apps/rastgeletor.png"

# --- Betikleri yerleştir ---
for script in postinst prerm postrm; do
    cp "${PROJ_DIR}/debian/${script}" "${DEB_DIR}/DEBIAN/${script}"
    chmod 0755 "${DEB_DIR}/DEBIAN/${script}"
done

# --- /usr/bin sembolik linki ---
mkdir -p "${DEB_DIR}/usr/bin"
ln -sf /opt/rastgeletor/bin/Rastgeletor "${DEB_DIR}/usr/bin/rastgeletor" 2>/dev/null || true

# --- NTFS mount kaynaklı izin sorununu düzelt ---
# Gradle .deb'inden gelen tüm DEBIAN/ betiklerini çalıştırılabilir yap
chmod 755 "${DEB_DIR}/DEBIAN"
find "${DEB_DIR}/DEBIAN" -type f -name "control" -o -type f -name "md5sums" | xargs -r chmod 644
find "${DEB_DIR}/DEBIAN" -type f ! -name "control" ! -name "md5sums" | xargs -r chmod 755

# --- Son .deb dosyasını oluştur ---
OUTPUT_DEB="${PROJ_DIR}/${APP_NAME}_${VERSION}_${ARCH}.deb"
echo "==> Paket oluşturuluyor: ${APP_NAME}_${VERSION}_${ARCH}.deb"
dpkg-deb --build --root-owner-group "$DEB_DIR" "$OUTPUT_DEB"

rm -rf "$DEB_DIR"

echo ""
echo "✓ Başarıyla oluşturuldu: ${OUTPUT_DEB}"
echo ""
echo "Kurulum için:"
echo "  sudo dpkg -i ${OUTPUT_DEB}"
echo "  sudo apt-get install -f   # eksik bağımlılıklar varsa"
