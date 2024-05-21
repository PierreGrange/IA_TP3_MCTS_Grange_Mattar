package player.ia;

import game.BoardHelper;
import game.BoardPrinter;

import java.awt.*;
import java.util.ArrayList;

public class MCTS {

    static int nodesExplored = 0;

    public static Point solve(int[][] board, int player,int depth, EvaluatorCustom e){
        nodesExplored = 0;
        int bestScore = Integer.MIN_VALUE;
        Point bestMove = null;
        for(Point move : BoardHelper.getAllPossibleMoves(board,player)){
            //create new node
            int[][] newNode = BoardHelper.getNewBoardAfterMove(board,move,player);
            //recursive call
            int childScore = 0; //MMAB(newNode,player,depth-1,false,Integer.MIN_VALUE,Integer.MAX_VALUE, e); // ICI REMPLACER PAR FONCTION DE CALCUL MCTS
            if(childScore > bestScore) {
                bestScore = childScore;
                bestMove = move;
            }
        }
        System.out.println("Nodes Explored : " + nodesExplored);
        //printMMAB(board, player, depth, e); // ICI REMPLACER PAR FONCTION DE PRINT DE CALCUL MCTS (pas obligé en vrai)
        return bestMove;
    }

}
