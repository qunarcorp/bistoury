package qunar.tc.bistoury.ui.model;

/**
 * @author: leix.xie
 * @date: 2018/12/4 11:44
 * @describe：
 */
public class PrivateToken {

    private String userCode;
    private String privateToken;

    public PrivateToken() {
    }

    public PrivateToken(String privateToken) {
        this.privateToken = privateToken;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPrivateToken() {
        return privateToken;
    }

    public void setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
    }
}
