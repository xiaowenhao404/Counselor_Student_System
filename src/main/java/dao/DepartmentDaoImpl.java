package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DepartmentDaoImpl implements DepartmentDao {

    @Override
    public Map<String, String> getAllDepartments() throws SQLException {
        Map<String, String> departments = new LinkedHashMap<>();
        String sql = "SELECT 院系编号, 院系名称 FROM 院系 ORDER BY 院系编号";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                departments.put(rs.getString("院系名称"), rs.getString("院系编号"));
            }
        }
        return departments;
    }
} 