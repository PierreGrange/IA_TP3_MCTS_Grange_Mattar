package player.ia;

import game.BoardHelper;

import java.util.ArrayList;

public class EvaluatorCustom implements Evaluator {
    boolean mobility, discDiff, corner, boardMap, parity = false;

    int mobilityWeight = 1;
    int discDiffWeight = 1;
    int cornerWeight = 1;
    int boardMapWeight = 1;
    int parityWeight = 1;
    ArrayList<String> functionList;

    public EvaluatorCustom(ArrayList<String> functions) {
        this.functionList = functions;
        ArrayList<String> functionsLabels = new ArrayList<>();

        for(String f : functions) {
            if(f == "mobility") {
                mobility = true;
                functionsLabels.add("-Mobility");
            }
            if(f == "discDiff") {
                discDiff = true;
                functionsLabels.add("-Disc difference");
            }
            if(f == "corner") {
                corner = true;
                functionsLabels.add("-Corner control");
            }
            if(f == "boardMap") {
                boardMap = true;
                functionsLabels.add("-Board Map");
            }
            if(f == "parity") {
                parity = true;
                functionsLabels.add("-Parity");
            }
        }
        this.functionList = functionsLabels;
    }

    public int eval(int[][] board , int player){
        int value = 0;
        if(mobility) {
            value += mobilityWeight * evalMobility(board, player);
        }
        if(discDiff) {
            value += discDiffWeight * evalDiscDiff(board, player);
        }
        if(corner) {
            value += cornerWeight * evalCorner(board, player);
        }
        if(boardMap) {
            value += boardMapWeight * evalBoardMap(board, player);
        }
        if(parity) {
            value += parityWeight * evalParity(board);
        }

        return value;
    }

    public ArrayList<String> getFunctionList(){
        if(functionList.isEmpty()) {
            functionList.add("None");
            return functionList;
        }
        return functionList;
    }



//region Fonctions d'évaluation
    public static int evalMobility(int[][] board , int player){
        int oplayer = (player==1) ? 2 : 1;

        int myMoveCount = BoardHelper.getAllPossibleMoves(board,player).size();
        int opMoveCount = BoardHelper.getAllPossibleMoves(board,oplayer).size();

        return 100 * (myMoveCount - opMoveCount) / (myMoveCount + opMoveCount + 1);
    }

    public static int evalDiscDiff(int[][] board , int player){
        int oplayer = (player==1) ? 2 : 1;

        int mySC = BoardHelper.getPlayerStoneCount(board,player);
        int opSC = BoardHelper.getPlayerStoneCount(board,oplayer);

        return 100 * (mySC - opSC) / (mySC + opSC);
    }

    public static int evalCorner(int[][] board , int player){
        int oplayer = (player==1) ? 2 : 1;

        int myCorners = 0;
        int opCorners = 0;

        if(board[0][0]==player) myCorners++;
        if(board[7][0]==player) myCorners++;
        if(board[0][7]==player) myCorners++;
        if(board[7][7]==player) myCorners++;

        if(board[0][0]==oplayer) opCorners++;
        if(board[7][0]==oplayer) opCorners++;
        if(board[0][7]==oplayer) opCorners++;
        if(board[7][7]==oplayer) opCorners++;

        return 100 * (myCorners - opCorners) / (myCorners + opCorners + 1);
    }

    public static int evalBoardMap(int[][] board , int player){
        int oplayer = (player==1) ? 2 : 1;
        int[][] W = {
                {200 , -100, 100,  50,  50, 100, -100,  200},
                {-100, -200, -50, -50, -50, -50, -200, -100},
                {100 ,  -50, 100,   0,   0, 100,  -50,  100},
                {50  ,  -50,   0,   0,   0,   0,  -50,   50},
                {50  ,  -50,   0,   0,   0,   0,  -50,   50},
                {100 ,  -50, 100,   0,   0, 100,  -50,  100},
                {-100, -200, -50, -50, -50, -50, -200, -100},
                {200 , -100, 100,  50,  50, 100, -100,  200}};

        //if corners are taken W for that 1/4 loses effect
        if(board[0][0] != 0){
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j <= 3; j++) {
                    W[i][j] = 0;
                }
            }
        }

        if(board[0][7] != 0){
            for (int i = 0; i < 3; i++) {
                for (int j = 4; j <= 7; j++) {
                    W[i][j] = 0;
                }
            }
        }

        if(board[7][0] != 0){
            for (int i = 5; i < 8; i++) {
                for (int j = 0; j <= 3; j++) {
                    W[i][j] = 0;
                }
            }
        }

        if(board[7][7] != 0){
            for (int i = 5; i < 8; i++) {
                for (int j = 4; j <= 7; j++) {
                    W[i][j] = 0;
                }
            }
        }

        int myW = 0;
        int opW = 0;

        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j]==player) myW += W[i][j];
                if(board[i][j]==oplayer) opW += W[i][j];
            }
        }

        return (myW - opW) / (myW + opW + 1);
    }

    public static int evalParity(int[][] board){
        int remDiscs = 64 - BoardHelper.getTotalStoneCount(board);
        return remDiscs % 2 == 0 ? -1 : 1;
    }
//endregion
}
