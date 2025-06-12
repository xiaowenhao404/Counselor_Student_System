package dao;

import db.DatabaseConnection;
import entity.Reply;
import java.sql.*;
import java.util.*;

public class ReplyDaoImpl implements ReplyDao {
    @Override
    public List<Reply> getRepliesByQNumber(String qNumber) {
        List<Reply> list = new ArrayList<>();
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
                // responderName可根据业务补全
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}