package dao;

import entity.Student;
import java.sql.SQLException;

public interface StudentDao {
    Student getStudentByIdAndPassword(String studentId, String password) throws SQLException;

    Student getStudentById(String studentId) throws SQLException;
}