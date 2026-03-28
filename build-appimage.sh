#!/bin/bash
set -e

echo "Derleme başlatılıyor..."
./gradlew packageReleaseAppImage

APP_DIR="build/AppDir"
SRC_APP="build/compose/binaries/main-release/app/rastgeletor"

echo "AppDir hazırlanıyor..."
rm -rf "$APP_DIR"
mkdir -p "$APP_DIR/usr"
mkdir -p "$APP_DIR/usr/share/icons/hicolor/512x512/apps"
mkdir -p "$APP_DIR/usr/share/pixmaps"

# Dosyaları usr içine aktar
cp -r "$SRC_APP/"* "$APP_DIR/usr/"

# AppRun oluştur (geliştirilmiş)
cat << 'EOF' > "$APP_DIR/AppRun"
#!/bin/sh
HERE="$(dirname "$(readlink -f "${0}")")"
export PATH="${HERE}/usr/bin:${PATH}"
export LD_LIBRARY_PATH="${HERE}/usr/lib:${LD_LIBRARY_PATH}"
export XDG_DATA_DIRS="${HERE}/usr/share:${XDG_DATA_DIRS}"
# Force Java to use proper WM_CLASS
export _JAVA_AWT_WM_NONREPARENTING=1
export AWT_TOOLKIT=MToolkit
# Case-sensitive execution fix (binary name depends on packageName/mainClass)
exec "${HERE}/usr/bin/Rastgeletor" "$@"
EOF

chmod +x "$APP_DIR/AppRun"

# Desktop Entry (geliştirilmiş)
cat << 'EOF' > "$APP_DIR/rastgeletor.desktop"
[Desktop Entry]
Type=Application
Name=Rastgeletor
GenericName=Student Selector
Comment=Öğrenci Seçme ve Gruplandırma Aracı
Exec=rastgeletor
Icon=rastgeletor
Categories=Education;
Keywords=student;random;selection;grouping;education;
StartupNotify=true
# Matches the WM_CLASS set in main.kt (sun.java2d.wm.classname)
StartupWMClass=Rastgeletor
X-GNOME-FullName=Rastgeletor
X-GNOME-UsesNotifications=true
MimeType=
EOF

# İkonları çoğalt (PNG olarak)
cp "src/main/resources/app_icon.png" "$APP_DIR/usr/share/icons/hicolor/512x512/apps/rastgeletor.png"
cp "src/main/resources/app_icon.png" "$APP_DIR/usr/share/pixmaps/rastgeletor.png"
cp "src/main/resources/app_icon.png" "$APP_DIR/rastgeletor.png"

# .DirIcon olarak PNG kullan (en yüksek kalite)
cp "src/main/resources/app_icon.png" "$APP_DIR/.DirIcon"

echo "AppImage paketleniyor..."
appimagetool "$APP_DIR" rastgeletor-1.0.0-x86_64.AppImage || echo "appimagetool hatası: $?"

echo "Başarıyla tamamlandı!"
