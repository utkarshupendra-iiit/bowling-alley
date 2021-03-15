package ViewControl;

import javax.swing.*;
import java.awt.*;

public class UserLaneView extends JFrame {
    private JLabel msg;
    private Container c;

    public UserLaneView(String username) {
        setTitle("User View Window");
        setBounds(300, 90, 500, 200);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setResizable(false);

        c = getContentPane();
        c.setLayout(null);

        msg = new JLabel("Development of User View is in progress. Please login as Admin.");
        setComponentProperties(msg, 10, 200, 20, 70, 80);
        c.add(msg);

        setVisible(true);
    }

    private void setComponentProperties(Component component, int fontSize, int width, int height, int x, int y) {
        component.setFont(new Font("Arial", Font.PLAIN, fontSize));
        component.setSize(width, height);
        component.setLocation(x, y);
    }
}
