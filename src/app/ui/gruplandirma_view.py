from typing import Callable, List
import gi

gi.require_version("Gtk", "4.0")
from gi.repository import Gtk
from app.logic import Ogrenci, OgrenciRepository
from .dialogs import show_alert_dialog, show_grup_ayarlari_dialog


class GruplandirmaView(Gtk.Box):
    def __init__(self, on_back: Callable[[], None], get_toplevel: Callable[[], Gtk.Window]):
        super().__init__(orientation=Gtk.Orientation.VERTICAL, spacing=0)
        self.on_back = on_back
        self.get_toplevel = get_toplevel

        self.gruplar: List[List[Ogrenci]] = []
        self.aktif_grup_index = 0

        self._build_ui()

    def _build_ui(self) -> None:
        self.set_hexpand(True)
        self.set_vexpand(True)

        # 1. Top Header Bar
        header = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=16)
        header.set_margin_start(32)
        header.set_margin_end(32)
        header.set_margin_top(8)
        header.set_margin_bottom(16)

        back_btn = Gtk.Button()
        back_btn.add_css_class("back-btn")
        back_btn.set_halign(Gtk.Align.START)
        back_btn.set_valign(Gtk.Align.CENTER)
        
        btn_box = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=8)
        btn_box.set_valign(Gtk.Align.CENTER)
        
        icon = Gtk.Image.new_from_icon_name("go-previous-symbolic")
        icon.set_pixel_size(18)
        
        lbl = Gtk.Label(label="Geri")
        lbl.set_valign(Gtk.Align.CENTER)
        
        btn_box.append(icon)
        btn_box.append(lbl)
        back_btn.set_child(btn_box)
        back_btn.connect("clicked", lambda _: self.on_back())
        header.append(back_btn)

        title_label = Gtk.Label(label="Gruplayıcı")
        title_label.add_css_class("app-header-title")
        title_label.set_halign(Gtk.Align.CENTER)
        title_label.set_hexpand(True)
        header.append(title_label)

        yeni_grup_btn = Gtk.Button(label="Yeni Gruplandırma")
        yeni_grup_btn.add_css_class("touch-btn")
        yeni_grup_btn.add_css_class("touch-btn-outline")
        yeni_grup_btn.set_halign(Gtk.Align.END)
        yeni_grup_btn.connect("clicked", self._on_yeni_grup_clicked)
        header.append(yeni_grup_btn)

        self.append(header)

        # 2. Main Content Box (Stacked empty placeholder vs carousel)
        self.main_content = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=20)
        self.main_content.set_vexpand(True)
        self.main_content.set_hexpand(True)
        self.main_content.set_valign(Gtk.Align.CENTER)
        self.main_content.set_halign(Gtk.Align.CENTER)

        # Empty State
        self.empty_label = Gtk.Label(label="Gruplandırma başlatmak için sağ üstten 'Yeni Gruplandırma' seçin.")
        self.empty_label.add_css_class("dialog-text")
        self.empty_label.set_margin_top(40)
        self.empty_label.set_margin_bottom(40)
        self.main_content.append(self.empty_label)

        # Active Group Container (hidden initially)
        self.group_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=20)
        self.group_box.set_visible(False)
        self.group_box.set_halign(Gtk.Align.CENTER)

        self.group_title = Gtk.Label(label="G R U P   1")
        self.group_title.add_css_class("sub-header-label")
        self.group_box.append(self.group_title)

        # Carousel Row: [Left Button] [Student Cards Card] [Right Button]
        carousel = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=24)
        carousel.set_halign(Gtk.Align.CENTER)
        carousel.set_valign(Gtk.Align.CENTER)

        self.left_btn = Gtk.Button(label="◀")
        self.left_btn.add_css_class("group-nav-btn")
        self.left_btn.connect("clicked", lambda _: self.onceki_grup())
        carousel.append(self.left_btn)

        # Center Card
        card = Gtk.Box(orientation=Gtk.Orientation.VERTICAL)
        card.add_css_class("card-box")
        card.set_size_request(440, 340)

        scrolled = Gtk.ScrolledWindow()
        scrolled.set_vexpand(True)
        scrolled.set_hexpand(True)
        scrolled.set_margin_top(16)
        scrolled.set_margin_bottom(16)
        scrolled.set_margin_start(16)
        scrolled.set_margin_end(16)
        scrolled.set_policy(Gtk.PolicyType.NEVER, Gtk.PolicyType.AUTOMATIC)

        self.students_list_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=10)
        scrolled.set_child(self.students_list_box)
        card.append(scrolled)
        carousel.append(card)

        self.right_btn = Gtk.Button(label="▶")
        self.right_btn.add_css_class("group-nav-btn")
        self.right_btn.connect("clicked", lambda _: self.sonraki_grup())
        carousel.append(self.right_btn)

        self.group_box.append(carousel)
        self.main_content.append(self.group_box)

        self.append(self.main_content)

    def _on_yeni_grup_clicked(self, _) -> None:
        def on_onayla(grup_sayisi_ile: bool, deger: int):
            self.gruplar = OgrenciRepository.gruplar_olustur(grup_sayisi_ile, deger)
            self.aktif_grup_index = 0
            self._render_active_group()

        show_grup_ayarlari_dialog(
            parent=self.get_toplevel(),
            on_onayla=on_onayla,
        )

    def _render_active_group(self) -> None:
        if not self.gruplar:
            self.empty_label.set_visible(True)
            self.group_box.set_visible(False)
            return

        self.empty_label.set_visible(False)
        self.group_box.set_visible(True)

        self.group_title.set_label(f"G R U P   {self.aktif_grup_index + 1}")

        # Clear old items
        while True:
            child = self.students_list_box.get_first_child()
            if child is None:
                break
            self.students_list_box.remove(child)

        current_group = self.gruplar[self.aktif_grup_index]
        for ogr in current_group:
            item_card = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL)
            item_card.add_css_class("group-item-card")
            lbl = Gtk.Label(label=ogr.ad_soyad)
            lbl.set_halign(Gtk.Align.START)
            item_card.append(lbl)
            self.students_list_box.append(item_card)

    def onceki_grup(self) -> None:
        if not self.gruplar:
            return
        if self.aktif_grup_index > 0:
            self.aktif_grup_index -= 1
            self._render_active_group()
        else:
            show_alert_dialog(self.get_toplevel(), "Bilgi", "Bu ilk gruptu :)")

    def sonraki_grup(self) -> None:
        if not self.gruplar:
            return
        if self.aktif_grup_index < len(self.gruplar) - 1:
            self.aktif_grup_index += 1
            self._render_active_group()
        else:
            show_alert_dialog(self.get_toplevel(), "Bilgi", "Bu son gruptu :)")
