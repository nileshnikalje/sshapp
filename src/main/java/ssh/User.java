package ssh;

public class User{
    String userId;
    String password;
    String homeDirectory;
    String loginShell;

    public User(String userId, String password, String homeDirectory, String loginShell) {
        this.userId = userId;
        this.password = password;
        this.homeDirectory = homeDirectory;
        this.loginShell = loginShell;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(String homeDirectory) {
        this.homeDirectory = homeDirectory;
    }

    public String getLoginShell() {
        return loginShell;
    }

    public void setLoginShell(String loginShell) {
        this.loginShell = loginShell;
    }
}
