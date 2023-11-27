package log320_hnefatafl;

import java.util.ArrayList;
import java.util.Date;
/**
 * Implements a bot that uses the Minimax strategy to chooses moves.
 */
public class StrategieMinmax implements Strategie {
	
    private ArrayList<Move> moveHistory = new ArrayList<Move>();

    private long startTime;
    private long elapsedTime;
    private int WAIT_TIME_MILLI = 4800;
    
    /** Helper class that represents a (action, score) tuple */
    public static final class Result {
        public final Move action;
        public final float score;
        public Result(Move action, float score) {
            this.action = action;
            this.score = score;
        }
    }

    private final Hnefatafl ruleset = new Hnefatafl();
    
    /** The default value for searchDepth */
    public static final int DEFAULT_SEARCH_DEPTH = 2;

    /** The number of Plys to search forward */
    private final int searchDepth = ruleset.getAISearchDepth();

    /**
     * Variable used for counting how many leaf nodes of the game tree we visit. It is set to zero
     * before each search, and read after the search.
     * */
    private int leaves = 0;

    
    
    @Override
    public String coupAJouer(Board currentBoard, Equipe equipe) {

        // Clear leaves count.
        leaves = 0;

        startTime = System.currentTimeMillis();
        elapsedTime = 0L;
        
        System.out.println("Recherche en cours : minmax profondeur " + searchDepth + ".");
        Move action = max(currentBoard, searchDepth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY).action;
        System.out.println("Noeuds explorés : " +leaves + ".");
        
        /*if(moveHistory.size()>=2 && moveHistory.get(moveHistory.size()-2).equals(action)) {
        	//au cas où le coup a déjà été joué à l'avant-dernier coup
        	System.err.println("Coup en boucle.");
        	action = null;
        }*/

        if (action == null) {
            System.err.println("Utilisation de la stratégie Aléatoire.");
            action = new Move(new StrategieAleatoire().coupAJouer(currentBoard, Client.equipe));
            moveHistory.add(action);
        }

        moveHistory.add(action);
        return action.toString();
    }

    public static float WIN_SCORE = 1000.0f;
    public static float LOSE_SCORE = -1000.0f;

    /** Evaluate how good the board for the given player. */
    public static float eval(Board board, Equipe equipe) {
    	
    	Board parent = board.getParent();
    	int historySize = 0;
    	if(parent != null) {
    		historySize++;
    	}
    	while(parent != null) {
    		parent = parent.getParent();
    		historySize++;
    	}
    	
    	Winner gagnantEquipe = Winner.UNDETERMINED;
    	
        if (equipe == Equipe.ROUGE) {
        	gagnantEquipe = Winner.ATTACKER;
        }
        else{
        	gagnantEquipe = Winner.DEFENDER;
        }
    	
        // If the game already has a winner, then return -infinity or infinity
        if(board.isOver()) {
            if(board.getWinner() == Winner.DRAW) {
            	return 0;
            }
            else{
            	if(board.getWinner() == gagnantEquipe){
            		return WIN_SCORE - historySize;
            	}
            	else {
            		return LOSE_SCORE;
            	}
            }
        }

        float score = 0.0f;

        // Start by counting how many pieces each player has.
        int rouges = board.getnbPiecesRouge();
        int noires = board.getnbPiecesNoir();
        int xRoi = board.getxRoi();
        int yRoi = board.getyRoi();
        int[][] actualBoard = board.getBoard();
        
        if(equipe == Equipe.ROUGE) { //si on joue les rouges
        	score += (rouges - noires);
        	/*if(noires <= board.getParent().getnbPiecesNoir() ) { //si l'enfant a moins de noires que le parent
        		score += 100f;
        	}
        	else{
        		//score -= 100f;
        	}
   
        	if(rouges < board.getParent().getnbPiecesRouge()) { //si l'enfant a moins de rouges que le parent
        		score -= 100f;
        	}*/
        }
        else { //si on joue les noirs
        	score += (noires - rouges);
        	/*if(rouges <= board.getParent().getnbPiecesRouge()) { //si l'enfant a moins de rouges que le parent
        		score += 100f;
        	}
        	else {
        		//score -= 500f;
        	}
        	
        	if(noires < board.getParent().getnbPiecesNoir() ) { //si l'enfant a moins de noires que le parent
        		score -= 100f;
        	}*/
        }
        
        // Find the shortest distance between the King and any corner of the board.
    	int topLeftDist = Math.abs(0 - xRoi) + Math.abs(0 - yRoi);
    	int topRightDist = Math.abs(12 - xRoi) + Math.abs(0 - yRoi);
    	int bottomLeftDist = Math.abs(0 - xRoi) + Math.abs(12 - yRoi);
    	int bottomRightDist = Math.abs(12 - xRoi) + Math.abs(12 - yRoi);
    	int roiEntoure = 0;

        int shortestDist = Math.min(
                Math.min(topLeftDist, topRightDist),
                Math.min(bottomLeftDist, bottomRightDist)
        );

        // For the attacking player, the larger the distance, the higher the score. For the
        // defending player, the shorter the distance, the higher the score.
       if (equipe == Equipe.ROUGE){
            score += 0.5f*shortestDist;
            if(xRoi > 1 && yRoi > 1 && xRoi < 12 && yRoi < 12) {
            	if(xRoi < 12) {
		            if(actualBoard[xRoi+1][yRoi] == 4 && xRoi < 12) {
		            	//score += 1f;
		            	roiEntoure++;
		            }
	            }
            	if(yRoi < 12) {
		            if(actualBoard[xRoi][yRoi+1] == 4) {
		            	//score += 1f;
		            	roiEntoure++;
		            }
            	}
	            if(xRoi > 1) {
		            if(actualBoard[xRoi-1][yRoi] == 4) {
		            	//score += 1f;
		            	roiEntoure++;
		            }
		        }
	            if(yRoi > 1) {
		            if(actualBoard[xRoi][yRoi-1] == 4) {
		            	//score += 1f;
		            	roiEntoure++;
		            }
	            }
            }
            score += roiEntoure*10/4;
        }
        else{
            score -= 0.5f*shortestDist;
            if(xRoi > 1 && yRoi > 1 && xRoi < 12 && yRoi < 12) {
	            if(actualBoard[xRoi+1][yRoi] == 4) {
	            	score -= 1f;
	            }
	            if(actualBoard[xRoi][yRoi+1] == 4) {
	            	score -= 1f;
	            }
	            if(actualBoard[xRoi-1][yRoi] == 4) {
	            	score -= 1f;
	            }
	            if(actualBoard[xRoi][yRoi-1] == 4) {
	            	score -= 1f;
	            }
            }
        }
       
       return score;
    }

    /** Whether or not to use ALPHA_BETA_PRUNING to prune search paths */
    public static final boolean ALPHA_BETA_PRUNING = true;

    public Result max(Board board, int depth, float alpha, float beta) {
    	ArrayList<Move> possibilities = ruleset.getActionsForBoard(board, Client.equipe);
    	elapsedTime = (new Date()).getTime() - startTime;
    	
    	if(possibilities.isEmpty()) {
    		board.setOver(true);
    		board.setWinner(Winner.DRAW);
    	}
    	if(board.isOver() || depth == 0) {
            ++leaves; // We've reached a leaf.
            return new Result(null, eval(board, Client.equipe));
        }

        Result max = null;
        for(Move action : possibilities) {
            // Simulate a step, and then recurse.
	        	if(elapsedTime < WAIT_TIME_MILLI) {
	            Board newBoard = new Board(board, action, Client.equipe);
	            Result result = min(newBoard, depth - 1, alpha, beta);
	
	            if(max == null || result.score > max.score)
	                max = new Result(action, result.score);
	
	            if(ALPHA_BETA_PRUNING) {
	                alpha = Math.max(alpha, result.score);
	                if(beta <= alpha) break;
	            }
        	}
        	else {
        		System.out.println("Stop !");
        		break;
        	}
        }
        //System.out.println(max.score);
        return max;
    }

    public Result min(Board board, int depth, float alpha, float beta) {
    	ArrayList<Move> possibilities = ruleset.getActionsForBoard(board, Client.equipe.opposite());
    	elapsedTime = (new Date()).getTime() - startTime;
    	if(possibilities.isEmpty()) {
    		board.setOver(true);
    	}
        if(board.isOver() || depth == 0) {
            ++leaves; // We've reached a leaf.
            return new Result(null, eval(board, Client.equipe));
        }

        Result min = null;
        for(Move action : possibilities) {
            // Simulate a step, and then recurse.
        	if(elapsedTime < WAIT_TIME_MILLI) {
        		Board newBoard = new Board(board, action, Client.equipe.opposite());
                Result result = max(newBoard, depth - 1, alpha, beta);
                
                if(min == null || result.score < min.score)
                    min = new Result(action, result.score);

                if(ALPHA_BETA_PRUNING) {
                    beta = Math.min(beta, result.score);
                    if(beta <= alpha) break;
                }
        	}
        	else {
        		System.out.println("Stop !");
        		break;
        	}
        	
        }
        return min;
    }
}