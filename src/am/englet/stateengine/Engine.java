package am.englet.stateengine;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import am.englet.Utils;

public/* abstract */class Engine implements Serializable {
    private static final String FINISHED2 = "FINISHED";

    private static final long serialVersionUID = 8524516136701984810L;

    public final static Object DEFAULT = abstractListSingleton("DEFAULT");

    public final static Object FINISHED = abstractListSingleton(FINISHED2);

    public final static Object ALL = abstractListSingleton("ALL");

    private static Object abstractListSingleton(final Object all22) {
        return Collections.singletonList(intern(all22));
    }

    final protected Map rules = new HashMap();
    protected transient Object state = DEFAULT;

    // final private Map defaultActions = new HashMap();
    // final private List defaults = new Vector();

    /**
     * @return the state
     */
    public Object getState() {
        return state;
    }

    public void add(final Action action) {
        final Object state = intern(action.state());
        final HashMap events = (HashMap) container(state, rules, HashMap.class);
        final Object event = intern(action.event());
        // if (event.equals(DEFAULT))
        // putDefault(events, state);
        // else
        events.put(event, action);
    }

    /**
     * @param arg
     * @return
     */
    private static Object intern(final Object arg) {
        return arg instanceof String ? ((String) arg).intern() : arg;
    }

    public void add(final Action[] actions) {
        for (int i = 0; i < actions.length; i++)
            add(actions[i]);
    }

    /**
     * @param key
     * @param map
     * @param containerClass
     * @return
     */
    private static Object container(final Object key, final Map map, final Class containerClass) {
        final Object actions = map.get(key);
        if (actions == null)
            try {
                final Object value = containerClass.newInstance();
                map.put(key, value);
                return value;
            } catch (final InstantiationException e) {
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        else
            return actions;
        return null;
    }

    public void init(final Object state) {
        this.state = state;
    }

    /**
     * Engine kter Werk al kWum.
     * 
     * @param event
     */
    public void act(final Object event) {
        if (finished())
            throw new IllegalStateException(FINISHED2);
        act0(ALL, event);
        if (finished())
            return;
        if (!(act0(state, event) || act0(state, DEFAULT) || act0(DEFAULT, event) || act0(DEFAULT, DEFAULT))) {
            reportIllegalState(event);
            throw new IllegalStateException("" + state + ':' + event);
        }
    }

    protected void reportIllegalState(final Object event) {
        Utils.debug(null, "Illegal event: ", event, " in state: ", state);
    }

    /**
     * @param state0
     * @param event
     */
    private boolean act0(final Object state0, final Object event) {
        final Map evts = (Map) rules.get(state0);
        if (evts != null) {
            final Action action = (Action) evts.get(event);
            if (action != null) {
                final Object act = action.act();
                if (!DEFAULT.equals(act)) {
                    Utils.debug(null, state, ":", event, "->", act);
                    state = act;
                }

                // System.out.println("Engine.act0():state:" + state);
                return true;
            }
        }
        return false;
    }

    /**
     * @return
     */
    public boolean finished() {
        return (state != null) && is(FINISHED);
    }

    /**
     * @param state
     * @return
     */
    public boolean is(final Object state) {
        return (this.state == null) ? (state == null) : this.state.equals(state);
    }
}
