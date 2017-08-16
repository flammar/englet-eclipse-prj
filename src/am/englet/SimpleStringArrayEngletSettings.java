/**
 * 30.10.2009
 *
 * 1
 *
 */
package am.englet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;

import am.englet.MethodsStorage.MethodRecord;
import am.englet.cast.ClassPool;

/**
 * @author 1
 * 
 */
public class SimpleStringArrayEngletSettings implements EngletSettings {
    final String[] args;
    private final Properties props;

    public SimpleStringArrayEngletSettings(final Properties props) {
        this(new String[0], props);
    }

    public SimpleStringArrayEngletSettings(final String[] args, final Properties props) {
        super();
        this.args = args;
        this.props = props;
    }

    public SimpleStringArrayEngletSettings() {
        this(new String[0], getProperties());
    }

    public SimpleStringArrayEngletSettings(final String[] args) {
        this(args, getProperties());
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.EngletSettings#setupEnglet(am.englet.Englet)
     */
    public void apply(final Englet englet) throws Exception {
        // props = getProperties();
        // System.out.println(props.getProperty("englet.debug"));
        Englet.debug = Boolean.valueOf(props.getProperty("englet.debug", "false")).booleanValue();
        // System.out.println("apply:Englet.debug:" + Englet.debug);
        adaptClasses(englet, props.getProperty("englet.processing.classes", ""));
        adaptMethods(englet, props.getProperty("englet.processing.methods.additional", "|,"));
        englet.parse(getPre());
        final StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < args.length; i++)
            (i == 0 ? stringBuffer : stringBuffer.append(' ')).append(args[i]);
        englet.parse(stringBuffer.toString());
        // englet.setCastingContext(prepareCastingContext());
        // props.getProperty("englet.casting.context",
        // "false")
    }

    public String getPre() {
        return props.getProperty("englet.pre", "");
    }

    // private CastingContext prepareCastingContext() {
    // final CastingContextImpl1 res = new CastingContextImpl1();
    // res.add(new ForkCaster(CharSequence.class, null, res.getImplementor(
    // CharSequence.class, Object.class)));
    // res.add(new ForkCaster(StringBuffer.class, null, res.getImplementor(
    // StringBuffer`1`.class, Object.class)));
    // return res;
    //
    // }

    private void adaptClasses(final Englet englet, final String property) {
        final StringTokenizer st = new StringTokenizer(property);
        while (st.hasMoreTokens())
            Management.adaptClass(st.nextToken(), englet.getMethods(), MethodRecord.Type.PROCESSING, (ClassPool) englet
                    .getSingleton(ClassPool.class));
    }

    /**
     * @param englet
     * @param description
     *            String in format: <descriprtions delimimter>(<qualified
     *            classname><delimiter><match regexp>[<delimiter><keystring>])+
     */
    private void adaptMethods(final Englet englet, final String description) {
        final String del1 = description.substring(0, 1), del2 = description.substring(1, 2);
        final StringTokenizer tok = new StringTokenizer(description.substring(2), del1);
        while (tok.hasMoreElements())
            adaptMethod(englet, tok.nextToken(), del2);

    }

    /**
     * @param englet
     * @param description
     *            String in format: <qualified classname><delimiter><match
     *            regexp><delimiter>[<keystring>]
     * @param delimiter
     */
    private void adaptMethod(final Englet englet, final String description, final String delimiter) {
        final StringTokenizer tok = new StringTokenizer(description, delimiter);
        final String className = tok.nextToken();
        try {
            final String mask = tok.nextToken();
            Management.adapt_method(englet.getMethods(), Class.forName(className), mask, tok.hasMoreTokens() ? tok
                    .nextToken() : mask);
        } catch (final Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private static Properties getProperties() {
        try {
            return Utils.props(new FileInputStream(propsFile()));
        } catch (final Exception e) {
        }
        try {
            return Utils.props(propsURLInput().openStream());
        } catch (final Exception e) {
        }
        try {
            return Utils.props(SimpleStringArrayEngletSettings.class.getResourceAsStream("englet.properties"));
        } catch (final Exception e) {
        }
        try {
            return Utils.props(SimpleStringArrayEngletSettings.class.getResourceAsStream("/englet.properties"));
        } catch (final Exception e) {
        }
        return new Properties(System.getProperties());
    }

    private static File propsFile() {
        final String property = System.getProperty("englet.properties.file");
        final File file = new File(property);
        final File file2 = file.exists() && file.canRead() ? file : null;
        return file2;
    }

    private static URL propsURLInput() throws IOException {
        final String property = System.getProperty("englet.properties.url");
        return new URL(property);
    }

}
