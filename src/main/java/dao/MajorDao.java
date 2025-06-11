package dao;

import java.sql.SQLException;
import java.util.Map;

public interface MajorDao {
    Map<String, String> getAllMajors() throws SQLException; // 返回专业名称到专业编号的映射
}