/**
 * 25.11.2009
 * 
 * 1
 * 
 */
package am.englet.bsh;

import am.englet.Invokable;
import bsh.BshMethod;
import bsh.Interpreter;

public final class MethodInvokableMetadata implements
		am.englet.MethodsStorage.MethodRecord.InvokableMetadata {
	private final BshMethod method;

	public MethodInvokableMetadata(final BshMethod method) {
		this.method = method;
	}

	public Class targetType() {
		return Interpreter.class;
	}

	public boolean isStatic() {
		return false;
	}

	public Invokable invokable() {
		return new MethodInvokable(method);
	}
}