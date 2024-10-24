package tictactoe;

import java.util.Random;
import java.util.Scanner;

class TicTacToeGame {
    private static final int ROWS = 3;
    private static final int COLUMNS = 3;
    static Scanner sc = new Scanner(System.in);
    Random randomInt = new Random();

    private final String[][] gameBoard;
    private static String xPlayer = "";
    private static String oPlayer = "";
    private static boolean isXTurn = true;  // Keep track of whose turn it is
    static boolean gameOver = false;
    static boolean playGame = false;
    private String gameState = "";

    private int countX = 0;
    private int countO = 0;

    String gameLevel = "";

    enum Level {
        EASY,
        MEDIUM,
        HARD

    }

    private TicTacToeGame() {
        this.gameBoard = new String[][]{
                {"_", "_", "_"},
                {"_", "_", "_"},
                {"_", "_", "_"},
        };
    }


    private void printBoard() {
        String border = "---------";
        System.out.println(border);

        countX = 0; // Reset counts before printing
        countO = 0; // Reset counts before printing

        for (int i = 0; i < ROWS; i++) {
            System.out.print("| ");
            for (int j = 0; j < COLUMNS; j++) {
                System.out.print(gameBoard[i][j] + " ");
                if (gameBoard[i][j].equals("X")) countX++;
                if (gameBoard[i][j].equals("O")) countO++;
            }
            System.out.println("|");
        }
        System.out.println(border);
    }

    private void getPlayerCoordinates() {
        boolean valid = false;
        while (!valid) {
            System.out.println("Enter the coordinates: ");
            String userInput = sc.nextLine();
            String[] splitString = userInput.split(" ");

            // Check if exactly two inputs are given
            if (splitString.length == 2) {
                try {
                    // Check if both inputs are valid integers
                    int userX = Integer.parseInt(splitString[0]);
                    int userY = Integer.parseInt(splitString[1]);

                    // Adjust range check to be from 1 to 3 (not 0 to 2)
                    if (userX < 1 || userX > COLUMNS || userY < 1 || userY > ROWS) {
                        System.out.println("Coordinates should be from 1 to 3!");
                    } else if (gameBoard[userX - 1][userY - 1].equals("O") || gameBoard[userX - 1][userY - 1].equals("X")) {
                        // Adjust for 0-based index in the array
                        System.out.println("This cell is occupied! Choose another one!");
                    } else {
                        // Place either "X" or "O" based on the turn
                        String currentPlayerSymbol = isXTurn ? "X" : "O";
                        gameBoard[userX - 1][userY - 1] = currentPlayerSymbol;
                        valid = true;
                        printBoard();
                        checkGameBoard();
                    }
                } catch (NumberFormatException e) {
                    // Catch any invalid number format (non-integer input)
                    System.out.println("You should enter numbers!");
                }
            }
        }
    }

    private void makeComputerCoordinates(Level difficultySetting) {
        String currentPlayerSymbol = isXTurn ? "X" : "O"; // AI's symbol
        String opponentSymbol = isXTurn ? "O" : "X";      // Opponent's symbol
        int[] move = null;

        System.out.println("Making move for " + currentPlayerSymbol);

        switch (difficultySetting) {
            case EASY:
                gameLevel = Level.EASY.name().toLowerCase();
                makeRandomMove(currentPlayerSymbol); // Random move for Easy
                break;

            case MEDIUM:
                gameLevel = Level.MEDIUM.name().toLowerCase();

                // 1. Check if the AI can win
                move = findWinningOrBlockingMove(currentPlayerSymbol, opponentSymbol);
                if (move != null) {
                    gameBoard[move[0]][move[1]] = currentPlayerSymbol; // Make the winning move
                    System.out.println("AI playing to win!");
                    break;
                }

                // 2. Check if the opponent can win and block them
                move = findWinningOrBlockingMove(opponentSymbol, currentPlayerSymbol); // Pass both symbols
                if (move != null) {
                    gameBoard[move[0]][move[1]] = currentPlayerSymbol; // Block the opponent's winning move
                    System.out.println("AI blocking opponent!");
                    break;
                }

                // 3. Make a random move if no winning/blocking move is found
                System.out.println("AI making a random move.");
                makeRandomMove(currentPlayerSymbol);
                break;

            case HARD:
                gameLevel = Level.HARD.name().toLowerCase();
                makeBestMove();
                break;
        }

        System.out.println("Making move level \"" + gameLevel + "\"");
        printBoard();
        checkGameBoard();
    }

    private void checkGameBoard() {
        // Check rows, columns, and diagonals
        if (checkWinCondition(gameBoard)) {
            gameOver = true;
            return; // gameState has been updated in checkWinCondition
        }

        // If the game board is full and no winner, declare draw
        if (countX + countO == 9) {
            gameState = "Draw";
            gameOver = true;
        } else {
            gameState = "Game not finished";
        }
    }

    private boolean checkWinCondition(String[][] board) {
        // Check rows and columns in a single loop
        for (int i = 0; i < 3; i++) {
            if (checkLine(board[i][0], board[i][1], board[i][2])) {
                gameState = board[i][0] + " wins"; // row check
                return true;
            }
            if (checkLine(board[0][i], board[1][i], board[2][i])) {
                gameState = board[0][i] + " wins"; // column check
                return true;
            }
        }
        // Check diagonals
        if (checkLine(board[0][0], board[1][1], board[2][2])) {
            gameState = board[0][0] + " wins";
            return true;
        }
        if (checkLine(board[0][2], board[1][1], board[2][0])) {
            gameState = board[0][2] + " wins";
            return true;
        }
        return false; // No winner yet
    }

    private boolean checkLine(String a, String b, String c) {
        return !a.equals("_") && a.equals(b) && b.equals(c);
    }

    private void makeRandomMove(String currentPlayerSymbol) {
        boolean validMove = false;

        while (!validMove) {
            int randomX = randomInt.nextInt(ROWS);
            int randomY = randomInt.nextInt(COLUMNS);
            if (gameBoard[randomX][randomY].equals("_")) {
                gameBoard[randomX][randomY] = currentPlayerSymbol;
                validMove = true;
            }
        }
    }

    // Function to check if there are two identical symbols and one empty spot
    private int[] checkForTwoInLine(String a, String b, String c, int rowA, int colA, int rowB, int colB, int rowC, int colC, String playerSymbol, String opponentSymbol) {
        // Check if two of the same symbols are in a row with one empty spot
        if (a.equals(playerSymbol) && b.equals(playerSymbol) && c.equals("_")) {
            return new int[]{rowC, colC}; // Place at third spot to win
        }
        if (a.equals(playerSymbol) && c.equals(playerSymbol) && b.equals("_")) {
            return new int[]{rowB, colB}; // Place at second spot to win
        }
        if (b.equals(playerSymbol) && c.equals(playerSymbol) && a.equals("_")) {
            return new int[]{rowA, colA}; // Place at first spot to win
        }
        return null; // No winning move found
    }

    private int[] findWinningOrBlockingMove(String playerSymbol, String opponentSymbol) {
        int[] move;

        // Check rows for winning/blocking move
        for (int i = 0; i < 3; i++) {
            move = checkForTwoInLine(gameBoard[i][0], gameBoard[i][1], gameBoard[i][2], i, 0, i, 1, i, 2, playerSymbol, opponentSymbol);
            if (move != null) return move;
        }

        // Check columns for winning/blocking move
        for (int i = 0; i < 3; i++) {
            move = checkForTwoInLine(gameBoard[0][i], gameBoard[1][i], gameBoard[2][i], 0, i, 1, i, 2, i, playerSymbol, opponentSymbol);
            if (move != null) return move;
        }

        // Check diagonals for winning/blocking move
        move = checkForTwoInLine(gameBoard[0][0], gameBoard[1][1], gameBoard[2][2], 0, 0, 1, 1, 2, 2, playerSymbol, opponentSymbol);
        if (move != null) return move;

        move = checkForTwoInLine(gameBoard[0][2], gameBoard[1][1], gameBoard[2][0], 0, 2, 1, 1, 2, 0, playerSymbol, opponentSymbol);
        if (move != null) return move;

        return null; // No winning or blocking move found
    }

    private void makeBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (gameBoard[i][j].equals("_")) {  // If the spot is empty
                    gameBoard[i][j] = isXTurn ? "X" : "O";  // AI makes a move
                    int score = minimax(gameBoard, false);  // Evaluate move
                    gameBoard[i][j] = "_";  // Undo move

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }

        // Make the best move found by minimax
        if (bestMove != null) {
            gameBoard[bestMove[0]][bestMove[1]] = isXTurn ? "X" : "O";
            printBoard();
            checkGameBoard();
        }
    }

    private int minimax(String[][] board, boolean isMaximizingPlayer) {
        // Check if there's a winner
        if (checkWinCondition(board)) {
            if (gameState.equals("X wins")) return isXTurn ? 1 : -1;
            if (gameState.equals("O wins")) return isXTurn ? -1 : 1;
        }

        // Check for a draw
        if (isBoardFull()) return 0;

        // Maximizing player (AI)
        if (isMaximizingPlayer) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    if (board[i][j].equals("_")) {  // If the spot is empty
                        board[i][j] = isXTurn ? "X" : "O"; // AI's move
                        int score = minimax(board, false);  // Call minimax for the opponent
                        board[i][j] = "_";  // Undo move
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
        // Minimizing player (Opponent)
        else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    if (board[i][j].equals("_")) {  // If the spot is empty
                        board[i][j] = isXTurn ? "O" : "X"; // Opponent's move
                        int score = minimax(board, true);  // Call minimax for the AI
                        board[i][j] = "_";  // Undo move
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private boolean isBoardFull() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (gameBoard[i][j].equals("_")) {
                    return false;
                }
            }
        }
        return true;
    }


    private void menu() {
        boolean valid = false;
        while (!valid) {
            System.out.println("Input command: ");
            String userInput = sc.nextLine();
            if (userInput.contains("exit")) {
                valid = true;
            } else {
                String[] splitString = userInput.split(" ");
                if (splitString.length == 3) {
                    String firstString = splitString[0];
                    String secondString = splitString[1];
                    String thirdString = splitString[2];

                    boolean validFirstPlayer = secondString.equalsIgnoreCase(Level.EASY.name()) ||
                            secondString.equalsIgnoreCase(Level.MEDIUM.name()) ||
                            secondString.equalsIgnoreCase(Level.HARD.name()) ||
                            secondString.equalsIgnoreCase("user");

                    boolean validSecondPlayer = thirdString.equalsIgnoreCase(Level.EASY.name()) ||
                            thirdString.equalsIgnoreCase(Level.MEDIUM.name()) ||
                            thirdString.equalsIgnoreCase(Level.HARD.name()) ||
                            thirdString.equalsIgnoreCase("user");

                    if (firstString.equals("start") && validFirstPlayer && validSecondPlayer) {
                        xPlayer = secondString.toUpperCase(); // Convert to uppercase for consistency
                        oPlayer = thirdString.toUpperCase();  // Convert to uppercase for consistency
                        playGame = true;
                        valid = true;
                    }
                } else {
                    System.out.println("Bad Parameters!");
                }
            }
        }
    }


    public static void main(String[] args) {
        // Initialize game
        TicTacToeGame game = new TicTacToeGame();
        game.menu();

        if (playGame) {
            game.printBoard();

            while (!gameOver) {
                if (isXTurn) {
                    // X player's turn
                    if (xPlayer.equalsIgnoreCase("user")) {
                        game.getPlayerCoordinates();
                    } else {
                        game.makeComputerCoordinates(Level.valueOf(xPlayer));
                    }
                } else {
                    // O player's turn
                    if (oPlayer.equalsIgnoreCase("user")) {
                        game.getPlayerCoordinates();
                    } else {
                        game.makeComputerCoordinates(Level.valueOf(oPlayer));
                    }
                }
                // Check if the game is over after a move
                if (gameOver) {
                    break;  // Exit the loop if the game is over
                }
                // Switch turns only after checking if the game is over
                isXTurn = !isXTurn;
            }
            // Output the final game state
            System.out.println(game.gameState);
        } else {
            System.out.println("Goodbye!");
        }
        sc.close();
    }
}
