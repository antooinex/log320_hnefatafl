package log320_hnefatafl;

import simulator.Action;
import net.varunramesh.hnefatafl.simulator.Board;
import net.varunramesh.hnefatafl.simulator.History;

import java.util.List;
import java.util.Set;

public interface Strategy {
    Action decide(History history, List<Action> actions);
}
