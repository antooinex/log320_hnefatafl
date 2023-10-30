package log320_hnefatafl;

public class RandomStrategy implements Strategy {
    private final Random random = new Random();

    @Override
    public Action decide(History history, List<Action> actions) {
        assert actions.size() > 0 : "Actions set must not be empty.";
        return actions.get(random.nextInt(actions.size()));
    }
}
