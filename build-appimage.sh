#!/bin/bash
# Rastgeletor AppImage Build Script
# Pardus ETAP 23 (Debian tabanlı, XFCE/GNOME, X11/Wayland) uyumlu
set -e

echo "=== Rastgeletor AppImage Build ==="
echo "Tarih: $(date)"

# --- Java ortamı ---
# WSL veya Linux build ortamı için doğru JDK'yı bul
if [ -d "/usr/lib/jvm/java-21-openjdk-amd64" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
elif [ -d "/usr/lib/jvm/java-21-openjdk" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-21-openjdk"
elif [ -d "/usr/lib/jvm/temurin-21" ]; then
    export JAVA_HOME="/usr/lib/jvm/temurin-21"
else
    echo "[UYARI] Java 21 bulunamadı, JAVA_HOME mevcut değer kullanılıyor: $JAVA_HOME"
fi
export PATH="$JAVA_HOME/bin:$PATH"
echo "JAVA_HOME: $JAVA_HOME"
java -version

# --- appimagetool kontrolü ---
APPIMAGETOOL=""
if command -v appimagetool &>/dev/null; then
    APPIMAGETOOL="appimagetool"
elif [ -f "./appimagetool-x86_64.AppImage" ]; then
    chmod +x ./appimagetool-x86_64.AppImage
    APPIMAGETOOL="./appimagetool-x86_64.AppImage"
else
    echo "[BİLGİ] appimagetool bulunamadı, indiriliyor..."
    wget -q "https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-x86_64.AppImage" \
        -O appimagetool-x86_64.AppImage
    chmod +x appimagetool-x86_64.AppImage
    APPIMAGETOOL="./appimagetool-x86_64.AppImage"
fi
echo "appimagetool: $APPIMAGETOOL"

# --- Gradle build ---
echo ""
echo "[1/4] Gradle ile uygulama derleniyor..."
./gradlew packageReleaseAppImage --info 2>&1 | tail -30

APP_DIR="build/AppDir"
SRC_APP="build/compose/binaries/main-release/app/Rastgeletor"

if [ ! -d "$SRC_APP" ]; then
    echo "[HATA] Derleme çıktısı bulunamadı: $SRC_APP"
    exit 1
fi

# --- AppDir hazırlama ---
echo ""
echo "[2/4] AppDir hazırlanıyor..."
rm -rf "$APP_DIR"
mkdir -p "$APP_DIR/usr/bin"
mkdir -p "$APP_DIR/usr/lib"
mkdir -p "$APP_DIR/usr/share/applications"
mkdir -p "$APP_DIR/usr/share/icons/hicolor/256x256/apps"
mkdir -p "$APP_DIR/usr/share/icons/hicolor/512x512/apps"
mkdir -p "$APP_DIR/usr/share/pixmaps"
mkdir -p "$APP_DIR/usr/share/rastgeletor"

# Uygulama dosyalarını kopyala
cp -r "$SRC_APP/"* "$APP_DIR/usr/"

# --- AppRun (Pardus ETAP 23 uyumlu) ---
echo "[2/4] AppRun oluşturuluyor..."
cat > "$APP_DIR/AppRun" << 'APPRUN_EOF'
#!/bin/sh
# Rastgeletor AppRun - Pardus ETAP 23 uyumlu
HERE="$(dirname "$(readlink -f "${0}")")"

# --- Temel PATH ---
export PATH="${HERE}/usr/bin:${PATH}"
export LD_LIBRARY_PATH="${HERE}/usr/lib:${LD_LIBRARY_PATH}"
export XDG_DATA_DIRS="${HERE}/usr/share:${XDG_DATA_DIRS:-/usr/local/share:/usr/share}"

# --- Log dizini ---
LOG_DIR="${HOME}/.local/share/Rastgeletor/logs"
mkdir -p "$LOG_DIR"
LAUNCH_LOG="${LOG_DIR}/launch_$(date +%Y-%m-%d_%H-%M-%S).log"

log() {
    echo "[$(date '+%H:%M:%S')] $*" | tee -a "$LAUNCH_LOG"
}

log "=== Rastgeletor başlatma logu ==="
log "AppImage konumu: ${HERE}"
log "Kullanıcı: $(whoami)"
log "OS: $(uname -a)"
log "DISPLAY: ${DISPLAY:-'(yok)'}"
log "XDG_SESSION_TYPE: ${XDG_SESSION_TYPE:-'(yok)'}"
log "XDG_CURRENT_DESKTOP: ${XDG_CURRENT_DESKTOP:-'(yok)'}"
log "DBUS_SESSION_BUS_ADDRESS: ${DBUS_SESSION_BUS_ADDRESS:-'(yok)'}"

# --- DISPLAY kontrolü ---
# Cinnamon X11 tabanlıdır, DISPLAY mutlaka set olmalı
if [ -z "$DISPLAY" ]; then
    log "UYARI: DISPLAY değişkeni yok, :0 deneniyor..."
    export DISPLAY=":0"
fi

# --- Cinnamon / X11 ortam ayarları ---
# Mutter tabanlı WM — non-reparenting gerekmez, kaldırıldı
unset _JAVA_AWT_WM_NONREPARENTING

# MToolkit kaldırıldı, XToolkit kullan
unset AWT_TOOLKIT

# GTK scaling (akıllı tahta HiDPI ekranları için)
export GDK_SCALE="${GDK_SCALE:-1}"
export GDK_DPI_SCALE="${GDK_DPI_SCALE:-1}"

# GTK3 tema entegrasyonu — Cinnamon temasını kullan
export GTK_THEME="${GTK_THEME:-}"

# Java rendering — Cinnamon/X11 için optimize
export _JAVA_OPTIONS="\
  -Dawt.useSystemAAFontSettings=lcd \
  -Dswing.aatext=true \
  -Dsun.java2d.xrender=true \
  -Dsun.java2d.opengl=false \
  -Djava.awt.headless=false \
  -Dsun.java2d.wm.classname=Rastgeletor \
  ${_JAVA_OPTIONS}"

log "Uygulama başlatılıyor: ${HERE}/usr/bin/Rastgeletor"
log "Log dosyası: ${LAUNCH_LOG}"
log "---"

# Uygulamayı çalıştır — hem terminale hem log dosyasına yaz
exec "${HERE}/usr/bin/Rastgeletor" "$@" 2>&1 | tee -a "$LAUNCH_LOG"
APPRUN_EOF

chmod +x "$APP_DIR/AppRun"

# --- Desktop Entry ---
echo "[2/4] Desktop entry oluşturuluyor..."
cat > "$APP_DIR/rastgeletor.desktop" << 'DESKTOP_EOF'
[Desktop Entry]
Type=Application
Version=1.0
Name=Rastgeletor
GenericName=Öğrenci Seçici
Comment=Öğrenci Seçme ve Gruplandırma Aracı
Exec=Rastgeletor
Icon=rastgeletor
Categories=Education;Utility;
Keywords=öğrenci;rastgele;seçim;gruplama;eğitim;student;random;
StartupNotify=true
StartupWMClass=Rastgeletor
X-GNOME-FullName=Rastgeletor
Terminal=false
DESKTOP_EOF

# Desktop entry'yi usr/share/applications'a da kopyala
cp "$APP_DIR/rastgeletor.desktop" "$APP_DIR/usr/share/applications/rastgeletor.desktop"

# --- İkonlar ---
echo "[2/4] İkonlar kopyalanıyor..."
ICON_SRC="src/commonMain/composeResources/drawable/app_icon.png"
cp "$ICON_SRC" "$APP_DIR/rastgeletor.png"
cp "$ICON_SRC" "$APP_DIR/.DirIcon"
cp "$ICON_SRC" "$APP_DIR/usr/share/pixmaps/rastgeletor.png"
cp "$ICON_SRC" "$APP_DIR/usr/share/icons/hicolor/512x512/apps/rastgeletor.png"
cp "$ICON_SRC" "$APP_DIR/usr/share/icons/hicolor/256x256/apps/rastgeletor.png"

# --- AppImage oluştur ---
echo ""
echo "[3/4] AppImage paketleniyor..."
OUTPUT_NAME="Rastgeletor-1.0.0-x86_64.AppImage"

# ARCH belirt (appimagetool için gerekli)
export ARCH="x86_64"

$APPIMAGETOOL --no-appstream "$APP_DIR" "$OUTPUT_NAME" 2>&1

if [ -f "$OUTPUT_NAME" ]; then
    chmod +x "$OUTPUT_NAME"
    SIZE=$(du -sh "$OUTPUT_NAME" | cut -f1)
    echo ""
    echo "[4/4] Tamamlandı!"
    echo "Çıktı: $(pwd)/$OUTPUT_NAME ($SIZE)"
    echo ""
    echo "Log konumu: ~/.local/share/Rastgeletor/logs/"
    echo "Çalıştırmak için: ./$OUTPUT_NAME"
    echo ""
    echo "Pardus ETAP'ta sorun yaşarsanız:"
    echo "  DISPLAY=:0 ./$OUTPUT_NAME"
    echo "  veya"
    echo "  GDK_BACKEND=x11 ./$OUTPUT_NAME"
else
    echo "[HATA] AppImage oluşturulamadı!"
    exit 1
fi
