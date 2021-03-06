/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.search;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** 
 *  @author Sameer Sawant
 */
public class ClassAccessUtilities {	
	
	/**
	 * Gets all fields from a class and it's superclasses of a given type
	 * 
	 * @param clazz
	 * 		The class to explore for typed fields
	 * @param typeName
	 * 		The name of the type to search for
	 * @return
	 * 		All Fields from the class of the specified type
	 */
	public static Field[] getFieldsOfType(Class clazz, String typeName) {
		Set<Field> allFields = new HashSet<Field>();
		Class checkClass = clazz;
		while (checkClass != null) {
			Field[] classFields = checkClass.getDeclaredFields();
			Collections.addAll(allFields, classFields);
			checkClass = checkClass.getSuperclass();
		}
		List namedFields = new ArrayList();
		Iterator fieldIter = allFields.iterator();
		while (fieldIter.hasNext()) {
			Field field = (Field) fieldIter.next();
			if (field.getType().getName().equals(typeName)) {
				namedFields.add(field);
			}
		}
		Field[] fieldArray = new Field[namedFields.size()];
		namedFields.toArray(fieldArray);
		return fieldArray;
	}
	
	
	/**
	 * Gets the setter methods of a class that take a parameter of a certain type
	 * 
	 * @param clazz
	 * 		The class to find setter methods on
	 * @param typeName
	 * 		The name of the type to find setters for
	 * @return
	 * 		All setter methods which take a parameter of the named type
	 */
	public static Method[] getSettersForType(Class clazz, String typeName) {
		Set allMethods = new HashSet();
		Class checkClass = clazz;
		while (checkClass != null) {
			Method[] classMethods = checkClass.getDeclaredMethods();
			for (int i = 0; i < classMethods.length; i++) {
				Method current = classMethods[i];
				if (current.getName().startsWith("set")) {
					if (Modifier.isPublic(current.getModifiers())) {
						Class[] paramTypes = current.getParameterTypes();
						if (paramTypes.length == 1) {
							if (paramTypes[0].getName().equals(typeName)) {
								allMethods.add(current);
							}
						}
					}
				}
			}
			checkClass = checkClass.getSuperclass();
		}
		Method[] methodArray = new Method[allMethods.size()];
		allMethods.toArray(methodArray);
		return methodArray;
	}
	
	
	/**
	 * Gets the named field from the class or any of its super classes
	 * 
	 * @param clazz
	 * 		The class to retrieve a field from
	 * @param fieldName
	 * 		The name of the field to retrieve
	 * @return
	 * 		The named field, or <code>null</code> if it was not found
	 */
	public static Field getNamedField(Class clazz, String fieldName) {
		Class checkClass = clazz;
		while (checkClass != null) {
			Field[] classFields = checkClass.getDeclaredFields();
			for (int i = 0; i < classFields.length; i++) {
				if (classFields[i].getName().equals(fieldName)) {
					return classFields[i];
				}
			}
			checkClass = checkClass.getSuperclass();
		}
		return null;
	}
	
	
	/**
	 * Gets the 'getter' method from the class or any of its super 
	 * classes for a named field.  For example, if a field name
	 * of 'foo' is given, this method looks for a method named
	 * <code>getFoo</code> on the class.
	 * 
	 * @param clazz
	 * 		The class to retrieve a getter method from
	 * @param fieldName
	 * 		The name of the field
	 * @return
	 * 		The getter method for the field, or <code>null</code> if it was not found
	 */
	public static Method getNamedGetterMethod(Class clazz, String fieldName) {
		Class checkClass = clazz;
		while (checkClass != null) {
			Method[] methods = checkClass.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				String methodName = methods[i].getName();
				if (methodName.startsWith("get") && methods[i].getParameterTypes().length == 0) {
					// strip off the 'get'
					String getterFieldName = methodName.substring(3);
					if (getterFieldName.length() == 1) {
						getterFieldName = String.valueOf(Character.toLowerCase(getterFieldName.charAt(0)));
					} else {
						getterFieldName = String.valueOf(Character.toLowerCase(getterFieldName.charAt(0))) 
							+ getterFieldName.substring(1);
					}
					if (getterFieldName.equals(fieldName)) {
						return methods[i];
					}
				}			
			}
			checkClass = checkClass.getSuperclass();
		}
		return null;
	}
}
