package civicloop.gui;

import civicloop.data.DataStore;
import civicloop.model.User;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
/**
 * Main dashboard after login.
 * Contains a JTabbedPane hosting all 5 module panels plus a profile header.
 */

public class MainFrame extends JFrame {
    private DataStore dataStore;
    private User currentUser;
    private JLabel welcomeLabel;
    private ItemPanel itemPanel;
    private ServicePanel servicePanel;
    private TimeBankPanel timeBankPanel;
    private TrustPanel trustPanel;
    private FeedPanel feedPanel;

    public MainFrame(DataStore dataStore, User user) {
        this.dataStore = dataStore;
        this.currentUser = user;
        setTitle("CivicLoop - " + user.getName() + " (" + user.getUserId() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top welcome bar
        welcomeLabel = new JLabel("Welcome, " + user.getName() +
                " | Area: " + user.getArea() +
                " | Balance: " + user.getTimeCreditBalance() + " TC");
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(welcomeLabel, BorderLayout.NORTH);

        // Tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        itemPanel = new ItemPanel(this);
        servicePanel = new ServicePanel(this);
        timeBankPanel = new TimeBankPanel(this);
        trustPanel = new TrustPanel(this);
        feedPanel = new FeedPanel(this);

        tabs.add("Item Sharing", itemPanel);
        tabs.add("Service Exchange", servicePanel);
        tabs.add("TimeBank", timeBankPanel);
        tabs.add("Trust & Profile", trustPanel);
        tabs.add("Community Feed", feedPanel);

        add(tabs, BorderLayout.CENTER);

         // Save data on window close

         
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    dataStore.saveToFile("civicloop_data.dat");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Could not save data: " + ex.getMessage());
                }
            }
        });
    }

    /** Refresh all panels to reflect the latest data. */

     
    public void refreshAll() {
        welcomeLabel.setText("Welcome, " + currentUser.getName() +
                " | Area: " + currentUser.getArea() +
                " | Balance: " + currentUser.getTimeCreditBalance() + " TC");
        itemPanel.refreshTable();
        servicePanel.refreshTable();
        timeBankPanel.refresh();
        trustPanel.refresh();
        feedPanel.refresh();
    }
// Convenience getters for panels


    
    public DataStore getDataStore() { return dataStore; }
    public User getCurrentUser() { return currentUser; }
}