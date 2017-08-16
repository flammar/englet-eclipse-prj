package am.englet.inputtokenizers;

import java.io.PushbackReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import am.englet.EngletParserReaderTokenizerFactory;
import am.englet.ServiceTokenizerFactory;
import am.englet.Utils;

public class EngletParserReaderToTokenizerAdapter extends ReaderToTokenizerAdapter {
    private static final String ENGINE_DOT = "Engine.";
    private static final String ON = ".on.";
    private static final String EVENT_RANGE_DOT = "event-range.";
    private static final String EVENT_DOT = "event.";
    private static String[] EVENTS = new String[128];
    final static Class class1 = EngletParserReaderToTokenizerAdapter.class;
    final static Properties propertiesByResource = new Properties(Utils.getPropertiesByResource(class1, Utils
            .simpleName(class1)
            + ".properties"));
    static {
        final String last = propertiesByResource.getProperty("event-last");
        final char lastC = last != null && last.length() > 0 ? last.charAt(0) : 127;
        if (EVENTS == null)
            EVENTS = new String[lastC + 1];
        final String event = propertiesByResource.getProperty("event");
        Arrays.fill(EVENTS, 0, 128, event != null ? event : BASIC);
        final Enumeration propertyNames = propertiesByResource.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String pname = (String) propertyNames.nextElement();
            final String value = propertiesByResource.getProperty(pname).intern();
            if (pname.startsWith(EVENT_DOT)) {
                final char[] charArray = arrayAfter(pname, EVENT_DOT);
                for (int i = 0; i < charArray.length; i++)
                    EVENTS[charArray[i]] = value;
            } else if (pname.startsWith(EVENT_RANGE_DOT)) {
                final char[] arrayAfter = arrayAfter(pname, EVENT_RANGE_DOT);
                for (int j = 1; j < arrayAfter.length; j += 3)
                    Arrays.fill(EVENTS, arrayAfter[j - 1], arrayAfter[j] + 1, value);
            }
        }
    }

    static char[] arrayAfter(final String pname, final String eventDot) {
        return pname.substring(eventDot.length()).toCharArray();
    }

    {
        final ReaderTokenizerEngine rte = readerTokenizerEngine;
        final Enumeration propertyNames = propertiesByResource.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String pname = (String) propertyNames.nextElement();
            final int indexOf = pname.indexOf(ON);
            if (indexOf > 0) {
                final String value = propertiesByResource.getProperty(pname).intern();
                final String[] split = value.split("(->|=>| +>| )", 2);
                final Object act0 = resolveObject(split[0]);
                final String stateName = pname.substring(0, indexOf);
                final String eventName = pname.substring(indexOf + ON.length());
                (act0 instanceof Act ? (Act) act0 : rte.newAct(this, act0)).addTo(rte, resolveObject(stateName),
                        resolveObject(eventName), resolveObject(split.length > 1 ? split[1] : stateName));
            }
        }

    }

    private Object resolveObject(final String state0) {
        final String state = state0.trim();
        if (state.startsWith(ENGINE_DOT))
            try {
                return getFieldValue(state.substring(ENGINE_DOT.length()));
            } catch (final SecurityException e) {
            } catch (final NoSuchFieldException e) {
            } catch (final IllegalArgumentException e) {
            } catch (final IllegalAccessException e) {
            }
        try {
            return getFieldValue(state);
        } catch (final SecurityException e) {
        } catch (final NoSuchFieldException e) {
        } catch (final IllegalArgumentException e) {
        } catch (final IllegalAccessException e) {
        }
        return state.intern();
    }

    private Object getFieldValue(final String name) throws NoSuchFieldException, IllegalAccessException {
        final Field field = readerTokenizerEngine.getClass().getField(name);
        field.setAccessible(true);
        return field.get(readerTokenizerEngine);
    }

    public EngletParserReaderToTokenizerAdapter(final Reader back) {
        super(new PushbackReader(back));
        readerTokenizerEngine.init(BASIC);
    }

    public EngletParserReaderToTokenizerAdapter() {
        super();
    }

    protected String event(final int read) {
        // System.out.println((char) read);
        // System.out.println(read);
        final String event = super.event(read);
        final String string = event != null ? event : read >= 128 ? BASIC : EVENTS[read];
        // System.out.println("ReaderToTokenizerAdapter.event():" + string);
        return string;
    }

    static public ServiceTokenizerFactory FACTORY = new EngletParserReaderTokenizerFactory();
}
