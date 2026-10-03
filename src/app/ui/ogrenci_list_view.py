from typing import Callable, List, Optional
import gi

gi.require_version("Gtk", "4.0")
from gi.repository import Gtk
from app.logic import Ogrenci, OgrenciRepository
from .dialogs import show_alert_dialog, show_confirm_dialog


class OgrenciListView(Gtk.Box):
    def __init__(self, on_back: Callable[[], None], get_toplevel: Callable[[], Gtk.Window]):
        super().__init__(orientation=Gtk.Orientation.VERTICAL, spacing=0)
        self.on_back = on_back
        self.get_toplevel = get_toplevel

        self.ogrenciler: List[Ogrenci] = []
        self.secilen_ogrenci: Optional[Ogrenci] = None

        self._build_ui()
        self.refresh_list()

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

        title_label = Gtk.Label(label="Sınıf Listesi")
        title_label.add_css_class("app-header-title")
        title_label.set_halign(Gtk.Align.CENTER)
        title_label.set_hexpand(True)
        header.append(title_label)

        # Placeholder to balance header
        dummy_box = Gtk.Box()
        dummy_box.set_size_request(80, -1)
        header.append(dummy_box)

        self.append(header)

        # 2. Main 2-Column Section
        content_box = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=24)
        content_box.set_margin_start(32)
        content_box.set_margin_end(32)
        content_box.set_margin_top(8)
        content_box.set_margin_bottom(8)
        content_box.set_hexpand(True)
        content_box.set_vexpand(True)

        # Left Panel: Form
        left_card = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=20)
        left_card.add_css_class("card-box")
        left_card.set_size_request(320, -1)
        left_card.set_margin_top(4)
        left_card.set_margin_bottom(4)
        left_card.set_margin_start(4)
        left_card.set_margin_end(4)

        form_padding = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=20)
        form_padding.set_margin_top(24)
        form_padding.set_margin_bottom(24)
        form_padding.set_margin_start(24)
        form_padding.set_margin_end(24)
        form_padding.set_vexpand(True)

        # Top of form
        form_top = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=18)
        new_label = Gtk.Label(label="Yeni Öğrenci")
        new_label.add_css_class("dialog-title")
        new_label.set_halign(Gtk.Align.START)
        form_top.append(new_label)

        # Name entry
        self.entry_name = Gtk.Entry()
        self.entry_name.set_placeholder_text("Ad Soyad")
        self.entry_name.add_css_class("touch-entry")
        self.entry_name.connect("activate", lambda _: self._on_ekle_clicked())
        form_top.append(self.entry_name)

        # Gender Radios
        gender_row = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=16)
        self.radio_erkek = Gtk.CheckButton(label="Erkek")
        self.radio_erkek.add_css_class("touch-radio")
        self.radio_erkek.set_active(True)

        self.radio_kiz = Gtk.CheckButton(label="Kız")
        self.radio_kiz.add_css_class("touch-radio")
        self.radio_kiz.set_group(self.radio_erkek)

        gender_row.append(self.radio_erkek)
        gender_row.append(self.radio_kiz)
        form_top.append(gender_row)

        # EKLE Button
        ekle_btn = Gtk.Button(label="EKLE")
        ekle_btn.add_css_class("touch-btn")
        ekle_btn.add_css_class("touch-btn-outline")
        ekle_btn.connect("clicked", lambda _: self._on_ekle_clicked())
        form_top.append(ekle_btn)

        form_padding.append(form_top)

        # Bottom of form: Delete Selected button
        form_bottom = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=12)
        form_bottom.set_valign(Gtk.Align.END)
        form_bottom.set_vexpand(True)

        self.sil_btn = Gtk.Button(label="Seçili Öğrenciyi Sil")
        self.sil_btn.add_css_class("touch-btn")
        self.sil_btn.add_css_class("touch-btn-danger")
        self.sil_btn.set_visible(False)
        self.sil_btn.connect("clicked", self._on_sil_clicked)
        form_bottom.append(self.sil_btn)

        form_padding.append(form_bottom)
        left_card.append(form_padding)
        content_box.append(left_card)

        # Right Panel: List
        right_card = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=0)
        right_card.add_css_class("card-box")
        right_card.set_hexpand(True)
        right_card.set_vexpand(True)
        right_card.set_margin_top(4)
        right_card.set_margin_bottom(4)
        right_card.set_margin_start(4)
        right_card.set_margin_end(4)

        # Header Row
        table_hdr = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=16)
        table_hdr.set_margin_start(24)
        table_hdr.set_margin_end(24)
        table_hdr.set_margin_top(16)
        table_hdr.set_margin_bottom(14)

        hdr_name = Gtk.Label(label="AD SOYAD")
        hdr_name.add_css_class("sub-header-label")
        hdr_name.set_halign(Gtk.Align.START)
        hdr_name.set_hexpand(True)
        table_hdr.append(hdr_name)

        hdr_gender = Gtk.Label(label="CİNSİYET")
        hdr_gender.add_css_class("sub-header-label")
        hdr_gender.set_halign(Gtk.Align.START)
        hdr_gender.set_size_request(90, -1)
        table_hdr.append(hdr_gender)

        right_card.append(table_hdr)
        right_card.append(Gtk.Separator(orientation=Gtk.Orientation.HORIZONTAL))

        # Scrolled Student List
        scrolled = Gtk.ScrolledWindow()
        scrolled.set_hexpand(True)
        scrolled.set_vexpand(True)
        scrolled.set_policy(Gtk.PolicyType.NEVER, Gtk.PolicyType.AUTOMATIC)

        self.listbox = Gtk.ListBox()
        self.listbox.set_selection_mode(Gtk.SelectionMode.SINGLE)
        self.listbox.connect("row-selected", self._on_row_selected)
        scrolled.set_child(self.listbox)
        right_card.append(scrolled)

        content_box.append(right_card)
        self.append(content_box)

        # 3. Bottom Reset All Bar
        bottom_bar = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL)
        bottom_bar.set_halign(Gtk.Align.END)
        bottom_bar.set_margin_end(32)
        bottom_bar.set_margin_top(10)
        bottom_bar.set_margin_bottom(20)

        reset_btn = Gtk.Button(label="Tüm Kayıtları Sıfırla")
        reset_btn.add_css_class("touch-btn-flat")
        reset_btn.connect("clicked", self._on_reset_all_clicked)
        bottom_bar.append(reset_btn)

        self.append(bottom_bar)

    def refresh_list(self) -> None:
        """Fetches all students and populates the list box."""
        self.ogrenciler = OgrenciRepository.tum_ogrenciler()
        self.secilen_ogrenci = None
        self.sil_btn.set_visible(False)

        # Clear existing rows
        while True:
            row = self.listbox.get_row_at_index(0)
            if row is None:
                break
            self.listbox.remove(row)

        for ogr in self.ogrenciler:
            row = Gtk.ListBoxRow()
            row.add_css_class("touch-list-row")
            row._ogrenci = ogr

            row_box = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=16)

            name_lbl = Gtk.Label(label=ogr.ad_soyad)
            name_lbl.set_halign(Gtk.Align.START)
            name_lbl.set_hexpand(True)
            row_box.append(name_lbl)

            gender_lbl = Gtk.Label(label=ogr.cinsiyet)
            gender_lbl.set_halign(Gtk.Align.START)
            gender_lbl.set_size_request(90, -1)
            row_box.append(gender_lbl)

            row.set_child(row_box)
            self.listbox.append(row)

    def _on_row_selected(self, _, row: Optional[Gtk.ListBoxRow]) -> None:
        if row is not None and hasattr(row, "_ogrenci"):
            self.secilen_ogrenci = row._ogrenci
            self.sil_btn.set_visible(True)
        else:
            self.secilen_ogrenci = None
            self.sil_btn.set_visible(False)

    def _on_ekle_clicked(self) -> None:
        name = self.entry_name.get_text().strip()
        if not name:
            show_alert_dialog(self.get_toplevel(), "Uyarı", "Ad Soyad boş olamaz!")
            return

        cinsiyet = "Erkek" if self.radio_erkek.get_active() else "Kız"
        OgrenciRepository.ekle(name, cinsiyet)
        self.entry_name.set_text("")
        self.refresh_list()

    def _on_sil_clicked(self, _) -> None:
        if self.secilen_ogrenci is None:
            return

        def do_delete():
            if self.secilen_ogrenci:
                OgrenciRepository.sil(self.secilen_ogrenci.id)
                self.refresh_list()

        show_confirm_dialog(
            parent=self.get_toplevel(),
            title="Silme Onayı",
            message=f"{self.secilen_ogrenci.ad_soyad} isimli öğrenci silinecek. Onaylıyor musunuz?",
            confirm_label="Evet, Sil",
            on_confirm=do_delete,
            is_danger=True,
        )

    def _on_reset_all_clicked(self, _) -> None:
        def do_reset_all():
            OgrenciRepository.tumunu_sil()
            self.refresh_list()

        show_confirm_dialog(
            parent=self.get_toplevel(),
            title="Tümünü Sil Onayı",
            message="Listeyi tamamen temizlemek üzeresiniz. Bu işlem geri alınamaz. Devam edilsin mi?",
            confirm_label="Hepsini Sil",
            on_confirm=do_reset_all,
            is_danger=True,
        )
