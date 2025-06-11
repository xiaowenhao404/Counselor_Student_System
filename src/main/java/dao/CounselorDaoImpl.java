package dao;

import db.DatabaseConnection;
import entity.Counselor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CounselorDaoImpl implements CounselorDao {

    @Override
    public Counselor getCounselorByIdAndPassword(String counselorId, String password) throws SQLException {
        String sql = "SELECT 辅导员工号, 姓名, 性别, 手机号码, 密码 FROM 辅导员 WHERE 辅导员工号 = ? AND 密码 = ?";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Counselor counselor = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, counselorId);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                counselor = new Counselor();
                counselor.setCounselorId(rs.getString("辅导员工号"));
                counselor.setName(rs.getString("姓名"));
                counselor.setGender(rs.getString("性别"));
                counselor.setPhoneNumber(rs.getString("手机号码"));
                counselor.setPassword(rs.getString("密码"));
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
        return counselor;
    }

    @Override
    public Map<String, String> getAllCounselors() throws SQLException {
        Map<String, String> counselors = new LinkedHashMap<>();
        String sql = "SELECT 辅导员工号, 姓名 FROM 辅导员";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                counselors.put(rs.getString("姓名"), rs.getString("辅导员工号"));
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
        return counselors;
    }

    @Override
    public List<Counselor> getAllCounselorsList() throws SQLException {
        List<Counselor> counselors = new ArrayList<>();
        String sql = "SELECT 辅导员工号, 姓名, 性别, 出生日期, 手机号码, 院系名称, 负责班级 FROM 辅导员视图";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Counselor counselor = new Counselor();
                counselor.setCounselorId(rs.getString("辅导员工号"));
                counselor.setName(rs.getString("姓名"));
                counselor.setGender(rs.getString("性别"));
                if (rs.getDate("出生日期") != null) {
                    counselor.setDateOfBirth(rs.getDate("出生日期").toLocalDate());
                }
                counselor.setPhoneNumber(rs.getString("手机号码"));
                counselor.setDepartmentName(rs.getString("院系名称"));
                counselor.setClassList(rs.getString("负责班级"));
                counselors.add(counselor);
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
        return counselors;
    }

    @Override
    public Counselor getCounselorById(String counselorId) throws SQLException {
        String sql = "SELECT 辅导员工号, 姓名, 性别, 手机号码, 密码 FROM 辅导员 WHERE 辅导员工号 = ?";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Counselor counselor = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, counselorId);
            rs = ps.executeQuery();

            if (rs.next()) {
                counselor = new Counselor();
                counselor.setCounselorId(rs.getString("辅导员工号"));
                counselor.setName(rs.getString("姓名"));
                counselor.setGender(rs.getString("性别"));
                counselor.setPhoneNumber(rs.getString("手机号码"));
                counselor.setPassword(rs.getString("密码"));
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
        return counselor;
    }
}