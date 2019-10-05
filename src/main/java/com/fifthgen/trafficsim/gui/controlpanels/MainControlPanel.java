package com.fifthgen.trafficsim.gui.controlpanels;

import com.fifthgen.trafficsim.localization.Messages;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public final class MainControlPanel extends JPanel implements ChangeListener {

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final SimulateControlPanel simulatePanel = new SimulateControlPanel();
    private final EditControlPanel editPanel = new EditControlPanel();
    private final FileFilter xmlFileFilter;
    private final FileFilter osmFileFilter;
    private JFileChooser fileChooser = null;
    private boolean hideBar = false;

    public MainControlPanel() {

        java.awt.EventQueue.invokeLater(() -> {
            JFileChooser tmpChooser = new JFileChooser();
            tmpChooser.setMultiSelectionEnabled(false);
            fileChooser = tmpChooser;
        });

        xmlFileFilter = new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                return f.getName().toLowerCase().endsWith(".xml");
            }

            public String getDescription() {
                return Messages.getString("MainControlPanel.xmlFiles") + " (*.xml)";
            }
        };

        osmFileFilter = new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                return f.getName().toLowerCase().endsWith(".osm");
            }

            public String getDescription() {
                return Messages.getString("MainControlPanel.openStreetMapFiles") + " (*.osm)";
            }
        };

        setLayout(new GridBagLayout());
        Dimension size = simulatePanel.getPreferredSize();
        size.setSize(size.width + 250, Math.max(size.height, 400));
        setMinimumSize(new Dimension(size.width + 50, 400));
        editPanel.setMinimumSize(size);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;

        tabbedPane.addTab(Messages.getString("MainControlPanel.editTab"), editPanel);
        tabbedPane.addTab(Messages.getString("MainControlPanel.simulateTab"), simulatePanel);
        tabbedPane.setMinimumSize(new Dimension(size.width + 50, 400));
        tabbedPane.addChangeListener(this);

        UIManager.put("TabbedPane.contentOpaque", false);

        JScrollPane scrollPane = new JScrollPane(tabbedPane);
        tabbedPane.setOpaque(false);
        simulatePanel.setOpaque(false);
        editPanel.setOpaque(false);

        scrollPane.setOpaque(false);

        scrollPane.getViewport().setOpaque(false);

        JViewport jv = scrollPane.getViewport();
        jv.setViewPosition(new Point(0, 0));
        scrollPane.getVerticalScrollBar().setValue(0);
        add(scrollPane, c);
    }

    public JFileChooser getFileChooser() {
        if (fileChooser == null) {
            do {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (fileChooser == null);
        }

        return fileChooser;
    }

    public void changeFileChooser(boolean acceptAll, boolean acceptXML, boolean acceptOSM) {
        if (fileChooser == null) {
            do {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (fileChooser == null);
        }

        fileChooser.resetChoosableFileFilters();

        if (acceptAll) fileChooser.setAcceptAllFileFilterUsed(true);
        else fileChooser.setAcceptAllFileFilterUsed(true);

        if (acceptOSM) {
            fileChooser.addChoosableFileFilter(osmFileFilter);
            fileChooser.setFileFilter(osmFileFilter);
        }

        if (acceptXML) {
            fileChooser.addChoosableFileFilter(xmlFileFilter);
            fileChooser.setFileFilter(xmlFileFilter);
        }
    }

    public SimulateControlPanel getSimulatePanel() {
        return simulatePanel;
    }

    public EditControlPanel getEditPanel() {
        return editPanel;
    }

    public Component getSelectedTabComponent() {
        return tabbedPane.getSelectedComponent();
    }

    public void activateEditPane() {
        tabbedPane.setSelectedIndex(1);
    }

    public void stateChanged(ChangeEvent e) {
    }

    public void tooglePanel() {
        Dimension size = simulatePanel.getPreferredSize();
        hideBar = !hideBar;

        if (hideBar) {
            size.setSize(0, Math.max(size.height, 400));
        } else {
            size.setSize(size.width + 250, Math.max(size.height, 400));
        }

        setMinimumSize(new Dimension(size.width + 50, 400));
        editPanel.setMinimumSize(new Dimension(size.width, size.height));

        this.revalidate();
        this.repaint();
    }

    public void switchToTab(int tabNr) {
        tabbedPane.setSelectedIndex(tabNr);
    }
}