import java.io.IOException;

public class QuartoPlayerAgent {
    private final GameClient gameClient;
    private int playerNumber;

    private static final String ACKNOWLEDGMENT_MOVE_HEADER = "ACK_MOVE:";
    private static final String ACKNOWLEDGMENT_PIECE_HEADER = "ACK_PIECE:";
    private static final String ERROR_MOVE_HEADER = "ERR_MOVE:";
    private static final String ERROR_PIECE_HEADER = "ERR_PIECE:";
    private static final String GAME_OVER_HEADER = "GAME_OVER:";
    private static final String INFORM_PLAYER_NUMBER_HEADER = "PLAYER:";
    private static final String MOVE_MESSAGE_HEADER = "MOVE:";
    private static final String SELECT_MOVE_HEADER = "Q2:";
    private static final String SELECT_PIECE_HEADER = "Q1:";
    private static final String TURN_TIME_LIMIT_HEADER = "TURN_TIME_LIMIT:";

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("No IP Specified");
            System.exit(0);
        }
        String ip = args[0];
        GameClient gameClient = new GameClient();

        gameClient.connectToServer(ip, 4321);

        QuartoPlayerAgent quartoPlayerAgent = new QuartoPlayerAgent(gameClient);
        if(args.length == 2) {
            quartoPlayerAgent.initializeBoard(args[1]);
        }
        quartoPlayerAgent.play();
    }

    private QuartoPlayerAgent(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    private void initializeBoard(String filePath) {
        try {
            Board.getBoard().fillFromFile(filePath);
            Board.getBoard().printBoard();
        } catch (IOException e) {
            System.out.print("Cannot read file");
            System.exit(0);
        }
    }

    private void play() {
        GameManager gameManager;
        Move move;
        Move opponentMove;
        int firstPiece;
        setPlayerNumber();
        setTurnTimeLimit();
        boolean playerTurn = playerNumber == 2;
        if(playerTurn) {
            firstPiece = Board.getBoard().getUnwinnablePiece();
            sendPiece(Integer.toBinaryString(firstPiece | 32).substring(1));
            opponentMove = getMove();
            gameManager = new GameManager(firstPiece, opponentMove);
        } else {
            firstPiece = getNextPiece();
            move = new Move(0, 0, 1, true);
            gameManager = new GameManager(firstPiece, move);
        }
        playerTurn = true;
        while(true) {
            if(playerTurn) {
                move = gameManager.getNextMove();
                gameManager.playMove(move);
                sendMove(move);
            } else {
                opponentMove = getMove();
                gameManager.playMove(opponentMove);
            }
            Board.getBoard().printBoard();
            if(Board.getBoard().winner()) {
                gameClient.closeConnection();
                return;
            }
            playerTurn = !playerTurn;
        }
    }

    private Move getMove() {
        String messageFromServer = gameClient.readFromServer(1000000);
        String[] splittedMoveResponse = messageFromServer.split("\\s+");
        isExpectedMessage(splittedMoveResponse, MOVE_MESSAGE_HEADER);
        String[] moveString = splittedMoveResponse[1].split(",");
        return new Move(Integer.parseInt(moveString[0]),
                Integer.parseInt(moveString[1]),
                getNextPiece(),
                GameNode.OPPONENT_TURN);
    }

    private int getNextPiece() {
        String messageFromServer;
        messageFromServer = gameClient.readFromServer(1000000);
        String[] splittedMessage = messageFromServer.split("\\s+");
        isExpectedMessage(splittedMessage, SELECT_MOVE_HEADER);
        return Integer.parseInt(splittedMessage[1], 2);
    }

    private void sendMove(Move move) {
        sendSquare(move.squareToString());
        sendPiece(move.pieceToString());
    }

    private void sendPiece(String piece) {
        String messageFromServer;
        messageFromServer = gameClient.readFromServer(1000000);
        String[] splittedMessage = messageFromServer.split("\\s+");
        isExpectedMessage(splittedMessage, SELECT_PIECE_HEADER);
        gameClient.writeToServer(piece);
        messageFromServer = gameClient.readFromServer(1000000);
        String[] splittedResponse = messageFromServer.split("\\s+");
        if (!isExpectedMessage(splittedResponse, ACKNOWLEDGMENT_PIECE_HEADER) && !isExpectedMessage(splittedResponse, ERROR_PIECE_HEADER)) {
            turnError(messageFromServer);
        }
    }

    private void sendSquare(String move) {
        String messageFromServer;
        gameClient.writeToServer(move);
        messageFromServer = gameClient.readFromServer(1000000);
        String[] splittedMoveResponse = messageFromServer.split("\\s+");
        if (!isExpectedMessage(splittedMoveResponse, ACKNOWLEDGMENT_MOVE_HEADER) && !isExpectedMessage(splittedMoveResponse, ERROR_MOVE_HEADER)) {
            turnError(messageFromServer);
        }
    }

    private void setPlayerNumber() {
        String message = gameClient.readFromServer(1000000);
        String[] splittedMessage = message.split("\\s+");
        System.out.println(splittedMessage[0] + " " + splittedMessage[1]);
        if (isExpectedMessage(splittedMessage, INFORM_PLAYER_NUMBER_HEADER)) {
            playerNumber = Integer.parseInt(splittedMessage[1], 10);
        } else {
            System.out.println("Did not Receive Player Number");
            System.exit(0);
        }
    }

    private void setTurnTimeLimit() {
        String message = this.gameClient.readFromServer(1000000);
        String[] splittedMessage = message.split("\\s+");
        System.out.println(splittedMessage[0] + " " + splittedMessage[1]);
        if (!isExpectedMessage(splittedMessage, TURN_TIME_LIMIT_HEADER)) {
            System.out.println("Did not Receive TURN_TIME_LIMIT_HEADER");
            System.exit(0);
        }
    }

    private static boolean isExpectedMessage(String[] splittedMessage, String header) {
        if (splittedMessage.length > 0 && splittedMessage[0].equals(header)) {
            return true;
        } else if (splittedMessage.length > 0 && splittedMessage[0].equals(GAME_OVER_HEADER)) {
            gameOver(splittedMessage);
        }
        return false;
    }

    private static void turnError(String message) {
        System.out.println("Turn order out of sync, last message received was: " + message);
        System.exit(0);
    }

    private static void gameOver(String[] splittedMessage) {
        StringBuilder builder = new StringBuilder();
        for (String s : splittedMessage) {
            builder.append(s).append(" ");
        }
        System.out.println(builder.toString());
        System.exit(0);
    }
}
