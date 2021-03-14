package ViewControl;

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

    private JButton sub, reset, queryPlayerScoreAvg, queryPlayerScoreMax, queryPlayerScoreMin, listAllBowlers;

    public QueryView() {
        setTitle("Query Window");
        setBounds(300, 90, 600, 600);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setResizable(false);

        c = getContentPane();
        c.setLayout(null);

        queryPlayerScoreAvg = new JButton("Player Wise Average Score");
        queryPlayerScoreAvg.setFont(new Font("Arial", Font.PLAIN, 10));
        queryPlayerScoreAvg.setSize(150, 20);
        queryPlayerScoreAvg.setLocation(100, 100);
        queryPlayerScoreAvg.addActionListener(this);
        c.add(queryPlayerScoreAvg);

        queryPlayerScoreMax = new JButton("Player Wise Max Score");
        queryPlayerScoreMax.setFont(new Font("Arial", Font.PLAIN, 10));
        queryPlayerScoreMax.setSize(150, 20);
        queryPlayerScoreMax.setLocation(270, 100);
        queryPlayerScoreMax.addActionListener(this);
        c.add(queryPlayerScoreMax);

        queryPlayerScoreMin = new JButton("Player Wise Min Score");
        queryPlayerScoreMin.setFont(new Font("Arial", Font.PLAIN, 10));
        queryPlayerScoreMin.setSize(150, 20);
        queryPlayerScoreMin.setLocation(270, 50);
        queryPlayerScoreMin.addActionListener(this);
        c.add(queryPlayerScoreMin);

        listAllBowlers = new JButton("List All Bowlers");
        listAllBowlers.setFont(new Font("Arial", Font.PLAIN, 10));
        listAllBowlers.setSize(150, 20);
        listAllBowlers.setLocation(100, 50);
        listAllBowlers.addActionListener(this);
        c.add(listAllBowlers);

        queryFor = new JLabel("Query For: ");
        queryFor.setFont(new Font("Arial", Font.PLAIN, 20));
        queryFor.setSize(200, 20);
        queryFor.setLocation(100, 150);
        c.add(queryFor);

        Object[] tables = new String[0];
        Object[] cols = new String[0];
        try {
            tables = SearchDb.getTables();
            cols = SearchDb.getColumnsByTable(String.valueOf(tables[0]));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        queryForInput = createComboBox(250, 150, tables);
        queryForInput.addItemListener(this);
        c.add(queryForInput);

        queryUsing = new JLabel("Query Using: ");
        queryUsing.setFont(new Font("Arial", Font.PLAIN, 20));
        queryUsing.setSize(200, 20);
        queryUsing.setLocation(100, 200);
        c.add(queryUsing);

        queryUsingInput = createComboBox(250, 200, cols);
        c.add(queryUsingInput);

        queryValue = new JLabel("Query Value: ");
        queryValue.setFont(new Font("Arial", Font.PLAIN, 20));
        queryValue.setSize(200, 20);
        queryValue.setLocation(100, 250);
        c.add(queryValue);

        queryValueInput = new JTextField();
        queryValueInput.setFont(new Font("Arial", Font.PLAIN, 15));
        queryValueInput.setSize(190, 20);
        queryValueInput.setLocation(250, 250);
        c.add(queryValueInput);


        sub = new JButton("Submit");
        sub.setFont(new Font("Arial", Font.PLAIN, 15));
        sub.setSize(100, 20);
        sub.setLocation(150, 500);
        sub.addActionListener(this);
        c.add(sub);

        reset = new JButton("Reset");
        reset.setFont(new Font("Arial", Font.PLAIN, 15));
        reset.setSize(100, 20);
        reset.setLocation(270, 500);
        reset.addActionListener(this);
        c.add(reset);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource().equals(sub)) {
                System.out.println(queryForInput.getSelectedItem() + "," + queryUsingInput.getSelectedItem() + "," + queryValueInput.getText());
                java.util.List<Map<String, String>> result = null;

                result = SearchDb.getQueryResult(String.valueOf(queryForInput.getSelectedItem()),
                        String.valueOf(queryUsingInput.getSelectedItem()),
                        queryValueInput.getText());

                Object[] columns = SearchDb.getColumnsByTable(String.valueOf(queryForInput.getSelectedItem()));
                Object[][] data = parseMapToData(result, columns.length);
                QueryResultView queryResultView = new QueryResultView(columns, data);

            }
            if (e.getSource().equals(reset)) {
                queryForInput.setSelectedItem(queryForInput.getItemAt(0));
                queryValueInput.setText("");
            }
            if (e.getSource().equals(queryPlayerScoreAvg)) {
                List<Map<String, String>> result = SearchDb.getPlayerWiseScores("AVG");
                Object[] cols = new Object[]{"Name", "Average Score"};
                Object[][] data = parseMapToData(result, cols.length);
                QueryResultView queryResultView = new QueryResultView(cols, data);
            }
            if (e.getSource().equals(queryPlayerScoreMax)) {
                List<Map<String, String>> result = SearchDb.getPlayerWiseScores("MAX");
                Object[] cols = new Object[]{"Name", "Max Score"};
                Object[][] data = parseMapToData(result, cols.length);
                QueryResultView queryResultView = new QueryResultView(cols, data);
            }
            if (e.getSource().equals(queryPlayerScoreMin)) {
                List<Map<String, String>> result = SearchDb.getPlayerWiseScores("MIN");
                Object[] cols = new Object[]{"Name", "Min Score"};
                Object[][] data = parseMapToData(result, cols.length);
                QueryResultView queryResultView = new QueryResultView(cols, data);
            } if (e.getSource().equals(listAllBowlers)) {
                List<Map<String, String>> result = SearchDb.getAllBowlers();
                Object[] cols = SearchDb.getColumnsByTable(String.valueOf(queryForInput.getSelectedItem()));
                Object[][] data = parseMapToData(result, cols.length);
                QueryResultView queryResultView = new QueryResultView(cols, data);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
