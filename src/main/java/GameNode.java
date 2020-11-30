import java.util.ArrayList;
import java.util.List;

public class GameNode {
    public static final boolean PLAYER_TURN = true;
    public static final boolean OPPONENT_TURN = false;

    private boolean nodeType;
    private List<GameNode> childNodes;
    private Move move;

    public GameNode(Move move) {
        this.nodeType = move.playerTurn;
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

    public double evaluate() {
        double value = 0.0d;
        Board.getBoard().play(move);
        if(childNodes.isEmpty()) {
            value = Board.getBoard().getScore();
        } else {
            for(GameNode n : childNodes) {
                value += 0.3 * n.evaluate();
            }
        }
        Board.getBoard().unplay(move);
        return value;
    }
}
