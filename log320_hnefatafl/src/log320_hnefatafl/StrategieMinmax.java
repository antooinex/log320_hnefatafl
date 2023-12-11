package log320_hnefatafl;

import java.util.ArrayList;
import java.util.Date;

public class StrategieMinmax implements Strategie {
	
    private ArrayList<Move> moveHistory = new ArrayList<Move>();

    private long startTime;
    private long elapsedTime;
    private int WAIT_TIME_MILLI = 4800;
    
    public class Result {
        public final Move action;
        public final float score;
        public Result(Move action, float score) {
            this.action = action;
            this.score = score;
        }
    }

    private Hnefatafl ruleset = new Hnefatafl();

    private int searchDepth;

    private int leaves = 0;

    @Override
    public String coupAJouer(Board currentBoard, Equipe equipe) {

        leaves = 0;

        startTime = System.currentTimeMillis();
        elapsedTime = 0L;
        
        if(Client.equipe == Equipe.NOIR) {
        	this.searchDepth = 2;
        }
        else {
        	this.searchDepth = 3;
        }
        
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
					score += 0f;
				}
				else if(gagnantBoard == gagnantEquipe){
					score += WIN_SCORE - historySize;
				}
				else{
					score += LOSE_SCORE;
				}
			}			
			// compter le nb de pièces pour chaque équipe
			int rouges = board.getnbPiecesRouge();
			int noires = board.getnbPiecesNoir();
			
			score = 0.0f;
			
			//moins il y a de noires mieux c'est (coef x5 sur norme 100)
			score += ((13f - noires)*100f/13f)*5f;
			
			//chaque rouge en moins vaut 1 point de moins (coef x5 sur norme 100)
			score -= (24f - rouges)*100f/24f*5f;
			
			//favoriser encercler le roi
			int compteurRoi = board.getCompteurRoi();
			if(compteurRoi == 1) {
				score += 50f;
			}
			else if(compteurRoi == 2) {
				score += 200f;
			}
			else if(compteurRoi == 3) {
				score += 800f;
			}
			else if(compteurRoi == 4) {
				return WIN_SCORE;
			}
			
			//bloquer les coins
			int[][] actualBoard = board.getBoard();
			
			if(actualBoard[0][2] == 4) {
				score+=15f;
			}
			if(actualBoard[1][1] == 4) {
				score+=15f;
			}
			if(actualBoard[2][0] == 4) {
				score+=15f;
			}
			if(actualBoard[10][0] == 4) {
				score+=15f;
			}
			if(actualBoard[11][1] == 4) {
				score+=15f;
			}
			if(actualBoard[12][2] == 4) {
				score+=15f;
			}
			if(actualBoard[2][12] == 4) {
				score+=15f;
			}
			if(actualBoard[1][11] == 4) {
				score+=15f;
			}
			if(actualBoard[0][10] == 4) {
				score+=15f;
			}
			if(actualBoard[12][10] == 4) {
				score+=15f;
			}
			if(actualBoard[11][11] == 4) {
				score+=15f;
			}
			if(actualBoard[10][12] == 4) {
				score+=15f;
			}
		}
		else{ //on évalue pour les noirs
			
			gagnantEquipe = Winner.DEFENDER;
			
			if(board.isOver()){ //si un gagnant est détecté
				Winner gagnantBoard = board.getWinner();
				if(gagnantBoard == Winner.DRAW){
					score += 0f;
				}
				else if(gagnantBoard == gagnantEquipe){
					score += WIN_SCORE - historySize;
				}
				else{
					score += LOSE_SCORE;
				}
			}			
			// compter le nb de pièces pour chaque équipe
			int rouges = board.getnbPiecesRouge();
			int noires = board.getnbPiecesNoir();
			int xRoi = board.getxRoi();
			int yRoi = board.getyRoi();
			
			score = 0.0f;
			
			//plus il y a de noires mieux c'est (coef x2 sur norme 100)
			score += (noires*100f/13f)*2f;
			
			//chaque rouge en moins vaut 1 point de plus (coef x1 sur norme 100)
			score += (24f - rouges)*100f/24f;
			
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
        return score;
    }
    
    private boolean ALPHA_BETA_PRUNING = true;

    public Result max(Board board, int depth, float alpha, float beta) {
    	ArrayList<Move> possibilities = ruleset.getActionsForBoard(board, Client.equipe);
    	
    	if(possibilities.isEmpty()) {
    		board.setOver(true);
    		board.setWinner(Winner.DRAW);
    	}
    	if(depth == 0) {
            ++leaves;
            return new Result(null, eval(board, Client.equipe));
        }

        Result max = null;
        for(Move action : possibilities) {
        	elapsedTime = (new Date()).getTime() - startTime;
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
        return max;
    }

    public Result min(Board board, int depth, float alpha, float beta) {
    	ArrayList<Move> possibilities = ruleset.getActionsForBoard(board, Client.equipe.opposite());
    	elapsedTime = (new Date()).getTime() - startTime;
    	if(possibilities.isEmpty()) {
    		board.setOver(true);
    		board.setWinner(Winner.DRAW);
    	}
        if(depth == 0) {
            ++leaves;
            return new Result(null, eval(board, Client.equipe));
        }

        Result min = null;
        for(Move action : possibilities) {
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