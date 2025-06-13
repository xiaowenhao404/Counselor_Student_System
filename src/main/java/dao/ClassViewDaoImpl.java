package dao;

import db.DatabaseConnection;
import entity.ClassView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClassViewDaoImpl implements ClassViewDao {

    @Override
    public List<ClassView> getAllClassViews() throws SQLException {
        List<ClassView> classViews = new ArrayList<>();
        String sql = "SELECT 专业编号, 年级编号, 班级编号, 辅导员工号, 专业名称, 辅导员姓名, 学生人数 FROM 班级视图";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                ClassView classView = new ClassView();
                classView.setMajorId(rs.getString("专业编号"));
                classView.setMajorName(rs.getString("专业名称"));
                classView.setGradeNumber(rs.getString("年级编号"));
                classView.setClassId(rs.getString("班级编号"));
                classView.setClassName(rs.getString("班级编号")); // 使用班级编号作为班级名称
                classView.setCounselorId(rs.getString("辅导员工号"));
                classView.setCounselorName(rs.getString("辅导员姓名"));
                classView.setStudentCount(rs.getInt("学生人数")); // 设置学生人数
                classViews.add(classView);
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
        return classViews;
    }

    @Override
    public List<ClassView> getClassViewsByCounselorId(String counselorId) throws SQLException {
        List<ClassView> classViews = new ArrayList<>();
        String sql = "SELECT 专业编号, 年级编号, 班级编号, 辅导员工号, 专业名称, 辅导员姓名, 学生人数 FROM 班级视图 WHERE 辅导员工号 = ?";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, counselorId);
            rs = ps.executeQuery();

            while (rs.next()) {
                ClassView classView = new ClassView();
                classView.setMajorId(rs.getString("专业编号"));
                classView.setMajorName(rs.getString("专业名称"));
                classView.setGradeNumber(rs.getString("年级编号"));
                classView.setClassId(rs.getString("班级编号"));
                classView.setClassName(rs.getString("班级编号")); // 使用班级编号作为班级名称
                classView.setCounselorId(rs.getString("辅导员工号"));
                classView.setCounselorName(rs.getString("辅导员姓名"));
                classView.setStudentCount(rs.getInt("学生人数")); // 设置学生人数
                classViews.add(classView);
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
        return classViews;
    }
}