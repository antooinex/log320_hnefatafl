package log320_hnefatafl;

import java.util.Random;
import java.util.ArrayList;

public class StrategieAleatoire implements Strategie {
    private final Random random = new Random();
	private ArrayList<String> possibilites = new ArrayList<String>();

    @Override
    public String coupAJouer(Board board, Equipe equipe) {
    	possibilites.clear();
    	for(int xDep = 1; xDep <= 13; xDep += 1) {
    		for(int yDep = 1; yDep <= 13; yDep += 1) {
    			for(int xArr = 1; xArr <= 13; xArr += 1) {
    				for(int yArr = 1; yArr <= 13; yArr +=1) {
    					if(xDep == xArr || yDep == yArr) {
    						Move move = new Move(xDep, yDep, xArr, yArr);
							if(board.update(move, equipe, false)){
								possibilites.add(move.toString());
							}
    					}
    				}
    			}
    		}
    	}
    	
    	if(possibilites.size() == 0) {
    		possibilites.add("Z0 - Z0");
    	}
    	
    	return possibilites.get(random.nextInt(possibilites.size()));
    }
}
