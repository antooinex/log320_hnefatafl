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
        Result minmax =  max(currentBoard, searchDepth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        Move action = minmax.action;
        System.out.println("Noeuds explorés : " +leaves + ".\nScore du coup trouvé : "+minmax.score);

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
    	
    	float score = 0.0f;
	
		//calcul taille historique
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
	
		if(equipe == Equipe.ROUGE){ //on évalue pour les rouges
			
			gagnantEquipe = Winner.ATTACKER;
			
			if(board.isOver()){ //si un gagnant est détecté
				Winner gagnantBoard = board.getWinner();
				if(gagnantBoard == Winner.DRAW){
					return 0f;
				}
				else if(gagnantBoard == gagnantEquipe){
					return WIN_SCORE - historySize;
				}
				else{
					return LOSE_SCORE;
				}
			}
			else{ //si pas de gagnant détecté
			
				// compter le nb de pièces pour chaque équipe
				int rouges = board.getnbPiecesRouge();
				int noires = board.getnbPiecesNoir();
				
				score = 0.0f;
				
				//moins il y a de noires mieux c'est (coef x5 sur norme 100)
				score += ((13f - noires)*100f/13f)*5f;
				
				//chaque rouge en moins vaut 1 point de moins (coef x1 sur norme 100)
				score -= (24f - rouges)*100f/24f;
				
				//favoriser encercler le roi
				int compteurRoi = board.getCompteurRoi();
				score += 25f*compteurRoi;
			}
		}
		else{ //on évalue pour les noirs
			
			gagnantEquipe = Winner.DEFENDER;
			
			if(board.isOver()){ //si un gagnant est détecté
				Winner gagnantBoard = board.getWinner();
				if(gagnantBoard == Winner.DRAW){
					return 0f;
				}
				else if(gagnantBoard == gagnantEquipe){
					return WIN_SCORE - historySize;
				}
				else{
					return LOSE_SCORE;
				}
			}
			else{ //si pas de gagnant détecté
			
				// compter le nb de pièces pour chaque équipe
				int rouges = board.getnbPiecesRouge();
				int noires = board.getnbPiecesNoir();
				int xRoi = board.getxRoi();
				int yRoi = board.getyRoi();
				int[][] actualBoard = board.getBoard();
				
				score = 0.0f;
				
				//plus il y a de noires mieux c'est (coef x2 sur norme 100)
				score += (noires*100f/13f)*2f;
				
				//chaque rouge en moins vaut 1 point de plus (coef x1 sur norme 100)
				score += (24f - rouges)*100f/24f;
				
				//trouver coordonnées du roi
				for(int i = 0; i < 13; i++){
					for(int j = 0; j < 13; j++){
						if(actualBoard[i][j] == 5){
							xRoi = i;
							yRoi = j;
							break;
						}
					}
				}
				
				//calculer les distances manhattan entre le roi et les coins
				int topLeftDist = Math.abs(0 - xRoi) + Math.abs(0 - yRoi);
				int topRightDist = Math.abs(12 - xRoi) + Math.abs(0 - yRoi);
				int bottomLeftDist = Math.abs(0 - xRoi) + Math.abs(12 - yRoi);
				int bottomRightDist = Math.abs(12 - xRoi) + Math.abs(12 - yRoi);
				
				//prendre la distance la plus courte
				int shortestDist = Math.min(
					Math.min(topLeftDist, topRightDist),
					Math.min(bottomLeftDist, bottomRightDist)
				);
				
				//plus le roi est proche d'un coin mieux c'est (coef 10x sur norme 100)
				score += (12f-shortestDist)*100f/12f*10f; //la distance max est 12
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
	            
	            if(result != null) {
		            if(max == null || result.score > max.score)
		                max = new Result(action, result.score);
		
		            if(ALPHA_BETA_PRUNING) {
		                alpha = Math.max(alpha, result.score);
		                if(beta <= alpha) break;
		            }
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
            return new Result(null, eval(board, Client.equipe.opposite()));
        }

        Result min = null;
        for(Move action : possibilities) {
            // Simulate a step, and then recurse.
        	if(elapsedTime < WAIT_TIME_MILLI) {
        		Board newBoard = new Board(board, action, Client.equipe.opposite());
                Result result = max(newBoard, depth - 1, alpha, beta);
                
                if(result != null) {
	                if(min == null || result.score < min.score)
	                    min = new Result(action, result.score);
	
	                if(ALPHA_BETA_PRUNING) {
	                    beta = Math.min(beta, result.score);
	                    if(beta <= alpha) break;
	                }
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