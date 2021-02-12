package entity;
/* $Id$
 *
 * Revisions:
 *   $Log: entity.Lane.java,v $
 *   Revision 1.52  2003/02/20 20:27:45  ???
 *   Fouls disables.
 *
 *   Revision 1.51  2003/02/20 20:01:32  ???
 *   Added things.
 *
 *   Revision 1.50  2003/02/20 19:53:52  ???
 *   Added foul support.  Still need to update laneview and test this.
 *
 *   Revision 1.49  2003/02/20 11:18:22  ???
 *   Works beautifully.
 *
 *   Revision 1.48  2003/02/20 04:10:58  ???
 *   entity.Score reporting code should be good.
 *
 *   Revision 1.47  2003/02/17 00:25:28  ???
 *   Added disbale controls for View objects.
 *
 *   Revision 1.46  2003/02/17 00:20:47  ???
 *   fix for event when game ends
 *
 *   Revision 1.43  2003/02/17 00:09:42  ???
 *   fix for event when game ends
 *
 *   Revision 1.42  2003/02/17 00:03:34  ???
 *   Bug fixed
 *
 *   Revision 1.41  2003/02/16 23:59:49  ???
 *   Reporting of sorts.
 *
 *   Revision 1.40  2003/02/16 23:44:33  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.39  2003/02/16 23:43:08  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.38  2003/02/16 23:41:05  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.37  2003/02/16 23:00:26  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.36  2003/02/16 21:31:04  ???
 *   entity.Score logging.
 *
 *   Revision 1.35  2003/02/09 21:38:00  ???
 *   Added lots of comments
 *
 *   Revision 1.34  2003/02/06 00:27:46  ???
 *   Fixed a race condition
 *
 *   Revision 1.33  2003/02/05 11:16:34  ???
 *   Boom-Shacka-Lacka!!!
 *
 *   Revision 1.32  2003/02/05 01:15:19  ???
 *   Real close now.  Honest.
 *
 *   Revision 1.31  2003/02/04 22:02:04  ???
 *   Still not quite working...
 *
 *   Revision 1.30  2003/02/04 13:33:04  ???
 *   entity.Lane may very well work now.
 *
 *   Revision 1.29  2003/02/02 23:57:27  ???
 *   fix on pinsetter hack
 *
 *   Revision 1.28  2003/02/02 23:49:48  ???
 *   entity.Pinsetter generates an event when all pins are reset
 *
 *   Revision 1.27  2003/02/02 23:26:32  ???
 *   entity.ControlDesk now runs its own thread and polls for free lanes to assign queue members to
 *
 *   Revision 1.26  2003/02/02 23:11:42  ???
 *   parties can now play more than 1 game on a lane, and lanes are properly released after games
 *
 *   Revision 1.25  2003/02/02 22:52:19  ???
 *   entity.Lane compiles
 *
 *   Revision 1.24  2003/02/02 22:50:10  ???
 *   entity.Lane compiles
 *
 *   Revision 1.23  2003/02/02 22:47:34  ???
 *   More observering.
 *
 *   Revision 1.22  2003/02/02 22:15:40  ???
 *   Add accessor for pinsetter.
 *
 *   Revision 1.21  2003/02/02 21:59:20  ???
 *   added conditions for the party choosing to play another game
 *
 *   Revision 1.20  2003/02/02 21:51:54  ???
 *   events.LaneEvent may very well be observer method.
 *
 *   Revision 1.19  2003/02/02 20:28:59  ???
 *   fixed sleep thread bug in lane
 *
 *   Revision 1.18  2003/02/02 18:18:51  ???
 *   more changes. just need to fix scoring.
 *
 *   Revision 1.17  2003/02/02 17:47:02  ???
 *   Things are pretty close to working now...
 *
 *   Revision 1.16  2003/01/30 22:09:32  ???
 *   Worked on scoring.
 *
 *   Revision 1.15  2003/01/30 21:45:08  ???
 *   Fixed speling of received in entity.Lane.
 *
 *   Revision 1.14  2003/01/30 21:29:30  ???
 *   Fixed some MVC stuff
 *
 *   Revision 1.13  2003/01/30 03:45:26  ???
 *   *** empty log message ***
 *
 *   Revision 1.12  2003/01/26 23:16:10  ???
 *   Improved thread handeling in lane/controldesk
 *
 *   Revision 1.11  2003/01/26 22:34:44  ???
 *   Total rewrite of lane and pinsetter for R2's observer model
 *   Added entity.Lane/entity.Pinsetter Observer
 *   Rewrite of scoring algorythm in lane
 *
 *   Revision 1.10  2003/01/26 20:44:05  ???
 *   small changes
 *
 *
 */

import events.LaneEvent;
import observer.LaneObserver;
import view.EndGamePrompt;
import view.EndGameReport;

import java.util.Iterator;
import java.util.Vector;

public class Lane extends Thread {
    private Game game;
    private Vector subscribers;
    private boolean halted;

    public Game getGame() {
        return game;
    }

    public Lane() {
        this.game = new Game();
        this.subscribers = new Vector();
        this.start();
    }

    public void maintenanceCall() {
        setHalted(true);
        publish(new LaneEvent(true));
    }

    public void setHalted(boolean halted) {
        this.halted = halted;
    }

    public void resumeGame() {
        setHalted(false);
        game.publish();
    }

    public void publish(LaneEvent event) {
        if (subscribers.size() > 0) {
            Iterator eventIterator = subscribers.iterator();

            while (eventIterator.hasNext()) {
                ((LaneObserver) eventIterator.next()).receiveLaneEvent(event);
            }
        }
    }

    /**
     * run()
     * <p>
     * entry point for execution of this lane
     */
    public void run() {
        while (true) {
            if (game.getParty() != null && !game.isFinished()) {    // we have a party on this lane,
                // so next bower can take a throw
                game.startGame();
                while (halted) {
                    try {
                        sleep(10);
                    } catch (Exception e) {
                    }
                }
            } else if (game.getParty() != null && game.isFinished()) {
                EndGamePrompt egp = new EndGamePrompt(((Bowler) game.getParty().getMembers().get(0)).getNickName() + "'s Party");
                int result = egp.getResult();
                egp.distroy();
                System.out.println("result was: " + result);

                // TODO: send record of scores to control desk
                if (result == 1) {                    // yes, want to play again
                    game.resetScores();
                    game.resetBowlerIterator();

                } else if (result == 2) {// no, dont want to play another game
                    Vector printVector;
                    EndGameReport egr = new EndGameReport(((Bowler) game.getParty().getMembers().get(0)).getNickName() + "'s Party", game.getParty());
                    printVector = egr.getResult();
                    Iterator scoreIt = game.getParty().getMembers().iterator();
                    game.setParty(null);
                    game.publish();
                    int myIndex = 0;
                    while (scoreIt.hasNext()) {
                        Bowler thisBowler = (Bowler) scoreIt.next();
                        ScoreReport sr = new ScoreReport(thisBowler, game.getFinalScores()[myIndex++], game.getGameNumber());
                        sr.sendEmail(thisBowler.getEmail());
                        Iterator printIt = printVector.iterator();
                        while (printIt.hasNext()) {
                            if (thisBowler.getNick().equals(printIt.next())) {
                                System.out.println("Printing " + thisBowler.getNick());
                                sr.sendPrintout();
                            }
                        }
                    }
                }
            }
            try {
                sleep(10);
            } catch (Exception e) {
            }
        }
    }
}
