package am.englet.lookup;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import am.englet.$;
import am.englet.ArgumentProvider;
import am.englet.CodeBlock;
import am.englet.FieldGetInvokable;
import am.englet.FieldSetInvokable;
import am.englet.Invokable;
import am.englet.Links;
import am.englet.Links.ValueConverter;
import am.englet.Management;
import am.englet.MethodInvokable;
import am.englet.MethodsStorage;
import am.englet.ServiceObject;
import am.englet.Utils;
import am.englet.VariablesStorage;
import am.englet.cast.ClassPool;
import am.englet.reflect.MemberInvokable;

public class Lookup {

    public static final LookUpMethodOfClassParameterResolver LOOK_UP_METHOD_OF_CLASS_PARAMETER_RESOLVER = new LookUpMethodOfClassParameterResolver();
    public static final ValueConverter LOOK_UP_METHOD_OF_CLASS_PARAMETER_RESOLVER_EXTENDED = new LookUpMethodOfClassParameterResolverExtended();
    public static final Class[] ARRAY_COMPONENT_CLASSES = new Class[] { Object.class, byte.class, short.class,
            char.class, int.class, float.class, long.class, double.class };
    public static final int MethodBasicMaxArgCount = 5;
    public static final int MethodNonBasicMaxArgCount = 5;
    public static final int InstantiatorMaxArgCount = 3;
    public static final int SetOrAddMaxArgCount = 2;
    public static final int CollectionsMethodMaxArgCount = 3;
    public static final int TypedMaxArgCount = 4;
    public static final int StaticMethodMaxArgCount = 4;
    private static Set staticClasses = new HashSet(Arrays.asList(new Class[] { System.class, Array.class, Arrays.class,
            Collections.class }));
    private static Properties lookUpProperties;

    public static boolean lookUp(final MethodsStorage methodsStorage, final String command,
            final ArgumentProvider prov, final ClassPool classPool) {
        Utils.debug(System.out, "Lookup.lookUp():", command);
        final LookupContext context = new LookupContext(methodsStorage, command, prov, classPool);
        final boolean processCollection = processIterator(context, getAsCollection(getLookUpProperties(), "lookers-up",
                classPool).iterator());
        return processCollection
                ||
                // Lookup.lookUpMethodBasic(context)
                // || Lookup.lookUpInstantiator(methodsStorage, command, prov,
                // classPool, "-java.awt") ||
                // Lookup.lookupMethodNonBasic(context) ||
                Lookup.lookUpInstantiator(context) || Lookup.lookUpGetter(context) || Lookup.lookUpSetter(context)
                || Lookup.lookUpStatic(context) || Lookup.lookUpCollections(context)
                || Lookup.lookUpImportedStatic(context) || Lookup.lookUpCaster(context)
                || Lookup.lookUpSetOrAdd(context) || Lookup.lookUpCommandAccParamType0(context)
                || Lookup.lookUpConstantProxy(context);
    }

    private static Collection getAsCollection(final Properties properties, final String key, final ClassPool classPool) {
        final String val = properties.getProperty(key);
        if (val == null)
            return Collections.EMPTY_SET;
        // TODO to recursively proxy
        return asCandidateConverters(asStringList(val), classPool);
    }

    private static Collection asCandidateConverters(final List stringList, final ClassPool classPool) {
        final List converting = Utils.converting(stringList, new StringToCandidateConverterConverter(classPool));
        return Utils.cachingProxy(converting);
    }

    private static List asStringList(final String val) {
        return Arrays.asList(val.trim().split("(\\s+|,)"));
    }

    private static Properties getLookUpProperties() {
        final Properties propertiesByResource = Utils.getPropertiesByResource(Lookup.class, "lookup.properties");
        PropertiesUtils.prepareProperties(propertiesByResource);
        return propertiesByResource;
    }

    private static boolean processIterator(final Candidate candidate, final Iterator iterator) {
        Utils.debug(null, "processIterator:start:", "");
        for (; iterator.hasNext();) {
            final Object next = iterator.next();
            Utils.debug(null, "processIterator:next:", next);
            final CandidateConverter converter = getCandidateConverter(next, candidate);
            Utils.debug(null, "processIterator:converter:", converter);
            final boolean tryConverter = converter != null && tryConverter(candidate, converter, iterator);
            if (tryConverter) {
                Utils.debug(null, "processIterator:to return true:", "");
                return true;
            }
        }
        Utils.debug(null, "processIterator:to return false:", "");
        return false;
    }

    private static CandidateConverter getCandidateConverter(final Object object, final Candidate candidate) {
        if (object instanceof CandidateConverter)
            return (CandidateConverter) object;
        if (object instanceof String)
            try {
                return stringToCandidateConverter(candidate.getInitialLookupContext().classPool, (String) object);
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return null;
    }

    private static CandidateConverter stringToCandidateConverter(final ClassPool classPool, final String string)
            throws InstantiationException, IllegalAccessException {
        final CandidateConverter object = (CandidateConverter) CANDIDATE_CONVERTERS.get(string);
        if (object != null)
            return object;
        else
            synchronized (CANDIDATE_CONVERTERS) {
                final CandidateConverter converter = getCandidateConverterInstance(classPool, string);
                CANDIDATE_CONVERTERS.put(string, converter);
                return converter;
            }
    }

    private static CandidateConverter getCandidateConverterInstance(final ClassPool classPool, final String name)
            throws InstantiationException, IllegalAccessException {
        {
            final String resolvedToEnd = resolveToEnd(name, getProperties());
            final List stringList = asStringList(resolvedToEnd);
            Utils.debug(null, "stringList: ", stringList);
            Utils.debug(null, "stringList.size(): ", new Integer(stringList.size()));
            if (stringList.size() > 1)
                return new StringListToCandidateConverterListConverter(stringList, name, classPool, resolvedToEnd);
            final Class forName = stringList.isEmpty() ? null : forName(classPool, String.valueOf(stringList.get(0)));
            Utils.debug(null, "forName: ", resolvedToEnd, " -> ", forName);
            if (forName != null)
                return customized((CandidateConverter) forName.newInstance(), name);
            final $ $ = new $(Lookup.class, Field.class);
            final Field field = $.field(new String[] { resolvedToEnd, name });
            Utils.debug(null, "Field: ", field);

            try {
                if (field != null && Modifier.isStatic(field.getModifiers()))
                    if (CandidateConverter.class.isAssignableFrom(field.getType())) {
                        final CandidateConverter candidateConverter = (CandidateConverter) field.get(null);
                        Utils.debug(null, "as field: ", resolvedToEnd, "(", name, ") -> ", candidateConverter);
                        return candidateConverter;
                    } else if (ValueConverter.class.isAssignableFrom(field.getType())) {
                        final ValueConverter valueConverter = (ValueConverter) field.get(null);
                        final ValueConverterBasedCandidateConverter candidateConverter = new ValueConverterBasedCandidateConverter(
                                valueConverter);
                        Utils.debug(null, "as wrapped field: ", resolvedToEnd, "(", name, ") -> ", valueConverter);
                        return candidateConverter;
                    }
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                Utils.debug(null, "as field: ", resolvedToEnd, "(", name, ") failed ");
                e.printStackTrace();
            }
        }
        {
            final Properties properties = getProperties();
            final CandidateConverter candidateConverter = getCandidateConverter(classPool, properties, name);
            if (candidateConverter != null)
                return candidateConverter;
        }
        return null;
    }

    private static String resolveToEnd(final String name, final Properties properties) {
        final String val = properties.getProperty(name);
        Utils.debug(null, "resolveToEnd0: ", name, " -> ", val);
        final String res0 = val != null ? resolveToEnd(val, properties) : name;
        Utils.debug(null, "resolveToEnd: ", name, " -> ", res0);
        return res0;
    }

    private static CandidateConverter customized(final CandidateConverter newInstance, final String name) {
        if (newInstance instanceof Customizable) {
            final Properties properties = getProperties();
            ((Customizable) newInstance).customize(PropertiesUtils.getSubproperties(properties, name));
        }
        return newInstance;
    }

    private static CandidateConverter getCandidateConverter(final ClassPool classPool, final Properties properties,
            final String key) {
        // PropertiesUtils.prepareProperties(properties, key);
        // TODO Auto-generated method stub
        return null;
    }

    private static Properties getProperties() {
        if (lookUpProperties == null)
            lookUpProperties = new Properties(getLookUpProperties());
        return lookUpProperties;
    }

    private static boolean tryConverter(final Candidate candidate, final CandidateConverter converter,
            final Iterator iterator) {
        final Object convert = converter.convert(candidate);
        Utils.debug(null, "tryConverter:convert.getClass():", (convert != null ? (Object) convert.getClass() : "null"));
        if (Boolean.TRUE.equals(convert))
            return true;
        else {
            final LookupContext initialLookupContext = candidate.getInitialLookupContext();
            if (convert instanceof Invokable) {
                Utils.debug(null, "convert instanceof Invokable", "");
                final Invokable invokable = ((Invokable) convert);
                tryToAdapt(initialLookupContext, invokable, "");
                return true;
            } else if (convert instanceof Candidate) {
                Utils.debug(null, "convert instanceof Candidate", "");
                return processIterator((Candidate) convert, iterator);
            } else if (convert instanceof Candidate[]) {
                Utils.debug(null, "convert instanceof Candidate[]", "");
                if (!iterator.hasNext())
                    return false;
                // TODO onI jav Kash fen Werk
                final CandidateConverter converter2 = (CandidateConverter) iterator.next();
                final Iterator iterator2 = Arrays.asList((Candidate[]) convert).iterator();
                final boolean processIterator = processIterator(candidate, new CandidateArrayWithSameConverterIterator(
                        iterator2, converter2));
                if (processIterator)
                    return true;
            } else if (convert instanceof Collection) {
                Utils.debug(null, "convert instanceof Collection", "");
                final boolean processCollection = processIterator(candidate, ((Collection) convert).iterator());
                if (processCollection)
                    return true;
            } else {
                Utils.debug(null, "convert not recognized.", " To describe fail.");
                final Object describeFail = converter.describeFail(initialLookupContext);
                if (describeFail instanceof CodeBlock)
                    tryToAdapt(initialLookupContext, null, ((CodeBlock) describeFail));
                else
                    tryToAdapt(initialLookupContext, null, "" + describeFail);
            }
        }
        return false;
    }

    private static Class forName(final ClassPool classPool, final String string) {
        final Class forName = classPool.forName(string);
        if (forName != null)
            return forName;
        final String name = Lookup.class.getPackage().getName();
        return classPool.forName(name + '.' + string);
    }

    public static void importStatic(final Class cl) {
        final Method[] methods = cl.getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            if (!method.getDeclaringClass().equals(Object.class) && !Modifier.isStatic(method.getModifiers()))
                return;
            Lookup.staticClasses.add(cl);
        }
    }

    private static final class CandidateArrayWithSameConverterIterator implements Iterator {
        private static final class ConstantCandidateCandidateConverter implements CandidateConverter {
            private final Candidate next;

            private ConstantCandidateCandidateConverter(final Candidate next) {
                this.next = next;
            }

            public Object describeFail(final LookupContext lookupContext) {
                return "Constant CandidateConverter for " + next;
            }

            public Object convert(final Candidate candidate) {
                return next;
            }
        }

        private final Iterator iterator2;
        private final CandidateConverter converter2;
        private boolean even = true;
        private boolean bad = false;

        private CandidateArrayWithSameConverterIterator(final Iterator iterator2, final CandidateConverter converter2) {
            this.iterator2 = iterator2;
            this.converter2 = converter2;
        }

        public void remove() {
        }

        public Object next() {
            if (bad)
                return null;
            final Object result = even ? new ConstantCandidateCandidateConverter((Candidate) iterator2.next())
                    : converter2;
            even = !even;
            return result;
        }

        public boolean hasNext() {
            if (bad)
                return false;
            final boolean good = !even || iterator2.hasNext();
            if (!good)
                bad = true;
            return good;
        }
    }

    private static final class StringListToCandidateConverterListConverter implements CandidateConverter {
        private final List stringList;
        private final String name;
        private final ClassPool classPool;
        private final String resolvedToEnd;

        private StringListToCandidateConverterListConverter(final List stringList, final String name,
                final ClassPool classPool, final String resolvedToEnd) {
            this.stringList = stringList;
            this.name = name;
            this.classPool = classPool;
            this.resolvedToEnd = resolvedToEnd;
        }

        public Object describeFail(final LookupContext lookupContext) {
            return '[' + name + "=+>" + resolvedToEnd + ']';
        }

        public Object convert(final Candidate candidate) {
            return asCandidateConverters(stringList, classPool);
        }
    }

    private static final class BreededLookUpMethodOfClassParameterList extends AbstractList {
        private final LookUpMethodOfClassParameter pO;
        private final int size;

        private BreededLookUpMethodOfClassParameterList(final LookUpMethodOfClassParameter pO, final int size) {
            this.pO = pO;
            this.size = size;
        }

        public int size() {
            return size - 1;
        }

        public Object get(final int index) {
            return castToFromIndexNonStatic(pO, index + 1);
        }
    }

    private static final class StringToCandidateConverterConverter implements ValueConverter {
        private final ClassPool classPool;
        private static final long serialVersionUID = 1L;

        private StringToCandidateConverterConverter(final ClassPool classPool) {
            this.classPool = classPool;
        }

        public Object convert(final Object object) {
            final String s = (String) object;
            try {
                return stringToCandidateConverter(classPool, s);
            } catch (final InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

    private static final class LookUpMethodOfClassParameterResolverExtended implements ValueConverter {
        private static final long serialVersionUID = 3117642203203477498L;

        public Object convert(final Object object) {
            return Lookup.lookUpMethod((LookUpMethodOfClassParameter) object);
        }
    }

    private static final class LookUpMethodOfClassParameterResolver implements ValueConverter {
        private static final long serialVersionUID = -3210073501704756185L;

        public Object convert(final Object object) {
            final LookUpMethodOfClassParameter parameterObject = (LookUpMethodOfClassParameter) object;
            Utils.debug(null, new CodeBlock() {

                public Object result() {
                    final String mn = "LookUpMethodOfClassParameterResolver";
                    return new Object[] { mn, ":name:", parameterObject.getMethodName(), Lookup.CR, mn, ":classes:",
                            parameterObject.getArgClassesList() };
                }
            });
            final Method extracted = (Method) Lookup.lookUpMethodOfClass(parameterObject);
            return extracted;
        }
    }

    public static class LookUpMethodOfClassParameter implements Candidate {
        private final int staticFlagMask;
        private final Class ofWhat;
        private final List argClassesList;
        private final Class retClass;
        private final String methodName;
        private final boolean checkAssignable;
        private final LookupContext context;

        public LookUpMethodOfClassParameter(final Candidate candidate, final int staticFlagMask, final Class ofWhat,
                final List argClassesList, final Class retClass, final String methodName, final boolean checkAssignable) {
            context = candidate.getInitialLookupContext();
            this.staticFlagMask = staticFlagMask;
            this.ofWhat = ofWhat;
            // unsafe!!
            this.argClassesList = argClassesList;
            this.retClass = retClass;
            this.methodName = methodName;
            this.checkAssignable = checkAssignable;
        }

        public int getStaticFlagMask() {
            return staticFlagMask;
        }

        public Class getOfWhat() {
            return ofWhat;
        }

        public List getArgClassesList() {
            return argClassesList;
        }

        public Class getRetClass() {
            return retClass;
        }

        public String getMethodName() {
            return methodName;
        }

        public boolean isCheckAssignable() {
            return checkAssignable;
        }

        public LookupContext getInitialLookupContext() {
            return context;
        }
    }

    private static Member lookUpMethodOfClass(final LookUpMethodOfClassParameter parameterObject) {
        final boolean isStringClass = String.class.equals(parameterObject.getOfWhat());
        final boolean isArray = parameterObject.getOfWhat().isArray();
        final Method[] methods = parameterObject.getOfWhat().getMethods();

        final Member res = lookUpMethodOfClass(parameterObject.getArgClassesList(), parameterObject.getMethodName(),
                parameterObject.getRetClass(), parameterObject.isCheckAssignable(), isStringClass, methods,
                parameterObject.getStaticFlagMask() & Modifier.STATIC, isArray);
        Utils.debug(System.out, new CodeBlock() {

            public Object result() {
                return Arrays.asList(new Object[] { "lookUpMethodOfClass: result: ", res,
                        " <= staticFlagMask: " + parameterObject.getStaticFlagMask() + ", argClasses: ",
                        parameterObject.getArgClassesList(), ", ofWhat: ", parameterObject.getOfWhat(),
                        ", methodName: ", parameterObject.getMethodName(), ", retClass: ",
                        parameterObject.getRetClass(), ", checkAssignable: " + parameterObject.isCheckAssignable() });
            }
        });
        return res;
    }

    private static Member lookUpMethodOfClass(final List argClassesList, final String methodName, final Class retClass,
            final boolean checkAssignable, final boolean isStringClass, final Method[] methods,
            final int staticFlagMask1, final boolean isArray) {
        for (int i = 0; i < methods.length; i++) {
            final Method m = methods[i];
            if (isArray && m.getName().equals("toString"))
                continue;
            if ((retClass == null || (checkAssignable ? retClass.isAssignableFrom(m.getReturnType()) : retClass
                    .equals(m.getReturnType())))
                    && Lookup.checkMethod(m, staticFlagMask1, argClassesList, isStringClass, methodName))
                return m;
        }
        return null;
    }

    private static boolean checkMethod(final Method m, final int staticFlagMask, final List ccl,
            final boolean isStringClass, final String name) {
        return (ccl == null || (isStringClass ? Lookup.stringClassMethodParamTypesEqual(ccl, m) : Lookup.paramTypesFit(
                ccl, m)))
                && (name == null || name.equals(m.getName()))
                && (m.getModifiers() & Modifier.STATIC) == (staticFlagMask & Modifier.STATIC);
    }

    private static boolean paramTypesFit(final List ccl, final Method m) {
        final Class[] parameterTypes = (m).getParameterTypes();
        int j = 0;
        final int[] ix = new int[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++)
            if (!ServiceObject.class.isAssignableFrom(parameterTypes[i]))
                ix[j++] = i;
        final Class[] paramTypes2 = new Class[j];
        for (int i = 0; i < j; i++)
            paramTypes2[i] = parameterTypes[ix[i]];
        if (ccl.size() != paramTypes2.length)
            return false;
        else
            for (int i = 0; i < paramTypes2.length; i++) {
                final Class from = (Class) ccl.get(i);
                if ((from != null) && !Utils.isCastable(paramTypes2[i], from))
                    return false;
            }
        return true;
    }

    /**
     * @param to
     * @param from
     * @return
     * @deprecated Use {@link Utils#isCastable(Class,Class)} instead
     */
    public static boolean isCastable(final Class to, final Class from) {
        return Utils.isCastable(to, from);
    }

    private static boolean stringClassMethodParamTypesEqual(final List ccl, final Method m) {
        final Class[] paramTypes2 = (m).getParameterTypes();
        final int length = ccl.size();
        if (length != paramTypes2.length)
            return false;
        else
            for (int i = 0; i < paramTypes2.length; i++) {
                final Class from = Utils.deprimitivized((Class) ccl.get(i));
                // if null provided then do not check <= BSH
                final Class to = Utils.deprimitivized(paramTypes2[i]);
                if ((from != null) && !to.equals(from) && !(from.equals(StringBuffer.class) && to.equals(String.class)))
                    return false;
            }
        return true;
    }

    /**
     * @param command
     * @param types
     * @return
     */
    public static MemberInvokable lookUpSetterInvokable(final String command, final Class[] types) {
        if (types.length > 1) {
            final String camelCaseFieldName = Lookup.underscoredToMemberName(command);
            final Class objCls = types[types.length - 2], fldValCls = types[types.length - 1];
            final DataProvider[] nps = new DataProvider[] { new DataProvider() {

                public Object provide() {
                    return camelCaseFieldName;
                }
            }, new DataProvider() {

                public Object provide() {
                    return command.toUpperCase();
                }
            } };
            // public, setter, declared
            return (MemberInvokable) Lookup.firstNotNull(new DataProvider[] {
                    Lookup.fieldSetInvokableProvider(objCls, fldValCls, nps, false, false), new DataProvider() {

                        public Object provide() {
                            final Method lookUpMethod = am.englet.reflect.Utils.lookUpSetterMethod(objCls,
                                    camelCaseFieldName, fldValCls);
                            return lookUpMethod != null ? new MethodInvokable(Lookup.correct(lookUpMethod)) : null;
                        }
                    }, Lookup.fieldSetInvokableProvider(objCls, fldValCls, nps, true, true) });
        }
        return null;
    }

    private static DataProvider fieldSetInvokableProvider(final Class declaringClass, final Class valueType,
            final DataProvider[] dps, final boolean declaredOnly, final boolean includeDeclared) {
        return new DataProvider() {

            public Object provide() {
                final Enumeration n = Lookup.dataProvidersEnumeration(dps);
                while (n.hasMoreElements()) {
                    final Field lookUpField = am.englet.reflect.Utils.lookUpField(declaringClass, n.nextElement()
                            .toString(), false, includeDeclared, declaredOnly);
                    if ((lookUpField != null)
                            && (lookUpField.getType().isAssignableFrom(valueType) || (!valueType.equals(String.class)
                                    && CharSequence.class.isAssignableFrom(valueType) && lookUpField.getType().equals(
                                    String.class))))
                        return new FieldSetInvokable(lookUpField);
                }
                return null;
            }
        };
    }

    public static MemberInvokable lookUpGetterInvokable(final String command, final Class[] types) {
        MemberInvokable invokable = null;
        main: {
            if (types.length > 0) {
                final String name = Lookup.underscoredToMemberName(command);
                final Class cls = types[0];
                {
                    final Field lookUpField1 = am.englet.reflect.Utils.lookUpField(cls, name, false, false, false);
                    if ((lookUpField1 != null)) {
                        invokable = new FieldGetInvokable(lookUpField1);
                        break main;
                    }
                    final Field lookUpField3 = am.englet.reflect.Utils.lookUpField(cls, command.toUpperCase(), false,
                            false, false);
                    if ((lookUpField3 != null)) {
                        invokable = new FieldGetInvokable(lookUpField3);
                        break main;
                    }
                }
                final Method lookUpGetterMethod = am.englet.reflect.Utils.lookUpGetterMethod(cls, name);
                if (lookUpGetterMethod != null) {
                    invokable = new MethodInvokable(Lookup.correct(lookUpGetterMethod));
                    break main;
                }
                {
                    final Field lookUpField1 = am.englet.reflect.Utils.lookUpField(cls, name, false, true, true);
                    if ((lookUpField1 != null)) {
                        invokable = new FieldGetInvokable(lookUpField1);
                        break main;
                    }
                    final Field lookUpField3 = am.englet.reflect.Utils.lookUpField(cls, command.toUpperCase(), false,
                            true, true);
                    if ((lookUpField3 != null)) {
                        invokable = new FieldGetInvokable(lookUpField3);
                        break main;
                    }
                }
            }
        }
        return invokable;
    }

    private static Method correct(final Method m) {
        final Class cl = m.getDeclaringClass();
        if (Utils.isPublic(cl))
            return m;
        final String[] names = new String[] { m.getName() };
        final Class[] parameterTypes = m.getParameterTypes();
        final Method m2 = Lookup.searchMethod(cl, names, parameterTypes);
        return m2 != null ? m2 : m;
    }

    private static Iterator getPossibleClassNameStarts(final Class[] types, int cnt) {
        final int length = types.length;
        if (cnt < 1)
            cnt = length;
        final List list = new ArrayList();
        for (int i = length - cnt; i < length; i++) {
            final Class class1 = types[i];
            if (class1 == null)
                continue;
            final Matcher matcher = Lookup.PART_PATTERN.matcher(Utils.simpleName(class1));// ...[Ab][Cd][Ef][Gh]
            final int base = list.size();
            while (matcher.find()) {
                final String group = matcher.group(0);// [Ef]
                list.add(base, group);
                // AbCdEf,CdEf,Ef - ByteArrayOutputStream, ArrayOutputStream,
                // OutputStream, Stream
                for (int j = base + 1; j < list.size(); j++)
                    list.set(j, list.get(j).toString() + group);
            }
        }
        return list.iterator();
    }

    private static final Pattern PART_PATTERN = Pattern.compile("[A-Z][0-9a-z]*");

    private static Method searchMethod(final Class cl, final String[] names, final Class[] parameterTypes) {
        final Method m1 = Lookup.findMethod(names, parameterTypes, cl);
        if (m1 == null)
            return null;
        final boolean public1 = Utils.isPublic(m1.getDeclaringClass());
        if (public1)
            return m1;
        final Class superclass = cl.getSuperclass();
        if (superclass != null) {
            final Method m2 = Lookup.searchMethod(superclass, names, parameterTypes);
            if (m2 != null)
                return m2;
        }
        final Class[] interfaces = cl.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            final Method m2 = Lookup.searchMethod(interfaces[i], names, parameterTypes);
            if (m2 != null)
                return m2;
        }
        return public1 ? m1 : null;
    }

    /**
     * @param names
     * @param parameterTypes
     * @param superclass
     * @return
     */
    private static Method findMethod(final String[] names, final Class[] parameterTypes, final Class superclass) {
        final Method method = new $(superclass, Method.class).method(Modifier.STATIC, 0, parameterTypes, names);
        if ((method != null) && Utils.isPublic(method.getDeclaringClass()))
            return method;
        return method;
    }

    private static boolean lookUpCaster(final LookupContext context) {
        final boolean b = Lookup.lookUpCaster(context, Lookup.underscoredToClassName(context.command))
                || Lookup.lookUpCaster(context, context.command.toUpperCase());
        if (!b)
            Utils.debug(System.out, "Lookup.lookUpCaster() failed", "");
        return b;
    }

    private static boolean lookUpCaster(final LookupContext context, final String className) {
        final Class cls = context.classPool.forName(className);
        return Lookup.lookUpCaster(context, cls);
    }

    private static boolean lookUpCaster(final LookupContext context, final Class cls) {
        if (cls == null)
            return false;
        final int n = 1;
        final Class[] types = context.getNNextArgumentTypes(n);
        return (types.length > 0) && Lookup.tryToAdapt2(context, cls, types[0]);
    }

    private static boolean tryToAdapt2(final LookupContext context, final Class dstType, final Class srcType) {
        final Method method = findCaster(srcType, dstType);
        if (method != null) {
            Lookup.adaptInvokable(context, new MethodInvokable(method));
            return true;
        }
        return false;
    }

    private static Method findCaster(final Class srcType, final Class dstType) {
        final Method method = new $(srcType) {
            public boolean check(final Method mtd) {
                return mtd.getReturnType().equals(dstType);
            }
        }.method(Modifier.STATIC, 0, new Class[0]);
        return method;
    }

    private static boolean lookUpSetOrAdd(final LookupContext context) {
        final int setoraddmaxargcount2 = Lookup.SetOrAddMaxArgCount;
        final Class[] nNextArgumentTypes = context.getNNextArgumentTypes(setoraddmaxargcount2);
        if ((nNextArgumentTypes.length < 2) || !(context.command.equals("set") || context.command.equals("add")))
            return false;
        final Method m = findSetOrAddMethod(context, nNextArgumentTypes);
        return Lookup.tryToAdapt(context, m != null ? new MethodInvokable(m) : null, "Lookup.lookUpSetOrAdd() failed");
    }

    private static Method findSetOrAddMethod(final LookupContext context, final Class[] nNextArgumentTypes) {
        final Method m = new $(nNextArgumentTypes[0]) {

            public boolean check(final Method mtd) {
                final Class[] parameterTypes = mtd.getParameterTypes();
                final String name = mtd.getName();
                return name.startsWith(context.command)
                        && ((parameterTypes.length == 1) && parameterTypes[0].isAssignableFrom(nNextArgumentTypes[1]));
            }
        }.method(Modifier.STATIC, 0);
        return m;
    }

    private static boolean lookUpCommandAccParamType0(final LookupContext context) {
        return Lookup.tryToAdapt(context, lookUpCommandAccParamType00(context),
                "Lookup.lookUpCommandAccParamType0() failed");
    }

    private static MemberInvokable lookUpCommandAccParamType00(final LookupContext context) {
        if (context.command.indexOf('_') < 0)
            for (int n = 2; n <= Lookup.TypedMaxArgCount; n++) {
                final Method m = Lookup.lookUpCommandAccParamType0(context, n);
                if (m != null)
                    return new MethodInvokable(m);
            }
        return null;
    }

    private static Method lookUpCommandAccParamType0(final LookupContext context, final int n) {
        final Class[] nNextArgumentTypes = context.getNNextArgumentTypes(n);
        final Method m = nNextArgumentTypes.length < n ? null : findCommandAccParamType0(context.command, n - 1,
                nNextArgumentTypes[0], nNextArgumentTypes[1]);
        return m;
    }

    private static Method findCommandAccParamType0(final String command, final int paramCount, final Class targetClass,
            final Class firstParamClass) {
        final Method m = new $(targetClass) {

            public boolean check(final Method mtd) {
                final Class[] parameterTypes = mtd.getParameterTypes();
                final String name = mtd.getName();
                return name.startsWith(command)
                        && ((parameterTypes.length == paramCount) && parameterTypes[0]
                                .isAssignableFrom(firstParamClass))
                        && name.substring(command.length()).equals(Utils.simpleName(parameterTypes[0]));
            }
        }.method(Modifier.STATIC, 0);
        return m;
    }

    private static boolean lookUpConstantProxy(final LookupContext context) {
        return Lookup.tryToAdapt(context, lookUpConstantProxyItself(context), "Lookup.lookUpConstantProxy()");
    }

    private static Invokable lookUpConstantProxyItself(final LookupContext context) {
        if (!"set".equals(context.command)) {
            final Class cl = context.classPool.forName(Lookup.underscoredToClassName(context.command));
            if ((cl != null) && cl.isInterface())
                return Utils.constantProxyInvokable((VariablesStorage) (context.prov.getArgumentsAndTarget(null,
                        VariablesStorage.class).target()), cl, context.prov.getCastingContext());
        }
        return null;
    }

    private static boolean lookUpCollections(final LookupContext context) {
        final Class[] types = context.getNNextArgumentTypes(Lookup.CollectionsMethodMaxArgCount);
        final String memberName = Lookup.underscoredToMemberName(context.command);
        final LookUpContext2 context2 = Lookup.newLookUpContext2(context, types);
        for (int argCount = types.length; argCount >= 0; argCount--) {
            final boolean tryToLookUpCollectionsMethod = Lookup.tryToLookUpCollectionsMethod(context2, memberName,
                    argCount);
            if (tryToLookUpCollectionsMethod)
                return true;
        }
        return false;
    }

    private static boolean tryToLookUpCollectionsMethod(final LookUpContext2 context2, final String memberName,
            final int argCount) {
        if (argCount <= 0)
            return false;
        final Class[] types = context2.argTypes;
        final List argClassesList = lastArgClassesList(types, argCount);
        final Class cls = (Class) argClassesList.get(0);
        if (!(Collection.class.isAssignableFrom(cls) || Map.class.isAssignableFrom(cls)))
            return false;
        final Method method = doLookUpCollectionsMethod(context2, memberName, argClassesList);
        if (method != null) {
            Lookup.adaptInvokable(context2.context, Utils.toStatic(new MethodInvokable(Lookup.correct(method)), -1));
            return true;
        }
        return false;
    }

    private static Method doLookUpCollectionsMethod(final LookUpContext2 context2, final String memberName,
            final List argClassesList) {
        Utils.debug(null, new CodeBlock() {

            public Object result() {
                final String mn = "Lookup.lookUpCollectionsMethod()";
                return new Object[] { mn, ":name:", memberName, Lookup.CR, mn, ":types:", argClassesList };
            }
        });
        final Iterator iter1 = Lookup.iter1((Class) argClassesList.get(0));
        // ~"<SortedSet> unmodifiable @y contains"
        final ValueConverter c2p = new ValueConverter() {

            private static final long serialVersionUID = 7357724556615843530L;

            public Object convert(final Object next) {
                return new LookUpMethodOfClassParameter(context2, -1, Collections.class, argClassesList, null,
                        memberName + Utils.simpleName((Class) next), true);
            }
        };
        final Iterator createConvertingIterator = LookupUtils.createConvertingIterator(iter1, Utils.compose(c2p,
                Lookup.LOOK_UP_METHOD_OF_CLASS_PARAMETER_RESOLVER));

        final Method method = (Method) Utils.firstNotNullIfExists(createConvertingIterator);
        return method;
    }

    private static Class[] lastArgClasses(final Class[] types, final int argCount) {
        final Class[] argClasses = new Class[argCount];
        if (argCount > 0)
            System.arraycopy(types, types.length - argCount, argClasses, 0, argCount);
        return argClasses;
    }

    private static List lastArgClassesList(final Class[] types, final int argCount) {
        final List typesList = Arrays.asList(types);
        final int size = typesList.size();
        return typesList.subList(size - argCount, size);
    }

    private static boolean lookUpSetter(final LookupContext context) {
        final int n = 2;
        final MemberInvokable invokable2 = Lookup.lookUpSetterInvokable(context.command, context
                .getNNextArgumentTypes(n));
        return Lookup.tryToAdapt(context, invokable2, "Lookup.lookUpSetter()");
    }

    private static boolean lookUpGetter(final LookupContext context) {
        final int n = 1;
        final Invokable invokable = Lookup.lookUpGetterInvokable(context.command, context.getNNextArgumentTypes(n));
        return Lookup.tryToAdapt(context, invokable, "Lookup.lookUpGetter()");
    }

    private static boolean tryToAdapt(final LookupContext context, final MemberInvokable invokable, final String string) {
        if (invokable != null) {
            Lookup.adaptMemberInvokable(context, invokable);
            return true;
        }
        Utils.debug(System.out, string + " failed", "");
        return false;
    }

    private static void adaptMemberInvokable(final LookupContext context, final MemberInvokable invokable) {
        Lookup.adaptInvokable(context, invokable);
    }

    private static boolean lookUpInstantiator(final LookupContext context) {
        final String clsName = Lookup.underscoredToClassName(context.command);
        final List asList = Arrays.asList(new Object[] { context.classPool.forName(clsName),
                context.classPool.foundClassesIterator(clsName) });
        final Iterator flattenedIterator = LookupUtils.createFlattenedIterator(asList.iterator());
        final ValueConverter converter = new ValueConverter() {

            private static final long serialVersionUID = 1L;

            public Object convert(final Object object) {
                return Lookup.createLookUpInstantiator1Candidate(context, clsName, (Class) object);
            }
        };
        final Iterator createConvertingIterator = LookupUtils.createConvertingIterator(flattenedIterator, converter);
        final InvokableCandidate candidate = new IteratorBasedInvokableCandidate(createConvertingIterator);
        final Invokable realisation = candidate.getRealisation();
        final boolean tryToAdapt = Lookup.tryToAdapt(context, realisation, "lookUpInstantiator");
        return tryToAdapt;
    }

    private static InvokableCandidate createLookUpInstantiator1Candidate(final LookupContext context,
            final String clsName, final Class cls) {
        final Class[] types = context.getNNextArgumentTypes(Lookup.InstantiatorMaxArgCount);
        final int length = types.length;

        final AbstractList abstractList = new AbstractList() {

            public int size() {
                return length + 1;
            }

            public Object get(final int j) {
                return Lookup.createTryToAdapt1Candidate(context, clsName, cls, types, length - j);
            }
        };
        final IteratorBasedInvokableCandidate candidate0 = new IteratorBasedInvokableCandidate(abstractList.iterator());
        final InvokableCandidate candidate = LookupUtils.wrapToFailReporter(candidate0
        /* , "Utils.tryToAdapt1()" */);
        return candidate;
    }

    private static InvokableCandidate createTryToAdapt1Candidate(final LookupContext context, final String clsName,
            final Class cls, final Class[] types, final int i) {
        Utils.debug(System.out, "Utils.tryToAdapt1():i:", new Integer(i));
        Utils.debug(System.out, "Utils.tryToAdapt1():cls:", cls);
        final Class[] argClasses = Lookup.lastArgClasses(types, i);
        final InvokableCandidate createFailReportingCandidateProxy = LookupUtils.wrapToFailReporter(LookupUtils
                .getInstantiatorCandidate(cls, argClasses), LookupUtils.createInstantiatorCandidateFailDescription(cls,
                argClasses));
        final InvokableCandidate createBackedCandidate = LookupUtils.createBackedCandidate(
                createFailReportingCandidateProxy, Lookup.createPossibleClassNamesBasedCandidate(context, clsName,
                        types, i));
        return createBackedCandidate;
    }

    private static InvokableCandidate createPossibleClassNamesBasedCandidate(final LookupContext context,
            final String clsName, final Class[] types, final int cnt) {
        final Iterator possibleClassNameStarts = Lookup.getPossibleClassNameStarts(types, cnt);
        final Links.ValueConverter valueConverter = new Links.ValueConverter() {
            private static final long serialVersionUID = 1L;

            public Object convert(final Object object) {
                final String name = object.toString() + clsName;
                final Class cls = context.classPool.forName(name);
                final InvokableCandidate createFailReportingCandidateProxy = LookupUtils.wrapToFailReporter(LookupUtils
                        .getInstantiatorCandidate(cls, types), LookupUtils.createInstantiatorCandidateFailDescription(
                        cls, types));
                return createFailReportingCandidateProxy;
            }
        };
        final Iterator convertingIterator = LookupUtils.createConvertingIterator(possibleClassNameStarts,
                valueConverter);
        final IteratorBasedInvokableCandidate candidate = new IteratorBasedInvokableCandidate(convertingIterator);
        return candidate;
    }

    private static boolean lookUpStatic(final LookupContext context) {
        final String[] split = context.command.split("_0_", 2);
        if ((split.length < 2) || split[0].endsWith("_"))
            return false;
        split[0] = split[0].replaceAll("_1_", "\\$_");
        final Class cls = context.classPool.forName(Lookup.underscoredToClassName(split[0]));
        if (cls == null)
            return false;
        final String name = Lookup.underscoredToMemberName(split[1]);
        final String upperCase = split[1].toUpperCase();
        if (Lookup.lookUpStaticField(context, cls, name) || Lookup.lookUpStaticField(context, cls, upperCase))
            return true;
        return Lookup.tryToLookUpStaticMethod(context, name, cls);
    }

    private static boolean tryToLookUpStaticMethod(final LookupContext context, final String name, final Class cls) {
        final Invokable res = Lookup.lookUpStaticMethod(context, cls, name);
        return Lookup.tryToAdapt(context, res, Lookup.LOOK_UP_METHOD_OF_CLASS);
    }

    private static Invokable lookUpStaticMethod(final LookupContext context, final Class cls, final String name) {
        final int staticmethodmaxargcount2 = Lookup.StaticMethodMaxArgCount;
        final Class[] types = context.getNNextArgumentTypes(staticmethodmaxargcount2);
        final int staticFlagMask = -1;
        final Class retClass = null;
        final boolean checkAssignable = true;
        final List asList = Arrays.asList(types);
        final LookUpMethodOfClassParameter pO0 = new LookUpMethodOfClassParameter(context, staticFlagMask, cls, asList,
                retClass, name, checkAssignable);
        for (int argCount = types.length; argCount >= 0; argCount--) {
            // final Class[] argClasses = Lookup.lastArgClasses(types,
            // argCount);
            final LookUpMethodOfClassParameter parameterObject = castToArgCountStatic(pO0, argCount);
            final Invokable lookUpMethodOfClass = Lookup.lookUpMethod(parameterObject);
            if (lookUpMethodOfClass != null)
                return lookUpMethodOfClass;
        }
        Utils.debug(System.out, "Lookup.lookUpStaticMethod() failed", "");
        return null;
    }

    private static LookUpMethodOfClassParameter castToArgCountStatic(final LookUpMethodOfClassParameter pO0, final int i) {
        final int size = pO0.argClassesList.size();
        final LookUpMethodOfClassParameter parameterObject = new LookUpMethodOfClassParameter(pO0.context,
                pO0.staticFlagMask, pO0.ofWhat, pO0.argClassesList.subList(size - i, size), pO0.retClass,
                pO0.methodName, pO0.checkAssignable);
        return parameterObject;
    }

    private static LookUpMethodOfClassParameter castToFromIndexNonStatic(final LookUpMethodOfClassParameter pO,
            final int fromIndex) {
        final List lastArgClassesList = pO.argClassesList.subList(fromIndex, pO.argClassesList.size());
        final LookUpMethodOfClassParameter parameterObject = new LookUpMethodOfClassParameter(pO.context,
                pO.staticFlagMask, (Class) pO.argClassesList.get(fromIndex - 1), lastArgClassesList, pO.retClass,
                pO.methodName, pO.checkAssignable);
        return parameterObject;
    }

    private static Invokable lookUpMethod(final LookUpMethodOfClassParameter parameterObject) {
        final Method method = (Method) Lookup.lookUpMethodOfClass(parameterObject);
        final Method method2 = method != null || mustNotCheckThatOfString(parameterObject) ? method : (Method) Lookup
                .lookUpMethodOfClass(copyAsOfString(parameterObject));
        final Invokable staticInvokable = method2 != null
                && (!Utils.checkIfOfArray(method2) || ((Class) parameterObject.argClassesList.get(0)).isArray()) ? Utils
                .toStatic(new MethodInvokable(Lookup.correct(method2)), -1)
                : null;
        return staticInvokable;
    }

    private static boolean mustNotCheckThatOfString(final LookUpMethodOfClassParameter parameterObject) {
        return (parameterObject.staticFlagMask != 0) || parameterObject.ofWhat.equals(String.class)
                || !CharSequence.class.isAssignableFrom(parameterObject.ofWhat);
    }

    private static LookUpMethodOfClassParameter copyAsOfString(final LookUpMethodOfClassParameter parameterObject) {
        return new LookUpMethodOfClassParameter(parameterObject.context, 0, String.class,
                parameterObject.argClassesList, parameterObject.retClass, parameterObject.methodName,
                parameterObject.checkAssignable);
    }

    public static boolean tryToAdapt(final LookupContext context, final Invokable invokable,
            final CodeBlock descIfFailed) {
        if (invokable != null) {
            Lookup.adaptInvokable(context, invokable);
            return true;
        }
        Utils.debug(System.out, LookupUtils.stringify(descIfFailed.result()), " failed");
        return false;
    }

    public static boolean tryToAdapt(final LookupContext context, final Invokable invokable, final String descIfFailed) {
        final CodeBlock descIfFailed1 = new CodeBlock() {

            public Object result() {
                return descIfFailed + " failed";
            }
        };
        return Lookup.tryToAdapt(context, invokable, descIfFailed1);
        // if (invokable != null) {
        // Lookup.adaptInvokable(context, invokable);
        // return true;
        // }
        // Utils.debug(System.out, descIfFailed + " failed", "");
        // return false;
    }

    private static void adaptInvokable(final LookupContext context, final Invokable invokable) {
        Management.adapt_invokable(context.methodsStorage, invokable, context.command);
    }

    private static boolean lookUpStaticField(final LookupContext context, final Class cls, final String name) {
        Utils.debug(System.out, "Lookup.lookUpStaticField():", cls);
        Utils.debug(System.out, "Lookup.lookUpStaticField():", name);
        final Field lookUpField = am.englet.reflect.Utils.lookUpField(cls, name, true, true, false);
        if ((lookUpField != null)) {
            Lookup.adaptInvokable(context, new FieldGetInvokable(lookUpField));
            return true;
        }
        Utils.debug(System.out, "Lookup.lookUpStaticField() failed", "");
        return false;
    }

    private static boolean lookUpImportedStatic(final LookupContext context) {
        final String get_command = context.getCommandWithPrefix("get_");
        final String set_command = context.getCommandWithPrefix("set_");
        final String is_command = context.getCommandWithPrefix("is_");
        final String command = context.command;
        return Lookup.lookUpImportedStatic(context, command) || Lookup.lookUpImportedStatic(context, get_command)
                || Lookup.lookUpImportedStatic(context, set_command)
                || Lookup.lookUpImportedStatic(context, is_command);
    }

    private static boolean lookUpImportedStatic(final LookupContext context, final String command2) {
        final String underscoredToMemberName = Lookup.underscoredToMemberName(command2);
        final Iterator iterator = Lookup.staticClasses.iterator();
        while (iterator.hasNext()) {
            final Class next = (Class) iterator.next();
            if (Lookup.tryToLookUpStaticMethod(context, underscoredToMemberName, next))
                return true;
        }
        return false;
    }

    public static boolean lookUpMethodBasic(final LookupContext context) {
        Utils.debug(System.out, "Lookup.lookupMethodBasic():", context.command);
        final LookUpMethodOfClassParameter pO = new LookUpMethodOfClassParameter(context, 0, null, Arrays
                .asList(context.getNNextArgumentTypes(Lookup.MethodBasicMaxArgCount)), (Class) null, Lookup
                .underscoredToMemberName(context.command), false);
        return Lookup.lookUpNonStaticMethod0(pO);
    }

    private static Iterator iter1(final Class cls) {
        final Iterator iterator = new Iterator() {
            Class current = cls;
            final Class base = base();
            boolean wasnext = true;

            public void remove() {
            }

            private Class base() {
                final Class class1 = Collection.class.isAssignableFrom(cls) ? Collection.class : Map.class;
                // ntx();
                return class1;
            }

            private Class nxt() {
                final Class[] interfaces = current.getInterfaces();
                for (int j = 0; j < interfaces.length; j++)
                    if (base.isAssignableFrom(interfaces[j]))
                        return interfaces[j];
                return null;
            }

            public Object next() {
                if (wasnext && (current != null))
                    current = nxt();
                wasnext = true;
                return current;
            }

            public boolean hasNext() {
                if (wasnext)
                    current = nxt();
                wasnext = false;
                return current != null;
            }
        };
        return iterator;
    }

    public static boolean lookupMethodNonBasic(final LookupContext context) throws SecurityException {
        Utils.debug(System.out, "Lookup.lookupMethodNonBasic():", context.command);
        final int methodnonbasicmaxargcount2 = Lookup.MethodNonBasicMaxArgCount;
        final Class[] nNextArgumentTypes = context.getNNextArgumentTypes(methodnonbasicmaxargcount2);
        final LookUpContext2 context2 = Lookup.newLookUpContext2(context, nNextArgumentTypes);
        return Lookup.lookUpGetOrCreateOrSetAccCommandNamedMethod(context2)
                || Lookup.lookUpCommandAccTypeNamedMethod(context2)
                || Lookup.lookUpCommandNamedTypeReturningMethod(context2);
    }

    private static boolean lookUpCommandNamedTypeReturningMethod(final LookUpContext2 context2) {
        final Class retClass = context2.context.classPool.forName(Lookup
                .underscoredToClassName(context2.context.command));
        return retClass != null
                && (Lookup.lookUpNonStaticMethod0(new LookUpMethodOfClassParameter(context2.context, 0, null, Arrays
                        .asList(context2.argTypes), retClass, (String) null, false)) || Lookup
                        .lookUpNonStaticMethod0(new LookUpMethodOfClassParameter(context2.context, 0, null, Arrays
                                .asList(context2.argTypes), retClass, (String) null, true)));
    }

    private static boolean lookUpCommandAccTypeNamedMethod(final LookUpContext2 context2) {
        final Class[] nNextArgumentTypes = context2.argTypes;
        final Class retClass = null;
        final boolean checkAssignable = false;
        for (int l = 1; l <= 3; l++) {
            if (nNextArgumentTypes.length < l)
                return false;
            final Class class1 = nNextArgumentTypes[nNextArgumentTypes.length - l];
            if (class1 != null
                    && Lookup.lookUpNonStaticMethod0(new LookUpMethodOfClassParameter(context2.context, 0, null, Arrays
                            .asList(context2.argTypes), retClass, context2.context.command + Utils.simpleName(class1),
                            checkAssignable)))
                return true;
        }
        return false;
    }

    private static boolean lookUpNonStaticMethod0(final LookUpMethodOfClassParameter pO) {
        final List candidateList = breedCandidateNonStatic(pO);
        final Invokable result = (Invokable) Utils.firstNotNullIfExists(Utils.converting(candidateList,
                LOOK_UP_METHOD_OF_CLASS_PARAMETER_RESOLVER_EXTENDED).iterator());
        return Lookup.tryToAdapt(pO.context, result, Lookup.LOOK_UP_METHOD_OF_CLASS);
    }

    public static List breedCandidateNonStatic(final LookUpMethodOfClassParameter pO) {
        final int size = pO.argClassesList.size();
        final List candidateList = size <= 1 ? Collections.EMPTY_LIST : new BreededLookUpMethodOfClassParameterList(pO,
                size);
        return candidateList;
    }

    private static LookUpContext2 newLookUpContext2(final LookupContext context, final Class[] nNextArgumentTypes) {
        return new LookUpContext2(context, nNextArgumentTypes);
    }

    public static class LookUpContext2 implements Candidate {
        public LookupContext context;
        public Class[] argTypes;

        public LookUpContext2(final LookupContext context, final Class[] nNextArgumentTypes) {
            this.context = context;
            this.argTypes = nNextArgumentTypes;
        }

        public LookupContext getInitialLookupContext() {
            return context;
        }
    }

    private static boolean lookUpGetOrCreateOrSetAccCommandNamedMethod(final LookUpContext2 context2) {
        return (Lookup.lookUpNonStaticMethod0(new LookUpMethodOfClassParameter(context2.context, 0, null, Arrays
                .asList(context2.argTypes), (Class) null, Lookup.underscoredToMemberName(context2.context
                .getCommandWithPrefix("get_")), false))
                || Lookup.lookUpNonStaticMethod0(new LookUpMethodOfClassParameter(context2.context, 0, null, Arrays
                        .asList(context2.argTypes), (Class) null, Lookup.underscoredToMemberName(context2.context
                        .getCommandWithPrefix("create_")), false)) || Lookup
                    .lookUpNonStaticMethod0(new LookUpMethodOfClassParameter(context2.context, 0, null, Arrays
                            .asList(context2.argTypes), void.class, Lookup.underscoredToMemberName(context2.context
                            .getCommandWithPrefix("set_")), false)));
    }

    private static String underscoredToClassName(final String s) {
        return Lookup.underscoredToCamel(s, true);
    }

    public static String underscoredToMemberName(final String s) {
        return Lookup.underscoredToCamel(s, false);
    }

    /**
     * @param s
     * @param initialBig
     * @return
     */
    public static String underscoredToCamel(final String s, final boolean initialBig) {
        final char[] cs = s.toCharArray();
        final int l = cs.length;
        final char[] res = new char[l];
        int j = 0;
        boolean bigFlag = initialBig;
        for (int i = 0; i < l; i++) {
            final char ch = cs[i];
            if ((ch == '_') && !bigFlag)
                bigFlag = true;
            else {
                res[j++] = bigFlag ? Character.toTitleCase(ch) : Character.toLowerCase(ch);
                bigFlag = false;
            }
        }
        return new String(res, 0, j);
    }

    private static final String CR = System.getProperty("line.separator");
    private static final String LOOK_UP_METHOD_OF_CLASS = "Lookup.lookUpMethodOfClass()";
    private static final Map CANDIDATE_CONVERTERS = new HashMap();

    static interface DataProvider {
        Object provide();
    }

    private static Enumeration dataProvidersEnumeration(final DataProvider[] dps) {
        return new Enumeration() {
            int l = dps.length;
            int i = 0;

            public Object nextElement() {
                return dps[i++].provide();
            }

            public boolean hasMoreElements() {
                return i < l;
            }
        };
    }

    private static Object firstNotNull(final DataProvider[] dps) {
        for (int i = 0; i < dps.length; i++) {
            final Object provide = dps[i].provide();
            if (provide != null)
                return provide;
        }
        return null;
    }
}
