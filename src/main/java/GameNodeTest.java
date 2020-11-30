public class GameNodeTest {
    public static void main(String[] args) {
        GameManager gameManager = new GameManager(0, new Move(0, 0, 1, GameNode.PLAYER_TURN), 2);
        for(int i = 0; i < 25; i++) {
            Board.getBoard().printBoard();
            gameManager.playMove(gameManager.getNextMove());
            if(Board.getBoard().winner()) {
                break;
            }
        }
        Board.getBoard().printBoard();
    }
}
