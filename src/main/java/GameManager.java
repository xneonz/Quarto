public class GameManager {
    private GameNode gameNode;
    private int turnNum;
    private int depth = 1;

    public GameManager(int piece, Move move) {
        Board.getBoard().initializeBoard(piece);
        gameNode = new GameNode(move);
        turnNum = 0;
        Board.getBoard().play(move);
    }

    public Move getNextMove() {
        if(gameNode.getMove().getPlayerTurn() == GameNode.OPPONENT_TURN) {
            return getLowestMove();
        } else {
            return getHighestMove();
        }
    }

    public Move getHighestMove() {
        Move m = null;
        double max = -Double.MAX_VALUE;
        for(GameNode n : gameNode.getChildNodes()) {
            double val = n.evaluate(depth);
            if(val > max) {
                max = val;
                m = n.getMove();
            }
        }
        return m;
    }

    public Move getLowestMove() {
        Move m = null;
        double min = Double.MAX_VALUE;
        for(GameNode n : gameNode.getChildNodes()) {
            double val = n.evaluate(depth);
            if(val < min) {
                min = val;
                m = n.getMove();
            }
        }
        return m;
    }

    public void playMove(Move move) {
        for(GameNode childNode : gameNode.getChildNodes()) {
            if(childNode.getMove().equals(move)) {
                gameNode = childNode;
                break;
            }
        }
        turnNum++;
        if(turnNum == 7) {
            depth++;
        }
        if(turnNum == 15) {
            depth++;
        }
        Board.getBoard().play(move);
    }
}
