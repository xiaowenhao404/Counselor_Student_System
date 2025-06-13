package dao;

import entity.Consultation;
import java.sql.SQLException;
import java.util.List;

public interface ConsultationDao {
    List<Consultation> getAllConsultations() throws SQLException;

    Consultation getConsultationByQNumber(String qNumber) throws SQLException;

    // 新增查询方法
    List<Consultation> getConsultationsByMajor(String majorId) throws SQLException;
    List<Consultation> getFeaturedConsultations() throws SQLException;
    List<Consultation> getConsultationsByStudentIds(List<String> studentIds) throws SQLException;

    // 增删改方法
    boolean addConsultation(Consultation consultation) throws SQLException;

    void updateConsultation(Consultation consultation) throws SQLException;

    boolean deleteConsultation(String qNumber) throws SQLException;

    boolean toggleHighlight(String qNumber) throws SQLException;
}