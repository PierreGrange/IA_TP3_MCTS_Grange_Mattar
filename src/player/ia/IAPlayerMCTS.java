package player.ia;

import game.GamePlayer;

import java.awt.*;
import java.util.ArrayList;

public class IAPlayerMCTS extends GamePlayer {

    private int searchDepth;
    private EvaluatorCustom evaluator;

    public IAPlayerMCTS(int mark, int depth, ArrayList<String> functions) {
        super(mark);
        searchDepth = depth;
        evaluator = new EvaluatorCustom(functions);
    }

    @Override
    public boolean isUserPlayer() {
        return false;
    }

    @Override
    public String playerName() {
        return "MCTS AI (Depth " + searchDepth + ")";
    }

    @Override
    public Point play(int[][] board) {
        return MCTS.solve(board, myMark, searchDepth, evaluator);
    }

    @Override
    public EvaluatorCustom getEvaluator() {
        return evaluator;
    }
}
