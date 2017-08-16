package am.englet.link;


public abstract class AbstractLink implements Link {

	protected boolean rewindable = false;

	public final boolean rewindable() {
		return rewindable ;
	}

}
