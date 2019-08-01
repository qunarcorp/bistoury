package qunar.tc.bistoury.ui.model;

/**
 * @author leix.xie
 * @date 2019/7/4 10:51
 * @describe
 */
public class User {
    private String userCode;
    private String password;

    public User() {

    }

    public User(String userCode, String password) {
        this.userCode = userCode;
        this.password = password;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userCode='" + userCode + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
