package am.englet.stateengine;

public interface Action {
    /**
     * State in which the Action is to be performed
     * 
     * @return
     */
    Object state();

    /**
     * State after which the Action is to be performed
     * 
     * @return
     */
    Object event();

    /**
     * Action returning next state
     * 
     * @return
     */
    Object act();

}
