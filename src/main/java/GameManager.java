public class GameManager {
    private GameNode gameNode;
    private int turnNum;

    public GameManager(GameNode node, int frontierSize) {
        gameNode = node;
        turnNum = 0;
        for(int i = 0; i < frontierSize; i++) {
            populateFrontier(gameNode);
        }
    }

    public Move getNextMove() {
        Move nextMove = null;
        int score = (gameNode.getNodeType() == GameNode.OPPONENT_TURN) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for(GameNode n : gameNode.getChildNodes()) {
            int val = n.evaluate();
            if(gameNode.getNodeType() == GameNode.OPPONENT_TURN && val >= score) {
                score = val;
                nextMove = n.getMove();
            } else if(gameNode.getNodeType() == GameNode.PLAYER_TURN && val <= score) {
                score = val;
                nextMove = n.getMove();
            }
        }
        return nextMove;
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
        if(turnNum == 15) {
            populateFrontier(gameNode);
        }
        if(turnNum == 16) {
            populateFrontier(gameNode);
        }
        Board.getBoard().play(move);
    }

    private void populateFrontier(GameNode node) {
        Board.getBoard().play(node.getMove());
        if(node.getChildNodes().isEmpty()) {
            for(Move m : Board.getBoard().getAllMoves()) {
                node.addChildNode(new GameNode(!node.getNodeType(), m));
            }
        } else {
            for(GameNode n : node.getChildNodes()) {
                populateFrontier(n);
            }
        }
        Board.getBoard().unplay(node.getMove());
    }
}
