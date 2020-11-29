public class GameManager {
    private GameNode gameNode;

    public GameManager(GameNode node, int frontierSize) {
        gameNode = node;
        for(int i = 0; i < frontierSize; i++) {
            populateFrontier(gameNode);
        }
    }

    public Move getHighestMove() {
        GameNode node = gameNode.getChildNodes().get(0);
        int limit = (gameNode.getNodeType() == GameNode.MAX_NODE) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        int max = node.evaluate(limit);
        for(int i = 0; i < gameNode.getChildNodes().size(); i++) {
            int val = gameNode.getChildNodes().get(i).evaluate(limit);
            if(val > max) {
                max = val;
                node = gameNode.getChildNodes().get(i);
            }
        }
        return node.getMove();
    }

    public Move getLowestMove() {
        GameNode node = gameNode.getChildNodes().get(0);
        int limit = (gameNode.getNodeType() == GameNode.MAX_NODE) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        int min = node.evaluate(limit);
        for(int i = 0; i < gameNode.getChildNodes().size(); i++) {
            int val = gameNode.getChildNodes().get(i).evaluate(limit);
            if(val < min) {
                min = val;
                node = gameNode.getChildNodes().get(i);
            }
        }
        return node.getMove();
    }

    public void playMove(Move move) {
        for(GameNode childNode : gameNode.getChildNodes()) {
            if(childNode.getMove().equals(move)) {
                gameNode = childNode;
                break;
            }
        }
        Board.getBoard().play(move);
        populateFrontier(gameNode);
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
