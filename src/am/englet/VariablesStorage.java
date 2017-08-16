package am.englet;

public interface VariablesStorage extends ServiceObject {

	public Object get(final Object name);

	/**
	 * Put mapping to top level of frames stack.
	 * 
	 * @param name
	 * @param val
	 */
	public void put(final Object name, final Object val);

	/**
	 * Find in stack frames depth mapping with the name specified and set value to
	 * it. If not found, then do put(name, val)
	 * 
	 * @param name
	 * @param val
	 */
	public void set(final Object name, final Object val);

	public boolean has(final Object name);

}