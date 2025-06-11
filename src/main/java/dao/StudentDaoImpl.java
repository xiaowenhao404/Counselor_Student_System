package dao;

import db.DatabaseConnection;
import entity.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class StudentDaoImpl implements StudentDao {

    @Override
    public Student getStudentByIdAndPassword(String studentId, String password) throws SQLException {
        String sql = "SELECT 学生学号, 专业编号, 年级编号, 班级编号, 姓名, 性别, 出生日期, 手机号码, 密码 FROM 学生 WHERE 学生学号 = ? AND 密码 = ?";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Student student = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                student = new Student();
                student.setStudentId(rs.getString("学生学号"));
                student.setMajorId(rs.getString("专业编号"));
                student.setGradeNumber(rs.getString("年级编号"));
                student.setClassId(rs.getString("班级编号"));
                student.setName(rs.getString("姓名"));
                student.setGender(rs.getString("性别"));

                // 处理可能为 null 的日期
                java.sql.Date sqlDateOfBirth = rs.getDate("出生日期");
                if (sqlDateOfBirth != null) {
                    student.setDateOfBirth(sqlDateOfBirth.toLocalDate());
                }

                student.setPhoneNumber(rs.getString("手机号码"));
                student.setPassword(rs.getString("密码"));
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
        return student;
    }

    @Override
    public Student getStudentById(String studentId) throws SQLException {
        String sql = "SELECT 学生学号, 专业编号, 年级编号, 班级编号, 姓名, 性别, 出生日期, 手机号码, 密码 FROM 学生 WHERE 学生学号 = ?";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Student student = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, studentId);
            rs = ps.executeQuery();

            if (rs.next()) {
                student = new Student();
                student.setStudentId(rs.getString("学生学号"));
                student.setMajorId(rs.getString("专业编号"));
                student.setGradeNumber(rs.getString("年级编号"));
                student.setClassId(rs.getString("班级编号"));
                student.setName(rs.getString("姓名"));
                student.setGender(rs.getString("性别"));

                java.sql.Date sqlDateOfBirth = rs.getDate("出生日期");
                if (sqlDateOfBirth != null) {
                    student.setDateOfBirth(sqlDateOfBirth.toLocalDate());
                }

                student.setPhoneNumber(rs.getString("手机号码"));
                student.setPassword(rs.getString("密码"));
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
        return student;
    }

    @Override
    public boolean addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO 学生 (学生学号, 专业编号, 年级编号, 班级编号, 姓名, 性别, 出生日期, 手机号码, 密码) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, student.getStudentId());
            ps.setString(2, student.getMajorId());
            ps.setString(3, student.getGradeNumber());
            ps.setString(4, student.getClassId());
            ps.setString(5, student.getName());
            ps.setString(6, student.getGender());
            ps.setDate(7, student.getDateOfBirth() != null ? java.sql.Date.valueOf(student.getDateOfBirth()) : null);
            ps.setString(8, student.getPhoneNumber());
            ps.setString(9, student.getPassword());
            
            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    @Override
    public boolean updateStudent(Student student) throws SQLException {
        String sql = "UPDATE 学生 SET 专业编号 = ?, 年级编号 = ?, 班级编号 = ?, 姓名 = ?, 性别 = ?, 出生日期 = ?, 手机号码 = ?, 密码 = ? WHERE 学生学号 = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, student.getMajorId());
            ps.setString(2, student.getGradeNumber());
            ps.setString(3, student.getClassId());
            ps.setString(4, student.getName());
            ps.setString(5, student.getGender());
            ps.setDate(6, student.getDateOfBirth() != null ? java.sql.Date.valueOf(student.getDateOfBirth()) : null);
            ps.setString(7, student.getPhoneNumber());
            ps.setString(8, student.getPassword());
            ps.setString(9, student.getStudentId());
            
            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    @Override
    public boolean deleteStudent(String studentId) throws SQLException {
        String sql = "DELETE FROM 学生 WHERE 学生学号 = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, studentId);
            
            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    @Override
    public int getStudentCountByClassId(String classId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM 学生 WHERE 班级编号 = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, classId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
                return 0;
            }
        }
    }
}