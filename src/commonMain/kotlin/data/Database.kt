package data

import java.io.File
import java.sql.Connection
import java.sql.DriverManager

object Database {
    private val DB_URL: String

    init {
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")
        val appDataDir = when {
            os.contains("win") -> File(System.getenv("APPDATA") ?: "$userHome\\AppData\\Roaming", "Rastgeletor")
            os.contains("mac") -> File(userHome, "Library/Application Support/Rastgeletor")
            else -> File(System.getenv("XDG_DATA_HOME") ?: "$userHome/.local/share", "Rastgeletor")
        }
        
        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }
        
        val dbFile = File(appDataDir, "ogrenciler.db")
        DB_URL = "jdbc:sqlite:${dbFile.absolutePath.replace("\\", "/")}"
    }

    private var connection: Connection? = null

    fun getConnection(): Connection {
        if (connection == null || connection!!.isClosed) {
            Class.forName("org.sqlite.JDBC")
            connection = DriverManager.getConnection(DB_URL)
            createTable()
        }
        return connection!!
    }

    private fun createTable() {
        val sql = """
            CREATE TABLE IF NOT EXISTS OGRENCILER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                adSoyad TEXT NOT NULL,
                cinsiyet TEXT NOT NULL
            )
        """.trimIndent()
        getConnection().createStatement().execute(sql)
    }
}
