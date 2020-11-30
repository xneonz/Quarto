import java.security.Provider;

public class CustomAgent {
    private final GameClient gameClient;
    private int playerNumber;
    private int timeLimitForResponse;

    private static final String ACKNOWLEDGMENT_PIECE_HEADER = "ACK_PIECE:";
    private static final String ERROR_PIECE_HEADER = "ERR_PIECE:";
    private static final String GAME_OVER_HEADER = "GAME_OVER:";
    private static final String INFORM_PLAYER_NUMBER_HEADER = "PLAYER:";
    private static final String SELECT_PIECE_HEADER = "Q1:";
    private static final String TURN_TIME_LIMIT_HEADER = "TURN_TIME_LIMIT:";

    public static void main(String[] args) {
        String ip = args[0];
        GameClient gameClient = new GameClient();

        gameClient.connectToServer(ip, 4321);

        CustomAgent customAgent = new CustomAgent(gameClient);
        customAgent.play();
    }

    private CustomAgent(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    private void play() {
        GameManager gameManager;
        Move move;
        Move opponentMove;
        int firstPiece = 0;
        setPlayerNumber();
        setTurnTimeLimit();
        boolean playerTurn = playerNumber == 2;
        if(playerTurn) {
            sendPiece(Integer.toBinaryString(firstPiece | 32).substring(1));
            opponentMove = getMove();
            gameManager = new GameManager(firstPiece, opponentMove, 2);
        } else {
            firstPiece = getNextPiece();
            move = new Move(0, 0, 1, true);
            gameManager = new GameManager(firstPiece, move, 2);
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
            playerTurn = !playerTurn;
        }
    }

    private int getNextPiece() {
        return 0;
    }

    private void sendMove(Move move) {
        sendSquare(move.squareToString());
        sendPiece(move.pieceToString());
    }

    private Move getMove() {
        return null;
    }

    private void sendPiece(String piece) {
        String messageFromServer;
        messageFromServer = gameClient.readFromServer(1000000);
        String[] splittedMessage = messageFromServer.split("\\s+");
        isExpectedMessage(splittedMessage, SELECT_PIECE_HEADER, true);
        gameClient.writeToServer(piece);
        messageFromServer = gameClient.readFromServer(1000000);
        String[] splittedResponse = messageFromServer.split("\\s+");
        if (!isExpectedMessage(splittedResponse, ACKNOWLEDGMENT_PIECE_HEADER) && !isExpectedMessage(splittedResponse, ERROR_PIECE_HEADER)) {
            turnError(messageFromServer);
        }
    }

    private void sendSquare(String move) {}

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
        if (isExpectedMessage(splittedMessage, TURN_TIME_LIMIT_HEADER)) {
            timeLimitForResponse = Integer.parseInt(splittedMessage[1], 10);
        } else {
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

    private static boolean isExpectedMessage(String[] splittedMessage, String header, boolean fatal) {
        if (splittedMessage.length > 0 && splittedMessage[0].equals(header)) {
            return true;
        } else if (splittedMessage.length > 0 && splittedMessage[0].equals(GAME_OVER_HEADER)) {
            gameOver(splittedMessage);
        } else if (fatal) {
            turnError(splittedMessage[0] + " " + splittedMessage[1]);
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
