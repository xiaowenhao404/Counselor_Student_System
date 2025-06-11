package dao;

import entity.StudentView;
import java.sql.SQLException;
import java.util.List;

public interface StudentViewDao {
    List<StudentView> getAllStudentViews() throws SQLException;

    List<StudentView> getStudentViewsByClassId(String classId) throws SQLException;
}