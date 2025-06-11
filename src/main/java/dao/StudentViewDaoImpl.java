package dao;

import db.DatabaseConnection;
import entity.StudentView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentViewDaoImpl implements StudentViewDao {

    @Override
    public List<StudentView> getAllStudentViews() throws SQLException {
        List<StudentView> studentViews = new ArrayList<>();
        String sql = "SELECT 学生学号, 姓名, 性别, 出生日期, 手机号码, 专业名称, 年级编号, 班级编号, 辅导员姓名 FROM 学生视图";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                StudentView studentView = new StudentView();
                studentView.setStudentId(rs.getString("学生学号"));
                studentView.setStudentName(rs.getString("姓名"));
                studentView.setGender(rs.getString("性别"));
                if (rs.getDate("出生日期") != null) {
                    studentView.setDateOfBirth(rs.getDate("出生日期").toLocalDate());
                }
                studentView.setPhoneNumber(rs.getString("手机号码"));
                // 注意：学生视图中没有专业编号，只有专业名称
                studentView.setMajorName(rs.getString("专业名称"));
                studentView.setGradeNumber(rs.getString("年级编号"));
                studentView.setClassName(rs.getString("班级编号")); // 使用班级编号作为班级名称
                // 注意：学生视图中没有辅导员编号，只有辅导员姓名
                studentView.setCounselorName(rs.getString("辅导员姓名"));
                studentViews.add(studentView);
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
        return studentViews;
    }

    @Override
    public List<StudentView> getStudentViewsByClassId(String classId) throws SQLException {
        List<StudentView> studentViews = new ArrayList<>();
        String sql = "SELECT 学生学号, 姓名, 性别, 出生日期, 手机号码, 专业名称, 年级编号, 班级编号, 辅导员姓名 FROM 学生视图 WHERE 班级编号 = ?";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, classId);
            rs = ps.executeQuery();

            while (rs.next()) {
                StudentView studentView = new StudentView();
                studentView.setStudentId(rs.getString("学生学号"));
                studentView.setStudentName(rs.getString("姓名"));
                studentView.setGender(rs.getString("性别"));
                if (rs.getDate("出生日期") != null) {
                    studentView.setDateOfBirth(rs.getDate("出生日期").toLocalDate());
                }
                studentView.setPhoneNumber(rs.getString("手机号码"));
                // 注意：学生视图中没有专业编号，只有专业名称
                studentView.setMajorName(rs.getString("专业名称"));
                studentView.setGradeNumber(rs.getString("年级编号"));
                studentView.setClassName(rs.getString("班级编号")); // 使用班级编号作为班级名称
                // 注意：学生视图中没有辅导员编号，只有辅导员姓名
                studentView.setCounselorName(rs.getString("辅导员姓名"));
                studentViews.add(studentView);
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
        return studentViews;
    }
}