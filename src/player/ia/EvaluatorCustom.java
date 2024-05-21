package player.ia;

import player.ai.Evaluator;

import java.util.ArrayList;

import static player.ai.StaticEvaluator.*;

public class EvaluatorCustom implements Evaluator {
    boolean mobility, discDiff, corner, boardMap, parity = false;

    int mobilityWeight = 1;
    int discDiffWeight = 1;
    int cornerWeight = 1;
    int boardMapWeight = 1;
    int parityWeight = 1;

    public EvaluatorCustom(ArrayList<String> functions) {
        for(String f : functions) {
            if(f == "mobility") {
                mobility = true;
            }
            if(f == "discDiff") {
                discDiff = true;
            }
            if(f == "corner") {
                corner = true;
            }
            if(f == "boardMap") {
                boardMap = true;
            }
            if(f == "parity") {
                parity = true;
            }
        }
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
}
