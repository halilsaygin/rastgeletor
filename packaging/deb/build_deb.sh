#!/bin/bash
# build_deb.sh - Pardus ETAP (Debian) .deb packaging script
set -e

PACKAGE_NAME="rastgeletor"
VERSION="2.2.0"
ARCH="all"
PROJ_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
STAGING_DIR="/tmp/rastgeletor-deb-staging"
OUTPUT_DEB="${PROJ_DIR}/${PACKAGE_NAME}_${VERSION}_${ARCH}.deb"

echo "=== [1/4] Temizlik ve Hazırlık Yapılıyor ==="
rm -rf "$STAGING_DIR"
mkdir -p "${STAGING_DIR}/DEBIAN"
mkdir -p "${STAGING_DIR}/usr/bin"
mkdir -p "${STAGING_DIR}/usr/share/rastgeletor/app"
mkdir -p "${STAGING_DIR}/usr/share/applications"
mkdir -p "${STAGING_DIR}/usr/share/pixmaps"

echo "=== [2/4] Kaynak Dosyaları Yerleştiriliyor ==="
# DEBIAN kontrol dosyaları
cp "${PROJ_DIR}/packaging/deb/DEBIAN/"* "${STAGING_DIR}/DEBIAN/"
chmod 0755 "${STAGING_DIR}/DEBIAN/postinst" "${STAGING_DIR}/DEBIAN/prerm" "${STAGING_DIR}/DEBIAN/postrm"
chmod 0644 "${STAGING_DIR}/DEBIAN/control"

# Python uygulama kaynakları (/usr/share/rastgeletor/app)
cp -r "${PROJ_DIR}/src/app/"* "${STAGING_DIR}/usr/share/rastgeletor/app/"
rm -rf "${STAGING_DIR}/usr/share/rastgeletor/app"/**/__pycache__ 2>/dev/null || true

# /usr/bin/rastgeletor başlatıcı betiği
cat > "${STAGING_DIR}/usr/bin/rastgeletor" << 'EOF'
#!/bin/sh
export PYTHONPATH="/usr/share/rastgeletor:${PYTHONPATH}"
exec /usr/bin/python3 -m app.main "$@"
EOF
chmod 0755 "${STAGING_DIR}/usr/bin/rastgeletor"

# Geriye dönük uyumluluk için /usr/bin/rastgeletor linki


# Masaüstü dosyaları (.desktop)
cp "${PROJ_DIR}/data/rastgeletor.desktop" "${STAGING_DIR}/usr/share/applications/"
chmod 0644 "${STAGING_DIR}/usr/share/applications/"*.desktop

# Simgeler (Hicolor + Pixmaps)
ICON_SRC="${PROJ_DIR}/src/commonMain/composeResources/drawable/app_icon.png"
if [ ! -f "$ICON_SRC" ]; then
    ICON_SRC="${PROJ_DIR}/data/icons/rastgeletor.png"
fi

cp "$ICON_SRC" "${STAGING_DIR}/usr/share/pixmaps/rastgeletor.png"

for size in 16x16 24x24 32x32 48x48 64x64 128x128 256x256 512x512; do
    icon_dir="${STAGING_DIR}/usr/share/icons/hicolor/${size}/apps"
    mkdir -p "$icon_dir"
    cp "$ICON_SRC" "${icon_dir}/rastgeletor.png"
done

echo "=== [3/4] İzinler ve MD5 Sağlama Toplamı Düzenleniyor ==="
find "${STAGING_DIR}" -type d -exec chmod 755 {} +
find "${STAGING_DIR}/usr/share" -type f -exec chmod 644 {} +
chmod 755 "${STAGING_DIR}/usr/bin/rastgeletor"
chmod 755 "${STAGING_DIR}/DEBIAN/postinst" "${STAGING_DIR}/DEBIAN/prerm" "${STAGING_DIR}/DEBIAN/postrm"

(cd "$STAGING_DIR" && find usr -type f -exec md5sum {} + > DEBIAN/md5sums 2>/dev/null || true)
chmod 644 "${STAGING_DIR}/DEBIAN/md5sums"

echo "=== [4/4] .deb Paketi Üretiliyor ==="
if command -v dpkg-deb >/dev/null 2>&1; then
    dpkg-deb --build --root-owner-group "$STAGING_DIR" "$OUTPUT_DEB"
else
    echo "dpkg-deb bulunamadı, standart ar ve tar ile debian arşivi üretiliyor..."
    BUILD_TMP="/tmp/deb-archive-tmp"
    rm -rf "$BUILD_TMP"
    mkdir -p "$BUILD_TMP"

    echo "2.0" > "${BUILD_TMP}/debian-binary"
    (cd "${STAGING_DIR}/DEBIAN" && tar --owner=0 --group=0 -czf "${BUILD_TMP}/control.tar.gz" .)
    (cd "${STAGING_DIR}" && tar --owner=0 --group=0 -czf "${BUILD_TMP}/data.tar.gz" usr)

    (cd "$BUILD_TMP" && ar -rcs "$OUTPUT_DEB" debian-binary control.tar.gz data.tar.gz)
    rm -rf "$BUILD_TMP"
fi

# Uyumluluk linki


rm -rf "$STAGING_DIR"

echo ""
echo "✓ Paket başarıyla oluşturuldu: $OUTPUT_DEB"
echo "  Boyut: $(du -sh "$OUTPUT_DEB" | cut -f1)"
echo ""
echo "Pardus ETAP üzerinde kurulum:"
echo "  sudo dpkg -i $(basename "$OUTPUT_DEB")"
echo "  sudo apt-get install -f   # (Eğer sistem kütüphaneleri eksikse)"
