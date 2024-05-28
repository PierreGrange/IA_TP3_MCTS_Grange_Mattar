package player.ia;

import game.BoardHelper;
import game.GamePlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class IAMCTSPlayer extends GamePlayer {

    private final Random random = new Random();
    private int simulation;
    private double constant;

    public IAMCTSPlayer(int mark, int simulation, double constant) {
        super(mark);
        this.simulation = simulation;
        this.constant = constant;
    }

    public IAMCTSPlayer(int mark) {
        super(mark);
        this.simulation = 1000;
        this.constant = Math.sqrt(2);
    }

    @Override
    public boolean isUserPlayer() {
        return false;
    }

    @Override
    public String playerName() {
        return "MCTS AI";
    }

    @Override
    public Point play(int[][] board) {
        return UCT(board, myMark);
    }

    private Point UCT(int[][] board, int player) {
        MCTSNode root = new MCTSNode(board, player);
        for (int i = 0; i < this.simulation; i++) {
            MCTSNode v = treePolicy(root);
            int reward = defaultPolicy(v.state, v.player);
            backPropagate(v, reward);
        }
        return bestChild(root, 0).move;
    }

    private MCTSNode treePolicy(MCTSNode v) {
        while (!BoardHelper.isGameFinished(v.state)) {
            if (!v.isFullyExpanded()) {
                return expand(v);
            } else {
                if (v.children.isEmpty()) {
                    v.switchPlayer();

                    if (!v.isFullyExpanded()) {
                        return expand(v);
                    }
                    else{
                        v = bestChild(v, constant);
                    }
                }
                else{
                v = bestChild(v, constant);
                }
            }
        }
        return v;
    }

    private MCTSNode expand(MCTSNode node) {
        ArrayList<Point> possibleMoves = BoardHelper.getAllPossibleMoves(node.state, node.player);
        possibleMoves.removeAll(node.getChildrenMoves());
        Point move = possibleMoves.get(random.nextInt(possibleMoves.size()));
        int[][] newState = BoardHelper.getNewBoardAfterMove(node.state, move, node.player);
        int nextPlayer = (node.player == 1) ? 2 : 1;
        MCTSNode child = new MCTSNode(newState, nextPlayer, move, node);
        node.addChild(child);
        return child;
    }

    private int defaultPolicy(int[][] state, int player) {
        int winner = BoardHelper.getWinner(state);
        while (winner == -1) {
            ArrayList<Point> possibleMoves = BoardHelper.getAllPossibleMoves(state, player);
            if (!possibleMoves.isEmpty()) {
                Point move = possibleMoves.get(random.nextInt(possibleMoves.size()));
                state = BoardHelper.getNewBoardAfterMove(state, move, player);
            }
            player = (player == 1) ? 2 : 1;
            winner = BoardHelper.getWinner(state);
        }
      if (winner == myMark) {
        return 1;
      }
      else if( winner != 0){
        return -1;
      }
      else {
        return 0;
      }
    }

    private void backPropagate(MCTSNode node, int bonus) {
        while (node != null) {
            node.visits++;
            node.wins=node.wins+bonus;
            node = node.parent;
        }
    }

    private MCTSNode bestChild(MCTSNode node, double c) {
        MCTSNode bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (MCTSNode child : node.children) {
            double uctValue = (child.wins / (double) child.visits) +
                    c * Math.sqrt(2 * Math.log(node.visits) / (double) child.visits);
            if (uctValue > bestValue) {
                bestChild = child;
                bestValue = uctValue;
            }
        }

        return bestChild;
    }


}