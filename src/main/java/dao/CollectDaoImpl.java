package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CollectDaoImpl implements CollectDao {
    @Override
    public boolean isCollected(String qNumber, String studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM 收藏 WHERE Q编号 = ? AND 学生学号 = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, qNumber);
            ps.setString(2, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public boolean addCollect(String qNumber, String studentId) throws SQLException {
        String sql = "INSERT INTO 收藏 (Q编号, 学生学号) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, qNumber);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean removeCollect(String qNumber, String studentId) throws SQLException {
        String sql = "DELETE FROM 收藏 WHERE Q编号 = ? AND 学生学号 = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, qNumber);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }
}