import java.util.ArrayList;
import java.util.Collections;
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
        if(winner()) {
            return Collections.EMPTY_LIST;
        }
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

    public void initializeBoard(int piece, Move move) {
        this.piece = piece;
        pieces[piece] = false;
    }

    public boolean winner() {
        int sameDiaDown = 31;
        int sameDiaDownInv = 31;
        int sameDiaUp = 31;
        int sameDiaUpInv = 31;
        for(int i = 0; i < 5; i++) {
            int sameRow = 31;
            int sameRowInv = 31;
            int sameCol = 31;
            int sameColInv = 31;
            if(squares[i][i] != 32) {
                sameDiaDown = sameDiaDown & squares[i][i];
                sameDiaDownInv = sameDiaDownInv & ~squares[i][i];
            } else {
                sameDiaDown = 0;
                sameDiaDownInv = 0;
            }
            if(squares[i][4-i] != 32) {
                sameDiaUp = sameDiaUp & squares[i][4 - i];
                sameDiaUpInv = sameDiaUpInv & ~squares[i][4 - i];
            } else {
                sameDiaUp = 0;
                sameDiaUpInv = 0;
            }
            for(int j = 0; j < 5; j++) {
                if(squares[i][j] != 32) {
                    sameRow = sameRow & squares[i][j];
                    sameRowInv = sameRowInv & ~squares[i][j];
                } else {
                    sameRow = 0;
                    sameRowInv = 0;
                }
                if(squares[j][i] != 32) {
                    sameCol = sameCol & squares[j][i];
                    sameColInv = sameColInv & ~squares[j][i];
                } else {
                    sameCol = 0;
                    sameColInv = 0;
                }
            }
            if(sameRow > 0 || sameRowInv > 0 || sameCol > 0 || sameColInv > 0) {
                return true;
            }
        }
        return (sameDiaDown > 0 || sameDiaDownInv > 0 || sameDiaUp > 0 || sameDiaUpInv > 0);
    }

    public int getScore() {
        return 0;
    }

    public void printBoard() {
        System.out.println("+-----+-----+-----+-----+-----+");
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                System.out.print("|");
                if(squares[j][i] == 32) {
                    System.out.print("     ");
                } else {
                    System.out.print(Integer.toBinaryString(squares[j][i] | 32).substring(1));
                }
            }
            System.out.println("|");
            System.out.println("+-----+-----+-----+-----+-----+");
        }
    }

    public static Board getBoard() {
        if(board == null) {
            board = new Board();
        }
        return board;
    }
}
