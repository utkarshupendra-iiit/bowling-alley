package entity;

import observer.GameObserver;
import observer.PinsetterObserver;
import persistence.ScoreHistoryDb;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class Game implements PinsetterObserver {

    private boolean finished;
    private int[] curScores;
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

    public int getGameNumber() {
        return gameNumber;
    }

    private int gameNumber;
    private int[][] cumulScores;

    public Game() {
        this.finished = false;
        this.gameNumber = 0;
        this.setter = new Pinsetter();
        this.setter.subscribe(this);
        this.scores = new HashMap();
        this.subscribers = new Vector();
    }

    public void subscribe( GameObserver adding ) {
        subscribers.add( adding );
    }

    public void getScore(Bowler Cur, int frame) {
        int[] curScore;
        int strikeballs;
        curScore = (int[]) scores.get(Cur);
        for (int i = 0; i != 10; i++){
            cumulScores[bowlIndex][i] = 0;
        }
        int current = 2*(frame - 1)+ball-1;
        //Iterate through each ball until the current one.
        for (int i = 0; i != current+2; i++){
            //Spare:
            if( i%2 == 1 && curScore[i - 1] + curScore[i] == 10 && i < current - 1 && i < 19){
                cumulScores[bowlIndex][(i/2)] += curScore[i+1] + curScore[i];
            } else if( i < current && i%2 == 0 && curScore[i] == 10  && i < 18){
                strikeballs = 0;
                //This ball is the first ball, and was a strike.
                //If we can get 2 balls after it, good add them to cumul.
                strikeballs = getStrikeForFirstThrow(curScore, i);

                if (strikeballs == 2){
                    //Add up the strike.
                    //Add the next two balls to the current cumulscore.
                    setCumulForNextTwoBalls(i, curScore);
                } else {
                    break;
                }
            }else {
                //We're dealing with a normal throw, add it and be on our way.
                normalThrow(i, curScore);
            }
        }
    }

    private void normalThrow(int i, int[] curScore) {
        if( i%2 == 0 && i < 18){
            addCurrentBallToCumul(i, curScore);
        } else if (i < 18){
            if(curScore[i] != -1 && i > 2 && curScore[i] != -2){
                cumulScores[bowlIndex][i/2] += curScore[i]; // nested if else removed
            }
        }
        if (i/2 == 9){
            if (i == 18){
                cumulScores[bowlIndex][9] += cumulScores[bowlIndex][8];
            }
            if(curScore[i] != -2){
                cumulScores[bowlIndex][9] += curScore[i];
            }
        } else if (i/2 == 10 && curScore[i] != -2) {
            cumulScores[bowlIndex][9] += curScore[i]; // nested if else removed
        }
    }

    private void addCurrentBallToCumul(int i, int[] curScore) {
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
    }

    private void setCumulForNextTwoBalls(int i, int[] curScore) {
        cumulScores[bowlIndex][i/2] += 10;
        if(curScore[i+1] != -1) {
            cumulScores[bowlIndex][i/2] += curScore[i+1] + cumulScores[bowlIndex][(i/2)-1];
            if (curScore[i+2] != -1 && curScore[i+2] != -2){

                    cumulScores[bowlIndex][(i/2)] += curScore[i+2];

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
            if (curScore[i+3] != -1 && curScore[i+3] != -2){

                    cumulScores[bowlIndex][(i/2)] += curScore[i+3];

            } else {
                cumulScores[bowlIndex][(i/2)] += curScore[i+4];
            }
        }
    }

    private int getStrikeForFirstThrow(int[] curScore, int i) {
        int strikeballs = 0;
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
        return strikeballs;
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

    private void tenthFrameThrow(int totalPinsDown, int throwNumber) {
        if (totalPinsDown == 10) {
            setter.resetPins();
            if(throwNumber == 1) {
                tenthFrameStrike = true;
            }
        }

        if ((totalPinsDown != 10) && (throwNumber == 2 && !tenthFrameStrike)) {
            canThrowAgain = false;
            publish();
        }

        if (throwNumber == 3) {
            canThrowAgain = false;
            publish();
        }
    }

    private void nonTenthFrameThrow(int throwNumber, int jdpins) {
        if (jdpins == 10) {
            canThrowAgain = false;
            publish();
        } else if (throwNumber == 2) {
            canThrowAgain = false;
            publish();
        } else if (throwNumber == 3)
            System.out.println("I'm here...");
    }

    public void receivePinsetterEvent(boolean pins[], boolean foul, int throwNumber, int jdpins, int totalPinsDown) {

        if (jdpins >=  0) {			// this is a real throw
            markScore(currentThrower, frameNumber + 1, throwNumber, jdpins);
            if (frameNumber == 9) {
                tenthFrameThrow(totalPinsDown, throwNumber);
            } else {
                nonTenthFrameThrow(throwNumber, jdpins);
            }
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

    private void throwBall() {
        setter.ballThrown();		// simulate the thrower's ball hiting
        ball++;
    }

    private void persistScore() {
        finalScores[bowlIndex][gameNumber] = cumulScores[bowlIndex][9];
        try{
            Date date = new Date();
            String dateString = "" + date.getHours() + ":" + date.getMinutes() + " " + date.getMonth() + "/" + date.getDay() + "/" + (date.getYear() + 1900);
            ScoreHistoryDb.addScore(currentThrower.getNick(), dateString, String.valueOf(cumulScores[bowlIndex][9]).toString());
        } catch (Exception e) {System.err.println("Exception in addScore. "+ e );}
    }

    private void finishGame() {
        finished = true;
        gameNumber++;
    }

    private void setupGame() {
        currentThrower = bowlerIterator.next();
        canThrowAgain = true;
        tenthFrameStrike = false;
        ball = 0;
    }

    public void startGame() {
        if (bowlerIterator.hasNext()) {
            setupGame();
            while (canThrowAgain) {
                throwBall();
            }
            if (frameNumber == 9){
                persistScore();
            }
            setter.reset();
            bowlIndex++;
        } else {
            frameNumber++;
            resetBowlerIterator();
            bowlIndex = 0;
            if (frameNumber > 9) {
                finishGame();
            }
        }
    }

}
