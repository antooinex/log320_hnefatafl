package log320_hnefatafl;


import java.io.Serializable;

/**
 * Immutable class that represents an action a player can take.
 */
public final class Action implements Serializable {
    /** The player that is making this move. */
    private final Player player;

    /** The position that the piece is moving from. */
    private final Position from;

    /** The position that the piece is moving to. */
    private final Position to;

    public Action(Player player, Position from, Position to) {
        this.player = player;
        this.from = from;
        this.to = to;
    }

    // Getters
    public Player getPlayer() { return player; }
    public Position getFrom() { return from; }
    public Position getTo() { return to; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;
        return player == action.player &&
                from.equals(action.from) &&
                to.equals(action.to);
    }

    @Override
    public int hashCode() {
        int result = player.hashCode();
        result = 31 * result + from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Action(from=" + from.toString() + ",to=" + to.toString() + ")";
    }
}
