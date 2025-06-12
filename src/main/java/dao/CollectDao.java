package dao;

import java.sql.SQLException;

public interface CollectDao {
    boolean isCollected(String qNumber, String studentId) throws SQLException;

    boolean addCollect(String qNumber, String studentId) throws SQLException;

    boolean removeCollect(String qNumber, String studentId) throws SQLException;
}