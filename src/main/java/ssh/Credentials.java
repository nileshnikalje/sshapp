package ssh;

public class Credentials {
    String newPasswd;
    String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Credentials(String userId, String newPasswd) {
        this.newPasswd = newPasswd;
        this.userId = userId;
    }


    public String getNewPasswd() {
        return newPasswd;
    }

    public void setNewPasswd(String newPasswd) {
        this.newPasswd = newPasswd;
    }

}
