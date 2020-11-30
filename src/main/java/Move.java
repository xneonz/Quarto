public class Move {
    int x;
    int y;
    int piece;
    boolean playerTurn;

    public Move(int x, int y, int piece, boolean playerTurn) {
        this.x = x;
        this.y = y;
        this.piece = piece;
        this.playerTurn = playerTurn;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPiece() {
        return piece;
    }

    public boolean getPlayerTurn() {
        return playerTurn;
    }

    public String squareToString() {
        return String.format("%d,%d", x, y);
    }

    public String pieceToString() {
        return Integer.toBinaryString(piece | 32).substring(1);
    }

    public boolean equals(Move m) {
        return getY() == m.getY() && getX() == m.getX() && getPiece() == m.getPiece();
    }
}
