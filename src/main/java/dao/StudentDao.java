package dao;

import entity.Student;
import java.sql.SQLException;

public interface StudentDao {
    Student getStudentByIdAndPassword(String studentId, String password) throws SQLException;

    Student getStudentById(String studentId) throws SQLException;
    
    // 增删改方法
    boolean addStudent(Student student) throws SQLException;
    
    boolean updateStudent(Student student) throws SQLException;
    
    boolean deleteStudent(String studentId) throws SQLException;
    
    // 统计班级学生人数
    int getStudentCountByClassId(String classId) throws SQLException;
}