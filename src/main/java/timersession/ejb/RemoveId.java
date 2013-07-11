package timersession.ejb;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.Timer;

public class RemoveId extends RemoveKey  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private Map<String, String> idmap;

	@Override
	void remove() {
		if (idmap.containsKey(id)) {
			idmap.remove(id);	
		}
		

	}
	
	public RemoveId(String id, Map<String, String> map ) {
		this.id = id;
		this.idmap = map;
		setMyType(TimerT.ID);
	}


}
