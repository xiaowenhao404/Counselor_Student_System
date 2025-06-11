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
}