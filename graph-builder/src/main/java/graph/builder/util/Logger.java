package graph.builder.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public enum LogLevel {
        INFO, ERROR, DEBUG, WARNING
    }

    private LogLevel currentLevel;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final Logger instance = new Logger(LogLevel.INFO);

    public static Logger getInstance() {
        return instance;
    }


    private Logger(LogLevel level) {
        this.currentLevel = level;
    }

    private void log(String message, LogLevel level) {
        if (level.ordinal() >= currentLevel.ordinal()) {
            String timeStamp = sdf.format(new Date());
            System.out.println("[" + timeStamp + "][" + level + "]: " + message);
        }
    }

    public void warning(String message) {
        log(message, LogLevel.WARNING);
    }

    public void info(String message) {
        log(message, LogLevel.INFO);
    }

    public void error(String message) {
        log(message, LogLevel.ERROR);
    }

    public void debug(String message) {
        log(message, LogLevel.DEBUG);
    }

    public void setLogLevel(LogLevel level) {
        this.currentLevel = level;
    }
}
