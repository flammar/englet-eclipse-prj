/**
 *
 */
package am.englet;

/**
 * @author Adm1
 * 
 */
public interface ArgumentProvider extends ServiceObject {

    // public Object getArgument();

    // public Object getArgument(Class clazz);

    /**
     * @param argumentClasses
     * @param targetClass
     *            null if static
     * @return
     */
    public ArgumentsAndTarget getArgumentsAndTarget(Class[] argumentClasses,
            Class targetClass);

    public interface ArgumentsAndTarget {

        public Object target();

        public Object[] arguments();

        /**
         * 
         * set internal references to arguments and target to null
         * 
         */
        public void clean();

    }

    public void setCastingContext(CastingContext context);

    public CastingContext getCastingContext();

    public Class[] getNNextArgumentTypes(int n);
}
