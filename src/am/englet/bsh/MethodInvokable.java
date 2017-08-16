/**
 * 25.11.2009
 * 
 * 1
 * 
 */
package am.englet.bsh;

import java.lang.reflect.InvocationTargetException;

import am.englet.Invokable;
import bsh.BshMethod;
import bsh.EvalError;
import bsh.Interpreter;

public final class MethodInvokable implements Invokable {
	final BshMethod method;

	public MethodInvokable(final BshMethod method) {
		this.method = method;
	}

	public Object invoke(final Object interpreter, final Object[] args)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		try {
			return method.invoke(args, (Interpreter) interpreter);
		} catch (final EvalError e) {
			throw new InvocationTargetException(e, e.getMessage() + "\n"
					+ e.getScriptStackTrace());
		}
	}

	public String toString() {
		return "Invocable:" + method.toString();
	}

	public Class returnType() {
		return method.getReturnType();
	}

	public Class[] parameterTypes() {
		return method.getParameterTypes();
	}

	public Class targetType() {
		return Interpreter.class;
	}
}