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
}
