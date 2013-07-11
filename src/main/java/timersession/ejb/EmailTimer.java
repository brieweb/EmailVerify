package timersession.ejb;

import java.io.Serializable;

import javax.ejb.Timer;

public class EmailTimer implements Serializable {
	private String email;
	
	
	public EmailTimer(String email1) {
		this.email = email1;

	}
	
	public EmailTimer() {
		
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}
