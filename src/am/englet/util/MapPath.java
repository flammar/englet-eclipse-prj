/**
 * 16.11.2009
 * 
 * 1
 * 
 */
package am.englet.util;

import java.util.List;
import java.util.Map;

import am.englet.Utils;

/**
 * @author 1
 * 
 */
public class MapPath {
	private final String path;
	private final char separator;

	/**
	 * @param path
	 */
	public MapPath(final String path) {
		this(path/* .substring(1) */, path.charAt(0));
	}

	/**
	 * @param path
	 * @param separator
	 */
	public MapPath(final String path, final char separator) {
		this.path = path;
		this.separator = separator;
	}

	public Object from(final Object obj) {
		int indexOf = 1, index1;
		Object obj1 = obj;
		Object res = null;
		main: {
			while (indexOf > 0) {
				index1 = indexOf;
				Object object = null;
				while (object == null && index1 >= 0) {
					index1 = path.indexOf(separator, index1 + 1);
					final String substring = index1 >= 0 ? path
							.substring(indexOf, index1) : path.substring(indexOf);
					object = obj1 instanceof Map ? ((Map) obj1).get(substring)
							: ((List) obj1).get(Integer.decode(substring).intValue());
					Utils.outPrintln(System.out, substring);
					System.out.println(object);
				}
				if (object == null)
					break main;
				else
					obj1 = object;
				indexOf = index1 + 1;
			}
			res = obj1;
		}
		return res;
	}

	public List listFrom(final Object obj) {
		final Object res = from(obj);
		return (List) (res != null && res instanceof List ? res : null);
	}

	public Map mapFrom(final Object obj) {
		final Object res = from(obj);
		return (Map) (res != null && res instanceof Map ? res : null);
	}

}
