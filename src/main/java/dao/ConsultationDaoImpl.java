package dao;

import db.DatabaseConnection;
import entity.Consultation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDaoImpl implements ConsultationDao {

    @Override
    public List<Consultation> getAllConsultations() throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT Q编号, 学生学号, 学生姓名, 类别, 状态, 提问时间, 回复次数, 追问次数, 是否加精 FROM 咨询汇总视图";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Consultation consultation = new Consultation();
                consultation.setQNumber(rs.getString("Q编号"));
                consultation.setStudentId(rs.getString("学生学号"));
                consultation.setStudentName(rs.getString("学生姓名"));
                consultation.setCategory(rs.getString("类别"));
                consultation.setStatus(rs.getString("状态"));
                java.sql.Timestamp ts = rs.getTimestamp("提问时间");
                if (ts != null)
                    consultation.setQuestionTime(ts.toLocalDateTime());
                consultation.setReplyCount(rs.getInt("回复次数"));
                consultation.setFollowupCount(rs.getInt("追问次数"));
                consultation.setHighlighted(rs.getBoolean("是否加精"));
                consultations.add(consultation);
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
}