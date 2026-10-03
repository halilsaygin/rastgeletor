from typing import Callable
import gi

gi.require_version("Gtk", "4.0")
from gi.repository import Gtk


def show_alert_dialog(parent: Gtk.Window, title: str, message: str) -> None:
    """Displays a modal information or warning dialog."""
    dialog = Gtk.Window(
        title=title,
        transient_for=parent,
        modal=True,
        resizable=False,
        default_width=360,
    )
    header = Gtk.HeaderBar()
    header.set_title_widget(Gtk.Box())
    dialog.set_titlebar(header)

    box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=20)
    box.set_margin_top(24)
    box.set_margin_bottom(24)
    box.set_margin_start(24)
    box.set_margin_end(24)

    title_label = Gtk.Label(label=title)
    title_label.add_css_class("dialog-title")
    title_label.set_halign(Gtk.Align.START)
    box.append(title_label)

    msg_label = Gtk.Label(label=message)
    msg_label.add_css_class("dialog-text")
    msg_label.set_wrap(True)
    msg_label.set_halign(Gtk.Align.START)
    box.append(msg_label)

    btn_box = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=12)
    btn_box.set_halign(Gtk.Align.END)

    ok_btn = Gtk.Button(label="Tamam")
    ok_btn.add_css_class("touch-btn")
    ok_btn.add_css_class("touch-btn-outline")
    ok_btn.connect("clicked", lambda _: dialog.close())
    btn_box.append(ok_btn)

    box.append(btn_box)
    dialog.set_child(box)
    dialog.present()


def show_confirm_dialog(
    parent: Gtk.Window,
    title: str,
    message: str,
    confirm_label: str,
    on_confirm: Callable[[], None],
    is_danger: bool = False,
) -> None:
    """Displays a modal confirmation dialog."""
    dialog = Gtk.Window(
        title=title,
        transient_for=parent,
        modal=True,
        resizable=False,
        default_width=400,
    )
    header = Gtk.HeaderBar()
    header.set_title_widget(Gtk.Box())
    dialog.set_titlebar(header)

    box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=20)
    box.set_margin_top(24)
    box.set_margin_bottom(24)
    box.set_margin_start(24)
    box.set_margin_end(24)

    title_label = Gtk.Label(label=title)
    title_label.add_css_class("dialog-title")
    title_label.set_halign(Gtk.Align.START)
    box.append(title_label)

    msg_label = Gtk.Label(label=message)
    msg_label.add_css_class("dialog-text")
    msg_label.set_wrap(True)
    msg_label.set_halign(Gtk.Align.START)
    box.append(msg_label)

    btn_box = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=12)
    btn_box.set_halign(Gtk.Align.END)

    cancel_btn = Gtk.Button(label="İptal")
    cancel_btn.add_css_class("touch-btn-flat")
    cancel_btn.connect("clicked", lambda _: dialog.close())
    btn_box.append(cancel_btn)

    confirm_btn = Gtk.Button(label=confirm_label)
    confirm_btn.add_css_class("touch-btn")
    if is_danger:
        confirm_btn.add_css_class("touch-btn-danger")
    else:
        confirm_btn.add_css_class("touch-btn-outline")

    def _on_confirm_clicked(_):
        dialog.close()
        on_confirm()

    confirm_btn.connect("clicked", _on_confirm_clicked)
    btn_box.append(confirm_btn)

    box.append(btn_box)
    dialog.set_child(box)
    dialog.present()


def show_secim_ayarlari_dialog(
    parent: Gtk.Window,
    mevcut_liste_modu: str,
    mevcut_cinsiyet_modu: str,
    on_onayla: Callable[[str, str], None],
) -> None:
    """Modal dialog for Random Student Selector settings."""
    dialog = Gtk.Window(
        title="Seçim Ayarları",
        transient_for=parent,
        modal=True,
        resizable=False,
        default_width=420,
    )
    header = Gtk.HeaderBar()
    header.set_title_widget(Gtk.Box())
    dialog.set_titlebar(header)

    box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=18)
    box.set_margin_top(24)
    box.set_margin_bottom(24)
    box.set_margin_start(24)
    box.set_margin_end(24)

    title_label = Gtk.Label(label="Seçim Ayarları")
    title_label.add_css_class("dialog-title")
    title_label.set_halign(Gtk.Align.START)
    box.append(title_label)

    # 1. Section: Liste Modu
    section1_label = Gtk.Label(label="Liste Modu")
    section1_label.add_css_class("sub-header-label")
    section1_label.set_halign(Gtk.Align.START)
    box.append(section1_label)

    mode_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=8)
    radio_eksilen = Gtk.CheckButton(label="Eksilen Liste")
    radio_eksilen.add_css_class("touch-radio")
    radio_sabit = Gtk.CheckButton(label="Sabit Liste")
    radio_sabit.add_css_class("touch-radio")
    radio_sabit.set_group(radio_eksilen)

    if mevcut_liste_modu == "eksilen_liste":
        radio_eksilen.set_active(True)
    else:
        radio_sabit.set_active(True)

    mode_box.append(radio_eksilen)
    mode_box.append(radio_sabit)
    box.append(mode_box)

    # Separator
    box.append(Gtk.Separator(orientation=Gtk.Orientation.HORIZONTAL))

    # 2. Section: Cinsiyet Filtresi
    section2_label = Gtk.Label(label="Cinsiyet Filtresi")
    section2_label.add_css_class("sub-header-label")
    section2_label.set_halign(Gtk.Align.START)
    box.append(section2_label)

    gender_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=8)
    radio_tum = Gtk.CheckButton(label="Tüm Sınıf")
    radio_tum.add_css_class("touch-radio")
    radio_erkek = Gtk.CheckButton(label="Sadece Erkekler")
    radio_erkek.add_css_class("touch-radio")
    radio_erkek.set_group(radio_tum)
    radio_kiz = Gtk.CheckButton(label="Sadece Kızlar")
    radio_kiz.add_css_class("touch-radio")
    radio_kiz.set_group(radio_tum)

    if mevcut_cinsiyet_modu == "erkek":
        radio_erkek.set_active(True)
    elif mevcut_cinsiyet_modu == "kiz":
        radio_kiz.set_active(True)
    else:
        radio_tum.set_active(True)

    gender_box.append(radio_tum)
    gender_box.append(radio_erkek)
    gender_box.append(radio_kiz)
    box.append(gender_box)

    # Action Buttons
    btn_box = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=14)
    btn_box.set_halign(Gtk.Align.END)
    btn_box.set_margin_top(10)

    cancel_btn = Gtk.Button(label="İptal")
    cancel_btn.add_css_class("touch-btn-flat")
    cancel_btn.connect("clicked", lambda _: dialog.close())
    btn_box.append(cancel_btn)

    apply_btn = Gtk.Button(label="Uygula")
    apply_btn.add_css_class("touch-btn")
    apply_btn.add_css_class("touch-btn-outline")

    def _on_apply(_):
        chosen_mode = "eksilen_liste" if radio_eksilen.get_active() else "sabit_liste"
        if radio_erkek.get_active():
            chosen_gender = "erkek"
        elif radio_kiz.get_active():
            chosen_gender = "kiz"
        else:
            chosen_gender = "tamami"

        dialog.close()
        on_onayla(chosen_mode, chosen_gender)

    apply_btn.connect("clicked", _on_apply)
    btn_box.append(apply_btn)

    box.append(btn_box)
    dialog.set_child(box)
    dialog.present()


def show_grup_ayarlari_dialog(
    parent: Gtk.Window,
    on_onayla: Callable[[bool, int], None],
) -> None:
    """Modal dialog for Group Generator settings."""
    dialog = Gtk.Window(
        title="Gruplama Ayarları",
        transient_for=parent,
        modal=True,
        resizable=False,
        default_width=420,
    )
    header = Gtk.HeaderBar()
    header.set_title_widget(Gtk.Box())
    dialog.set_titlebar(header)

    box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=18)
    box.set_margin_top(24)
    box.set_margin_bottom(24)
    box.set_margin_start(24)
    box.set_margin_end(24)

    title_label = Gtk.Label(label="Gruplama Ayarları")
    title_label.add_css_class("dialog-title")
    title_label.set_halign(Gtk.Align.START)
    box.append(title_label)

    # Radio options
    radio_grup_sayisi = Gtk.CheckButton(label="Grup sayısı ile")
    radio_grup_sayisi.add_css_class("touch-radio")
    radio_grup_sayisi.set_active(True)

    radio_kisi_sayisi = Gtk.CheckButton(label="Gruptaki kişi sayısı ile")
    radio_kisi_sayisi.add_css_class("touch-radio")
    radio_kisi_sayisi.set_group(radio_grup_sayisi)

    box.append(radio_grup_sayisi)
    box.append(radio_kisi_sayisi)

    # Input entry
    entry_label = Gtk.Label(label="Grup Sayısı")
    entry_label.add_css_class("sub-header-label")
    entry_label.set_halign(Gtk.Align.START)
    box.append(entry_label)

    entry = Gtk.Entry()
    entry.set_text("2")
    entry.add_css_class("touch-entry")
    entry.set_input_purpose(Gtk.InputPurpose.DIGITS)
    box.append(entry)

    def _on_radio_toggled(_):
        if radio_grup_sayisi.get_active():
            entry_label.set_label("Grup Sayısı")
        else:
            entry_label.set_label("Kişi Sayısı")

    radio_grup_sayisi.connect("toggled", _on_radio_toggled)

    # Buttons
    btn_box = Gtk.Box(orientation=Gtk.Orientation.HORIZONTAL, spacing=14)
    btn_box.set_halign(Gtk.Align.END)
    btn_box.set_margin_top(10)

    cancel_btn = Gtk.Button(label="İptal")
    cancel_btn.add_css_class("touch-btn-flat")
    cancel_btn.connect("clicked", lambda _: dialog.close())
    btn_box.append(cancel_btn)

    apply_btn = Gtk.Button(label="Uygula")
    apply_btn.add_css_class("touch-btn")
    apply_btn.add_css_class("touch-btn-outline")

    def _on_apply(_):
        val_text = entry.get_text().strip()
        val = int(val_text) if val_text.isdigit() else 2
        val = max(1, val)
        is_grup_sayisi = radio_grup_sayisi.get_active()
        dialog.close()
        on_onayla(is_grup_sayisi, val)

    apply_btn.connect("clicked", _on_apply)
    btn_box.append(apply_btn)

    box.append(btn_box)
    dialog.set_child(box)
    dialog.present()
