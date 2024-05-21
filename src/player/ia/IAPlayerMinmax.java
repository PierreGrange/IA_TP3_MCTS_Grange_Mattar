package player.ia;

import game.GamePlayer;
import player.ai.Evaluator;
import player.ai.Minimax;
import player.ai.StaticEvaluator;

import java.awt.*;

public class IAPlayerMinmax extends GamePlayer {

    private int searchDepth;
    private Evaluator evaluator;

    public IAPlayerMinmax(int mark, int depth) {
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
        return Minimax.solve(board, myMark, searchDepth, evaluator);
    }
}
