import java.sql.*;

public class DbTTT {
    private Connection connection;

    public DbTTT() {
        // Initialize database connection
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:tictactoe.db");
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS Players (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL UNIQUE," +
                "wins INTEGER DEFAULT 0)";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateWins(String playerName) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE Players SET wins = wins + 1 WHERE name = ?")) {
            pstmt.setString(1, playerName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerWins(String playerName) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT wins FROM Players WHERE name = ?")) {
            pstmt.setString(1, playerName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("wins");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Default to 0 wins if player not found
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkDatabase() {
    }
}