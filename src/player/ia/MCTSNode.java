package player.ia;

import game.BoardHelper;

import java.awt.*;
import java.util.ArrayList;

public class MCTSNode {

        int[][] state;
        int player;
        Point move;
        MCTSNode parent;
        ArrayList<MCTSNode> children;
        int wins;
        int visits;

    public MCTSNode(int[][] state, int player) {
            this.state = state;
            this.player = player;
            this.move = null;
            this.parent = null;
            this.children = new ArrayList<>();
            this.wins = 0;
            this.visits = 0;
        }
        public MCTSNode(int[][] state, int player, Point move, MCTSNode parent) {
            this(state, player);
            this.move = move;
            this.parent = parent;
        }

        boolean isFullyExpanded() {
            return BoardHelper.getAllPossibleMoves(state, player).size() == children.size();
        }

        void addChild(MCTSNode child) {
            children.add(child);
        }

        ArrayList<Point> getChildrenMoves() {
            ArrayList<Point> moves = new ArrayList<>();
            for (MCTSNode child : children) {
                moves.add(child.move);
            }
            return moves;
        }

        void switchPlayer() {
            player = (player == 1) ? 2 : 1;
        }
    }


