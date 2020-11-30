import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    private final int[][] squares;
    private final boolean[] pieces;
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
        playerTurn = !move.playerTurn;
    }

    public void unplay(Move move) {
        piece = squares[move.getX()][move.getY()];
        squares[move.getX()][move.getY()] = 32;
        pieces[move.getPiece()] = true;
        playerTurn = move.playerTurn;
    }

    public void initializeBoard(int piece) {
        this.piece = piece;
        pieces[piece] = false;
    }

    public void fillFromFile(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        int x = 0;
        while((line = br.readLine()) != null) {
            String[] splitted = line.split("\\s+");
            for(int y = 0; y < splitted.length; y++) {
                if(!splitted[y].equals("null")) {
                    int piece = Integer.parseInt(splitted[y], 2);
                    squares[x][y] = piece;
                    pieces[piece] = false;
                }
            }
            x++;
        }
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

    public double getScore() {
        if(winner()) {
            if(playerTurn == GameNode.PLAYER_TURN) {
                return 1.0d;
            } else {
                return -1.0d;
            }
        } else {
            return 0.0d;
        }
    }

    public void printBoard() {
        System.out.println((playerTurn == GameNode.PLAYER_TURN) ? "Player's turn" : "Opponent's turn");
        System.out.println(String.format("Next piece: %d", piece));
        System.out.println("+-----+-----+-----+-----+-----+");
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                System.out.print("|");
                if(squares[i][j] == 32) {
                    System.out.print("     ");
                } else {
                    System.out.print(Integer.toBinaryString(squares[i][j] | 32).substring(1));
                }
            }
            System.out.println("|");
            System.out.println("+-----+-----+-----+-----+-----+");
        }
    }

    public int getUnwinnablePiece() {
        for(int i = 0; i < 32; i++) {
            if(pieces[i]) {
                pieces[i] = false;
                boolean winnable = false;
                for(int x = 0; x < 5; x++) {
                    for(int y = 0; y < 5; y++) {
                        if(squares[x][y] == 32) {
                            squares[x][y] = i;
                            if(winner()) {
                                winnable = true;
                            }
                            squares[x][y] = 32;
                        }
                    }
                }
                pieces[i] = true;
                if(!winnable) {
                    return i;
                }
            }
        }
        for(int i = 0; i < 32; i++) {
            if(pieces[i]) {
                return i;
            }
        }
        return 0;
    }

    public static Board getBoard() {
        if(board == null) {
            board = new Board();
        }
        return board;
    }
}
