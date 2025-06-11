package dao;

import entity.Class;
import java.sql.SQLException;
import java.util.List;

public interface ClassDao {
    List<Class> getAllClasses() throws SQLException;

    Class getClassById(String classId) throws SQLException;

    Class getClassByFullKey(String majorId, String gradeNumber, String classId) throws SQLException;

    List<Class> getClassesByMajorId(String majorId) throws SQLException;
}