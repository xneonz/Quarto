import java.util.ArrayList;
import java.util.List;

public class GameNode {
    public static final boolean PLAYER_TURN = true;
    public static final boolean OPPONENT_TURN = false;

    private boolean nodeType;
    private List<GameNode> childNodes;
    private Move move;

    public GameNode(boolean nodeType, Move move) {
        this.nodeType = nodeType;
        this.move = move;
        childNodes = new ArrayList<>();
    }

    public void addChildNode(GameNode gameNode) {
        childNodes.add(gameNode);
    }

    public boolean getNodeType() {
        return nodeType;
    }

    public List<GameNode> getChildNodes() {
        return childNodes;
    }

    public Move getMove() {
        return move;
    }

    public int evaluate() {
        return 0;
    }
}
