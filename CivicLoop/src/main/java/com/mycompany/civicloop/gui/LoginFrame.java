package civicloop.gui;

import civicloop.data.DataStore;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
/**
 * Login and registration window.
 * Uses DataStore for persistence – loads saved data on startup and saves on exit.
 */
public class LoginFrame extends JFrame {
    private JTextField userIdField, nameField, areaField, regUserIdField;
    private JPasswordField passwordField, regPasswordField;
    private DataStore dataStore;

    public LoginFrame() {
        setTitle("CivicLoop - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);

        // Load existing data (if any)
        try {
            dataStore = DataStore.loadFromFile("civicloop_data.dat");
        } catch (IOException | ClassNotFoundException e) {
            dataStore = new DataStore();  // start fresh if file missing/corrupt
        }

        // Use a tabbed pane to separate Login and Registration
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Login", createLoginPanel());
        tabs.add("Register", createRegisterPanel());
        add(tabs);
    }
   
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lblId = new JLabel("User ID:");
        gbc.gridx=0; gbc.gridy=0; panel.add(lblId, gbc);
        userIdField = new JTextField(10);
        gbc.gridx=1; panel.add(userIdField, gbc);

        JLabel lblPass = new JLabel("Password:");
        gbc.gridx=0; gbc.gridy=1; panel.add(lblPass, gbc);
        passwordField = new JPasswordField(10);
        gbc.gridx=1; panel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Log In");
        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2;
        panel.add(loginBtn, gbc);

        
        // Login action: validate, then open MainFrame
        loginBtn.addActionListener((ActionEvent e) -> {
            String uid = userIdField.getText().trim();
            String pass = new String(passwordField.getPassword());
            if (uid.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }
            var user = dataStore.login(uid, pass);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid User ID or password.");
            } else {
                dispose();  // close login window
                new MainFrame(dataStore, user).setVisible(true);
            }
        });

        return panel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblName = new JLabel("Full Name:");
        gbc.gridx=0; gbc.gridy=0; panel.add(lblName, gbc);
        nameField = new JTextField(10);
        gbc.gridx=1; panel.add(nameField, gbc);

        JLabel lblArea = new JLabel("Area:");
        gbc.gridx=0; gbc.gridy=1; panel.add(lblArea, gbc);
        areaField = new JTextField(10);
        gbc.gridx=1; panel.add(areaField, gbc);
JLabel lblPass = new JLabel("Password:");
        gbc.gridx=0; gbc.gridy=2; panel.add(lblPass, gbc);
        regPasswordField = new JPasswordField(10);
        gbc.gridx=1; panel.add(regPasswordField, gbc);

        JButton regBtn = new JButton("Register");
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2;
        panel.add(regBtn, gbc);

        regBtn.addActionListener((ActionEvent e) -> {
            String name = nameField.getText().trim();
            String area = areaField.getText().trim();
            String pass = new String(regPasswordField.getPassword());
            if (name.isEmpty() || area.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }
            String newId = dataStore.registerUser(name, area, pass);
            JOptionPane.showMessageDialog(this,
                    "Registration successful! Your User ID is: " + newId);
            // Clear fields
            nameField.setText("");
            areaField.setText("");
            
            regPasswordField.setText("");
        });

        return panel;
    }
    
    // Save data when the whole application window closes

    @Override
    public void dispose() {
        try {
            dataStore.saveToFile("civicloop_data.dat");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save data: " + ex.getMessage());
        }
        super.dispose();
    }
}
