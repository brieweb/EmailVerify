package timersession.ejb;

import java.io.Serializable;

public abstract class RemoveKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TimerT myType;
	
	abstract void remove();

	public TimerT getMyType() {
		return myType;
	}

	public void setMyType(TimerT myType) {
		this.myType = myType;
	}
}
