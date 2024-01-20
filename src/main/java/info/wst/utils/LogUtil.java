package info.wst.utils;

import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.io.File;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogUtil {

    private static final String LOG_FMT_RESOURCE = "i18n/log";

    private static Logger logger = null;

    private static AtomicBoolean initialized = new AtomicBoolean(false);

    public static boolean enabled() {
        if (initialized.get() == false) {
            throw new IllegalStateException("The logger has not been initialized.");
        }
        return logger != null;
    }

    public static void setup(boolean enabled) {
        if (initialized.compareAndSet(false, true)) {
            if (enabled) {
                try (InputStream is = new ByteArrayInputStream(new byte[0])) {
                    /*
                     * We prevent to read outer files, which may be unsafe.
                     * The configuration is empty, because we programatically set it.
                     */
                    LogManager.getLogManager().readConfiguration(is);
                } catch (SecurityException | IOException e) {
                    throw new RuntimeException(e);
                }

                logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
                // We remove all the old handlers.
                for (Handler handler : logger.getHandlers()) {
                    logger.removeHandler(handler);
                }

                Handler handler = null;
                String pwd = System.getProperty("user.dir");
                if (pwd != null) {
                    try {
                        String pattern = String.join(File.separator, pwd, "wst_%u.log");
                        handler = new FileHandler(pattern);
                    } catch (SecurityException | IOException e) {
                        // We fallback to the console handler.
                        handler = new ConsoleHandler();
                    }
                }
                handler.setFormatter(new SimpleFormatter());

                logger.addHandler(handler);
                logger.setLevel(Level.INFO);
            }
        }
    }

    public static void warning(String message) {
        if (logger != null) {
            logger.log(Level.WARNING, message);
        }
    }

    public static void error(String message) {
        if (logger != null) {
            logger.log(Level.SEVERE, message);
        }
    }

    public static void info(String message) {
        if (logger != null) {
            logger.log(Level.INFO, message);
        }
    }

    public static String formatLogMessage(String logKey, Object... args) {
        Objects.requireNonNull(logKey);
        String fmtLogMessage = logKey;
        ResourceBundle resourceBundle = ResourceBundle.getBundle(LOG_FMT_RESOURCE);
        if (resourceBundle != null) {
            String logFmt = resourceBundle.getString(fmtLogMessage);
            if (logFmt != null) {
                fmtLogMessage = String.format(logFmt, args);
            }
        }
        return fmtLogMessage;
    }

}
