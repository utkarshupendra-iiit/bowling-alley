package entity;

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


    public void setHalted(boolean halted) {
        this.halted = halted;
    }

    public void resumeGame() {
        setHalted(false);
        game.publish();
    }

    public void maintenanceCall() {
        setHalted(true);
        publish(true);
    }

    public void publish( boolean mechProb ) {
        if( subscribers.size() > 0 ) {
            Iterator eventIterator = subscribers.iterator();

            while ( eventIterator.hasNext() ) {
                ( (LaneObserver) eventIterator.next()).receiveLaneEvent( mechProb );
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
