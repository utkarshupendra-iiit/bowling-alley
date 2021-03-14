package observer;/* $Id$
 *
 * Revisions:
 *   $Log: observer.LaneObserver.java,v $
 *   Revision 1.2  2003/01/30 21:44:25  ???
 *   Fixed speling of received in may places.
 *
 *   Revision 1.1  2003/01/19 22:12:40  ???
 *   created laneevent and laneobserver
 *
 *
 */

import entity.Bowler;
import entity.Party;

import java.util.HashMap;

public interface GameObserver {
	public void receiveGameEvent(Party p, int bI, Bowler cT, float[][] cS, HashMap scores, int frameNum, int[] curScores, int ball);
};

