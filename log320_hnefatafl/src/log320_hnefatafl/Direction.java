package log320_hnefatafl;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public Direction opposite() {
        switch (this) {
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
            case LEFT:
                return Direction.RIGHT;
            case RIGHT:
                return Direction.LEFT;
        }
        throw new UnsupportedOperationException("Direction inconnue.");
    }
}
