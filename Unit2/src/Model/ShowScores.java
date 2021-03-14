package Model;

//Read the SCOREHISTORY.dat and show scores in a window

//importing same libraries as in AddPartyView
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
//importing io library
import java.io.*;


public class ShowScores implements ActionListener, ListSelectionListener {

    private final JFrame win;

    private final JButton finished,lastScores,maxPlayerScore,minPlayerScore,minScore,maxScore;

    private final JList<Vector> outputList;
    private final JList<Vector> allBowlers;

    private String selectedNick;

    private Vector bowlerdb;
    private final Vector party;

    
    public Vector<String> getScores(String type,String nick){

        if (type.equals("personmin"))
        {   
            Vector scores = null;
            try {
                scores = ScoreHistoryFile.getScores(nick);
            } catch (Exception e) {
                System.err.println("Error: " + e);
            }

            assert scores != null;
            // Vector scores = ScoreHistoryFile.getScores(nick);
            Score scoresitem = (Score)scores.get(0);
            int min = Integer.parseInt(scoresitem.getScore());
            for(int index = 0; index < scores.size(); index++)
            {   
                scoresitem = (Score)scores.get(index);
                int s = Integer.parseInt(scoresitem.getScore());
                if (s<min)
                    min = s;
            }
            Vector<String> result =  new Vector<>();
            result.add(Integer.toString(min));
            System.out.println(result.get(0));
            return result;
        }
        if (type.equals("personmax"))
        {
            Vector scores = null;
            try {
                scores = ScoreHistoryFile.getScores(nick);
            } catch (Exception e) {
                System.err.println("Error: " + e);
            }

            assert scores != null;
            // Vector scores = ScoreHistoryFile.getScores(nick);
            int max = 0;
            Score scoresitem;
            for(int index = 0; index < scores.size(); index++)
            {   
                scoresitem = (Score)scores.get(index);
                int s = Integer.parseInt(scoresitem.getScore());
                if (s>max)
                    max = s;
            }
            Vector<String> result =  new Vector<>();
            result.add(Integer.toString(max));
            System.out.println(result.get(0));
            return result;
        }
        
        Vector scores = null;
            try {
                scores = ScoreHistoryFile.getScores(nick);
            } catch (Exception e) {
                System.err.println("Error: " + e);
            }
            assert scores != null;

            Score scoresitem;
            Vector<String> result =  new Vector<>();

            int numPastScores = 6;
            int i = 0;
            for (int index = (scores.size()-1); (index >= 0) && (i < numPastScores); index--,i++)
            {   
                System.out.println("printing index");
                System.out.println(index);
                scoresitem = (Score)scores.get(index);
                result.add(scoresitem.getScore());
            }
            return result;
    }

    public ShowScores() {

        win = new JFrame("Show Scores");
        win.getContentPane().setLayout(new BorderLayout());
        ((JPanel) win.getContentPane()).setOpaque(false);

        JPanel colPanel = new JPanel();
        colPanel.setLayout(new GridLayout(1, 3));

        // Controls Panel
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridLayout(5, 1));
        controlsPanel.setBorder(new TitledBorder("Controls"));

        minScore = new JButton("Min Score");
        JPanel minScorePanel = new JPanel();
        minScorePanel.setLayout(new FlowLayout());
        minScore.addActionListener(this);
        minScorePanel.add(minScore);
        controlsPanel.add(minScorePanel);

        maxScore = new JButton("Max Score");
        JPanel maxScorePanel = new JPanel();
        maxScorePanel.setLayout(new FlowLayout());
        maxScore.addActionListener(this);
        maxScorePanel.add(maxScore);
        controlsPanel.add(maxScorePanel);

        maxPlayerScore = new JButton("Player Max Score");
        JPanel maxPlayerScorePanel = new JPanel();
        maxPlayerScorePanel.setLayout(new FlowLayout());
        maxPlayerScore.addActionListener(this);
        maxPlayerScorePanel.add(maxPlayerScore);
        controlsPanel.add(maxPlayerScorePanel);

        minPlayerScore = new JButton("Player Min Score");
        JPanel minPlayerScorePanel = new JPanel();
        minPlayerScorePanel.setLayout(new FlowLayout());
        minPlayerScore.addActionListener(this);
        minPlayerScorePanel.add(minPlayerScore);
        controlsPanel.add(minPlayerScorePanel);

        lastScores = new JButton("Last Scores by player");
        JPanel lastScoresPanel = new JPanel();
        lastScoresPanel.setLayout(new FlowLayout());
        lastScores.addActionListener(this);
        lastScoresPanel.add(lastScores);
        controlsPanel.add(lastScoresPanel);

        finished = new JButton("Finished");
        JPanel finishedPanel = new JPanel();
        finishedPanel.setLayout(new FlowLayout());
        finished.addActionListener(this);
        finishedPanel.add(finished);
        controlsPanel.add(finishedPanel);

        // Scores Database
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new FlowLayout());
        scorePanel.setBorder(new TitledBorder("Score Database"));


        party = new Vector();

        Vector empty = new Vector();
        empty.add("(Empty)");

        outputList = new JList(empty);
        outputList.setFixedCellWidth(120);
        outputList.setVisibleRowCount(5); 
        outputList.addListSelectionListener(this);
        JScrollPane scorePane = new JScrollPane(outputList);
        scorePanel.add(scorePane);
       
       // Bowler Database
        JPanel bowlerPanel = new JPanel();
        bowlerPanel.setLayout(new FlowLayout());
        bowlerPanel.setBorder(new TitledBorder("Bowler Database"));

        try {
            bowlerdb = new Vector(BowlerFile.getBowlers());
        } catch (Exception e) {
            System.err.println("File Error");
            bowlerdb = new Vector();
        }
        allBowlers = new JList(bowlerdb);
        allBowlers.setVisibleRowCount(8);
        allBowlers.setFixedCellWidth(120);
        JScrollPane bowlerPane = new JScrollPane(allBowlers);
        bowlerPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        allBowlers.addListSelectionListener(this);
        bowlerPanel.add(bowlerPane);

        colPanel.add(scorePanel);
        colPanel.add(bowlerPanel);
        colPanel.add(controlsPanel);

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
        if (e.getSource().equals(minPlayerScore)) {
            System.out.println("in minPlayerScore");
            if (selectedNick != null) {
                party.clear();
                party.add(getScores("personmin",selectedNick).get(0));
                outputList.setListData(party);
            }
        }

        if (e.getSource().equals(maxPlayerScore)) {
            System.out.println("in maxPlayerScore");
            if (selectedNick != null) {
                party.clear();
                party.add(getScores("personmax",selectedNick).get(0));
                outputList.setListData(party);
            }
        }

        if (e.getSource().equals(minScore)) {
            System.out.println("in minScore");
            party.clear();
            int pm,min = Integer.parseInt(getScores("personmin",(bowlerdb.get(0)).toString()).get(0));
            for (int index=1;index < bowlerdb.size();index++)
            {
                pm = Integer.parseInt(getScores("personmin",(bowlerdb.get(index)).toString()).get(0));
                if (pm < min)
                    min = pm;
            }
            party.add(Integer.toString(min));
            outputList.setListData(party);
        }

        if (e.getSource().equals(maxScore)) {
            System.out.println("in maxScore");
            party.clear();
            int pm,max = Integer.parseInt(getScores("personmax",(bowlerdb.get(0)).toString()).get(0));
            for (int index=1;index < bowlerdb.size();index++)
            {
                pm = Integer.parseInt(getScores("personmax",(bowlerdb.get(index)).toString()).get(0));
                if (pm > max)
                    max = pm;
            }
            party.add(Integer.toString(max));
            outputList.setListData(party);
        }

        if (e.getSource().equals(lastScores)) {
            System.out.println("in lastScores");
            if (selectedNick != null) {
                party.clear();
                Vector<String> ls = getScores("lastscores",selectedNick);
                for (int index=1;index < ls.size();index++) {
                    System.out.println(ls.get(index));
                    party.add(ls.get(index));
                }
                outputList.setListData(party);
            }
           
        }
        if (e.getSource().equals(finished)) {
            win.hide();
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource().equals(allBowlers)) {
            selectedNick =
                    ((String) ((JList) e.getSource()).getSelectedValue());
        }
        
    }
    

}