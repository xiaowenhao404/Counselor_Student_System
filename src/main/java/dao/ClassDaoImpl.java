package dao;

import db.DatabaseConnection;
import entity.Class;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClassDaoImpl implements ClassDao {

    @Override
    public List<Class> getAllClasses() throws SQLException {
        List<Class> classes = new ArrayList<>();
        String sql = "SELECT 专业编号, 年级编号, 班级编号, 辅导员工号 FROM 班级";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Class classObj = new Class();
                classObj.setMajorId(rs.getString("专业编号"));
                classObj.setGradeNumber(rs.getString("年级编号"));
                classObj.setClassId(rs.getString("班级编号"));
                classObj.setCounselorId(rs.getString("辅导员工号"));
                classes.add(classObj);
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
        return classes;
    }

    @Override
    public Class getClassById(String classId) throws SQLException {
        // 注意：由于班级使用联合主键，这个方法可能返回多个结果
        // 如果只传入班级编号，可能匹配多个不同专业和年级的班级
        String sql = "SELECT 专业编号, 年级编号, 班级编号, 辅导员工号 FROM 班级 WHERE 班级编号 = ?";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Class classObj = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, classId);
            rs = ps.executeQuery();

            if (rs.next()) {
                classObj = new Class();
                classObj.setMajorId(rs.getString("专业编号"));
                classObj.setGradeNumber(rs.getString("年级编号"));
                classObj.setClassId(rs.getString("班级编号"));
                classObj.setCounselorId(rs.getString("辅导员工号"));
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
        return classObj;
    }

    // 新增：根据完整主键获取班级
    public Class getClassByFullKey(String majorId, String gradeNumber, String classId) throws SQLException {
        String sql = "SELECT 专业编号, 年级编号, 班级编号, 辅导员工号 FROM 班级 WHERE 专业编号 = ? AND 年级编号 = ? AND 班级编号 = ?";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Class classObj = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, majorId);
            ps.setString(2, gradeNumber);
            ps.setString(3, classId);
            rs = ps.executeQuery();

            if (rs.next()) {
                classObj = new Class();
                classObj.setMajorId(rs.getString("专业编号"));
                classObj.setGradeNumber(rs.getString("年级编号"));
                classObj.setClassId(rs.getString("班级编号"));
                classObj.setCounselorId(rs.getString("辅导员工号"));
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
        return classObj;
    }

    @Override
    public List<Class> getClassesByMajorId(String majorId) throws SQLException {
        List<Class> classes = new ArrayList<>();
        String sql = "SELECT 专业编号, 年级编号, 班级编号, 辅导员工号 FROM 班级 WHERE 专业编号 = ?";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, majorId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Class classObj = new Class();
                classObj.setMajorId(rs.getString("专业编号"));
                classObj.setGradeNumber(rs.getString("年级编号"));
                classObj.setClassId(rs.getString("班级编号"));
                classObj.setCounselorId(rs.getString("辅导员工号"));
                classes.add(classObj);
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
        return classes;
    }

    @Override
    public boolean addClass(Class clazz) throws SQLException {
        // 校验班级编号只能为正整数，禁止前导0
        if (!clazz.getClassId().matches("^[1-9]\\d*$")) {
            return false;
        }
        // 先检查班级是否已存在
        String checkSql = "SELECT COUNT(*) FROM 班级 WHERE 专业编号 = ? AND 年级编号 = ? AND 班级编号 = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(checkSql)) {

            ps.setString(1, clazz.getMajorId());
            ps.setString(2, clazz.getGradeNumber());
            ps.setString(3, clazz.getClassId());

            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // 班级已存在
            }
        }

        // 班级不存在，执行插入
        String sql = "INSERT INTO 班级 (专业编号, 年级编号, 班级编号, 辅导员工号) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, clazz.getMajorId());
            ps.setString(2, clazz.getGradeNumber());
            ps.setString(3, clazz.getClassId());
            ps.setString(4, clazz.getCounselorId());

            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    @Override
    public boolean updateClass(Class clazz) throws SQLException {
        // 校验班级编号只能为正整数，禁止前导0
        if (!clazz.getClassId().matches("^[1-9]\\d*$")) {
            return false;
        }
        String sql = "UPDATE 班级 SET 辅导员工号 = ? WHERE 专业编号 = ? AND 年级编号 = ? AND 班级编号 = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, clazz.getCounselorId());
            ps.setString(2, clazz.getMajorId());
            ps.setString(3, clazz.getGradeNumber());
            ps.setString(4, clazz.getClassId());

            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    @Override
    public boolean deleteClass(String majorId, String gradeNumber, String classId) throws SQLException {
        String sql = "DELETE FROM 班级 WHERE 专业编号 = ? AND 年级编号 = ? AND 班级编号 = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, majorId);
            ps.setString(2, gradeNumber);
            ps.setString(3, classId);

            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    @Override
    public boolean updateClassCounselor(String majorId, String gradeNumber, String classId, String counselorId)
            throws SQLException {
        String sql = "UPDATE 班级 SET 辅导员工号 = ? WHERE 专业编号 = ? AND 年级编号 = ? AND 班级编号 = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, counselorId);
            ps.setString(2, majorId);
            ps.setString(3, gradeNumber);
            ps.setString(4, classId);

            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    // 新增：根据辅导员工号查找所有班级
    public List<Class> getClassesByCounselorId(String counselorId) throws SQLException {
        List<Class> classes = new ArrayList<>();
        String sql = "SELECT 专业编号, 年级编号, 班级编号, 辅导员工号 FROM 班级 WHERE 辅导员工号 = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, counselorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Class classObj = new Class();
                    classObj.setMajorId(rs.getString("专业编号"));
                    classObj.setGradeNumber(rs.getString("年级编号"));
                    classObj.setClassId(rs.getString("班级编号"));
                    classObj.setCounselorId(rs.getString("辅导员工号"));
                    classes.add(classObj);
                }
            }
        }
        return classes;
    }
}