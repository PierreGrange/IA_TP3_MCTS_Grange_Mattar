package game;

import player.HumanPlayer;
import player.ia.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamePanel extends JPanel implements GameEngine {

    //reversi board
    int[][] board;

    //player turn
    //black plays first
    int turn = 1;

    //swing elements
    BoardCell[][] cells;
    JLabel score1;
    JLabel score2;
    JLabel labelFunctions1;
    JLabel labelFunctions2;

    int totalscore1 = 0;
    int totalscore2 = 0;

    JLabel tscore1;
    JLabel tscore2;

    ArrayList<String> functionListIA1 = new ArrayList<>(Arrays.asList("mobility", "discDiff", "corner", "boardMap", "parity"));


    //GamePlayer player1 = new HumanPlayer(1);
    //GamePlayer player2 = new IAPlayerMinimax(2, 6, functionListIA1);
    //GamePlayer player2 = new IAPlayerAlphaBeta(2, 6, functionListIA1);
    //GamePlayer player2 = new IAMCTSPlayer(1,1000, 42);

    //Disable this to play manually
    boolean autoplay = true;
    GamePlayer player1;
    GamePlayer player2;

    int nextDuel = 0;
    int lastDuel;
    int round = 1;
    int rounds;
    String duelsListPath = "duels.txt";

    Timer player1HandlerTimer;
    Timer player2HandlerTimer;

    @Override
    public int getBoardValue(int i,int j){
        return board[i][j];
    }

    @Override
    public void setBoardValue(int i,int j,int value){
        board[i][j] = value;
    }

    public GamePanel(){
        this.setBackground(Color.WHITE);
        this.setLayout(new BorderLayout());

        JPanel reversiBoard = new JPanel();
        reversiBoard.setLayout(new GridLayout(8,8));
        reversiBoard.setPreferredSize(new Dimension(500,500));
        reversiBoard.setBackground(new Color(41,100, 59));

        //init board
        resetBoard();

        cells = new BoardCell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cells[i][j] = new BoardCell(this,reversiBoard,i,j);
                reversiBoard.add(cells[i][j]);
            }
        }

        // INITIALIZE PLAYERS if autoplay is on
        if(autoplay) {
            try {
                initializePlayersForIATest();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            player1 = new HumanPlayer(1);
            player2 = new HumanPlayer(1);
        }

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200,0));

        score1 = new JLabel("Score 1");
        score2 = new JLabel("Score 2");
        labelFunctions1 = new JLabel("Evaluation functions :");

        tscore1 = new JLabel("Total Score 1");
        tscore2 = new JLabel("Total Score 2");
        labelFunctions2 = new JLabel("Evaluation functions :");

        sidebar.add(score1);
        sidebar.add(score2);
        sidebar.add(new JLabel(" "));
        sidebar.add(labelFunctions1);

        if(player1.getEvaluator() != null) {
            for(String s : player1.getEvaluator().getFunctionList()) {
                JLabel function = new JLabel(s);
                sidebar.add(function);
            }
        }


        sidebar.add(new JLabel("-----------"));

        sidebar.add(tscore1);
        sidebar.add(tscore2);
        sidebar.add(new JLabel(" "));
        sidebar.add(labelFunctions2);

        if(player2.getEvaluator() != null) {
            for(String s : player2.getEvaluator().getFunctionList()) {
                JLabel function = new JLabel(s);
                sidebar.add(function);
            }
        }

        this.add(sidebar,BorderLayout.WEST);
        this.add(reversiBoard);

        //
        updateBoardInfo();
        updateTotalScore();

        //AI Handler Timer (to unfreeze gui)
        player1HandlerTimer = new Timer(1000,(ActionEvent e) -> {
            handleAI(player1);
            player1HandlerTimer.stop();
            manageTurn();
        });

        player2HandlerTimer = new Timer(1000,(ActionEvent e) -> {
            handleAI(player2);
            player2HandlerTimer.stop();
            manageTurn();
        });

        manageTurn();
    }

    private boolean awaitForClick = false;

    public void manageTurn(){
        if(BoardHelper.hasAnyMoves(board,1) || BoardHelper.hasAnyMoves(board,2)) {
            updateBoardInfo();
            if (turn == 1) {
                if(BoardHelper.hasAnyMoves(board,1)) {
                    if (player1.isUserPlayer()) {
                        awaitForClick = true;
                        //after click this function should be call backed
                    } else {
                        player1HandlerTimer.start();
                    }
                }else{
                    //forfeit this move and pass the turn
                    System.out.println("Player 1 has no legal moves !");
                    turn = 2;
                    manageTurn();
                }
            } else {
                if(BoardHelper.hasAnyMoves(board,2)) {
                    if (player2.isUserPlayer()) {
                        awaitForClick = true;
                        //after click this function should be call backed
                    } else {
                        player2HandlerTimer.start();
                    }
                }else{
                    //forfeit this move and pass the turn
                    System.out.println("Player 2 has no legal moves !");
                    turn = 1;
                    manageTurn();
                }
            }
        }else{
            //game finished
            System.out.println("Game Finished !");
            int winner = BoardHelper.getWinner(board);
            System.out.println("Winner : " + winner);
            if(winner==1) totalscore1++;
            else if(winner==2) totalscore2++;
            updateTotalScore();

            try {
                IADuelParser.updateWins(duelsListPath, nextDuel, winner);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            if(round != rounds) {
                round++;
                //restart
                resetBoard();
                manageTurn();
            }
            else if(nextDuel != lastDuel){
                try {
                    initializePlayersForIATest();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                resetBoard();
                manageTurn();
            }

        }
    }

    public void resetBoard(){
        board = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j]=0;
            }
        }
        //initial board state
        setBoardValue(3,3,2);
        setBoardValue(3,4,1);
        setBoardValue(4,3,1);
        setBoardValue(4,4,2);
    }

    //update highlights on possible moves and scores
    public void updateBoardInfo(){

        int p1score = 0;
        int p2score = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j] == 1) p1score++;
                if(board[i][j] == 2) p2score++;

                if(BoardHelper.canPlay(board,turn,i,j)){
                    cells[i][j].highlight = 1;
                }else{
                    cells[i][j].highlight = 0;
                }
            }
        }

        score1.setText(player1.playerName() + " : " + p1score);
        score2.setText(player2.playerName() + " : " + p2score);
    }

    public void updateTotalScore(){
        tscore1.setText(player1.playerName() + " : " + totalscore1);
        tscore2.setText(player2.playerName() + " : " + totalscore2);
    }

    @Override
    public void handleClick(int i,int j){
        if(awaitForClick && BoardHelper.canPlay(board,turn,i,j)){
            System.out.println("User Played in : "+ i + " , " + j);

            //update board
            board = BoardHelper.getNewBoardAfterMove(board,new Point(i,j),turn);

            //advance turn
            turn = (turn == 1) ? 2 : 1;

            repaint();

            awaitForClick = false;

            //callback
            manageTurn();
        }
    }

    public void handleAI(GamePlayer ai){
        Point aiPlayPoint = ai.play(board);
        int i = aiPlayPoint.x;
        int j = aiPlayPoint.y;
        if(!BoardHelper.canPlay(board,ai.myMark,i,j)) System.err.println("FATAL : AI Invalid Move !");
        System.out.println(ai.playerName() + " Played in : "+ i + " , " + j);

        //update board
        board = BoardHelper.getNewBoardAfterMove(board,aiPlayPoint,turn);

        //advance turn
        turn = (turn == 1) ? 2 : 1;

        repaint();
    }

    public void initializePlayersForIATest() throws IOException {
        List<List<Object>> aiSettings = IADuelParser.parseAIFile(duelsListPath);

        if (aiSettings.size() < 1) {
            throw new IllegalArgumentException("Insufficient settings provided");
        }

        List<Object> settings = (List<Object>) aiSettings.get(nextDuel); // Assuming the first set of settings for initialization
        int player1Type = (int) settings.get(0);
        ArrayList<String> player1Functions = (ArrayList<String>) settings.get(1);
        int player2Type = (int) settings.get(2);
        ArrayList<String> player2Functions = (ArrayList<String>) settings.get(3);

        if (player1Type == 1) {
            player1 = new IAPlayerMinimax(1, 6, player1Functions);
        } else if (player1Type == 2) {
            player1 = new IAPlayerAlphaBeta(1, 6, player1Functions);
        } else if (player1Type == 3) {
            player1 = new IAPlayerMCTS(1);
        } else {
            throw new IllegalArgumentException("Unknown player1 type: " + player1Type);
        }

        if (player2Type == 1) {
            player2 = new IAPlayerMinimax(2, 6, player2Functions);
        } else if (player2Type == 2) {
            player2 = new IAPlayerAlphaBeta(2, 6, player2Functions);
        } else if (player2Type == 3) {
            player2 = new IAPlayerMCTS(2, 1000, 42);
        } else {
            throw new IllegalArgumentException("Unknown player2 type: " + player2Type);
        }

        round = 1;
        rounds = (int) settings.get(4);
        lastDuel = aiSettings.size();
        nextDuel++;
        // Update the sidebar to reflect the new player evaluators
        //updateSidebar();
    }
}
