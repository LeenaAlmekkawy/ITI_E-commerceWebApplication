package jets.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import jets.projects.beans.AdminBean;
import jets.projects.beans.NormalUserBean;

public class UsersDAO {
    public NormalUserBean userLogin(String email, String password) {
//        String query = """
//        SELECT * FROM NormalUser
//        WHERE email = ? and password = ?;""";
//
//        try (Connection connection = ConnectionManager.getConnection();
//            PreparedStatement stmt = connection.prepareStatement(query);) {
//            stmt.setString(1, email);
//            stmt.setString(2, password);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (!rs.next()) {
//                    return null;
//                }
//                int userID = rs.getInt("user_ID");
//                String firstName = rs.getString("first_name");
//                String lastName = rs.getString("last_name");
//                return new NormalUserBean(userID, email,
//                        password, firstName, lastName);
//            }
//        } catch (SQLException ex) {
//            System.err.println("DB: " + ex.getMessage());
            return null;
//        }
    }

    public Integer register(NormalUserBean user) {
//        String query = """
//        INSERT INTO NormalUser
//        (email, password, first_name, last_name)
//        VALUES (?, ?, ?, ?);""";
//
//        try (Connection connection = ConnectionManager.getConnection();
//            PreparedStatement stmt = connection.prepareStatement(
//                    query, Statement.RETURN_GENERATED_KEYS);) {
//
//            connection.setAutoCommit(false);
//
//            stmt.setString(1, user.getEmail());
//            stmt.setString(2, user.getPassword());
//            stmt.setString(3, user.getFirstName());
//            stmt.setString(4, user.getLastName());
//
//            int rowsAffected = stmt.executeUpdate();
//            if (rowsAffected != 1) {
//                return -1;
//            }
//
//            try (ResultSet keySet = stmt.getGeneratedKeys()) {
//                if (!keySet.next()) {
//                    connection.rollback();
//                    return null;
//                }
//                int userID = keySet.getInt(1);
//                user.setID(userID);
//                connection.commit();
//                return userID;
//            }
//        } catch (SQLException ex) {
//            System.err.println("DB Error: " + ex.getMessage());
            return null;
//        }
    }

    public AdminBean adminLogin(int userID, String password) {
//        String query = """
//        SELECT * FROM AdminUser
//        WHERE user_ID = ? and password = ?;""";
//
//        try (Connection connection = ConnectionManager.getConnection();
//            PreparedStatement stmt = connection.prepareStatement(query);) {
//            stmt.setInt(1, userID);
//            stmt.setString(2, password);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (!rs.next()) {
//                    return null;
//                }
//
//                String displayName = rs.getString("display_name");
//                return new AdminBean(userID,
//                        password, displayName);
//            }
//        } catch (SQLException ex) {
//            System.err.println("DB: " + ex.getMessage());
            return null;
//        }
    }
}
