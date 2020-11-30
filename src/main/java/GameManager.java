public class GameManager {
    private GameNode gameNode;
    private int turnNum;

    public GameManager(int piece, Move move, int frontierSize) {
        Board.getBoard().initializeBoard(piece);
        gameNode = new GameNode(move);
        turnNum = 0;
        for(int i = 0; i < frontierSize; i++) {
            populateFrontier(gameNode);
        }
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
            double val = n.evaluate();
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
            double val = n.evaluate();
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
        populateFrontier(gameNode);
        if(turnNum == 7) {
            populateFrontier(gameNode);
        }
        if(turnNum == 14) {
            populateFrontier(gameNode);
        }
        if(turnNum == 20) {
            populateFrontier(gameNode);
        }
        Board.getBoard().play(move);
    }

    private void populateFrontier(GameNode node) {
        Board.getBoard().play(node.getMove());
        if(node.getChildNodes().isEmpty()) {
            for(Move m : Board.getBoard().getAllMoves()) {
                node.addChildNode(new GameNode(m));
            }
        } else {
            for(GameNode n : node.getChildNodes()) {
                populateFrontier(n);
            }
        }
        Board.getBoard().unplay(node.getMove());
    }
}
