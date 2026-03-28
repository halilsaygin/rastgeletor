#!/bin/bash
set -e

echo "Derleme başlatılıyor..."
# Ensure Linux JDK is used in WSL to avoid Windows-style binaries
export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew packageReleaseAppImage

APP_DIR="build/AppDir"
SRC_APP="build/compose/binaries/main-release/app/Rastgeletor"

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
# Filename should match StartupWMClass for best compatibility with GNOME
cat << 'EOF' > "$APP_DIR/Rastgeletor.desktop"
[Desktop Entry]
Type=Application
Name=Rastgeletor
GenericName=Student Selector
Comment=Öğrenci Seçme ve Gruplandırma Aracı
Exec=Rastgeletor
Icon=Rastgeletor
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
cp "src/commonMain/composeResources/drawable/app_icon.png" "$APP_DIR/usr/share/icons/hicolor/512x512/apps/Rastgeletor.png"
cp "src/commonMain/composeResources/drawable/app_icon.png" "$APP_DIR/usr/share/pixmaps/Rastgeletor.png"
cp "src/commonMain/composeResources/drawable/app_icon.png" "$APP_DIR/Rastgeletor.png"

# .DirIcon olarak PNG kullan (en yüksek kalite)
cp "src/commonMain/composeResources/drawable/app_icon.png" "$APP_DIR/.DirIcon"

echo "AppImage paketleniyor..."
appimagetool "$APP_DIR" Rastgeletor-1.0.0-x86_64.AppImage || echo "appimagetool hatası: $?"

echo "Başarıyla tamamlandı!"
