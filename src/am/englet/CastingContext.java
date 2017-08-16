/**
 * 28.10.2009
 *
 * 1
 *
 */
package am.englet;

/**
 * @author 1
 * 
 */
public interface CastingContext extends ServiceObject {

    public Object cast(Class cls, Object obj);

    public boolean canCast(Class target, Class source);

}
