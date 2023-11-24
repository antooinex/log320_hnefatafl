package log320_hnefatafl;

import java.util.ArrayList;
/**
 * Implements a bot that uses the Minimax strategy to chooses moves.
 */
public class StrategieMinmax implements Strategie {
    private final String TAG = "MinimaxStrategy";

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

        System.out.println(TAG + ": Starting a Minimax search with depth " + searchDepth + ".");
        Move action = max(currentBoard, searchDepth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY).action;
        System.out.println(TAG + ": MinimaxStrategy searched " + leaves + " possible game states.");

        // Should never happen, but to prevent crashes.
        if (action == null) {
            System.err.print(TAG + ": MinimaxStrategy returned null action. Falling back to RandomStrategy.");
            return new StrategieAleatoire().coupAJouer(currentBoard, Client.equipe);
        }

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
        
        if(equipe == Equipe.ROUGE) {
        	score += (rouges - noires);
        }
        else {
        	score += (noires - rouges);
        }
        
        // Find the shortest distance between the King and any corner of the board.
        	int topLeftDist = Math.abs(0 - board.getxRoi()) + Math.abs(0 - board.getyRoi());
        	int topRightDist = Math.abs(12 - board.getxRoi()) + Math.abs(0 - board.getyRoi());
        	int bottomLeftDist = Math.abs(0 - board.getxRoi()) + Math.abs(12 - board.getyRoi());
        	int bottomRightDist = Math.abs(12 - board.getxRoi()) + Math.abs(12 - board.getyRoi());

            int shortestDist = Math.min(
                    Math.min(topLeftDist, topRightDist),
                    Math.min(bottomLeftDist, bottomRightDist)
            );

            // For the attacking player, the larger the distance, the higher the score. For the
            // defending player, the shorter the distance, the higher the score.
            if (equipe == Equipe.ROUGE)
                score += 0.5f*shortestDist;
            else
                score -= 0.5f*shortestDist;

        return score;
    }

    /** Whether or not to use ALPHA_BETA_PRUNING to prune search paths */
    public static final boolean ALPHA_BETA_PRUNING = true;

    public Result max(Board board, int depth, float alpha, float beta) {
    	ArrayList<Move> possibilities = ruleset.getActionsForBoard(board, Client.equipe);
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
            //Result result = min(history.advance(action, ruleset.step(history, action, null)), depth - 1, alpha, beta);
            Board newBoard = new Board(board, action);
            Result result = min(newBoard, depth - 1, alpha, beta);
            
            //Result result = min(new Board())

            if(max == null || result.score > max.score)
                max = new Result(action, result.score);

            if(ALPHA_BETA_PRUNING) {
                alpha = Math.max(alpha, result.score);
                if(beta <= alpha) break;
            }
        }
        return max;
    }

    public Result min(Board board, int depth, float alpha, float beta) {
    	ArrayList<Move> possibilities = ruleset.getActionsForBoard(board, Client.equipe);
    	if(possibilities.isEmpty()) {
    		board.setOver(true);
    	}
        if(board.isOver() || depth == 0) {
            ++leaves; // We've reached a leaf.
            return new Result(null, eval(board, Client.equipe));
        }

        Result min = null;
        for(Move action : ruleset.getActionsForBoard(board, Client.equipe)) {
            // Simulate a step, and then recurse.
            //Result result = max(history.advance(action, ruleset.step(history, action, null)), depth - 1, alpha, beta);
            Board newBoard = new Board(board, action);
            Result result = max(newBoard, depth - 1, alpha, beta);
            

            if(min == null || result.score < min.score)
                min = new Result(action, result.score);

            if(ALPHA_BETA_PRUNING) {
                beta = Math.min(beta, result.score);
                if(beta <= alpha) break;
            }
        }
        return min;
    }
}