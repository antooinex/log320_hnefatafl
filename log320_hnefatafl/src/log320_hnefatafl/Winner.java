package log320_hnefatafl;

/**
 * Enum to represent the winner of a game state
 */
public enum Winner {
    /** The winner of the game has not been determined yet */
    UNDETERMINED,
    /** The attacking player has won the game */
    ATTACKER,
    /** The defending player has won the game */
    DEFENDER,
    /** The game has ended in a tie / draw */
    DRAW;
}
