package medspeer.tech.model;

public class PasswordResetResource {
	
	private String Username;
	private String password;
	private String Newpassword;
	
	public String getUsername() {
		return Username;
	}
	public void setUsername(String username) {
		Username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNewpassword() {
		return Newpassword;
	}
	public void setNewpassword(String newpassword) {
		Newpassword = newpassword;
	}

}
