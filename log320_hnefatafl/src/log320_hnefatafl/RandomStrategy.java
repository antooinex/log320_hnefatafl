package log320_hnefatafl;

import java.util.Random;
import java.util.List;

public class RandomStrategy implements Strategy {
    private final Random random = new Random();

    @Override
    public String decide(List<String> actions) {
        assert actions.size() > 0 : "Actions set must not be empty.";
        return actions.get(random.nextInt(actions.size()));
    }
}
