package entity;

import observer.GameObserver;
import observer.LaneObserver;
import observer.PinsetterObserver;
import persistence.ScoreHistoryFile;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class Game implements PinsetterObserver {

    private boolean finished;
    private int[] curScores;
    private boolean halted;
    private Iterator<Bowler> bowlerIterator;
    private Party party;
    private HashMap scores;
    private Bowler currentThrower;
    private boolean canThrowAgain;
    private boolean tenthFrameStrike;
    private int ball;
    private Pinsetter setter;
    private int frameNumber;
    private Vector subscribers;
    private int[][] finalScores;
    private int bowlIndex;

    public int[][] getFinalScores() {
        return finalScores;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public boolean isFinished() {
        return finished;
    }

    public Pinsetter getSetter() {
        return setter;
    }

    public boolean isHalted() {
        return halted;
    }

    public void setHalted(boolean halted) {
        this.halted = halted;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    private int gameNumber;
    private int[][] cumulScores;

    public Game() {
        this.finished = false;
        this.halted = false;
        this.gameNumber = 0;
        this.setter = new Pinsetter();
        this.setter.subscribe(this);
        this.scores = new HashMap();
        this.subscribers = new Vector();
    }

    public void subscribe( GameObserver adding ) {
        subscribers.add( adding );
    }

    public void unsubscribe( LaneObserver removing ) {
        subscribers.remove( removing );
    }

    public int getScore( Bowler Cur, int frame) {
        int[] curScore;
        int strikeballs = 0;
        int totalScore = 0;
        curScore = (int[]) scores.get(Cur);
        for (int i = 0; i != 10; i++){
            cumulScores[bowlIndex][i] = 0;
        }
        int current = 2*(frame - 1)+ball-1;
        //Iterate through each ball until the current one.
        for (int i = 0; i != current+2; i++){
            //Spare:
            if( i%2 == 1 && curScore[i - 1] + curScore[i] == 10 && i < current - 1 && i < 19){
                //This ball was a the second of a spare.
                //Also, we're not on the current ball.
                //Add the next ball to the ith one in cumul.
                cumulScores[bowlIndex][(i/2)] += curScore[i+1] + curScore[i];
                if (i > 1) {
                    //cumulScores[bowlIndex][i/2] += cumulScores[bowlIndex][i/2 -1];
                }
            } else if( i < current && i%2 == 0 && curScore[i] == 10  && i < 18){
                strikeballs = 0;
                //This ball is the first ball, and was a strike.
                //If we can get 2 balls after it, good add them to cumul.
                if (curScore[i+2] != -1) {
                    strikeballs = 1;
                    if(curScore[i+3] != -1) {
                        //Still got em.
                        strikeballs = 2;
                    } else if(curScore[i+4] != -1) {
                        //Ok, got it.
                        strikeballs = 2;
                    }
                }
                if (strikeballs == 2){
                    //Add up the strike.
                    //Add the next two balls to the current cumulscore.
                    cumulScores[bowlIndex][i/2] += 10;
                    if(curScore[i+1] != -1) {
                        cumulScores[bowlIndex][i/2] += curScore[i+1] + cumulScores[bowlIndex][(i/2)-1];
                        if (curScore[i+2] != -1){
                            if( curScore[i+2] != -2){
                                cumulScores[bowlIndex][(i/2)] += curScore[i+2];
                            }
                        } else {
                            if( curScore[i+3] != -2){
                                cumulScores[bowlIndex][(i/2)] += curScore[i+3];
                            }
                        }
                    } else {
                        if ( i/2 > 0 ){
                            cumulScores[bowlIndex][i/2] += curScore[i+2] + cumulScores[bowlIndex][(i/2)-1];
                        } else {
                            cumulScores[bowlIndex][i/2] += curScore[i+2];
                        }
                        if (curScore[i+3] != -1){
                            if( curScore[i+3] != -2){
                                cumulScores[bowlIndex][(i/2)] += curScore[i+3];
                            }
                        } else {
                            cumulScores[bowlIndex][(i/2)] += curScore[i+4];
                        }
                    }
                } else {
                    break;
                }
            }else {
                //We're dealing with a normal throw, add it and be on our way.
                if( i%2 == 0 && i < 18){
                    if ( i/2 == 0 ) {
                        //First frame, first ball.  Set his cumul score to the first ball
                        if(curScore[i] != -2){
                            cumulScores[bowlIndex][i/2] += curScore[i];
                        }
                    } else if (i/2 != 9){
                        //add his last frame's cumul to this ball, make it this frame's cumul.
                        if(curScore[i] != -2){
                            cumulScores[bowlIndex][i/2] += cumulScores[bowlIndex][i/2 - 1] + curScore[i];
                        } else {
                            cumulScores[bowlIndex][i/2] += cumulScores[bowlIndex][i/2 - 1];
                        }
                    }
                } else if (i < 18){
                    if(curScore[i] != -1 && i > 2){
                        if(curScore[i] != -2){
                            cumulScores[bowlIndex][i/2] += curScore[i];
                        }
                    }
                }
                if (i/2 == 9){
                    if (i == 18){
                        cumulScores[bowlIndex][9] += cumulScores[bowlIndex][8];
                    }
                    if(curScore[i] != -2){
                        cumulScores[bowlIndex][9] += curScore[i];
                    }
                } else if (i/2 == 10) {
                    if(curScore[i] != -2){
                        cumulScores[bowlIndex][9] += curScore[i];
                    }
                }
            }
        }
        return totalScore;
    }

    private void markScore( Bowler Cur, int frame, int ball, int score ){
        int[] curScore;
        int index =  ( (frame - 1) * 2 + ball);

        curScore = (int[]) scores.get(Cur);


        curScore[ index - 1] = score;
        scores.put(Cur, curScore);
        getScore( Cur, frame );
        publish();
    }

    public void publish( Party p, int bI, Bowler cT, int[][] cS, HashMap scores, int frameNum, int[] curScores, int ball) {
        if( subscribers.size() > 0 ) {
            Iterator eventIterator = subscribers.iterator();

            while ( eventIterator.hasNext() ) {
                ( (GameObserver) eventIterator.next()).receiveGameEvent( p, bI, cT, cS, scores, frameNum, curScores, ball );
            }
        }
    }

    public void publish() {
        publish(party, bowlIndex, currentThrower, cumulScores, scores, frameNumber+1, curScores, ball);
    }

    public void pauseGame() {
        setHalted(true);
        publish();
    }

    public void unPauseGame() {
        setHalted(false);
        publish();
    }

    public void receivePinsetterEvent(boolean pins[], boolean foul, int throwNumber, int jdpins, int totalPinsDown) {

        if (jdpins >=  0) {			// this is a real throw
            markScore(currentThrower, frameNumber + 1, throwNumber, jdpins);

            if (frameNumber == 9) {
                if (totalPinsDown == 10) {
                    setter.resetPins();
                    if(throwNumber == 1) {
                        tenthFrameStrike = true;
                    }
                }

                if ((totalPinsDown != 10) && (throwNumber == 2 && tenthFrameStrike == false)) {
                    canThrowAgain = false;
                    publish();
                }

                if (throwNumber == 3) {
                    canThrowAgain = false;
                    publish();
                }
            } else { // its not the 10th frame

                if (jdpins == 10) {		// threw a strike
                    canThrowAgain = false;
                    publish();
                } else if (throwNumber == 2) {
                    canThrowAgain = false;
                    publish();
                } else if (throwNumber == 3)
                    System.out.println("I'm here...");
            }
        } else {								//  this is not a real throw, probably a reset
        }
    }


    public void resetScores() {
        Iterator bowlIt = (party.getMembers()).iterator();

        while ( bowlIt.hasNext() ) {
            int[] toPut = new int[25];
            for ( int i = 0; i != 25; i++){
                toPut[i] = -1;
            }
            scores.put( bowlIt.next(), toPut );
        }
        finished = false;
        frameNumber = 0;
    }

    public void assignParty( Party theParty ) {
        party = theParty;
        resetBowlerIterator();

        curScores = new int[party.getMembers().size()];
        cumulScores = new int[party.getMembers().size()][10];
        finalScores = new int[party.getMembers().size()][128]; //Hardcoding a max of 128 games, bite me.
        gameNumber = 0;

        resetScores();
    }

    public void resetBowlerIterator() {
        bowlerIterator = (party.getMembers()).iterator();
    }

    public void startGame() {
        if (bowlerIterator.hasNext()) {
            currentThrower = (Bowler)bowlerIterator.next();

            canThrowAgain = true;
            tenthFrameStrike = false;
            ball = 0;
            while (canThrowAgain) {
                setter.ballThrown();		// simulate the thrower's ball hiting
                ball++;
            }

            if (frameNumber == 9){
                finalScores[bowlIndex][gameNumber] = cumulScores[bowlIndex][9];
                try{
                    Date date = new Date();
                    String dateString = "" + date.getHours() + ":" + date.getMinutes() + " " + date.getMonth() + "/" + date.getDay() + "/" + (date.getYear() + 1900);
                    ScoreHistoryFile.addScore(currentThrower.getNick(), dateString, new Integer(cumulScores[bowlIndex][9]).toString());
                } catch (Exception e) {System.err.println("Exception in addScore. "+ e );}
            }


            setter.reset();
            bowlIndex++;

        } else {
            frameNumber++;
            resetBowlerIterator();
            bowlIndex = 0;
            if (frameNumber > 9) {
                finished = true;
                gameNumber++;
            }
        }
    }

}
