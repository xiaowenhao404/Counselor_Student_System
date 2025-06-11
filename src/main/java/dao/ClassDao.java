package dao;

import entity.Class;
import java.sql.SQLException;
import java.util.List;

public interface ClassDao {
    List<Class> getAllClasses() throws SQLException;

    Class getClassById(String classId) throws SQLException;

    Class getClassByFullKey(String majorId, String gradeNumber, String classId) throws SQLException;

    List<Class> getClassesByMajorId(String majorId) throws SQLException;
    
    // 增删改方法
    boolean addClass(Class clazz) throws SQLException;
    
    boolean updateClass(Class clazz) throws SQLException;
    
    boolean deleteClass(String majorId, String gradeNumber, String classId) throws SQLException;
    
    boolean updateClassCounselor(String majorId, String gradeNumber, String classId, String counselorId) throws SQLException;
}