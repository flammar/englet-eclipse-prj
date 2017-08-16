package am.englet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import am.englet.Links.NextContentProvider;
import am.englet.Links.ValueConverter;
import am.englet.MethodsStorage.Direct;
import am.englet.cast.ClassPool;
import am.englet.link.Chain;
import am.englet.link.FinalLink;
import am.englet.link.Link;
import am.englet.link.backadapters.slider.Slider;
import am.englet.util.Checker;

//TODO: stack frame as object
//DONE: 1 2 {{2+}{3+}} <+n>! --> 3 5

public class TestUtils0 {
	public static final String METHOD_NAME_REPLACEMENTS = "plus + minus - head - "
			+ "times * timesd *d timesdd *dd equals = unString obj div / " + "isLink link head head "
			+ "gt > ge >e directPlus +d plusz +z forLink for lt < not ! " + "stringDirect \\ "
			+ "TRUE true FALSE false BOOLEAN boolean BOOLEAN bool iif ? or | xor ^ " + "dp : divL /l plusn +n";

	public static Object unString(final String s, final String cl) {
		Class c = null, rt[];
		try {
			c = Class.forName(cl);
			final Constructor constructor = c.getConstructor(new Class[] { String.class });
			final Constructor con = constructor;
			return con.newInstance(new Object[] { s });
		} catch (final ClassNotFoundException x) {
			x.printStackTrace();
			return null;
		} catch (final Exception e) {
			final Method mm[] = c.getMethods();
			Method m;
			for (int i = 0; i < mm.length; i++)
				if (Modifier.isStatic((m = mm[i]).getModifiers()) && ((rt = m.getParameterTypes()).length == 1)
						&& rt[0].equals(String.class))
					try {
						return m.invoke(null, new Object[] { s });
					} catch (final Exception e1) {
						e1.printStackTrace();
					}
		}
		return null;
	}

	public static boolean not(final boolean b) {
		return !b;
	}

	public static Link forLink(final int start, final int end) {
		final Links.NextContentProvider nextContentProvider = new Links.NextContentProvider() {
			private static final long serialVersionUID = 1L;
			int current = start;

			public Object tryNextContent() {
				return current > end ? null : new Integer(current++);
			}
		};
		return Links.nextContentProviderBased(nextContentProvider);
	}

	public static boolean TRUE() {
		return true;
	}

	public static boolean FALSE() {
		return false;
	}

	public static boolean BOOLEAN(final Object o) {
		return Utils.toBoolean(o);
	}

	public static Object plus(final Object o1, final Object o2) {
		if ((o1 instanceof Number) && (o2 instanceof Number))
			return TestUtils0.bigdec(o1).add(TestUtils0.bigdec(o2));
		final StringBuffer stringBuffer = Utils.toStringBuffer(o1);
		return o2 instanceof StringBuffer ? stringBuffer.append((StringBuffer) o2) : stringBuffer.append(o2);
	}

	public static String plus(final Object o1, final String s2) {
		return o1 + s2;
	}

	public static Object plus(final Object o1, final Link link) {
		return new FinalLink(o1, link);
	}

	public static void dp(final Object o1, final String key, final Map m) {
		m.put(key, o1);
	}

	public static void dp(final Object o1, final List keys, final Map m) throws InstantiationException,
			IllegalAccessException {
		final Class cl = SortedMap.class.isInstance(m) ? TreeMap.class : HashMap.class;
		Map cm = m;
		final int toIndex = keys.size() - 1;
		for (int i = 0; i < toIndex; i++) {
			final Object object = keys.get(i);
			final Object object2 = cm.get(object);
			final boolean b = (object2 != null) && Map.class.isInstance(object2);
			final Map mr = (b ? (Map) object2 : (Map) cl.newInstance());
			if (!b)
				cm.put(object, mr);
			cm = mr;
		}
		cm.put(keys.get(toIndex), o1);
	}

	public static void dp(final Object o1, final int key, final List m) {
		m.set(key, o1);
	}

	public static void dp(final Object o1, final Collection m) {
		m.add(o1);
	}

	public static Link plusz(final Link o1, final Link link) {
		return Links.plusz(o1, link);
	}

	public static Object direct(final Object content) {
		return content instanceof String ? new MethodsStorage.Direct(content) : content;
	}

	public static MethodsStorage.Direct stringDirect(final String arg) {
		return new Direct(arg);
	}

	public static Object directPlus(final Object o1, final Link link) {
		return new FinalLink(new MethodsStorage.Direct(o1), link);
	}

	public static Object minus(final Number d1, final Number d2) {
		return TestUtils0.bigdec(d1).subtract(TestUtils0.bigdec(d2));
	}

	public static float minus(final float d1, final float d2) {
		return d1 - d2;
	}

	public static double minus(final double d1, final double d2) {
		return d1 - d2;
	}

	public static int minus(final int d1, final int d2) {
		return d1 - d2;
	}

	public static long minus(final long d1, final long d2) {
		return d1 - d2;
	}

	public static float times(final float d1, final float d2) {
		return d1 * d2;
	}

	public static double times(final double d1, final double d2) {
		return d1 * d2;
	}

	public static long times(final int d1, final int d2) {
		return d1 * d2;
	}

	public static long times(final long d1, final long d2) {
		return d1 * d2;
	}

	public static float plus(final float d1, final float d2) {
		return d1 + d2;
	}

	public static double plus(final double d1, final double d2) {
		return d1 + d2;
	}

	public static int plus(final int d1, final int d2) {
		return d1 + d2;
	}

	public static long plus(final long d1, final long d2) {
		return d1 + d2;
	}

	public static Object minus(final String d1, final String d2) {
		return TestUtils0.bigdec(d1).subtract(TestUtils0.bigdec(d2));
	}

	// public static Object times(final Object d1, final Object d2) {
	// return bigdec(d1).multiply(bigdec(d2));
	// }

	public static BigDecimal times(final BigDecimal d1, final BigDecimal d2) {
		return d1.multiply(d2);
	}

	public static Object times(final Object d1) {
		return d1;
	}

	public static Object plus(final Object d1) {
		return d1;
	}

	public static Link times(final Link through, final Link each) {

		final ValueConverter valueConverter = TestUtils0.tailer(each);
		final NextContentProvider ncp = Utils.oneLevelFlattingValueConverterBasedNextContentProvider(through,
				valueConverter);
		return Links.nextContentProviderBased(ncp);
	}

	private static ValueConverter tailer(final Link each) {
		return new ValueConverter() {

			private static final long serialVersionUID = 2508833137005886701L;

			public Object convert(final Object object) {
				return new FinalLink(object, each);
			}
		};
	}

	public static Link times(final Chain each, final Link through) {
		final NextContentProvider ncp = new NextContentProvider() {

			private static final long serialVersionUID = 3592126557079912655L;
			Link each1 = null;
			Link through1 = new FinalLink(null, through);
			Link through2 = null;

			public Object tryNextContent() {
				if (each1 != null) {
					final Object res = Links.nullCorrect(each1.content());
					each1 = each1.next();
					return res;
				} else if (through2 != null) {
					final Object res = Links.nullCorrect(through2.content());
					through2 = through2.next();
					return res;

				}
				through1 = through1.next();
				if (through1 != null) {
					final Object content = through1.content();
					through2 = content == null ? null : (content instanceof Link ? (Link) content : new FinalLink(
							content));
					each1 = each;
					return tryNextContent();
				}
				return null;
			}
		};
		return Links.nextContentProviderBased(ncp);
		// return times(through, each);
	}

	public static Link times(final Object each, final Link through) {
		final LinkBasedNextContentProvider throughProvider = new LinkBasedNextContentProvider(through);
		final NextContentProvider ncp = new NextContentProvider() {
			private static final long serialVersionUID = -4808097348576249927L;
			NextContentProvider current = null;

			public Object tryNextContent() {
				if (current != null) {
					final Object tryNextContent = current.tryNextContent();
					if (tryNextContent != null)
						return tryNextContent;
					else
						current = null;
				}
				final Object tryNextContent = throughProvider.tryNextContent();
				if (Links.isCorrectedLink(tryNextContent)) {
					current = new LinkBasedNextContentProvider(new FinalLink(each,
							(Link) Links.nullUncorrect(tryNextContent)));
					return tryNextContent();
				}
				return tryNextContent;
			}
		};
		return Links.nextContentProviderBased(ncp);
	}

	public static Link timesd(final Link through, final Link each) {
		return TestUtils0.times(Management.direct(through, null), each);
	}

	public static Link timesdd(final Link through, final Link each) {
		final Link direct = Links.valueConverterBased(through, new ValueConverter() {

			private static final long serialVersionUID = 5260236732005717442L;

			public Object convert(final Object object) {
				return new MethodsStorage.Direct(object);
			}
		});
		return TestUtils0.times(direct, each);
	}

	public static Link times(final boolean flag, final Link link) {
		return flag ? link : null;
	}

	public static Link times(final Link link, final boolean flag) {
		return flag ? link : null;
	}

	public static boolean times(final boolean flag, final boolean flag2) {
		return flag && flag2;
	}

	public static boolean plus(final boolean flag, final boolean flag2) {
		return flag || flag2;
	}

	public static Link times(final Object o, final boolean flag) {
		return flag ? new FinalLink(new MethodsStorage.Direct(o)) : null;
	}

	public static Link times(final boolean flag, final Object o) {
		return TestUtils0.times(o, flag);
	}

	public static Link times(final Link through, final Invokable each) {
		return each == null ? through : (each.returnType().equals(Boolean.class) || each.returnType().equals(
				boolean.class)) ? TestUtils0.filterLink(through, each) : TestUtils0.procLink(through, each);
	}

	private static Link procLink(final Link through, final Invokable each) {
		return Links.valueConverterBased(through, each.targetType() == null ? (ValueConverter) new ValueConverter() {
			private static final long serialVersionUID = 2520818081480374756L;

			public Object convert(final Object object) {
				try {
					return each.invoke(null, new Object[] { object });
				} catch (final Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		} : new ValueConverter() {
			private static final long serialVersionUID = 1503017881972880560L;

			public Object convert(final Object object) {
				try {
					return each.invoke(object, new Object[0]);
				} catch (final Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	// private static Link procLink(final Link through, final Invokable each) {
	// return Utils.sliderBasedLink(new InvocableBasedSlider(new LinkSlider(
	// through), each));
	// }

	private static Link filterLink(final Link through, final Invokable each) {

		return Links.checkerBased
		// Utils.filterLink
				(through, each.targetType() == null ? (Checker) TestUtils0.staticInvokableBasedCkecker(each)
						: (Checker) TestUtils0.nonstaticInvokableBasedCkecker(each));
	}

	private static Checker nonstaticInvokableBasedCkecker(final Invokable each) {
		return new Checker() {

			public boolean check(final Object o) {
				try {
					Utils.debug(System.out, "TestUtils0.filterLink(...).new Checker() {...}.check():", o);
					return Utils.toBoolean(each.invoke(o, new Object[0]));
				} catch (final Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		};
	}

	private static Checker staticInvokableBasedCkecker(final Invokable each) {
		return new Checker() {

			public boolean check(final Object o) {
				try {
					Utils.debug(System.out, "TestUtils0.filterLink(...).new Checker() {...}.check():", o);
					return Utils.toBoolean(each.invoke(null, new Object[] { o }));
				} catch (final Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		};
	}

	public static Link times(final Invokable through, final Object each) {
		return Links.nextContentProviderBased(new NextContentProvider() {
			private static final long serialVersionUID = 1L;

			public Object tryNextContent() {
				try {
					final Object res = through.invoke(null, null);
					final boolean boolean1 = Utils.toBoolean(res);
					return boolean1 ? each instanceof Invokable ? ((Invokable) each).invoke(null, null) : each : null;
				} catch (final Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	public static Link times(final Link through, final String each) {
		return TestUtils0.times(through, new FinalLink(each, null));

	}

	public static boolean equals(final Object o1, final Object o2) {
		return (o1 == null) ? (o2 == null) : o1.equals(o2);
	}

	public static boolean equals(final BigInteger o1, final BigInteger o2) {
		return o1.compareTo(o2) == 0/* equals((Object) o1, (Object) o2) */;
	}

	public static boolean equals(final BigDecimal o1, final BigDecimal o2) {
		return o1.compareTo(o2) == 0/* equals((Object) o1, (Object) o2) */;
	}

	public static boolean equals(final char o1, final char o2) {
		return o1 == o2;
	}

	public static boolean equals(final byte o1, final byte o2) {
		return o1 == o2;
	}

	public static boolean equals(final short o1, final short o2) {
		return o1 == o2;
	}

	public static boolean equals(final int o1, final int o2) {
		return o1 == o2;
	}

	public static boolean equals(final long o1, final long o2) {
		return o1 == o2;
	}

	public static boolean equals(final double o1, final double o2) {
		return o1 == o2;
	}

	public static boolean equals(final float o1, final float o2) {
		return o1 == o2;
	}

	public static ResultList isLink(final Object o1) {
		return new ResultList(new Object[] { o1, new Boolean((o1 == null) || (o1 instanceof Link)) });
	}

	public static ResultList head(final Link link) {
		final Object content = link.content();
		final Link next = link.next();
		return new ResultList(new Object[] { next, content });
	}

	public static ResultList tail(final Link link) {
		final Object content = link.content();
		final Link next = link.next();
		return new ResultList(new Object[] { content, next });
	}

	public static BigDecimal div(final BigDecimal d1, final BigDecimal d2) {
		return d1.divide(d2, BigDecimal.ROUND_HALF_EVEN);
	}

	public static Link div(final Link through, final int quotient) {
		return Links.div(through, quotient);
		// final Slider slider = new DivideSlider(through, quotient);
		// return Utils.sliderBasedLink(slider);
	}

	public static Link div(final Link through, final Link filter, final ClassPool classPool, final MethodsStorage m,
			final DataStack ds) {
		final Englet englet = Utils.deriveEnglet(ds, m, classPool);
		// return Utils
		// .filterLink(through, new EngletBasedChecker(englet, filter));
		final ValueConverter engletBasedValueConverter = new EngletBasedValueConverter(englet, filter);
		final ValueConverter engletBasedValueConverter2 = new ValueConverter() {

			private static final long serialVersionUID = -4350993497798156777L;

			public Object convert(final Object object) {
				final Object convert = engletBasedValueConverter.convert(object);
				// TODO new
				// {{1 2} {3 4}} {-x true}/ -> [1 || 2] || [3 || 4]
				return Boolean.FALSE.equals(convert) ? null
						: Boolean.TRUE.equals(convert) ? (Utils.isLink(object) ? new FinalLink(object, null) : object)
								: convert;
			}
		};
		final NextContentProvider ncp = Utils.oneLevelFlattingValueConverterBasedNextContentProvider(through,
				engletBasedValueConverter2);
		return Links.nextContentProviderBased(ncp);
	}

	public static Link plusn(final Link base, final DataStack ds) {
		final Link reverse = Processing.reverse(base);
		final ValueConverter vc = new ValueConverter() {

			private static final long serialVersionUID = 6318704837778734178L;

			public Object convert(final Object object) {
				return (object == null) || (object instanceof Link) ? new FinalLink(ds.pop(), (Link) object) : object;
			}
		};
		final Link valueConverterBased = Links.valueConverterBased(reverse, vc);
		final Link reverse2 = Processing.reverse(valueConverterBased);

		final NextContentProvider ncp = Utils.oneLevelFlattingValueConverterBasedNextContentProvider(reverse2,
				ValueConverter.TO_SAME_VALUE_CONVERTER);
		return Links.nextContentProviderBased(ncp);
	}

	public static Link div(final String src, final String with) {
		return (src == null) || (src.length() == 0) ? null : Utils.splitLink(src, with, src.indexOf(with));
	}

	public static Link div(final Link link, final Map map) {
		return Utils.curryLink(link, map);
	}

	public static Link divL(final InputStream stream) {
		final BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(stream));
		final NextContentProvider a = new Links.NextContentProvider() {

			/**
             *
             */
			private static final long serialVersionUID = 1L;

			public Object tryNextContent() {
				try {
					return bufferedInputStream.readLine();
				} catch (final IOException e) {
					if (Englet.debug)
						e.printStackTrace();
					return null;
				}
			}
		};
		return Links.nextContentProviderBased(a);
	}

	public static Link minus(final Link init, final Link each, final ClassPool classPool, final MethodsStorage m,
			final DataStack ds) throws Throwable {
		final Links.NextContentProvider nextContentProvider = new Links.NextContentProvider() {
			private static final long serialVersionUID = -7738331905975489965L;
			private final Englet englet = Utils.deriveEnglet(ds, m, classPool);
			boolean failed = false;
			{
				Management.excl(init, englet.getRstack());
				englet.run();
			}

			public Object tryNextContent() {
				if (failed)
					return null;
				Management.excl(each, englet.getRstack());
				try {
					englet.run();
				} catch (final Throwable e) {
					if (Englet.debug)
						e.printStackTrace();
					failed = true;
					return tryNextContent();
				}
				DataStack stack = englet.getStack();
				final Object pop = canPop(stack) ? stack.pop() : null;
				failed |= pop == null;
				return pop;
			}

			private boolean canPop(DataStack stack) {
				int n = stack.size();
				while (n-- > 0)
					if (stack.map(n).st.size() > 0)
						return true;
				return false;
			}
		};
		return Links.nextContentProviderBased(nextContentProvider);
	}

	private static BigDecimal bigdec(final Object o) {
		return o instanceof BigDecimal ? (BigDecimal) o : o instanceof Double ? new BigDecimal(
				((Double) o).doubleValue()) : new BigDecimal("" + o);
	}

	public static int rem(final int a, final int b) {
		return a % b;
	}

	public static boolean gt(final long a, final long b) {
		return a > b;
	}

	public static boolean gt(final Number d1, final Number d2) {
		return TestUtils0.bigdec(d1).compareTo(TestUtils0.bigdec(d2)) > 0;
	}

	public static boolean lt(final Number d1, final Number d2) {
		return TestUtils0.bigdec(d1).compareTo(TestUtils0.bigdec(d2)) < 0;
	}

	public static boolean lt(final double a, final double b) {
		return a < b;
	}

	public static boolean gt(final double a, final double b) {
		return a > b;
	}

	public static boolean lt(final long a, final long b) {
		return a < b;
	}

	public static boolean lt(final Comparable a, final Comparable b) {
		return a.compareTo(b) < 0;
	}

	public static boolean gt(final Comparable a, final Comparable b) {
		return a.compareTo(b) > 0;
	}

	public static boolean ge(final int a, final int b) {
		return a >= b;
	}

	public static Link rxlink(final String src, final String with) {
		final Pattern pat = Pattern.compile(with, Pattern.MULTILINE | Pattern.DOTALL);
		final Matcher matcher = pat.matcher(src);
		// TODO elx 'Serialisar-o frumTjjenjan orif 'matcher iT frumTjjenjan
		return Links.nextContentProviderBased(new NextContentProvider() {
			private static final long serialVersionUID = 4469427842502638376L;

			public Object tryNextContent() {
				return matcher.find() ? TestUtils0.extracted3(matcher) : null;
			}
		});

		// Utils.sliderBasedLink(new Slider() {
		// public boolean tryNext() {
		// return matcher.find();
		// }
		//
		// public Object content() {
		// return extracted1(matcher);
		// }
		// });
	}

	public static Link rxsplit(final String src, final String with) {
		final Pattern pat = Pattern.compile(with, Pattern.MULTILINE | Pattern.DOTALL);
		final Matcher matcher = pat.matcher(src);
		return Utils.sliderBasedLink(new Slider() {
			int end = 0;
			int step = 1;
			boolean match = false;
			Object[] content = new Object[2];

			boolean step() {
				step = 0;
				match = matcher.find();
				content[0] = match ? src.substring(end, matcher.start()) : src.substring(end);
				content[1] = match ? TestUtils0.extracted3(matcher) : null;
				end = match ? matcher.end() : -1;
				return true;
			}

			public boolean tryNext() {
				// if (step == 1)
				// return step() || true;
				// else {
				// ;
				// return ((step = 1) == 1) && match;
				// }

				return step == 1 ? (step() || true) : (((step = 1) == 1) && match);
			}

			public Object content() {
				return content[step];
			}
		});
	}

	private static Link extracted3(final Matcher matcher) {
		final int cnt = matcher.groupCount();
		final String[] buv = new String[cnt + 1];
		for (int i = 0; i <= cnt; i++) {
			final String group = matcher.group(i);
			buv[i] = // (group != null) || (i == 0) ?
			group
			// : buv[i - 1]
			;
		}
		return Links.toLazyChain(buv);

		// Links.toChain(buv);

		// return Utils.sliderBasedLink(new Slider() {
		// int i = -1;
		//
		// public boolean tryNext() {
		// return ++i <= cnt;
		// }
		//
		// public Object content() {
		// return matcher.group(i);
		// }
		// });
	}

	public static Object iif(final boolean b, final Object thenPart, final Object elsePart) {
		return b ? thenPart : elsePart;
	}

	/*
	 * 'Defaulting kje "<elsePart> { <check> }1! <thenPart> ? " jav
	 * "index_of {-1=}1! 0 ? " om najT'm
	 * 
	 * @param elsePart 'check-im em T
	 * 
	 * @param b 'Check-ung
	 * 
	 * @param thenPart 'Default om naj T
	 * 
	 * @return
	 */
	public static Object iif(final Object elsePart, final boolean b, final Object thenPart) {
		return b ? thenPart : elsePart;
	}

	public static Object lt(final Link l) {
		return l != null ? l.content() : l;
	}

	public static FileOutputStream lt(final File l) throws FileNotFoundException {
		return new FileOutputStream(l);
	}

	public static Link gt(final Link l) {
		return l != null ? l.next() : l;
	}

	public static InputStream gt(final File f) throws FileNotFoundException {
		return new FileInputStream(f);
	}

	public static ResultList xor(final Link l) {
		final Object content = l.content();
		return new ResultList(new Object[] { l.next(), content });
	}

	public static Link or(final Link l, final Link l2) {
		return l != null ? l : l2;
	}

	public static Object or(final Object l, final Object l2) {
		return l != null ? l : l2;
	}

	public static byte or(byte l, byte l2) {
		return (byte) (l | l2);
	}

	public static int or(int l, int l2) {
		return l | l2;
	}

	public static long or(long l, long l2) {
		return l | l2;
	}

	public static byte and(byte l, byte l2) {
		return (byte) (l & l2);
	}

	public static int and(int l, int l2) {
		return l & l2;
	}

	public static long and(long l, long l2) {
		return l & l2;
	}

}
