package dao;

import entity.Consultation;
import java.sql.SQLException;
import java.util.List;

public interface ConsultationDao {
    List<Consultation> getAllConsultations() throws SQLException;

    Consultation getConsultationByQNumber(String qNumber) throws SQLException;

    // 增删改方法
    boolean addConsultation(Consultation consultation) throws SQLException;

    boolean updateConsultation(Consultation consultation) throws SQLException;

    boolean deleteConsultation(String qNumber) throws SQLException;

    boolean toggleHighlight(String qNumber) throws SQLException;
}