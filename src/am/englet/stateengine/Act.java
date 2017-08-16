package am.englet.stateengine;

import java.util.ArrayList;
import java.util.List;

public class Act implements Action, Cloneable {

    protected Object state;
    protected Object result = null;
    protected Object event;

    /**
     * @param state
     * @param event
     */
    public Act(final Object state, final Object event) {
        this.state = state;
        this.event = event;
    }

    /**
     * @param state
     * @param event
     * @param result
     */
    public Act(final Object state, final Object event, final Object result) {
        this.state = state;
        this.event = event;
        this.result = result;
    }

    public Object act() {
        return result;
    }

    public Object event() {
        return event;
    }

    public Object state() {
        return state;
    }

    public Act[] s(final String events) {
        return (Act[]) s(null, events.split(" "), null, new ArrayList()).toArray(new Act[0]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Act [event=" + event + ", result=" + result + ", state=" + state + "]";
    }

    public Act[] s(final String states, final String events) {
        final String[] evt = events.split(" ");
        final List res = new ArrayList();
        final String[] state = states.split(" ");
        for (int i = 0; i < state.length; i++)
            s(state[i], evt, null, res);
        return (Act[]) res.toArray(new Act[0]);
    }

    protected List s(final Object state2, final String[] evt, final Object result, final List res) {
        for (int j = 0; j < evt.length; j++) {
            final Act clone = clone(state2, evt[j], result);
            if (clone != null)
                res.add(clone);
        }
        return res;
    }

    /**
     * @param state
     * @param evt
     * @param result
     *            TODO
     * @return
     * @throws CloneNotSupportedException
     */
    public Act clone(final Object state, final Object evt, final Object result) {
        try {
            final Act clone = (Act) this.clone();
            if (evt != null)
                clone.event = evt;
            if (state != null)
                clone.state = state;
            if (result != null)
                clone.result = result;
            return clone;
        } catch (final CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addTo(final Engine e, final Object state, final Object event, final Object resultState) {
        if (state instanceof String)
            addTo(e, ((String) state).split("[ ,]"), event, resultState);
        else if (!(state instanceof Object[]))
            addTo(e, new Object[] { state }, event, resultState);
        else if (event instanceof String)
            addTo(e, state, ((String) event).split("[ ,]"), resultState);
        else if (!(event instanceof Object[]))
            addTo(e, state, new Object[] { event }, resultState);
        else
            addTo(e, (Object[]) state, (Object[]) event, resultState);
    }

    /**
     * @param e
     * @param st
     * @param ev
     * @param resultState
     */
    private void addTo(final Engine e, final Object[] st, final Object[] ev, final Object resultState) {
        for (int i = 0; i < st.length; i++)
            for (int j = 0; j < ev.length; j++)
                e.add(clone(st[i], ev[j], resultState));
    }

    /*
     * public void addTo(final Engine e, final Object state, final String
     * events, final Object resultState) { e.add((Act[]) s(state,
     * events.split(" "), resultState, new ArrayList()) .toArray(new Act[0])); }
     * 
     * public void addTo(final Engine e, final String states, final String
     * events, final Object resultState) { final List res = new ArrayList();
     * final String[] state = states.split(" "); final String[] event =
     * events.split(" "); for (int i = 0; i < state.length; i++) s(state[i],
     * event, resultState, res); e.add((Act[]) res.toArray(new Act[0])); }
     * 
     * public void addTo(final Engine e, final String states, final Object
     * event, final Object resultState) { final String[] state =
     * states.split(" "); for (int i = 0; i < state.length; i++) addTo(e,
     * state[i], event, resultState); }
     */
}
