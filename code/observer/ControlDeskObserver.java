package observer;/* observer.ControlDeskObserver.java
 *
 *  Version
 *  $Id$
 * 
 *  Revisions:
 * 		$Log$
 * 
 */

import entity.Queue;
import java.util.Vector;

/**
 * Interface for classes that observe control desk events
 *
 */

public interface ControlDeskObserver {

	public void receiveControlDeskEvent(Vector partyQueue);

}
