/**
 * 25.11.2009
 * 
 * 1
 * 
 */
package am.englet;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import am.englet.MethodsStorage.MethodRecord.InvokableMetadata;

public final class MethodInvokableMetadata implements InvokableMetadata {
	private final Method method;

	public MethodInvokableMetadata(final Method method) {
		this.method = method;
	}

	public Class targetType() {
		return Modifier.isStatic(method.getModifiers()) ? null : method
				.getDeclaringClass();
	}

	public boolean isStatic() {
		return Modifier.isStatic(method.getModifiers());
	}

	public Invokable invokable() {
		return new MethodInvokable(method);
	}
}