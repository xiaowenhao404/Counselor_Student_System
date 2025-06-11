package dao;

import entity.ClassView;
import java.sql.SQLException;
import java.util.List;

public interface ClassViewDao {
    List<ClassView> getAllClassViews() throws SQLException;

    List<ClassView> getClassViewsByCounselorId(String counselorId) throws SQLException;
}