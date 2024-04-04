import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        // Create an instance of DbTTT to establish the database connection
        DbTTT db = new DbTTT();
        // Create and show the GUI
        SwingUtilities.invokeLater(() -> {
            // Prompt for player names
            String player1 = JOptionPane.showInputDialog(null, "Enter name for Player 1:");
            String player2 = JOptionPane.showInputDialog(null, "Enter name for Player 2:");

            JFrame frame = new JFrame("Tic Tac Toe");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new TicTacToePanel(player1, player2));
            frame.pack();
            frame.setVisible(true);
        });
    }
}

class TicTacToePanel extends JPanel {
    private TicTacToe ticTacToe;
    private JButton[][] buttons;
    private JLabel currentPlayerLabel;
    private String player1Name;
    private String player2Name;
    private DbTTT db;

    public TicTacToePanel(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.db = db;
        ticTacToe = new TicTacToe(player1Name, player2Name);
        buttons = new JButton[3][3];

        setLayout(new BorderLayout());

        // Set initial current player
        currentPlayerLabel = new JLabel("Current Player: " + player1Name);
        currentPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(currentPlayerLabel, BorderLayout.NORTH);

        JPanel gamePanel = new JPanel(new GridLayout(3, 3));

        // Create and add buttons to the panel
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton button = new JButton(" ");
                button.setFont(new Font("Arial", Font.PLAIN, 40));
                button.addActionListener(new ButtonClickListener(i, j));
                buttons[i][j] = button;
                button.setPreferredSize(new Dimension(100, 100)); // Set preferred size
                gamePanel.add(button);
            }
        }

        add(gamePanel, BorderLayout.CENTER);
    }

    private class ButtonClickListener implements ActionListener {
        private int row;
        private int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (ticTacToe.makeMove(row, col)) {
                buttons[row][col].setText(Character.toString(ticTacToe.getCurrentPlayer()));
                if (ticTacToe.checkWin() != '\0') {
                    String winnerName = ticTacToe.getWinnerName();
                    int option = JOptionPane.showOptionDialog(TicTacToePanel.this,
                            "Player " + winnerName + " wins!\nPlay again?",
                            "Game Over",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            new String[]{"Play Again", "Quit"},
                            "Play Again");

                    if (option == JOptionPane.YES_OPTION) {
                        // Update wins in the database
                        db.updateWins(winnerName);
                        resetGame(); // Play again
                    } else {
                        // Update wins in the database
                        db.updateWins(winnerName);
                        System.exit(0); // Quit the game
                    }
                } else if (ticTacToe.checkDraw()) {
                    JOptionPane.showMessageDialog(TicTacToePanel.this, "It's a draw!");
                    resetGame();
                } else {
                    updateCurrentPlayerLabel();
                }
            } else {
                JOptionPane.showMessageDialog(TicTacToePanel.this, "Invalid move! Try again.");
            }
        }

        private void resetGame() {
            ticTacToe = new TicTacToe(player1Name, player2Name); // Reset the game
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setText("-");
                }
            }
            updateCurrentPlayerLabel();
        }
    }

    private void updateCurrentPlayerLabel() {
        currentPlayerLabel.setText("Current Player: " + ticTacToe.getCurrentPlayerName());
    }
}

class TicTacToe {
    private char[][] board;
    private char currentPlayer;
    private String player1Name;
    private String player2Name;
    private int moveCount;

    public TicTacToe(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        board = new char[3][3];
        currentPlayer = 'X'; // Player one always starts with X
        initializeBoard();
    }

    private void initializeBoard() {
        // Initialize board with empty cells
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }

    public boolean makeMove(int row, int col) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3 || board[row][col] != '-') {
            return false; // Invalid move
        }
        board[row][col] = currentPlayer;

        // Increment move count
        moveCount++;
        // Update currentPlayer based on move count
        if (moveCount % 2 == 0) {
            currentPlayer = 'O'; // Even move count, current player is 'O'
        } else {
            currentPlayer = 'X'; // Odd move count, current player is 'X'
        }

        return true;
    }

    public char checkWin() {
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            // Check rows
            if (board[i][0] != '-' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0]; // Return the symbol of the winner
            }
            // Check columns
            if (board[0][i] != '-' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return board[0][i]; // Return the symbol of the winner
            }
        }

        // Check diagonals
        if (board[0][0] != '-' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0]; // Return the symbol of the winner
        }
        if (board[0][2] != '-' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2]; // Return the symbol of the winner
        }

        return '\0'; // No win
    }

    public String getWinnerName() {
        char winnerSymbol = checkWin();
        if (winnerSymbol == 'X') {
            return player1Name;
        } else if (winnerSymbol == 'O') {
            return player2Name;
        } else {
            return null; // No winner
        }
    }

    public boolean checkDraw() {
        // Check if any cell is empty
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false; // Game is not draw
                }
            }
        }
        // If no empty cell is found and there is no winner, it's a draw
        return checkWin() == '\0';
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public String getCurrentPlayerName() {
        return (currentPlayer == 'X') ? player1Name : player2Name;
    }
}