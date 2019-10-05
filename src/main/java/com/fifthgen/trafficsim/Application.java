package com.fifthgen.trafficsim;

import com.fifthgen.trafficsim.gui.DrawingArea;
import com.fifthgen.trafficsim.gui.Renderer;
import com.fifthgen.trafficsim.gui.controlpanels.MainControlPanel;
import com.fifthgen.trafficsim.gui.helpers.MouseClickManager;
import com.fifthgen.trafficsim.gui.helpers.ProgressOverlay;
import com.fifthgen.trafficsim.gui.helpers.ReRenderManager;
import com.fifthgen.trafficsim.localization.Messages;
import com.fifthgen.trafficsim.map.Map;
import com.fifthgen.trafficsim.simulation.SimulationMaster;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessSkin;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

public final class Application implements Runnable {

    private static SimulationMaster simulationMaster_;
    private static MainControlPanel controlPanel_;
    private static JFrame mainFrame_;
    private static boolean useDoubleBuffering_;
    private static boolean drawManualBuffered_;
    private static ProgressOverlay progressBar_;

    public Application() {
        readconfig("./config.txt");
    }

    public static DrawingArea addComponentsToPane(Container container) {
        container.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        DrawingArea drawarea = new DrawingArea(useDoubleBuffering_, drawManualBuffered_);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        container.add(drawarea, c);
        Renderer.getInstance().setDrawArea(drawarea);

        controlPanel_ = new MainControlPanel();

        controlPanel_.setPreferredSize(new Dimension(200, 100000));
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0;
        c.gridx = 1;
        c.gridy = 0;

        container.add(controlPanel_, c);

        return drawarea;
    }

    public static void setProgressBar(boolean state) {
        progressBar_.setVisible(state);
    }

    public static MainControlPanel getMainControlPanel() {
        return controlPanel_;
    }

    public static JFrame getMainFrame() {
        return mainFrame_;
    }

    public static SimulationMaster getSimulationMaster() {
        return simulationMaster_;
    }

    private static void readconfig(String configFilePath) {
        String loggerFormat, loggerDir;
        int loggerLevel;
        long loggerTrashtime;
        boolean loggerFormatError = false;
        Properties configFile = new Properties();

        try {
            configFile.load(new FileInputStream(configFilePath));

            String guiTheme = configFile.getProperty("gui_theme", "");

            SwingUtilities.invokeLater(() -> {
                SubstanceLookAndFeel.setSkin(new BusinessSkin());
            });

            loggerTrashtime = Long.parseLong(configFile.getProperty("logger_trashtime", "365000"));
            loggerDir = configFile.getProperty("logger_dir", "./");
            loggerFormat = configFile.getProperty("logger_format", "txt");
            loggerLevel = Integer.parseInt(configFile.getProperty("logger_level", "1"));

            if (!loggerFormat.equals("txt") && !loggerFormat.equals("xml")) {
                loggerFormatError = true;
                loggerFormat = "txt";
            }


            if (loggerTrashtime < 0 || loggerTrashtime > 365000) {
                loggerTrashtime = 365000;
            }
            useDoubleBuffering_ = Boolean.parseBoolean(configFile.getProperty("double_buffer", "true"));
            drawManualBuffered_ = Boolean.parseBoolean(configFile.getProperty("draw_manual_buffered", "false"));
        } catch (Exception e) {
            System.exit(1);
        }
    }

    public void run() {
        mainFrame_ = new JFrame();
        mainFrame_.setTitle(Messages.getString("StartGUI.applicationtitle"));
        mainFrame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        progressBar_ = new ProgressOverlay();

        URL appicon = ClassLoader.getSystemResource("logo.png");

        if (appicon != null) {
            mainFrame_.setIconImage(Toolkit.getDefaultToolkit().getImage(appicon));
        }
        DrawingArea drawarea = addComponentsToPane(mainFrame_.getContentPane());

        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        mainFrame_.pack();

        mainFrame_.setSize((int) bounds.getWidth(), (int) bounds.getHeight());
        mainFrame_.setLocationRelativeTo(null);
        mainFrame_.setResizable(true);
        mainFrame_.setVisible(true);

        simulationMaster_ = new SimulationMaster();
        simulationMaster_.start();
        Map.getInstance().initNewMap(100000, 100000, 10000, 10000);
        Map.getInstance().signalMapLoaded();
        ReRenderManager.getInstance().start();
        MouseClickManager.getInstance().setDrawArea(drawarea);
        MouseClickManager.getInstance().start();
    }
}