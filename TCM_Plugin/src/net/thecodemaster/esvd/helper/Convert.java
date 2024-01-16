package net.thecodemaster.esvd.helper;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

/**
 * @author Luciano Sampaio
 */
public class Convert {

	/**
	 * Returns a collection of strings that was inside the string passed as parameter.
	 * 
	 * @param content
	 *          The string containing its content separated by the SEPARATOR constant.
	 * @param separator
	 *          The separator that was used between each value.
	 * @return A collection of strings;
	 */
	public static List<String> fromStringToList(String content, String separator) {
		List<String> collection = Creator.newList();

		if ((null != content) && (content.length() > 0)) {
			collection = Arrays.asList(content.split(separator));
		}

		return collection;
	}

	/**
	 * Extract the project where this resource is under.
	 * 
	 * @param element
	 *          the element that will be used to extract the project.
	 * @return The project where this resource is under.
	 */
	public static IProject fromResourceToProject(Object element) {
		if (!(element instanceof IResource)) {
			if (!(element instanceof IAdaptable)) {
				return null;
			}
			element = ((IAdaptable) element).getAdapter(IResource.class);
			if (!(element instanceof IResource)) {
				return null;
			}
		}
		if (!(element instanceof IProject)) {
			element = ((IResource) element).getProject();
			if (!(element instanceof IProject)) {
				return null;
			}
		}

		return (IProject) element;
	}

	public static String fromPrimitiveNameToWrapperClass(String name) {
		if (null != name) {
			// boolean, byte, char, short, int, long, float, and double,
			if ("boolean".equals(name)) {
				return "java.lang.Boolean";
			} else if ("byte".equals(name)) {
				return "java.lang.Byte";
			} else if ("char".equals(name)) {
				return "java.lang.Character";
			} else if ("short".equals(name)) {
				return "java.lang.Short";
			} else if ("int".equals(name)) {
				return "java.lang.Integer";
			} else if ("long".equals(name)) {
				return "java.lang.Long";
			} else if ("float".equals(name)) {
				return "java.lang.Float";
			} else if ("double".equals(name)) {
				return "java.lang.Double";
			}
		}

		return "";
	}

}
