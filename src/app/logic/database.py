import os
import sys
import sqlite3
from pathlib import Path


class Database:
    _connection: sqlite3.Connection | None = None
    _db_path: Path | None = None

    @classmethod
    def get_db_path(cls) -> Path:
        if cls._db_path is not None:
            return cls._db_path

        user_home = Path.home()
        if sys.platform.startswith("win"):
            appdata = os.environ.get("APPDATA")
            base_dir = Path(appdata) if appdata else user_home / "AppData" / "Roaming"
        elif sys.platform == "darwin":
            base_dir = user_home / "Library" / "Application Support"
        else:
            xdg_data = os.environ.get("XDG_DATA_HOME")
            base_dir = Path(xdg_data) if xdg_data else user_home / ".local" / "share"

        app_dir = base_dir / "Rastgeletor"
        app_dir.mkdir(parents=True, exist_ok=True)
        cls._db_path = app_dir / "ogrenciler.db"
        return cls._db_path

    @classmethod
    def set_db_path(cls, path: Path | str) -> None:
        """Allow setting custom path for testing."""
        if cls._connection:
            cls._connection.close()
            cls._connection = None
        cls._db_path = Path(path)

    @classmethod
    def get_connection(cls) -> sqlite3.Connection:
        if cls._connection is None:
            db_path = cls.get_db_path()
            cls._connection = sqlite3.connect(
                str(db_path),
                check_same_thread=False,
                isolation_level=None  # autocommit mode
            )
            cls._connection.row_factory = sqlite3.Row
            cls._create_table()
        return cls._connection

    @classmethod
    def _create_table(cls) -> None:
        sql = """
        CREATE TABLE IF NOT EXISTS OGRENCILER (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            adSoyad TEXT NOT NULL,
            cinsiyet TEXT NOT NULL
        )
        """
        cursor = cls._connection.cursor()
        cursor.execute(sql)

    @classmethod
    def close(cls) -> None:
        if cls._connection is not None:
            cls._connection.close()
            cls._connection = None
