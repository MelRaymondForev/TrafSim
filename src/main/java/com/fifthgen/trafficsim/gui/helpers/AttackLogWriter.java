
package com.fifthgen.trafficsim.gui.helpers;

import com.fifthgen.trafficsim.localization.Messages;

import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AttackLogWriter {

    private static Logger logger = Logger.getLogger("attackerLog");

    private static String logPath = "";

    private static String logOldPath = "";

    private static FileHandler handler = null;

    public static void setParameters(String dir, String format) {
        logger.setLevel(Level.FINEST);
        logPath = dir;

        java.util.Date dt = new java.util.Date();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");

//        try {
//            if (!dir.equals(logOldPath)) {
//                if (handler != null) logger.removeHandler(handler);
//                handler = new FileHandler(dir + "log_" + df.format(dt) + "." + format, true);
//                logOldPath = dir;
//                logger.setUseParentHandlers(false);
//                logger.addHandler(handler);
//                if (format.equals("txt"))
//                    handler.setFormatter(new LogFormatter());
//                else
//                    handler.setFormatter(new XMLFormatter());
//            }
//        } catch (Exception e) {
//            ErrorLog.log(Messages.getString("ErrorLog.whileSetting"), 7, ErrorLog.class.getName(), "setParameters", e);
//            System.exit(1);
//        }
    }

    public static synchronized void log(String message) {
        try {
            logger.log(Level.FINEST, message);
        } catch (Exception new_e) {
            System.out.println(Messages.getString("ErrorLog.whileLogging") + message + ")! " + new_e.getLocalizedMessage());
            new_e.printStackTrace();
        }
    }

    public static String getLogPath() {
        return logPath;
    }

    public static void setLogPath(String logPath) {
        setParameters(logPath + "/", "txt");
        AttackLogWriter.logPath = logPath;
    }
}