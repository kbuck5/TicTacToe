import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void testDbTTT() {
        // Create an instance of DbTTT to establish the database connection
        DbTTT db = new DbTTT();

        // Check if the database is created successfully
        db.checkDatabase();

        // Close the database connection
        db.closeConnection();
    }
}
