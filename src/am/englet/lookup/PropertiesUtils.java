package am.englet.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesUtils {

    private static final Pattern RANGE_PATTERN = Pattern.compile("#([0-9]+)\\.\\.([0-9]+)");
    private static final String SUBCLOSING = "}$".intern();
    private static final String SUBOPENING = "${".intern();

    public static void prepareProperties(final Properties properties, final String key) {
        final String value = properties.getProperty(key);
        if (value == null)
            return;
        final List splitList = new ArrayList(LookupUtils.splitList(value, SUBOPENING, SUBCLOSING));

        do {
            final int indexOf = splitList.indexOf(SUBCLOSING);
            if (indexOf > 2 && SUBOPENING.equals(splitList.get(indexOf - 2))
                    && !SUBOPENING.equals(splitList.get(indexOf - 1))) {
                final String randomString = 'p' + LookupUtils.randomString();
                properties.setProperty(randomString, PropertiesUtils.getTrimmed(splitList, indexOf - 1));
                final boolean b = splitList.size() > indexOf + 1;
                final String trimmed = PropertiesUtils.getTrimmed(splitList, indexOf - 3) + ' ' + randomString
                        + (b ? ' ' + PropertiesUtils.getTrimmed(splitList, indexOf + 1) : "");
                splitList.set(indexOf - 3, trimmed.trim());
                splitList.subList(indexOf - 2, indexOf + (b ? 2 : 1)).clear();
            } else
                break;
        } while (true);
        properties.setProperty(key, LookupUtils.stringify(splitList));
    }

    public static void prepareProperties(final Properties properties) {
        final ArrayList arrayList = Collections.list(properties.propertyNames());
        for (final Iterator iterator = arrayList.iterator(); iterator.hasNext();)
            prepareProperties(properties, (String) iterator.next());
        for (final Iterator iterator = arrayList.iterator(); iterator.hasNext();) {
            final String key = (String) iterator.next();
            if (RANGE_PATTERN.matcher(key).find())
                processKey(properties, key, properties.getProperty(key));
        }
    }

    private static void processKey(final Properties properties, final String key, final String value) {
        final Matcher matcher = RANGE_PATTERN.matcher(key);
        if (matcher.find()) {
            final int i2 = Integer.parseInt(matcher.group(2));
            for (int i = Integer.parseInt(matcher.group(1)); i <= i2; i++)
                processKey(properties, matcher.replaceFirst("#" + i), value);
        } else
            properties.setProperty(key, value);
    }

    public static Properties getSubproperties(final Properties properties, final String key) {
        final String keyd = key + '.';
        final String prototype = properties.getProperty(keyd + "prototype");
        final Properties res = new Properties(prototype != null ? getSubproperties(properties, prototype) : null);
        final Enumeration propertyNames = properties.propertyNames();
        boolean mod = false;
        while (propertyNames.hasMoreElements()) {
            final String name = (String) propertyNames.nextElement();
            if (name.startsWith(keyd)) {
                mod = true;
                res.setProperty(name.substring(keyd.length()), properties.getProperty(name));
            }
        }
        return mod ? res : null;
    }

    private static String getTrimmed(final List arrayList, final int index) {
        return String.valueOf(arrayList.get(index)).trim();
    }

}
