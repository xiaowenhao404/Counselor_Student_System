package dao;

import db.DatabaseConnection;
import entity.Major;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MajorDaoImpl implements MajorDao {

    @Override
    public Map<String, String> getAllMajors() throws SQLException {
        Map<String, String> majors = new LinkedHashMap<>();
        String sql = "SELECT 专业编号, 专业名称 FROM 专业";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                majors.put(rs.getString("专业名称"), rs.getString("专业编号"));
            }
        } finally {
            DatabaseConnection.closeConnection(connection);
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return majors;
    }
}