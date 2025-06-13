package dao;

import db.DatabaseConnection;
import entity.Reply;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ReplyDaoImpl implements ReplyDao {
    @Override
    public List<Reply> getRepliesByQNumber(String qNumber) {
        List<Reply> list = new ArrayList<>();
        // 移除 SELECT 语句中的 responderName 列
        String sql = "SELECT R编号, Q编号, 回复内容, 回复时间 FROM 回复 WHERE Q编号 = ? ORDER BY 回复时间 ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, qNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reply r = new Reply();
                r.setRNumber(rs.getString("R编号"));
                r.setQNumber(rs.getString("Q编号"));
                r.setContent(rs.getString("回复内容"));
                r.setTime(rs.getTimestamp("回复时间").toLocalDateTime());
                // 移除设置 responderName 的代码行，因为数据库中没有此列
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean addReply(Reply reply) {
        // 移除 INSERT 语句中的 responderName 列
        String sql = "INSERT INTO 回复 (R编号, Q编号, 回复内容, 回复时间) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reply.getRNumber());
            ps.setString(2, reply.getQNumber());
            ps.setString(3, reply.getContent());
            ps.setTimestamp(4, Timestamp.valueOf(reply.getTime()));
            // 移除设置 responderName 参数的代码行
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}