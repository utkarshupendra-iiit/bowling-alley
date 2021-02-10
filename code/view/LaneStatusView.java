package view; /**
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import entity.Bowler;
import entity.Lane;
import entity.Pinsetter;
import events.LaneEvent;
import events.PinsetterEvent;
import observer.LaneObserver;
import observer.PinsetterObserver;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;
import java.util.Iterator;
import entity.ScoreReport;

public class LaneStatusView implements ActionListener, LaneObserver, PinsetterObserver {

	private JPanel jp;

	private JLabel curBowler, foul, pinsDown;
	private JButton viewLane;
	private JButton viewPinSetter, maintenance;

	private PinSetterView psv;
	private LaneView lv;
	private Lane lane;
	int laneNum;

	boolean laneShowing;
	boolean psShowing;

	public LaneStatusView(Lane lane, int laneNum ) {

		this.lane = lane;
		this.laneNum = laneNum;

		laneShowing=false;
		psShowing=false;

		psv = new PinSetterView( laneNum );
		Pinsetter ps = lane.getPinsetter();
		ps.subscribe(psv);

		lv = new LaneView( lane, laneNum );
		lane.subscribe(lv);


		jp = new JPanel();
		jp.setLayout(new FlowLayout());
		JLabel cLabel = new JLabel( "Now Bowling: " );
		curBowler = new JLabel( "(no one)" );
		JLabel fLabel = new JLabel( "Foul: " );
		foul = new JLabel( " " );
		JLabel pdLabel = new JLabel( "Pins Down: " );
		pinsDown = new JLabel( "0" );

		// Button Panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		//unused variables =>Insets buttonMargin = new Insets(4, 4, 4, 4);

		Cbutton cb = new Cbutton(this);

		viewLane = cb.createButton("View Lane", buttonPanel);
		viewPinSetter = cb.createButton("Pinsetter", buttonPanel);
		maintenance = cb.createButton("     ", buttonPanel);
		maintenance.setBackground( Color.GREEN );

		viewLane.setEnabled( false );
		viewPinSetter.setEnabled( false );


		jp.add( cLabel );
		jp.add( curBowler );
//		jp.add( fLabel );
//		jp.add( foul );
		jp.add( pdLabel );
		jp.add( pinsDown );

		jp.add(buttonPanel);


	}

	public JPanel showLane() {
		return jp;
	}

	public void actionPerformed( ActionEvent e ) {
		if ( lane.isPartyAssigned() ) {
			if (e.getSource().equals(viewPinSetter)) {
				if ( psShowing == false ) {
					psv.show();
					psShowing=true;
				} else if ( psShowing == true ) {
					psv.hide();
					psShowing=false;
				}
			}
		}
		if (e.getSource().equals(viewLane)) {
			if ( lane.isPartyAssigned() ) {
				if ( laneShowing == false ) {
					lv.show();
					laneShowing=true;
				} else if ( laneShowing == true ) {
					lv.hide();
					laneShowing=false;
				}
			}
		}
		if (e.getSource().equals(maintenance)) {
			if ( lane.isPartyAssigned() ) {
				lane.unPauseGame();
				maintenance.setBackground( Color.GREEN );
			}
		}
	}

	public void receiveLaneEvent(LaneEvent le) {
		if (lane.isPartyAssigned() && lane.isGameFinished()) {

			EndGamePrompt egp = new EndGamePrompt( ((Bowler) le.getParty().getMembers().get(0)).getNickName() + "'s Party" );
			int result = egp.getResult();
			egp.distroy();
			egp = null;
			System.out.println("result was: " + result);

			// TODO: send record of scores to control desk

			if (result == 1) {					// yes, want to play again
				lane.resetScores();
				lane.resetBowlerIterator();

			} else if (result == 2) {// no, dont want to play another game
				Vector printVector;
				EndGameReport egr = new EndGameReport( ((Bowler) le.getParty().getMembers().get(0)).getNickName() + "'s Party", le.getParty());
				printVector = egr.getResult();
				//partyAssigned = false;
				Iterator scoreIt = le.getParty().getMembers().iterator();

				lane.clearLane();
				//publish(lanePublish());

				int myIndex = 0;
				while (scoreIt.hasNext()){
					Bowler thisBowler = (Bowler)scoreIt.next();
					ScoreReport sr = new ScoreReport( thisBowler, lane.getFinalScores()[myIndex++], lane.getGameNumber());
					sr.sendEmail(thisBowler.getEmail());
					Iterator printIt = printVector.iterator();
					while (printIt.hasNext()){
						if (thisBowler.getNickName() == (String)printIt.next()){
							System.out.println("Printing " + thisBowler.getNickName());
							sr.sendPrintout();
						}
					}

				}
			}

		}
		curBowler.setText( ( (Bowler)le.getBowler()).getNickName() );

		if ( le.isMechanicalProblem() ) {
			maintenance.setBackground( Color.RED );
		}
		if ( lane.isPartyAssigned() == false ) {
			viewLane.setEnabled( false );
			viewPinSetter.setEnabled( false );
		} else {
			viewLane.setEnabled( true );
			viewPinSetter.setEnabled( true );
		}
	}

	public void receivePinsetterEvent(PinsetterEvent pe) {
		pinsDown.setText( ( new Integer(pe.totalPinsDown()) ).toString() );
//		foul.setText( ( new Boolean(pe.isFoulCommited()) ).toString() );

	}

}