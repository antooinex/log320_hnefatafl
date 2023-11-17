package log320_hnefatafl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hnefatafl implements Ruleset, Serializable {
    private static final String TAG = "Hnefatafl";
    public static final int BOARD_SIZE = 11;

    @Override
    public Board getStartingConfiguration() {
        Map<Position, Piece> pieces = new HashMap<Position, Piece>();

        // King position
        pieces.put(new Position(5, 5), Piece.KING);

        // Defender locations
        pieces.put(new Position(7, 5), Piece.DEFENDER);
        pieces.put(new Position(6, 5), Piece.DEFENDER);
        pieces.put(new Position(4, 5), Piece.DEFENDER);
        pieces.put(new Position(3, 5), Piece.DEFENDER);

        pieces.put(new Position(5, 7), Piece.DEFENDER);
        pieces.put(new Position(5, 6), Piece.DEFENDER);
        pieces.put(new Position(5, 4), Piece.DEFENDER);
        pieces.put(new Position(5, 3), Piece.DEFENDER);

        pieces.put(new Position(4, 4), Piece.DEFENDER);
        pieces.put(new Position(4, 6), Piece.DEFENDER);
        pieces.put(new Position(6, 4), Piece.DEFENDER);
        pieces.put(new Position(6, 6), Piece.DEFENDER);

        // Attacker locations
        pieces.put(new Position(0, 3), Piece.ATTACKER);
        pieces.put(new Position(0, 4), Piece.ATTACKER);
        pieces.put(new Position(0, 5), Piece.ATTACKER);
        pieces.put(new Position(0, 6), Piece.ATTACKER);
        pieces.put(new Position(0, 7), Piece.ATTACKER);
        pieces.put(new Position(1, 5), Piece.ATTACKER);

        pieces.put(new Position(10, 3), Piece.ATTACKER);
        pieces.put(new Position(10, 4), Piece.ATTACKER);
        pieces.put(new Position(10, 5), Piece.ATTACKER);
        pieces.put(new Position(10, 6), Piece.ATTACKER);
        pieces.put(new Position(10, 7), Piece.ATTACKER);
        pieces.put(new Position(9, 5), Piece.ATTACKER);

        pieces.put(new Position(3, 0), Piece.ATTACKER);
        pieces.put(new Position(4, 0), Piece.ATTACKER);
        pieces.put(new Position(5, 0), Piece.ATTACKER);
        pieces.put(new Position(6, 0), Piece.ATTACKER);
        pieces.put(new Position(7, 0), Piece.ATTACKER);
        pieces.put(new Position(5, 1), Piece.ATTACKER);

        pieces.put(new Position(3, 10), Piece.ATTACKER);
        pieces.put(new Position(4, 10), Piece.ATTACKER);
        pieces.put(new Position(5, 10), Piece.ATTACKER);
        pieces.put(new Position(6, 10), Piece.ATTACKER);
        pieces.put(new Position(7, 10), Piece.ATTACKER);
        pieces.put(new Position(5, 9), Piece.ATTACKER);

        return new Board(new Grid(BOARD_SIZE).add(pieces), Player.ATTACKER, Winner.UNDETERMINED, BOARD_SIZE);
    }

    @Override
    public String getRulesetName() {
        return "Fetlar Hnefatafl";
    }

    @Override
    public String getRulesHTML() {
        return "file:///android_asset/rules/tablut.html";
    }

    @Override
    public Board step(History history, Action action, EventHandler eventHandler) {
        Board currentBoard = history.getCurrentBoard();

        // Basic assertions about the current game state.
        assert currentBoard.getWinner().equals(Winner.UNDETERMINED) : "A winner has not yet been set";
        assert action.getPlayer().equals(currentBoard.getCurrentPlayer()) : "The provided action is for the currently active player.";
        assert action != null : "Action is non-null.";

        // TODO: Probably verify move and complain if it's illegal.
        // for now, assume that the move was given by us, and is thus valid.

        // Move the piece.
        Grid pieces = currentBoard.getPieces();
        Piece piece = pieces.get(action.getFrom());
        Grid newPieces = pieces.remove(action.getFrom());
        newPieces = newPieces.add(action.getTo(), piece);
        if(eventHandler != null) eventHandler.movePiece(action.getFrom(), action.getTo());

        // Look to see if any adjacent opposing piece has been sandwiched.
        for(Direction dir : directions) {
            final Position pos = action.getTo().getNeighbor(dir);
            if(newPieces.inBounds(pos) && newPieces.pieceAt(pos)) {
                Piece adjacentPiece = newPieces.get(pos);
                if(adjacentPiece.hostileTo(piece) && isCaptured(currentBoard, newPieces, pos, action.getTo())) {
                    newPieces = newPieces.remove(pos);
                    if(eventHandler != null) eventHandler.removePiece(pos);
                }
            }
        }

        // Check to see if someone has won.
        Winner winner = Winner.UNDETERMINED;
        Board tempBoard = new Board(newPieces, currentBoard.getCurrentPlayer().other(), winner, currentBoard.getBoardSize());
        if (kingInRefugeeSquare(tempBoard)) {
            // If the King is in a refugee square, the defenders win.
            winner = Winner.DEFENDER;
        } else if (tempBoard.getPositionsOfPiece(Piece.KING).size() == 0){
            // If the King has been captured, then the attackers win.
            winner = Winner.ATTACKER;
        } else {
            // If the next board would result in that player having no actions, then
            // the current player has won.
            if(getActionsForBoard(tempBoard).size() == 0) {
                winner = Winner.fromPlayer(currentBoard.getCurrentPlayer());
            }
        }

        if(winner != Winner.UNDETERMINED && eventHandler != null)
            eventHandler.setWinner(winner);

        return new Board(newPieces, currentBoard.getCurrentPlayer().other(), winner, currentBoard.getBoardSize());
    }

    @Override
    public int getAISearchDepth() {
        return 2;
    }


    public ArrayList<Move> getActionsForBoard(Board board) {
    	ArrayList<Move> possibilites = new ArrayList<Move>();
    	StringBuilder sb = new StringBuilder();	
        // Add all of the actions for pieces that the current player owns.
        possibilites.clear();
    	for(int xDep = 1; xDep <= 13; xDep += 1) {
    		for(int yDep = 1; yDep <= 13; yDep += 1) {
    			for(int xArr = 1; xArr <= 13; xArr += 1) {
    				for(int yArr = 1; yArr <= 13; yArr +=1) {
    					if(xDep == xArr || yDep == yArr) {
							sb.append(board.antiCoord(xDep));
							sb.append(Integer.toString(yDep));
							sb.append(" - ");
							sb.append(board.antiCoord(xArr));
							sb.append(Integer.toString(yArr));
							Move m = new Move(xDep, yDep, xArr, yArr);
							if(board.update(m, Client.equipe, false)){
								possibilites.add(m);
							}
							sb.setLength(0);
    					}
    				}
    			}
    		}
    	}
        return possibilites;
    }

    private static Direction[] directions = Direction.values();

    /*
    // Returns true if the square is a King-Only Square. Hard-coded for performance.
    public boolean isKingOnlySquare(Position pos) {
        return (pos.getX() == 5 && pos.getY() == 5) ||
                (pos.getX() == 0 && pos.getY() == 0) ||
                (pos.getX() == 0 && pos.getY() == 10) ||
                (pos.getX() == 10 && pos.getY() == 0) ||
                (pos.getX() == 10 && pos.getY() == 10);
    }
    */
    /*
    public boolean kingInRefugeeSquare(Board board) {
        Grid pieces = board.getPieces();
        return Stream.of(board.getCornerSquares()).anyMatch((Position pos) -> {
            return pieces.inBounds(pos) && pieces.pieceAt(pos) && pieces.get(pos) == Piece.KING;
        });
    }
	*/
    /*
    public static boolean isCaptured(Board board, Grid pieces, Position defendingPos, Position attackingPos) {
        final Piece piece = pieces.get(defendingPos);
        switch (piece) {
            case KING:
                return Stream.of(Direction.values()).allMatch((Direction dir) -> {
                    // The King is only sandwiched when all four s
                    Position adjacent = defendingPos.getNeighbor(dir);
                    return pieces.inBounds(adjacent) && pieces.pieceAt(adjacent)  && pieces.get(adjacent).hostileTo(piece);
                });
            case ATTACKER: {
                Position oppositePos = defendingPos.getNeighbor(defendingPos.directionTo(attackingPos).opposite());
                return board.getCenterSquare().equals(oppositePos)
                        || board.getCornerSquares().contains(oppositePos)
                        || (pieces.inBounds(oppositePos) && pieces.pieceAt(oppositePos) && pieces.get(oppositePos).hostileTo(piece));
            }
            case DEFENDER: {
                Position oppositePos = defendingPos.getNeighbor(defendingPos.directionTo(attackingPos).opposite());
                return board.getCornerSquares().contains(oppositePos)
                        || (pieces.inBounds(oppositePos) && pieces.pieceAt(oppositePos) && pieces.get(oppositePos).hostileTo(piece));
            }
        }
        return true;
    }
    */
}
