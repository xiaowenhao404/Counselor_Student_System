package service;

import dao.CounselorDaoImpl;
import dao.StudentDaoImpl;
import entity.Student;
import entity.Counselor;
import entity.Admin;

import java.sql.SQLException;

public class AuthService {

    private StudentDaoImpl studentDao;
    private CounselorDaoImpl counselorDao;

    // 硬编码的管理员凭据
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123321";

    public AuthService() {
        this.studentDao = new StudentDaoImpl();
        this.counselorDao = new CounselorDaoImpl();
    }

    public enum UserType {
        STUDENT,
        COUNSELOR,
        ADMIN,
        NONE
    }

    public LoginResult login(String username, String password, UserType userType) {
        switch (userType) {
            case STUDENT:
                try {
                    Student student = studentDao.getStudentByIdAndPassword(username, password);
                    if (student != null) {
                        return new LoginResult(UserType.STUDENT, student.getStudentId(), student.getName());
                    }
                } catch (SQLException e) {
                    System.err.println("学生登录数据库错误: " + e.getMessage());
                    e.printStackTrace();
                }
                break;

            case COUNSELOR:
                try {
                    Counselor counselor = counselorDao.getCounselorByIdAndPassword(username, password);
                    if (counselor != null) {
                        return new LoginResult(UserType.COUNSELOR, counselor.getCounselorId(), counselor.getName());
                    }
                } catch (SQLException e) {
                    System.err.println("辅导员登录数据库错误: " + e.getMessage());
                    e.printStackTrace();
                }
                break;

            case ADMIN:
                if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
                    return new LoginResult(UserType.ADMIN, ADMIN_USERNAME, "管理员");
                }
                break;
        }

        return new LoginResult(UserType.NONE, null, null);
    }

    public static class LoginResult {
        private final UserType userType;
        private final String userId;
        private final String userName;

        public LoginResult(UserType userType, String userId, String userName) {
            this.userType = userType;
            this.userId = userId;
            this.userName = userName;
        }

        public UserType getUserType() {
            return userType;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public boolean isSuccess() {
            return userType != UserType.NONE;
        }
    }
}