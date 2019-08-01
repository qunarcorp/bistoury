package qunar.tc.bistoury.ui.security;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * @author kelly.li
 */
public class LoginContext {

    public final static String CONTEXT = "context";

    private String loginUser;
    private boolean isAdmin;
    private String remoteIP;
    private String returnUrl;
    private boolean isAjax;

    public static LoginContext getLoginContext() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return (LoginContext) request.getAttribute(CONTEXT);
    }

    /**
     * @return the userName
     */
    public String getLoginUser() {
        return loginUser;
    }

    /**
     * @param username the userName to set
     */
    public void setLoginUser(String username) {
        this.loginUser = username;
    }

    /**
     * @return the isAdmin
     */
    public boolean isAdmin() {
        //return false;
        //return false;
        return isAdmin;
    }

    /**
     * @param isAdmin the isAdmin to set
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * @return the remoteIP
     */
    public String getRemoteIP() {
        return remoteIP;
    }

    /**
     * @param remoteIP the remoteIP to set
     */
    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public boolean isAjax() {
        return isAjax;
    }

    public void setAjax(boolean isAjax) {
        this.isAjax = isAjax;
    }

}
