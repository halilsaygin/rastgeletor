#!/usr/bin/env python3
import os
import sys
import logging
from pathlib import Path
from datetime import datetime

# Ensure project root / src is in sys.path
_src_dir = Path(__file__).resolve().parent.parent
if str(_src_dir) not in sys.path:
    sys.path.insert(0, str(_src_dir))

import gi
gi.require_version("Gtk", "4.0")
from gi.repository import Gtk, Gdk, Gio, GLib

from app.ui import MainWindow


def setup_logging() -> logging.Logger:
    """Configures application logging adhering to XDG standards."""
    user_home = Path.home()
    xdg_data = os.environ.get("XDG_DATA_HOME")
    base_dir = Path(xdg_data) if xdg_data else user_home / ".local" / "share"
    log_dir = base_dir / "Rastgeletor" / "logs"
    log_dir.mkdir(parents=True, exist_ok=True)

    timestamp = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
    log_file = log_dir / f"rastgeletor_{timestamp}.log"

    logger = logging.getLogger("Rastgeletor")
    logger.setLevel(logging.INFO)

    file_handler = logging.FileHandler(str(log_file), encoding="utf-8")
    file_formatter = logging.Formatter("[%(asctime)s] [%(levelname)s] %(message)s")
    file_handler.setFormatter(file_formatter)
    logger.addHandler(file_handler)

    console_handler = logging.StreamHandler(sys.stdout)
    console_handler.setFormatter(file_formatter)
    logger.addHandler(console_handler)

    logger.info("=== Rastgeletör / ETAP App başlatıldı ===")
    logger.info("Python Sürümü: %s", sys.version)
    logger.info("GTK Sürümü: %d.%d.%d", Gtk.get_major_version(), Gtk.get_minor_version(), Gtk.get_micro_version())
    logger.info("DISPLAY: %s", os.environ.get("DISPLAY", "(yok)"))
    logger.info("WAYLAND_DISPLAY: %s", os.environ.get("WAYLAND_DISPLAY", "(yok)"))
    logger.info("XDG_CURRENT_DESKTOP: %s", os.environ.get("XDG_CURRENT_DESKTOP", "(yok)"))
    logger.info("Log dosyası: %s", log_file)

    return logger


class EtapApplication(Gtk.Application):
    def __init__(self, logger: logging.Logger):
        super().__init__(
            application_id="com.halilsaygin.rastgeletor",
            flags=Gio.ApplicationFlags.FLAGS_NONE
        )
        self.logger = logger
        self.window: MainWindow | None = None

    def do_startup(self):
        Gtk.Application.do_startup(self)
        self._load_css()

    def do_activate(self):
        if not self.window:
            self.window = MainWindow(application=self)
        self.window.present()
        self.logger.info("Uygulama penceresi görüntülendi.")

    def _load_css(self):
        css_provider = Gtk.CssProvider()
        css_file = Path(__file__).resolve().parent / "ui" / "style.css"
        if css_file.exists():
            try:
                css_provider.load_from_path(str(css_file))
                display = Gdk.Display.get_default()
                if display:
                    Gtk.StyleContext.add_provider_for_display(
                        display,
                        css_provider,
                        Gtk.STYLE_PROVIDER_PRIORITY_APPLICATION
                    )
                    self.logger.info("CSS teması başarıyla yüklendi: %s", css_file)
            except Exception as e:
                self.logger.error("CSS teması yüklenemedi: %s", e)


def main():
    logger = setup_logging()
    app = EtapApplication(logger)
    exit_status = app.run(sys.argv)
    sys.exit(exit_status)


if __name__ == "__main__":
    main()
