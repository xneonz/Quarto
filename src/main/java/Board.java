import java.util.ArrayList;
import java.util.List;

public class Board {
    private int[][] squares;
    private boolean[] pieces;
    private int piece = 0;
    private boolean playerTurn = false;

    private static Board board = null;

    private Board() {
        squares = new int[5][5];
        pieces = new boolean[32];
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                squares[i][j] = 32;
            }
        }
        for(int i = 0; i < 32; i++) {
            pieces[i] = true;
        }
    }

    public List<Move> getAllMoves() {
        List<Move> moves = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                if(squares[i][j] == 32) {
                    for(int k = 0; k < 32; k++) {
                        if(pieces[k]) {
                            moves.add(new Move(i, j, k, playerTurn));
                        }
                    }
                }
            }
        }
        return moves;
    }

    public void play(Move move) {
        squares[move.getX()][move.getY()] = piece;
        piece = move.getPiece();
        pieces[piece] = false;
        playerTurn = !playerTurn;
    }

    public void unplay(Move move) {
        piece = squares[move.getX()][move.getY()];
        squares[move.getX()][move.getY()] = 32;
        pieces[move.getPiece()] = true;
        playerTurn = !playerTurn;
    }

    public void initializeBoard(int piece, boolean playerTurn) {
        this.piece = piece;
        this.playerTurn = playerTurn;
    }

    //TODO
    public boolean winner() {
        return false;
    }

    public int getScore() {
        return 0;
    }

    public static Board getBoard() {
        if(board == null) {
            board = new Board();
        }
        return board;
    }
}
