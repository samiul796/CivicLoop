package civicloop.gui;

import civicloop.model.TimeCreditTransaction;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TimeBankPanel extends JPanel {
    private MainFrame parent;
    private JLabel balanceLabel;
    private JTable txTable;
    private DefaultTableModel tableModel;

    public TimeBankPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(balanceLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"ID","From","To","Hours","Credits","Type"}, 0);
        txTable = new JTable(tableModel);
        add(new JScrollPane(txTable), BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        String uid = parent.getCurrentUser().getUserId();
        balanceLabel.setText("Your TimeCredit Balance: " +
                parent.getCurrentUser().getTimeCreditBalance() + " TC");

        tableModel.setRowCount(0);
        for (TimeCreditTransaction t : parent.getDataStore().getTransactions()) {
            if (t.getFromUserId().equals(uid) || t.getToUserId().equals(uid)) {
                tableModel.addRow(new Object[]{
                        t.getTransactionId(),
                        t.getFromUserId(),
                        t.getToUserId(),
                        t.getHoursSpent(),
                        t.getCreditAmount(),
                        t.getType()
                });
            }
        }
    }
}