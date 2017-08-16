package am.englet.lookup;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import am.englet.ConstructorInvokable;
import am.englet.FieldGetInvokable;
import am.englet.Invokable;
import am.englet.MethodInvokable;

// not used
public class MemberBased implements InvokableCandidate {
    private MemberCandidate mc;

    public Invokable getRealisation() {
        final Member member = mc.getRealisation();
        return member == null ? null : member instanceof Constructor ? (Invokable) new ConstructorInvokable(
                (Constructor) member) : member instanceof Method ? (Invokable) new MethodInvokable((Method) member)
                : member instanceof Field ? (Invokable) new FieldGetInvokable((Field) member) : null;
    }
}
