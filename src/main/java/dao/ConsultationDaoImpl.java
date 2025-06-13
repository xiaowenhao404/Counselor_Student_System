package dao;

import db.DatabaseConnection;
import entity.Consultation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDaoImpl implements ConsultationDao {

    @Override
    public List<Consultation> getAllConsultations() throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT Q编号, 学生学号, 学生姓名, 类别, 状态, 提问标题, 提问内容, 提问时间, 回复次数, 追问次数, 是否加精 FROM 咨询汇总视图";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                consultations.add(mapRowToConsultation(rs));
            }
        } finally {
            DatabaseConnection.closeConnection(connection);
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return consultations;
    }

    @Override
    public Consultation getConsultationByQNumber(String qNumber) throws SQLException {
        String sql = "SELECT Q编号, 学生学号, 学生姓名, 类别, 状态, 提问标题, 提问内容, 提问时间, 回复次数, 追问次数, 是否加精 FROM 咨询汇总视图 WHERE Q编号 = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, qNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToConsultation(rs);
            }
        }
        return null;
    }

    @Override
    public boolean addConsultation(Consultation consultation) throws SQLException {
        String nextQNumber = generateNextQNumber();
        consultation.setQNumber(nextQNumber);
        String sql = "INSERT INTO 咨询 (Q编号, 学生学号, 类别, 状态, 提问标题, 提问内容, 提问时间, 是否加精) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, consultation.getQNumber());
            ps.setString(2, consultation.getStudentId());
            ps.setString(3, consultation.getCategory());
            ps.setString(4, consultation.getStatus());
            ps.setString(5, consultation.getQuestionTitle());
            ps.setString(6, consultation.getQuestionContent());
            ps.setTimestamp(7, java.sql.Timestamp.valueOf(consultation.getQuestionTime()));
            ps.setBoolean(8, consultation.isHighlighted());
            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    @Override
    public void updateConsultation(Consultation consultation) throws SQLException {
        String sql = "UPDATE 咨询 SET 类别 = ?, 状态 = ?, 提问标题 = ?, 提问内容 = ?, 是否加精 = ? WHERE Q编号 = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, consultation.getCategory());
            ps.setString(2, consultation.getStatus());
            ps.setString(3, consultation.getQuestionTitle());
            ps.setString(4, consultation.getQuestionContent());
            ps.setBoolean(5, consultation.isHighlighted());
            ps.setString(6, consultation.getQNumber());

            ps.executeUpdate();
        }
    }

    @Override
    public boolean deleteConsultation(String qNumber) throws SQLException {
        String sql = "DELETE FROM 咨询 WHERE Q编号 = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, qNumber);

            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    @Override
    public boolean toggleHighlight(String qNumber) throws SQLException {
        String sql = "UPDATE 咨询 SET 是否加精 = NOT 是否加精 WHERE Q编号 = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, qNumber);

            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    // 新增方法：根据专业ID获取咨询
    @Override
    public List<Consultation> getConsultationsByMajor(String majorId) throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        // 假设 `咨询汇总视图` 包含了学生的专业信息，或者需要JOIN `学生` 表
        // 为了简化，我们假设可以通过学生学号关联到专业
        String sql = "SELECT c.Q编号, c.学生学号, c.学生姓名, c.类别, c.状态, c.提问标题, c.提问内容, c.提问时间, c.回复次数, c.追问次数, c.是否加精 " +
                     "FROM 咨询汇总视图 c JOIN 学生 s ON c.学生学号 = s.学生学号 WHERE s.专业编号 = ?";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, majorId);
            rs = ps.executeQuery();

            while (rs.next()) {
                consultations.add(mapRowToConsultation(rs));
            }
        } finally {
            DatabaseConnection.closeConnection(connection);
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return consultations;
    }

    // 新增方法：获取所有加精的咨询
    @Override
    public List<Consultation> getFeaturedConsultations() throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT Q编号, 学生学号, 学生姓名, 类别, 状态, 提问标题, 提问内容, 提问时间, 回复次数, 追问次数, 是否加精 FROM 咨询汇总视图 WHERE 是否加精 = TRUE";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                consultations.add(mapRowToConsultation(rs));
            }
        } finally {
            DatabaseConnection.closeConnection(connection);
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return consultations;
    }

    // 新增方法：根据学生ID列表获取咨询
    @Override
    public List<Consultation> getConsultationsByStudentIds(List<String> studentIds) throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        if (studentIds == null || studentIds.isEmpty()) {
            return consultations; // 没有学生ID，返回空列表
        }

        // 构建IN子句
        String placeholders = String.join(",", java.util.Collections.nCopies(studentIds.size(), "?"));
        String sql = "SELECT Q编号, 学生学号, 学生姓名, 类别, 状态, 提问标题, 提问内容, 提问时间, 回复次数, 追问次数, 是否加精 FROM 咨询汇总视图 WHERE 学生学号 IN (" + placeholders + ")";

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            for (int i = 0; i < studentIds.size(); i++) {
                ps.setString(i + 1, studentIds.get(i));
            }
            rs = ps.executeQuery();

            while (rs.next()) {
                consultations.add(mapRowToConsultation(rs));
            }
        } finally {
            DatabaseConnection.closeConnection(connection);
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return consultations;
    }

    // 辅助方法：将ResultSet行映射到Consultation对象
    private Consultation mapRowToConsultation(ResultSet rs) throws SQLException {
        Consultation consultation = new Consultation();
        consultation.setQNumber(rs.getString("Q编号"));
        consultation.setStudentId(rs.getString("学生学号"));
        consultation.setStudentName(rs.getString("学生姓名"));
        consultation.setCategory(rs.getString("类别"));
        consultation.setStatus(rs.getString("状态"));
        consultation.setQuestionTitle(rs.getString("提问标题"));
        consultation.setQuestionContent(rs.getString("提问内容"));
        java.sql.Timestamp ts = rs.getTimestamp("提问时间");
        if (ts != null)
            consultation.setQuestionTime(ts.toLocalDateTime());
        consultation.setReplyCount(rs.getInt("回复次数"));
        consultation.setFollowupCount(rs.getInt("追问次数"));
        consultation.setHighlighted(rs.getBoolean("是否加精"));
        return consultation;
    }

    public String generateNextQNumber() throws SQLException {
        String sql = "SELECT MAX(Q编号) FROM 咨询";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxQ = rs.getString(1);
                if (maxQ != null && maxQ.matches("Q\\d+")) {
                    int num = Integer.parseInt(maxQ.substring(1));
                    return String.format("Q%04d", num + 1);
                }
            }
        }
        return "Q0001";
    }
}