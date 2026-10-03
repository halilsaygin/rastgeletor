from dataclasses import dataclass


@dataclass(frozen=True)
class Ogrenci:
    id: int
    ad_soyad: str
    cinsiyet: str

    @property
    def adSoyad(self) -> str:
        """Compatibility property matching Kotlin naming."""
        return self.ad_soyad
