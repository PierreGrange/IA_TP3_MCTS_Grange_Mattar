package player.ia;

import game.BoardHelper;
import game.BoardPrinter;

import java.awt.*;
import java.util.ArrayList;

public class AlphaBeta {

    static int nodesExplored = 0;

    //returns max score move
    public static Point solve(int[][] board, int player,int depth, EvaluatorCustom e){
        nodesExplored = 0;
        int bestScore = Integer.MIN_VALUE;
        Point bestMove = null;
        for(Point move : BoardHelper.getAllPossibleMoves(board,player)){
            //create new node
            int[][] newNode = BoardHelper.getNewBoardAfterMove(board,move,player);
            //recursive call
            int childScore = MMAB(newNode,player,depth-1,false,Integer.MIN_VALUE,Integer.MAX_VALUE, e);
            if(childScore > bestScore) {
                bestScore = childScore;
                bestMove = move;
            }
        }
        System.out.println("Nodes Explored : " + nodesExplored);
        //printMMAB(board, player, depth, e);
        return bestMove;
    }

    public static void printMMAB(int[][] board, int player,int depth, EvaluatorCustom e){
        BoardPrinter BP = new BoardPrinter(board,"Main");
        int bestScore = Integer.MIN_VALUE;
        Point bestMove = null;
        for(Point move : BoardHelper.getAllPossibleMoves(board,player)){
            //create new node
            int[][] newNode = BoardHelper.getNewBoardAfterMove(board,move,player);
            //recursive call
            int childScore = MMAB(newNode,player,depth-1,false,Integer.MIN_VALUE,Integer.MAX_VALUE, e);
            BP.cells[move.x][move.y].text = String.valueOf(childScore);
        }
        BP.repaint();
    }

    //returns minimax value for a given node with A/B pruning
    private static int MMAB(int[][] node,int player,int depth,boolean max,int alpha,int beta, EvaluatorCustom e){
        nodesExplored++;
        //if terminal reached or depth limit reached evaluate
        if(depth == 0 || BoardHelper.isGameFinished(node)){
            //BoardPrinter bpe = new BoardPrinter(node,"Depth : " + depth);
            return e.eval(node,player);
        }
        int oplayer = (player==1) ? 2 : 1;
        //if no moves available then forfeit turn
        if((max && !BoardHelper.hasAnyMoves(node,player)) || (!max && !BoardHelper.hasAnyMoves(node,oplayer))){
            //System.out.println("Forfeit State Reached !");
            return MMAB(node,player,depth-1,!max,alpha,beta,e);
        }
        int score;
        if(max){
            //maximizing
            score = Integer.MIN_VALUE;
            for(Point move : BoardHelper.getAllPossibleMoves(node,player)){ //my turn
                //create new node
                int[][] newNode = BoardHelper.getNewBoardAfterMove(node,move,player);
                //recursive call
                int childScore = MMAB(newNode,player,depth-1,false,alpha,beta,e);
                if(childScore > score) score = childScore;
                //update alpha
                if(score > alpha) alpha = score;
                if(beta <= alpha) break; //Beta Cutoff
            }
        }else{
            //minimizing
            score = Integer.MAX_VALUE;
            for(Point move : BoardHelper.getAllPossibleMoves(node,oplayer)){ //opponent turn
                //create new node
                int[][] newNode = BoardHelper.getNewBoardAfterMove(node,move,oplayer);
                //recursive call
                int childScore = MMAB(newNode,player,depth-1,true,alpha,beta,e);
                if(childScore < score) score = childScore;
                //update beta
                if(score < beta) beta = score;
                if(beta <= alpha) break; //Alpha Cutoff
            }
        }
        return score;
    }

}
