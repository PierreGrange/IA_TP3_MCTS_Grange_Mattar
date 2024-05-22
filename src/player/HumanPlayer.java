package player;

import game.GamePlayer;
import player.ia.EvaluatorCustom;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class HumanPlayer extends GamePlayer {

    public HumanPlayer(int mark) {
        super(mark);
    }

    @Override
    public boolean isUserPlayer() {
        return true;
    }

    @Override
    public String playerName() {
        return "User" ;
    }

    @Override
    public Point play(int[][] board) {
        return null;
    }

    @Override
    public EvaluatorCustom getEvaluator() {
        return new EvaluatorCustom(new ArrayList<>(Arrays.asList("")));
    }
}
