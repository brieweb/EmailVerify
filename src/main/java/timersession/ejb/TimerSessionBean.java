package timersession.ejb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import timersession.entity.Person;


/**
 * TimerBean is a singleton session bean that creates a timer and prints out a
 * message when a timeout occurs.
 */
@Singleton
@Startup
public class TimerSessionBean {
    private static final Logger logger = Logger.getLogger(
                "com.brie.timersession.TimerSessionBean");
    @Resource
    TimerService timerService;
    
	@Resource (mappedName="java:jboss/mail/Default")
	private Session mySession;
    
    @PersistenceContext
    EntityManager em;
    
    private Date lastAutomaticTimeout;
    private Date lastProgrammaticTimeout;
    
    
    private Map<String, String> idmap ;
    private Map<String, Integer> emailmap  ;
    private Map<String, Timer> timerMap;
	
	private String site_name = "localhost:8080";
    
    private Random b = new Random();
    
    public TimerSessionBean() {
    	idmap = new HashMap<String, String>();
    	emailmap = new HashMap<String, Integer>();
    	timerMap = new HashMap<String, Timer>();
//    	try {
//			FileInputStream input = new FileInputStream( "timeouts.properties" );
//		} catch (FileNotFoundException e) {
//			logger.info(
//			e.toString());
//		}


	

		//Properties props = new Properties();
		//props.load("mailverify.properties");
		
    }
    
    public boolean hasAddress(String id, Person parg) {
    	if (idmap.containsKey(id))
    	{
    		//Person p = new Person();
    		logger.info("The ID does exist " + id + " for hasAddress()");
    		String email = idmap.get(id);

    		idmap.remove(id); // Remove the id from the hash
			
    		Person p = new Person(email);
    		em.persist(p);
    		parg.setId(p.getId());
    		
    		// Tried setting parg = p but that does not work. I think
    		// it is a scoping issue
    		parg.setEmail(p.getEmail());
    		
    		if (timerMap.containsKey(id)) {
    			logger.info("removing key and cancelling timer for idmap");
    			Timer t = timerMap.get(id);
    			t.cancel();
    			
        		//parg.setId(id);
    		} else {
    			logger.info("Timer hash map does not have value for key");
    		}

    		
    		return true;
    	}
    	return false;
    }
    
    public String emailAddr(String id) {
    	String email = idmap.get(id);
    	return email;
    }

    public void setTimer(long intervalDuration, String emailin) {
    	
    	String email =  emailin.toLowerCase();
    	if (emailmap.containsKey(email)) {
    		Integer count = emailmap.get(email);
    		
    		count++;
    		
    		// Only allow users to submit 3 times per email timeout
    		if (count > 3) {
    			logger.log(Level.INFO,"This email {0} already submitted 3 times", email);
    			return;
    		}
    		
    		logger.log(Level.INFO,"This email has submitted {0} times", count);
    		 emailmap.put(email,count);
    		
    	} else {
    		// first time
    		emailmap.put(email, 1);
    		RemoveEmail re = new RemoveEmail(email, emailmap);
    		Timer timer = timerService.createTimer(
                    120000, //two minutes
                    re);
            
    	}
    	
    	Integer id = b.nextInt(2000);
    	
    	String bs = id.toString(); // use the random number as default
    	
    	// see if we can create a random id

    	try {
			MessageDigest alg = MessageDigest.getInstance("MD5");
	    	String mailid = email + id;
	    	logger.log(Level.INFO, "converted string is {0}", mailid);
	    	char[] a = mailid.toCharArray();
	    	for (int i=0; i<a.length; i++) {
	    		alg.update((byte) a[i]);
	    	}
	    	byte[] hash = alg.digest();
	    	 String d = "";
	    	 for (int i = 0; i < hash.length; i++)
	    	 {
	    	           int v = hash[i] & 0xFF;
	    	            if (v < 16) d += "0";
	    	           d += Integer.toString(v, 16).toUpperCase() ;
	    	 }
	    	logger.log(
	                	Level.INFO,
	                	"has md5 hash {0} ",
	                	d);
	    	bs = d;
	  
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			logger.log(Level.WARNING, e1.getMessage());
			bs = id.toString();
		}
    	

        logger.log(
                Level.INFO,
                "Setting a programmatic timeout for id {0} ", bs);
        
        idmap.put(bs, email);
        
        RemoveId myRemove = new RemoveId(bs, idmap); 

//        Timer timer = timerService.createTimer(
//                    intervalDuration,
//                    id);
        
        Timer timer = timerService.createTimer(
                intervalDuration,
                myRemove);
        
        try {
        	send("verify@localhost",email,"Please verify your email address",
        		"Please visit http://localhost:8080/EmailVerify/faces/checklink.xhtml?token=" + bs);
        } catch(Exception e) {
        	logger.log(Level.WARNING, "Unable to send email for {0}" , email);
        }
        
        timerMap.put(bs, timer);
    
    }

    @Timeout
    public void programmaticTimeout(Timer timer) {
        this.setLastProgrammaticTimeout(new Date());
        RemoveKey rk = (RemoveKey)timer.getInfo();
        logger.info("Programmatic timeout occurred.");
 
        logger.info("Removing token for " + rk.getMyType() );
        if (rk.getMyType() == TimerT.ID)
        	logger.info("Size of idmap before is " + idmap.size());
        
        rk.remove();
        
        if (rk.getMyType() == TimerT.ID)
        	logger.info("Size of idmap after is " + idmap.size());
        
    }

    //@Schedule(minute = "*/3", hour = "*")
    public void automaticTimeout() {
        this.setLastAutomaticTimeout(new Date());
        logger.info("Automatic timeout occurred");
    }

    /**
     * @return the lastTimeout
     */
    public String getLastProgrammaticTimeout() {
        if (lastProgrammaticTimeout != null) {
            return lastProgrammaticTimeout.toString();
        } else {
            return "never";
        }
    }

    /**
     * @param lastTimeout the lastTimeout to set
     */
    public void setLastProgrammaticTimeout(Date lastTimeout) {
        this.lastProgrammaticTimeout = lastTimeout;
    }

    /**
     * @return the lastAutomaticTimeout
     */
    public String getLastAutomaticTimeout() {
        if (lastAutomaticTimeout != null) {
            return lastAutomaticTimeout.toString();
        } else {
            return "never";
        }
    }

    /**
     * @param lastAutomaticTimeout the lastAutomaticTimeout to set
     */
    public void setLastAutomaticTimeout(Date lastAutomaticTimeout) {
        this.lastAutomaticTimeout = lastAutomaticTimeout;
    }
    
	public void send(String from, String to, String subject, String body) throws Exception
	{
		Message message = new MimeMessage(mySession);
		message.setFrom(new InternetAddress(from));
		Address toAddress= new InternetAddress(to);
		message.addRecipient(Message.RecipientType.TO, toAddress);
		message.setSubject(subject);
		message.setContent(body, "text/plain");
		Transport.send(message);
	}
}
