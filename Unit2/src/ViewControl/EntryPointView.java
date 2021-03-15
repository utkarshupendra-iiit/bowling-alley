package ViewControl;

import Model.ControlDesk;
import persistence.ReferenceLRUCache;
import persistence.UserDb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Constructor for GUI used to Add Parties to the waiting party queue.
 */

public class EntryPointView extends JFrame implements ActionListener, ItemListener {

    private Container c;

    private JLabel username;
    private JTextField usernameInput;

    private JLabel password;
    private JTextField passwordInput;

    private JLabel loginAs;
    private JComboBox loginAsInput;

    private JButton login, register;

    public EntryPointView() {
        setTitle("Login Window");
        setBounds(300, 90, 600, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        c = getContentPane();
        c.setLayout(null);

        username = new JLabel("Username: ");
        setComponentProperties(username, 20, 200, 20, 100, 50);
        c.add(username);

        usernameInput = new JTextField();
        setComponentProperties(usernameInput, 15, 190, 20, 250, 50);
        c.add(usernameInput);

        password = new JLabel("Password: ");
        setComponentProperties(password, 20, 200, 20, 100, 100);
        c.add(password);

        passwordInput = new JPasswordField();
        setComponentProperties(passwordInput, 15, 190, 20, 250, 100);
        c.add(passwordInput);

        loginAs = new JLabel("Login As: ");
        setComponentProperties(loginAs, 20, 200, 20, 100, 150);
        c.add(loginAs);

        String[] opts = new String[]{"USER", "ADMIN"};
        loginAsInput = createComboBox(250, 150, opts);
        c.add(loginAsInput);

        login = new JButton("Login");
        setComponentProperties(login, 15, 100, 20, 150, 200);
        login.addActionListener(this);
        c.add(login);

        register = new JButton("Register");
        setComponentProperties(register, 15, 100, 20, 270, 200);
        register.addActionListener(this);
        c.add(register);

        setVisible(true);
    }

    private void setComponentProperties(Component component, int fontSize, int width, int height, int x, int y) {
        component.setFont(new Font("Arial", Font.PLAIN, fontSize));
        component.setSize(width, height);
        component.setLocation(x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(login)) {
            String id = usernameInput.getText();
            String password = passwordInput.getText();
            String type = (String) loginAsInput.getSelectedItem();
            try {
                List<String> dbType = UserDb.getUser(id, password);
                if (!dbType.contains(type)) {
                    JOptionPane.showMessageDialog(c,
                            "Error while logging in. Incorrect login info or user type selected");
                    return;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if (Objects.nonNull(type)) {
                try {
                    if (type.equals("ADMIN")) {
                        ControlDesk controlDesk = new ControlDesk(Integer.parseInt(ReferenceLRUCache.getEntry("NUM_LANES")));
                        ControlDeskView cdv = new ControlDeskView(controlDesk, Integer.parseInt(ReferenceLRUCache.getEntry("MAX_PATRONS_PER_ALLEY")));
                        controlDesk.addObserver(cdv);
                        setVisible(false);
                    } else if (type.equals("USER")) {
                        UserLaneView userLaneView = new UserLaneView(id);
                    }
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(c, "Error in the configuration. Please contact IT Admin");
                }
            } else {
                JOptionPane.showMessageDialog(c,
                        "Error while logging in. Please try again.");
            }
        } else if (e.getSource().equals(register)) {
            String id = usernameInput.getText();
            String password = passwordInput.getText();
            String type = (String) loginAsInput.getSelectedItem();
            try {
                if (UserDb.isRegistered(id)) {
                    JOptionPane.showMessageDialog(c,
                            "User already registered. Please try to login.");
                } else {
                    UserDb.putUser(id, password, type);
                    JOptionPane.showMessageDialog(c,
                            "User Registration Successful. Please login.");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
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