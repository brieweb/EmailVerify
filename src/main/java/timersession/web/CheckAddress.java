package timersession.web;

import java.util.logging.Logger;

import javax.ejb.EJB;

import javax.enterprise.context.RequestScoped;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.validator.constraints.Email;

import timersession.ejb.TimerSessionBean;
import timersession.entity.Person;


@Named
@RequestScoped
public class CheckAddress  {
	
    private static final Logger logger = Logger.getLogger(
            "com.brie.timersession.CheckAddress");


    private Person p;
    
	@Email
	private String email;
	
	private String emailId;
	
	private boolean exists = false;
	

	
    @EJB
    private TimerSessionBean timerSession;

	public CheckAddress() {
		// TODO Auto-generated constructor stub
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}
	
	public String idexists() {
		 p = new Person();
		if (timerSession.hasAddress(emailId, p)) {
				this.exists = true;
				 this.setEmail(p.getEmail());
		}
		

		
		//em.persist(p);
		
		return "";
	}





	public String getEmailId() {
		return emailId;
	}


	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}


	public boolean isExists() {
		return exists;
	}


	public void setExists(boolean exists) {
		this.exists = exists;
	}

}
