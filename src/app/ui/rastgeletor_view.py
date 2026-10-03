import random
from typing import Callable, List
import gi

gi.require_version("Gtk", "4.0")
from gi.repository import Gtk, Gdk, GLib
from app.logic import Ogrenci, OgrenciRepository
from .dialogs import show_secim_ayarlari_dialog


class RastgeletorView(Gtk.Box):
    def __init__(self, on_navigate: Callable[[str], None], get_toplevel: Callable[[], Gtk.Window]):
        super().__init__(orientation=Gtk.Orientation.VERTICAL, spacing=0)
        self.on_navigate = on_navigate
        self.get_toplevel = get_toplevel

        # State
        self.liste_modu = "eksilen_liste"
        self.cinsiyet_modu = "tamami"
        self.ogrenciler: List[Ogrenci] = []
        self.secilen_isim = "Seçim Bekleniyor"
        self.onceki_indeks = -1

        self._build_ui()
        self.refresh_student_pool()

    def _build_ui(self) -> None:
        self.set_hexpand(True)
        self.set_vexpand(True)

        # 1. Top Header Bar (CenterBox)
        header = Gtk.CenterBox()
        header.set_margin_start(32)
        header.set_margin_end(32)
        header.set_margin_top(8)
        header.set_margin_bottom(16)

        list_btn = Gtk.Button(label="Öğrenci Listesi")
        list_btn.add_css_class("touch-btn")
        list_btn.add_css_class("touch-btn-outline")
        list_btn.connect("clicked", lambda _: self.on_navigate("ogrenci_list"))
        header.set_start_widget(list_btn)

        ayarlar_btn = Gtk.Button(label="Seçim Ayarları")
        ayarlar_btn.add_css_class("touch-btn-flat")
        ayarlar_btn.connect("clicked", self._on_ayarlar_clicked)
        header.set_center_widget(ayarlar_btn)

        grup_btn = Gtk.Button(label="Gruplayıcı")
        grup_btn.add_css_class("touch-btn")
        grup_btn.add_css_class("touch-btn-outline")
        grup_btn.connect("clicked", lambda _: self.on_navigate("gruplandirma"))
        header.set_end_widget(grup_btn)

        self.append(header)

        # 2. Main Center Body
        center_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=24)
        center_box.set_vexpand(True)
        center_box.set_hexpand(True)
        center_box.set_valign(Gtk.Align.CENTER)
        center_box.set_halign(Gtk.Align.CENTER)

        # "SEÇİLEN ÖĞRENCİ" indicator
        subtitle = Gtk.Label(label="S E Ç İ L E N   Ö Ğ R E N C İ")
        subtitle.add_css_class("sub-header-label")
        center_box.append(subtitle)

        # Big name label
        self.name_label = Gtk.Label(label=self.secilen_isim)
        self.name_label.add_css_class("selected-student-text")
        self.name_label.set_justify(Gtk.Justification.CENTER)
        self.name_label.set_wrap(True)
        self.name_label.set_max_width_chars(30)
        center_box.append(self.name_label)

        # Dice button
        self.dice_button = Gtk.Button()
        self.dice_button.add_css_class("dice-button")
        self.dice_button.set_halign(Gtk.Align.CENTER)

        dice_label = Gtk.Label(label="⚄")
        dice_label.add_css_class("dice-symbol")
        self.dice_button.set_child(dice_label)
        self.dice_button.connect("clicked", lambda _: self.sec())
        center_box.append(self.dice_button)

        self.append(center_box)

        # 3. Bottom Status Pill
        bottom_box = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL)
        bottom_box.set_halign(Gtk.Align.CENTER)
        bottom_box.set_margin_bottom(32)

        self.status_pill = Gtk.Label(label="")
        self.status_pill.add_css_class("status-pill")
        bottom_box.append(self.status_pill)

        self.append(bottom_box)
        self._update_status_pill()

    def refresh_student_pool(self, reset_selection: bool = True) -> None:
        """Loads and shuffles students according to the selected gender mode."""
        self.ogrenciler = OgrenciRepository.karisik_liste(self.cinsiyet_modu)
        self.onceki_indeks = -1
        if reset_selection:
            self.secilen_isim = "Seçim Bekleniyor"
            self.name_label.set_label(self.secilen_isim)
        self._update_status_pill()

    def sec(self) -> None:
        """Selects a student at random or from shrinking list."""
        if self.ogrenciler:
            if self.liste_modu == "eksilen_liste":
                ogrenci = self.ogrenciler.pop(0)
                self.secilen_isim = ogrenci.ad_soyad
            else:
                if len(self.ogrenciler) > 1:
                    idx = random.randrange(len(self.ogrenciler))
                    while idx == self.onceki_indeks:
                        idx = random.randrange(len(self.ogrenciler))
                else:
                    idx = 0
                self.secilen_isim = self.ogrenciler[idx].ad_soyad
                self.onceki_indeks = idx
        else:
            self.secilen_isim = "LİSTE BOŞ"

        self.name_label.set_label(self.secilen_isim)
        self._update_status_pill()

    def _update_status_pill(self) -> None:
        mod_text = "Eksilen Liste" if self.liste_modu == "eksilen_liste" else "Sabit Liste"
        if self.cinsiyet_modu == "erkek":
            cinsiyet_text = "Sadece Erkekler"
        elif self.cinsiyet_modu == "kiz":
            cinsiyet_text = "Sadece Kızlar"
        else:
            cinsiyet_text = "Tüm Sınıf"

        kalan_metin = f"  •  Kalan: {len(self.ogrenciler)}" if self.liste_modu == "eksilen_liste" else ""
        self.status_pill.set_label(f"{mod_text}  •  {cinsiyet_text}{kalan_metin}")

    def _on_ayarlar_clicked(self, _) -> None:
        def on_onayla(yeni_liste_modu: str, yeni_cinsiyet_modu: str):
            self.liste_modu = yeni_liste_modu
            self.cinsiyet_modu = yeni_cinsiyet_modu
            self.refresh_student_pool(reset_selection=True)

        show_secim_ayarlari_dialog(
            parent=self.get_toplevel(),
            mevcut_liste_modu=self.liste_modu,
            mevcut_cinsiyet_modu=self.cinsiyet_modu,
            on_onayla=on_onayla,
        )
