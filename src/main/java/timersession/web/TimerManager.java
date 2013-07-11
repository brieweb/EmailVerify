
package timersession.web;

import java.io.Serializable;

import javax.ejb.EJB;

import javax.enterprise.context.SessionScoped; 

import javax.inject.Named;

import org.hibernate.validator.constraints.Email;

import timersession.ejb.TimerSessionBean;



@Named
@SessionScoped
public class TimerManager implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lastAutomaticTimeout;
    private String lastProgrammaticTimeout;
    
    @Email
    private String email;
    
    @EJB
    private TimerSessionBean timerSession;

    /** Creates a new instance of TimerManager */
    public TimerManager() {
        this.lastProgrammaticTimeout = "never";
        this.lastAutomaticTimeout = "never";
    }

    /**
     * @return the lastTimeout
     */
    public String getLastProgrammaticTimeout() {
        lastProgrammaticTimeout = timerSession.getLastProgrammaticTimeout();

        return lastProgrammaticTimeout;
    }

    /**
     * @param lastTimeout the lastTimeout to set
     */
    public void setLastProgrammaticTimeout(String lastTimeout) {
        this.lastProgrammaticTimeout = lastTimeout;
    }

    public void setTimer() {
        long timeoutDuration = 35000;
        timerSession.setTimer(timeoutDuration, email);
    }

    /**
     * @return the lastAutomaticTimeout
     */
    public String getLastAutomaticTimeout() {
        lastAutomaticTimeout = timerSession.getLastAutomaticTimeout();

        return lastAutomaticTimeout;
    }

    /**
     * @param lastAutomaticTimeout the lastAutomaticTimeout to set
     */
    public void setLastAutomaticTimeout(String lastAutomaticTimeout) {
        this.lastAutomaticTimeout = lastAutomaticTimeout;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}