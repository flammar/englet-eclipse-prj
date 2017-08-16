/**
 * 20.11.2009
 * 
 * 1
 * 
 */
package am.englet;

import java.lang.reflect.InvocationTargetException;

public interface Invokable {
	public final Class[] NO_CLASSES = new Class[0];

	public abstract Object invoke(final Object obj, final Object[] args)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException;

	public Class returnType();

	public Class[] parameterTypes();

	// public boolean isStatic();

	public Class targetType();

	public static final Invokable SAME_OBJECT_RETURNING_INVOKABLE = new Invokable() {

		public Class targetType() {
			return null;
		}

		public Class returnType() {
			return Object.class;
		}

		public Class[] parameterTypes() {
			return new Class[] { null };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "SAME_OBJECT_RETURNING_INVOKABLE" + '@' + hashCode();
		}

		public Object invoke(final Object obj, final Object[] args)
				throws IllegalAccessException, IllegalArgumentException,
				InvocationTargetException, InstantiationException {
			return args[0];
		}
	};

}