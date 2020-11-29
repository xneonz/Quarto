import java.util.ArrayList;
import java.util.List;

public class GameNode {
    public static final boolean MIN_NODE = false;
    public static final boolean MAX_NODE = true;

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

    public int evaluate(int limit) {
        int score = 0;
        Board.getBoard().play(move);
        if(Board.getBoard().winner()) {
            score = Integer.MAX_VALUE;
        } else if(childNodes.isEmpty()) {
            score = Board.getBoard().getScore();
        } else {
            if(nodeType == MAX_NODE) {
                score = childNodes.get(0).evaluate(Integer.MIN_VALUE);
                for(int i = 1; i < childNodes.size(); i++) {
                    if(score >= limit) {
                        break;
                    } else {
                        int nextScore = childNodes.get(i).evaluate(score);
                        if(nextScore > score) {
                            score = nextScore;
                        }
                    }
                }
            } else {
                score = childNodes.get(0).evaluate(Integer.MAX_VALUE);
                for(int i = 1; i < childNodes.size(); i++) {
                    if(score <= limit) {
                        break;
                    } else {
                        int nextScore = childNodes.get(i).evaluate(score);
                        if(nextScore < score) {
                            score = nextScore;
                        }
                    }
                }
            }
        }
        Board.getBoard().unplay(move);
        return score;
    }
}
