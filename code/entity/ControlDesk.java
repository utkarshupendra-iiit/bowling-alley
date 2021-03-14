package entity;

import observer.ControlDeskObserver;
import persistence.BowlerDb;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class ControlDesk extends Thread {

	/** The collection of Lanes */
	private HashSet lanes;

	/** The party wait queue */
	private Queue partyQueue;

	/** The number of lanes represented */
	private int numLanes;
	
	/** The collection of subscribers */
	private Vector subscribers;

	public ControlDesk(int numLanes) {
		this.numLanes = numLanes;
		lanes = new HashSet(numLanes);
		partyQueue = new Queue();

		subscribers = new Vector();

		for (int i = 0; i < numLanes; i++) {
			lanes.add(new Lane());
		}
		
		this.start();

	}
	
	/**
	 * Main loop for entity.ControlDesk's thread
	 * 
	 */
	public void run() {
		while (true) {
			
			assignLane();
			
			try {
				sleep(250);
			} catch (Exception e) {}
		}
	}
		

    /**
     * Retrieves a matching entity.Bowler from the bowler database.
     *
     * @param nickName	The NickName of the entity.Bowler
     *
     * @return a entity.Bowler object.
     *
     */

	private Bowler registerPatron(String nickName) {
		Bowler patron = null;

		try {
			// only one patron / nick.... no dupes, no checks

			patron = BowlerDb.getBowlerInfo(nickName);

		} catch (SQLException e) {
			System.err.println("Error..." + e);
		}

		return patron;
	}

    /**
     * Iterate through the available lanes and assign the paties in the wait queue if lanes are available.
     *
     */

	public void assignLane() {
		Iterator it = lanes.iterator();

		while (it.hasNext() && partyQueue.hasMoreElements()) {
			Lane curLane = (Lane) it.next();

			if (curLane.getGame().getParty() == null) {
				System.out.println("ok... assigning this party");
				curLane.getGame().assignParty(((Party) partyQueue.next()));
			}
		}
		publish(getPartyQueue());
	}

    /**
     */

	public void viewScores(Lane ln) {
		// TODO: attach a LaneScoreView object to that lane
	}

    /**
     * Creates a party from a Vector of nickNAmes and adds them to the wait queue.
     *
     * @param partyNicks	A Vector of NickNames
     *
     */

	public void addPartyQueue(Vector partyNicks) {
		Vector partyBowlers = new Vector();
		for (int i = 0; i < partyNicks.size(); i++) {
			Bowler newBowler = registerPatron(((String) partyNicks.get(i)));
			partyBowlers.add(newBowler);
		}
		Party newParty = new Party(partyBowlers);
		partyQueue.add(newParty);
		publish(getPartyQueue());
	}

    /**
     * Returns a Vector of party names to be displayed in the GUI representation of the wait queue.
	 *
     * @return a Vecotr of Strings
     *
     */

	public Vector getPartyQueue() {
		Vector displayPartyQueue = new Vector();
		for (int i = 0; i < partyQueue.asVector().size(); i++ ) {
			String nextParty =
				((Bowler) ((Party) partyQueue.asVector().get( i ) ).getMembers()
					.get(0))
					.getNickName() + "'s entity.Party";
			displayPartyQueue.addElement(nextParty);
		}
		return displayPartyQueue;
	}

    /**
     * Accessor for the number of lanes represented by the entity.ControlDesk
     * 
     * @return an int containing the number of lanes represented
     *
     */

	public int getNumLanes() {
		return numLanes;
	}

    /**
     * Allows objects to subscribe as observers
     * 
     * @param adding	the observer.ControlDeskObserver that will be subscribed
     *
     */

	public void subscribe(ControlDeskObserver adding) {
		subscribers.add(adding);
	}

    /**
     * Broadcast an event to subscribing objects.
     * 
     * @param event	the events.ControlDeskEvent to broadcast
     *
     */

	public void publish(Vector partyQueue) {
		Iterator eventIterator = subscribers.iterator();
		while (eventIterator.hasNext()) {
			(
				(ControlDeskObserver) eventIterator
					.next())
					.receiveControlDeskEvent(partyQueue);
		}
	}

    /**
     * Accessor method for lanes
     * 
     * @return a HashSet of Lanes
     *
     */

	public HashSet getLanes() {
		return lanes;
	}
}
