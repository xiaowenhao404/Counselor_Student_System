package dao;

import entity.Counselor;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CounselorDao {
    Counselor getCounselorByIdAndPassword(String counselorId, String password) throws SQLException;

    Map<String, String> getAllCounselors() throws SQLException;

    List<Counselor> getAllCounselorsList() throws SQLException;

    Counselor getCounselorById(String counselorId) throws SQLException;

    List<String> getStudentIdsByCounselorId(String counselorId) throws SQLException;
    
    // 增删改方法
    boolean addCounselor(Counselor counselor) throws SQLException;
    
    boolean updateCounselor(Counselor counselor) throws SQLException;
    
    boolean deleteCounselor(String counselorId) throws SQLException;
}