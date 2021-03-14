package view;/* view.AddPartyView.java
 *
 *  Version:
 * 		 $Id$
 *
 *  Revisions:
 * 		$Log: view.AddPartyView.java,v $
 * 		Revision 1.7  2003/02/20 02:05:53  ???
 * 		Fixed addPatron so that duplicates won't be created.
 *
 * 		Revision 1.6  2003/02/09 20:52:46  ???
 * 		Added comments.
 *
 * 		Revision 1.5  2003/02/02 17:42:09  ???
 * 		Made updates to migrate to observer model.
 *
 * 		Revision 1.4  2003/02/02 16:29:52  ???
 * 		Added events.ControlDeskEvent and observer.ControlDeskObserver. Updated entity.Queue to allow access to Vector so that contents could be viewed without destroying. Implemented observer model for most of ControlDesk.
 *
 *
 */

/**
 * Class for GUI components need to add a party
 */

import persistence.SearchDb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Constructor for GUI used to Add Parties to the waiting party queue.
 */

public class QueryView extends JFrame implements ActionListener, ItemListener {

    private Container c;
    private JLabel queryFor;
    private JComboBox queryForInput;
    private JLabel queryUsing;
    private JComboBox queryUsingInput;
    private JLabel queryValue;
    private JTextField queryValueInput;
    private JTable resultTable;

    private JButton sub;
    private JButton reset;

    public QueryView() {
        setTitle("Query Window");
        setBounds(300, 90, 600, 500);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setResizable(false);

        c = getContentPane();
        c.setLayout(null);

        queryFor = new JLabel("Query For: ");
        queryFor.setFont(new Font("Arial", Font.PLAIN, 20));
        queryFor.setSize(200, 20);
        queryFor.setLocation(100, 100);
        c.add(queryFor);

        Object[] tables = new String[0];
        try {
            tables = SearchDb.getTables();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        queryForInput = createComboBox(250, 100, tables);
        queryForInput.addItemListener(this);
        c.add(queryForInput);

        queryUsing = new JLabel("Query Using: ");
        queryUsing.setFont(new Font("Arial", Font.PLAIN, 20));
        queryUsing.setSize(200, 20);
        queryUsing.setLocation(100, 150);
        c.add(queryUsing);

        queryUsingInput = createComboBox(250, 150, new String[]{});
        c.add(queryUsingInput);

        queryValue = new JLabel("Query Value: ");
        queryValue.setFont(new Font("Arial", Font.PLAIN, 20));
        queryValue.setSize(200, 20);
        queryValue.setLocation(100, 200);
        c.add(queryValue);

        queryValueInput = new JTextField();
        queryValueInput.setFont(new Font("Arial", Font.PLAIN, 15));
        queryValueInput.setSize(190, 20);
        queryValueInput.setLocation(250, 200);
        c.add(queryValueInput);


        sub = new JButton("Submit");
        sub.setFont(new Font("Arial", Font.PLAIN, 15));
        sub.setSize(100, 20);
        sub.setLocation(150, 450);
        sub.addActionListener(this);
        c.add(sub);

        reset = new JButton("Reset");
        reset.setFont(new Font("Arial", Font.PLAIN, 15));
        reset.setSize(100, 20);
        reset.setLocation(270, 450);
        reset.addActionListener(this);
        c.add(reset);

        setVisible(true);
    }

    private JTable createTable(int x, int y, Object[] cols, Object[][] data) {
        JTable table = new JTable(data, cols);
        table.setBounds(x, y, 300, 150);
        return table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(sub)) {
            System.out.println(queryForInput.getSelectedItem() + "," + queryUsingInput.getSelectedItem() + "," + queryValueInput.getText());
            java.util.List<Map<String, String>> result = null;
            try {
                result = SearchDb.getQueryResult(String.valueOf(queryForInput.getSelectedItem()),
                        String.valueOf(queryUsingInput.getSelectedItem()),
                        queryValueInput.getText());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            System.out.println(result);
            try {
                Object[] columns = SearchDb.getColumnsByTable(String.valueOf(queryForInput.getSelectedItem()));
                Object[][] data = parseMapToData(result, columns.length);
                QueryResultView queryResultView = new QueryResultView(columns, data);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (e.getSource().equals(reset)) {
            queryForInput.setSelectedItem(queryForInput.getItemAt(0));
            queryValueInput.setText("");
        }
    }

    private Object[][] parseMapToData(List<Map<String, String>> result, int numCols) {
        Object[][] res = new Object[result.size()][numCols];
        int i = 0;
        for (Map<String, String> map : result) {
            int j = 0;
            for (String key : map.keySet()) {
                res[i][j] = map.get(key);
                j++;
            }
            i++;
        }
        return res;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (e.getSource().equals(queryForInput)) {
                System.out.println(e.getItem());
                Object[] cols;
                try {
                    cols = SearchDb.getColumnsByTable((String) e.getItem());
                    setItemsToComboBox(cols, queryUsingInput);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    private JComboBox createComboBox(int x, int y, Object[] content) {
        JComboBox box = new JComboBox(content);
        box.setFont(new Font("Arial", Font.PLAIN, 15));
        box.setSize(190, 20);
        box.setLocation(x, y);
        return box;
    }

    private void setItemsToComboBox(Object[] items, JComboBox box) {
        box.removeAllItems();
        for (Object item : items) {
            box.addItem(item);
        }
    }
}
