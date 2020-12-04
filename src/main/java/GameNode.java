import java.util.ArrayList;
import java.util.List;

public class GameNode {
    public static final boolean PLAYER_TURN = true;
    public static final boolean OPPONENT_TURN = false;

    private final Move move;

    public GameNode(Move move) {
        this.move = move;
    }

    public List<GameNode> getChildNodes() {
        List<Move> moves = Board.getBoard().getAllMoves();
        List<GameNode> nodes = new ArrayList<>();
        for(Move m : moves) {
            nodes.add(new GameNode(m));
        }
        return nodes;
    }

    public Move getMove() {
        return move;
    }

    public double evaluate(int d) {
        double value = 0.0d;
        Board.getBoard().play(move);
        if(d == 0) {
            value = Board.getBoard().getScore();
        } else {
            List<Move> moves = Board.getBoard().getAllMoves();
            for(Move m : moves) {
                value += 0.3 * new GameNode(m).evaluate(d - 1);
            }
        }
        Board.getBoard().unplay(move);
        return value;
    }
}
