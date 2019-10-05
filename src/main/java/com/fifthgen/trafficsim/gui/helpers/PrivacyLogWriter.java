package com.fifthgen.trafficsim.gui.helpers;

import com.fifthgen.trafficsim.localization.Messages;
import com.fifthgen.trafficsim.scenario.Scenario;

import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

public final class PrivacyLogWriter {

    private static Logger logger = Logger.getLogger("privacyLog");

    private static String logPath = "";

    private static String logOldPath = "";

    private static FileHandler handler = null;

    public static void setParameters(String dir, String format) {
        logger.setLevel(Level.FINEST);
        logPath = dir;

        java.util.Date dt = new java.util.Date();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");
        try {
            if (!dir.equals(logOldPath)) {
                if (handler != null)
                    logger.removeHandler(handler);
                String scenName = Scenario.getInstance().getScenarioName();
                if (scenName != null && !scenName.equals(""))
                    handler = new FileHandler(dir + scenName.substring(0, scenName.length() - 4) + "_" + df.format(dt) + "." + format, true);
                else
                    handler = new FileHandler(dir + "privacyLog_" + df.format(dt) + "." + format, true);
                logOldPath = dir;
                logger.setUseParentHandlers(false);
                logger.addHandler(handler);
                if (format.equals("log"))
                    handler.setFormatter(new LogFormatter());
                else
                    handler.setFormatter(new XMLFormatter());
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }

    public static synchronized void log(String message, int mode) {
        try {
            logger.log(Level.FINEST, message);
        } catch (Exception new_e) {
            System.out
                    .println(Messages.getString("ErrorLog.whileLogging") + message + ")! " + new_e.getLocalizedMessage());
            new_e.printStackTrace();
        }
    }

    public static synchronized void log(String message) {
        try {
            logger.log(Level.FINEST, message);
        } catch (Exception new_e) {
            System.out
                    .println(Messages.getString("ErrorLog.whileLogging") + message + ")! " + new_e.getLocalizedMessage());
            new_e.printStackTrace();
        }
    }

    public static String getLogPath() {
        return logPath;
    }

    public static void setLogPath(String logPath) {
        setParameters(logPath + "/", "log");
        PrivacyLogWriter.logPath = logPath;
        System.out.println("log is:" + logPath);
    }
}