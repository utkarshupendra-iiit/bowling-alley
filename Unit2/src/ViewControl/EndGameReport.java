package ViewControl;

import Model.Bowler;
import Model.Party;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

public class EndGameReport implements ActionListener, ListSelectionListener {

	private JFrame win;
	private JButton printButton, finished;
	private JList memberList;
	private Vector retVal;
	private int result;

	private String selectedMember;

	public EndGameReport( String partyName, Party party ) {
	
		result = 0;
		retVal = new Vector();
		win = new JFrame("End Game Report for " + partyName + "?" );
		win.getContentPane().setLayout(new BorderLayout());
		((JPanel) win.getContentPane()).setOpaque(false);

		JPanel colPanel = new JPanel();
		colPanel.setLayout(new GridLayout( 1, 2 ));

		// Member Panel
		JPanel partyPanel = new JPanel();
		partyPanel.setOpaque(true);  // needed for JLabel to show the background color
		partyPanel.setBackground(new Color(204, 238, 241));
		partyPanel.setLayout(new FlowLayout());
		partyPanel.setBorder(new TitledBorder("Party Members"));
		
		Vector myVector = new Vector();
		Iterator iter = (party.getMembers()).iterator();
		while (iter.hasNext()){
			myVector.add( ((Bowler)iter.next()).getNick() );
		}	
		memberList = new JList(myVector);
		memberList.setFixedCellWidth(120);
		memberList.setVisibleRowCount(5);
		memberList.addListSelectionListener(this);
		JScrollPane partyPane = new JScrollPane(memberList);
		partyPanel.add(partyPane);

		partyPanel.add( memberList );

		// Button Panel
		// Button Panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(true);  // needed for JLabel to show the background color
		buttonPanel.setBackground(new Color(204, 238, 241));
		buttonPanel.setLayout(new GridLayout(2, 1));

		Insets buttonMargin = new Insets(4, 4, 4, 4);

		printButton = new JButton("Print Report");
		JPanel printButtonPanel = new JPanel();
		printButton.setForeground(Color.red);
		printButton.setIcon(new ImageIcon(getScaledImage(printButtonPanel,
				"print")));
		printButtonPanel.setLayout(new FlowLayout());
		printButton.addActionListener(this);
		printButtonPanel.add(printButton);

		finished = new JButton("Finished");
		JPanel finishedPanel = new JPanel();
		finished.setForeground(Color.blue);
		finished.setIcon(new ImageIcon(getScaledImage(finishedPanel,
				"finish")));
		finishedPanel.setLayout(new FlowLayout());
		finished.addActionListener(this);
		finishedPanel.add(finished);

		buttonPanel.add(printButton);
		buttonPanel.add(finished);

		// Clean up main panel
		colPanel.add(partyPanel);
		colPanel.add(buttonPanel);

		win.getContentPane().add("Center", colPanel);

		win.pack();

		// Center Window on Screen
		Dimension screenSize = (Toolkit.getDefaultToolkit()).getScreenSize();
		win.setLocation(
				((screenSize.width) / 2) - ((win.getSize().width) / 2),
				((screenSize.height) / 2) - ((win.getSize().height) / 2));
		win.setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(printButton)) {		
			//Add selected to the vector.
			retVal.add(selectedMember);
		}
		if (e.getSource().equals(finished)) {
			win.setVisible(false);
			result = 1;
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		selectedMember =
			((String) ((JList) e.getSource()).getSelectedValue());
	}

	public Vector getResult() {
		while ( result == 0 ) {
			try {
				Thread.sleep(10);
			} catch ( InterruptedException e ) {
				System.err.println( "Interrupted" );
			}
		}
		return retVal;	
	}
	private Image getScaledImage(JPanel label, String emo) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(String.format("Unit2/resources/%s.png", emo)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img.getScaledInstance(20, 20, Image.SCALE_SMOOTH); // please check once giving error
	}
	
	public void destroy() {
		win.setVisible(false);
	}

	public static void main( String args[] ) {
		Vector bowlers = new Vector();
		for ( int i=0; i<4; i++ ) {
			bowlers.add( new Bowler( "aaaaa", "aaaaa", "aaaaa" ) );
		}
		Party party = new Party( bowlers );
		String partyName="wank";
		EndGameReport e = new EndGameReport( partyName, party );
	}	
}