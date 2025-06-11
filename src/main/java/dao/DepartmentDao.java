package dao;

import java.sql.SQLException;
import java.util.Map;

public interface DepartmentDao {
    Map<String, String> getAllDepartments() throws SQLException; // 返回院系名称到院系编号的映射
} 