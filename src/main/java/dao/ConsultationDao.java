package dao;

import entity.Consultation;
import java.sql.SQLException;
import java.util.List;

public interface ConsultationDao {
    List<Consultation> getAllConsultations() throws SQLException;
    // 例如：Consultation getConsultationByQNumber(String qNumber);
    // 其他方法，例如getConsultationById, addConsultation, updateConsultation,
    // deleteConsultation等
}