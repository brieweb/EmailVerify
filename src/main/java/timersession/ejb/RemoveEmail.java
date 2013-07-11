package timersession.ejb;

import java.io.Serializable;
import java.util.Map;

public class RemoveEmail extends RemoveKey  {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;
	private Map<String, Integer> emailmap;

	@Override
	void remove() {
		if (emailmap.containsKey(email)) {
			emailmap.remove(email);	
		}
		

	}
	
	public RemoveEmail(String st, Map<String, Integer> map ) {
		this.email = st;
		this.emailmap = map;
		setMyType(TimerT.EMAIL);
	}

}
