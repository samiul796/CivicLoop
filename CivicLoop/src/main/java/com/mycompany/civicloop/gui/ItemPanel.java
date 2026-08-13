package civicloop.gui;

import civicloop.model.Item;
import civicloop.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ItemPanel extends JPanel {
    private MainFrame parent;
    private JTable itemTable;
    private DefaultTableModel tableModel;

    public ItemPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"ID","Name","Owner","Available"}, 0);
        itemTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(itemTable);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Item");
        JButton requestBtn = new JButton("Request Selected Item");
        bottom.add(addBtn);
        bottom.add(requestBtn);
        add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addItem());
        requestBtn.addActionListener(e -> requestItem());
        refreshTable();
    }

    private void addItem() {
        String name = JOptionPane.showInputDialog(this, "Enter item name:");
        if (name == null || name.trim().isEmpty()) return;
        parent.getDataStore().addItem(name.trim(), parent.getCurrentUser());
        parent.refreshAll();
    }

    private void requestItem() {
        int row = itemTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }
        String itemId = (String) tableModel.getValueAt(row, 0);
        String hoursStr = JOptionPane.showInputDialog(this, "How many hours to borrow?");
        if (hoursStr == null) return;
        try {
            double hours = Double.parseDouble(hoursStr);
            if (hours <= 0) throw new IllegalArgumentException("Hours must be positive.");
            String result = parent.getDataStore().requestItem(itemId, parent.getCurrentUser(), hours);
            JOptionPane.showMessageDialog(this, result);
            parent.refreshAll();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number - please enter a valid decimal.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Item i : parent.getDataStore().getItems()) {
            User owner = parent.getDataStore().findUser(i.getOwnerId());
            String ownerName = owner != null ? owner.getName() : "Unknown";
            tableModel.addRow(new Object[]{
                    i.getItemId(), i.getItemName(), ownerName,
                    i.isAvailable() ? "Available" : "Borrowed"
            });
        }
    }
}