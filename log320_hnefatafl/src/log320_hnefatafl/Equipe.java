package log320_hnefatafl;

public enum Equipe {
    NOIR, ROUGE, UNDEFINED;

    public Equipe opposite() {
        switch (this) {
            case NOIR:
                return Equipe.ROUGE;
            case ROUGE:
                return Equipe.NOIR;
            default:
            	return Equipe.UNDEFINED;
        }
    }
}
