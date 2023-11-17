package log320_hnefatafl;

public enum Piece {
    ATTACKER,
    DEFENDER,
    KING,
    KNIGHT,
    COMMANDER;

    public boolean hostileTo(Piece other) {
        if(this == ATTACKER)
            return other == DEFENDER || other == KING;
        else
            return other == ATTACKER;
    }
}
