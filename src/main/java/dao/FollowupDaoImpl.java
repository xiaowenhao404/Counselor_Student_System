package dao;

import db.DatabaseConnection;
import entity.Followup;
import java.sql.*;
import java.util.*;

public class FollowupDaoImpl implements FollowupDao {
    @Override
    public List<Followup> getFollowupsByQNumber(String qNumber) {
        List<Followup> list = new ArrayList<>();
        String sql = "SELECT F编号, Q编号, 追问内容, 追问时间 FROM 追问 WHERE Q编号 = ? ORDER BY 追问时间 ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, qNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Followup f = new Followup();
                f.setFNumber(rs.getString("F编号"));
                f.setQNumber(rs.getString("Q编号"));
                f.setContent(rs.getString("追问内容"));
                f.setTime(rs.getTimestamp("追问时间").toLocalDateTime());
                list.add(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String generateNextFNumber() {
        String sql = "SELECT MAX(F编号) FROM 追问";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxF = rs.getString(1);
                if (maxF != null && maxF.matches("F\\d+")) {
                    int num = Integer.parseInt(maxF.substring(1));
                    return String.format("F%04d", num + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "F0001";
    }

    @Override
    public boolean addFollowup(String qNumber, String content, java.time.LocalDateTime time) {
        String fNumber = generateNextFNumber();
        String sql = "INSERT INTO 追问 (F编号, Q编号, 追问内容, 追问时间) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fNumber);
            ps.setString(2, qNumber);
            ps.setString(3, content);
            ps.setTimestamp(4, Timestamp.valueOf(time));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}