package log320_hnefatafl;

import java.util.ArrayList;

public class Hnefatafl {

    public int getAISearchDepth() {
        return 2;
    }

    public ArrayList<Move> getActionsForBoard(Board board, Equipe equipe) {
    	ArrayList<Move> possibilites = new ArrayList<Move>();
        // Add all of the actions for pieces that the current player owns.
        possibilites.clear();
    	for(int xDep = 1; xDep <= 13; xDep += 1) {
    		for(int yDep = 1; yDep <= 13; yDep += 1) {
    			for(int xArr = 1; xArr <= 13; xArr += 1) {
    				for(int yArr = 1; yArr <= 13; yArr +=1) {
    					if(xDep == xArr || yDep == yArr) {
							Move m = new Move(xDep, yDep, xArr, yArr);
							if(board.update(m, equipe, false)){
								possibilites.add(m);
							}
    					}
    				}
    			}
    		}
    	}
        return possibilites;
    }

}
