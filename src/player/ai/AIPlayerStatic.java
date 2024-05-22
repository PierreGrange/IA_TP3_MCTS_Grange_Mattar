package player.ai;

import game.GamePlayer;
import player.ia.EvaluatorCustom;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class AIPlayerStatic extends GamePlayer {

    private int searchDepth;
    private Evaluator evaluator;

    public AIPlayerStatic(int mark, int depth) {
        super(mark);
        searchDepth = depth;
        evaluator = new StaticEvaluator();
    }

    @Override
    public boolean isUserPlayer() {
        return false;
    }

    @Override
    public String playerName() {
        return "Static AI (Depth " + searchDepth + ")";
    }

    @Override
    public Point play(int[][] board) {
        return Minimax.solve(board,myMark,searchDepth,evaluator);
    }

    @Override
    public EvaluatorCustom getEvaluator() {
        return new EvaluatorCustom(new ArrayList<>(Arrays.asList("")));
    }
}
