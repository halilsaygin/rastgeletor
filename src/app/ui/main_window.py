import os
from pathlib import Path
import gi

gi.require_version("Gtk", "4.0")
from gi.repository import Gtk, Gdk
from .rastgeletor_view import RastgeletorView
from .ogrenci_list_view import OgrenciListView
from .gruplandirma_view import GruplandirmaView


class MainWindow(Gtk.ApplicationWindow):
    def __init__(self, application: Gtk.Application):
        super().__init__(application=application)
        self.set_title("Rastgeletör")
        self.set_default_size(800, 620)

        # Let the OS draw the window decorations (HeaderBar removed to save space)

        # Set App Window Icon if exists
        self._setup_icon()

        # Navigation Stack
        self.stack = Gtk.Stack()
        self.stack.set_transition_type(Gtk.StackTransitionType.SLIDE_LEFT_RIGHT)
        self.stack.set_transition_duration(250)

        # Subviews
        self.rastgeletor_view = RastgeletorView(
            on_navigate=self.navigate_to,
            get_toplevel=lambda: self
        )
        self.ogrenci_list_view = OgrenciListView(
            on_back=lambda: self.navigate_to("rastgeletor"),
            get_toplevel=lambda: self
        )
        self.gruplandirma_view = GruplandirmaView(
            on_back=lambda: self.navigate_to("rastgeletor"),
            get_toplevel=lambda: self
        )

        self.stack.add_named(self.rastgeletor_view, "rastgeletor")
        self.stack.add_named(self.ogrenci_list_view, "ogrenci_list")
        self.stack.add_named(self.gruplandirma_view, "gruplandirma")

        self.set_child(self.stack)
        self.navigate_to("rastgeletor")

        # Global Key Event Controller
        key_controller = Gtk.EventControllerKey.new()
        key_controller.set_propagation_phase(Gtk.PropagationPhase.CAPTURE)
        key_controller.connect("key-pressed", self._on_key_pressed)
        self.add_controller(key_controller)

    def navigate_to(self, screen_name: str) -> None:
        current_name = self.stack.get_visible_child_name()
        if screen_name == "rastgeletor" and current_name != "rastgeletor":
            # Refresh student pool in case students were modified
            self.rastgeletor_view.refresh_student_pool(reset_selection=False)
        elif screen_name == "ogrenci_list":
            self.ogrenci_list_view.refresh_list()

        self.stack.set_visible_child_name(screen_name)
        if screen_name == "rastgeletor":
            self.set_title("Rastgeletör")
        elif screen_name == "ogrenci_list":
            self.set_title("Sınıf Listesi")
        elif screen_name == "gruplandirma":
            self.set_title("Gruplayıcı")

    def _on_key_pressed(self, controller, keyval, keycode, state) -> bool:
        current_screen = self.stack.get_visible_child_name()

        # Enter triggers selection on Rastgeletor screen
        if current_screen == "rastgeletor":
            if keyval in (Gdk.KEY_Return, Gdk.KEY_KP_Enter):
                self.rastgeletor_view.dice_button.emit("clicked")
                return True

        # Left/Right arrow keys navigate groups on Gruplandirma screen
        elif current_screen == "gruplandirma":
            if keyval == Gdk.KEY_Left:
                self.gruplandirma_view.onceki_grup()
                return True
            elif keyval == Gdk.KEY_Right:
                self.gruplandirma_view.sonraki_grup()
                return True

        # Escape navigates back to main screen
        if keyval == Gdk.KEY_Escape and current_screen != "rastgeletor":
            self.navigate_to("rastgeletor")
            return True

        return False

    def _setup_icon(self) -> None:
        icon_theme = Gtk.IconTheme.get_for_display(Gdk.Display.get_default())
        
        # 1. Add local data/icons to search path for development/source runs
        try:
            base_dir = Path(__file__).resolve().parent.parent.parent.parent
            local_icon_dir = base_dir / "data" / "icons"
            if local_icon_dir.exists():
                icon_theme.add_search_path(str(local_icon_dir))
        except Exception:
            pass

        # 2. Try to set the icon
        icon_names = ["rastgeletor", "com.halilsaygin.rastgeletor"]
        for name in icon_names:
            if icon_theme.has_icon(name):
                self.set_icon_name(name)
                Gtk.Window.set_default_icon_name(name)
                break
