package log320_hnefatafl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

    /** The default value for searchDepth */
    public static final int DEFAULT_SEARCH_DEPTH = 2;

    /** The player that the AI represents. */
    private final Player player;

    /** The number of Plys to search forward */
    private final int searchDepth;

    /**
     * Variable used for counting how many leaf nodes of the game tree we visit. It is set to zero
     * before each search, and read after the search.
     * */
    private int leaves = 0;

    private final Hnefatafl ruleset;

    public StrategieMinmax(Hnefatafl ruleset, Player player) {
        this(ruleset, player, ruleset.getAISearchDepth());
    }

    public StrategieMinmax(Hnefatafl ruleset, Player player, int searchDepth) {
        this.ruleset = ruleset;
        this.player = player;
        this.searchDepth = searchDepth;
    }


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
    public static float eval(Board board, Player player) {

        // If the game already has a winner, then return -infinity or infinity
        if(board.isOver()) {
            if(board.getWinner() == Winner.DRAW) return 0;
            else return board.getWinner().equals(Winner.fromPlayer(player))
                    ? WIN_SCORE - history.size() // Penalize the win by match size, so that the MiniMax search prefers immediate wins.
                    : LOSE_SCORE; // A loss is always a loss.
        }

        float score = 0.0f;

        // Start by counting how many pieces each player has.
        for (Map.Entry<Position, Piece> piece : board.getPieces().getEntries()) {
            if(player.ownsPiece(piece.getValue()))
                score += 1.0f;
            else
                score -= 1.0f;
        }

        // Find the shortest distance between the King and any corner of the board.
        for(Position kingPos : board.getPositionsOfPiece(Piece.KING)) {
            int topLeftDist = kingPos.distanceTo(new Position(0, 0));
            int topRightDist = kingPos.distanceTo(new Position(board.getBoardSize() - 1, 0));
            int bottomLeftDist = kingPos.distanceTo(new Position(0, board.getBoardSize() - 1));
            int bottomRightDist = kingPos.distanceTo(new Position(board.getBoardSize() - 1, board.getBoardSize() - 1));

            int shortestDist = Math.min(
                    Math.min(topLeftDist, topRightDist),
                    Math.min(bottomLeftDist, bottomRightDist)
            );

            // For the attacking player, the larger the distance, the higher the score. For the
            // defending player, the shorter the distance, the higher the score.
            if (player == Player.ATTACKER)
                score += 0.5f*shortestDist;
            else
                score -= 0.5f*shortestDist;
        }

        return score;
    }

    /** Whether or not to use ALPHA_BETA_PRUNING to prune search paths */
    public static final boolean ALPHA_BETA_PRUNING = true;

    public Result max(Board board, int depth, float alpha, float beta) {
    	if(board.isOver() || depth == 0) {
            ++leaves; // We've reached a leaf.
            return new Result(null, eval(history, player));
        }

        Result max = null;
        for(Move action : ruleset.getActionsForBoard(board, Client.equipe)) {
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
        if(board.isOver() || depth == 0) {
            ++leaves; // We've reached a leaf.
            return new Result(null, eval(history, player));
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