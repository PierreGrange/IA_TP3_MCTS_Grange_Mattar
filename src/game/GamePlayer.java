package game;

import player.ia.EvaluatorCustom;

import java.awt.*;

public abstract class GamePlayer {

    protected int myMark;
    public GamePlayer(int mark){
        myMark = mark;
    }

    abstract public boolean isUserPlayer();

    abstract public String playerName();

    abstract public Point play(int[][] board);

    abstract public EvaluatorCustom getEvaluator();
}
