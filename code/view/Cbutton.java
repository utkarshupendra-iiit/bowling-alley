package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Cbutton {
    ActionListener actionListener;
    public Cbutton(ActionListener actionListener) {
        this.actionListener = actionListener;
    }
    
    public JButton createButton(String text, JPanel panel) {
        JButton button = new JButton(text);
		JPanel tp = new JPanel();
		tp.setLayout(new FlowLayout());
		button.addActionListener(actionListener);
		tp.add(button);
		panel.add(tp);
		return button;
    }
}
