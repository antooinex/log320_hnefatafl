package log320_hnefatafl;

import java.util.ArrayList;
import java.util.Collections;

public class Hnefatafl {

    public int getAISearchDepth() {
        return 3;
    }

    public ArrayList<Move> getActionsForBoard(Board board, Equipe equipe) {
    	ArrayList<Move> possibilites = new ArrayList<Move>();
        // Add all of the actions for pieces that the current player owns.
        possibilites.clear();
    	for(int xDep = 0; xDep < 13; xDep += 1) {
    		for(int yDep = 0; yDep < 13; yDep += 1) {
    			for(int xArr = 0; xArr < 13; xArr += 1) {
    				for(int yArr = 0; yArr < 13; yArr +=1) {
    					if(xDep == xArr || yDep == yArr) {
    						int piece = board.getBoard()[xDep][yDep];
    						if(piece != 0) {
    							if(equipe == Equipe.ROUGE && piece == 4) {
    								Move m = new Move(xDep, yDep, xArr, yArr);
    								if(board.update(m, equipe, false)){
    									possibilites.add(m);
    								}
    							}
    							else if(equipe == Equipe.NOIR && (piece == 2 || piece == 5)) {
    								Move m = new Move(xDep, yDep, xArr, yArr);
    								if(board.update(m, equipe, false)){
    									possibilites.add(m);
    								}
    							}
    						}
    					}
					}
				}
			}
		}
    	Collections.shuffle(possibilites);
        return possibilites;
    }
}
