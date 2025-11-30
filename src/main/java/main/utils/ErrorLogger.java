package main.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogger {
    private static final String LOG_FILE = "ErrorLog.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String context, Exception e) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println("=== " + LocalDateTime.now().format(formatter) + " ===");
            writer.println("Context: " + context);
            writer.println("Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            
            // Sadece ilgili stack trace satırlarını yaz
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (int i = 0; i < Math.min(3, stackTrace.length); i++) {
                if (stackTrace[i].getClassName().startsWith("main.")) {
                    writer.println("  at " + stackTrace[i]);
                }
            }
            writer.println();
        } catch (IOException ignored) {
            // Log yazılamazsa sessizce devam et
        }
    }

    public static void log(String context, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println("=== " + LocalDateTime.now().format(formatter) + " ===");
            writer.println("Context: " + context);
            writer.println("Message: " + message);
            writer.println();
        } catch (IOException ignored) {
            // Log yazılamazsa sessizce devam et
        }
    }
}
