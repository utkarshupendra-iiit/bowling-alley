package events;/*  $Id$
 *
 *  Revisions:
 *    $Log: events.LaneEvent.java,v $
 *    Revision 1.6  2003/02/16 22:59:34  ???
 *    added mechnanical problem flag
 *
 *    Revision 1.5  2003/02/02 23:55:31  ???
 *    Many many changes.
 *
 *    Revision 1.4  2003/02/02 22:44:26  ???
 *    More data.
 *
 *    Revision 1.3  2003/02/02 17:49:31  ???
 *    Modified.
 *
 *    Revision 1.2  2003/01/30 21:21:07  ???
 *    *** empty log message ***
 *
 *    Revision 1.1  2003/01/19 22:12:40  ???
 *    created laneevent and laneobserver
 *
 *
 */

import entity.Bowler;
import entity.Party;

import java.util.HashMap;

public class LaneEvent {

	boolean mechProb;
	
	public LaneEvent(boolean mechProblem) {
		mechProb = mechProblem;
	}
	public boolean isMechanicalProblem() {
		return mechProb;
	}
}
 
