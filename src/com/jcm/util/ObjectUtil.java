package com.jcm.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ObjectUtil {

	// 将DBOBJECT转为javabean
	@SuppressWarnings({ "unchecked", "rawtypes" })
  public static <T> T dbObj2Bean(DBObject dbObject, Class<T> c) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		T obj=c.newInstance();
		Method[] methods = c.getMethods();
		if (dbObject == null) return null;
		Set<String> set = dbObject.keySet();

		for (Method m : methods) {
			String name = m.getName();
			if (name.startsWith("set")) {
				String tempStr = name.substring(3);

				if ("id".equalsIgnoreCase(tempStr)) {
					m.invoke(obj, dbObject.get("_id"));
					continue;
				}
				for (String key : set) {
					if (tempStr.equalsIgnoreCase(key)) {
						Object arg = dbObject.get(key);
						Class<?> type = m.getParameterTypes()[0];
//						if (type.isArray() && arg instanceof List) {
//							Class<?> arrayType = type.getComponentType();
//							int size = ((List<?>)arg).size();
//							if (arrayType.equals(ObjectId.class)) {
//								ObjectId[] oarr = ((List<?>)arg).toArray(new ObjectId[size]);
//								m.invoke(obj, (Object)oarr);
//							} else if (arrayType.equals(String.class)) {
//								String[] oarr = ((List<?>)arg).toArray(new String[size]);
//								m.invoke(obj, (Object)oarr);
//							}
//						} else {
							if (type.isEnum()) {
								if (arg instanceof Integer) {
									Object[] enums = type.getEnumConstants();
									for (Object o : enums) {
										if (((Valueable)o).getValue() == ((Integer)arg).intValue()) {
											arg = o;
											break;
										}
									}
								} else {
									arg = Enum.valueOf((Class<Enum>)type, arg.toString());
								}
							} else if (type.getSuperclass() != null && type.getSuperclass().equals(HashMap.class)) {
								arg = dbObj2Bean((DBObject)arg, type);
							}
							m.invoke(obj, arg);
//						}
					}
				}
			}
		}
		return obj;
	}

	/**
	 * 将java bean 转换成 DBObject
	 * 
	 * @param object
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static DBObject bean2DBobj(Object object,int... updateType) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		return bean2DBobj(object, false, updateType);
	}

	/**
	 * 将java bean 转换成 DBObject  包含父类的属性
	 * 
	 * @param object
	 * @param withParent 是否将父类中的属性 放入DBObject 中
	 * @param updateType 有值为添加，无值为更新
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
  public static DBObject bean2DBobj(Object object,boolean withParent, int... updateType) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		DBObject dbObject = new BasicDBObject();
		if (object == null) return dbObject;
		Class<?> c = object.getClass();
		List<Method> methods = Arrays.asList(c.getMethods());
		List<Field> fields = Arrays.asList(c.getDeclaredFields());
		
		if (withParent){
			methods = getMethods(c);
			fields = getFields(c);
		}

		for (Method m : methods) {

			String name = m.getName();
			if (name.startsWith("set")) {
				String p = m.getParameterTypes()[0].equals(Boolean.class)
						|| m.getParameterTypes()[0].getSimpleName().equals(
								"boolean") ? "is" : "get";
				String tempStr = name.substring(3);
				Method method2 = c.getMethod(p + tempStr, new Class<?>[] {});
				if ("id".equalsIgnoreCase(tempStr)) {
					Object arg = method2.invoke(object, new Object[] {});
					if (arg != null) dbObject.put("_id", arg);
					continue;
				}
				for (Field field : fields) {
					String fieldName = field.getName();
					if (tempStr.equalsIgnoreCase(fieldName)) {
						// dbObject.put(fieldName, arg1);
						Object arg = method2.invoke(object, new Object[] {});
						if (null != arg) {
							if (updateType.length == 0 && arg instanceof Map) {
								@SuppressWarnings("rawtypes")
                Iterator<Entry<String, Object>> i = ((Map)arg).entrySet().iterator();
								while (i.hasNext()) {
									Entry<String, Object> entry = i.next();
									dbObject.put(fieldName + "." + entry.getKey(), entry.getValue());
								}
							} else {
								if (arg instanceof Enum) {
									if (arg instanceof Valueable) {
										arg = ((Valueable)arg).getValue();
									} else {
										arg = ((Enum<?>) arg).toString();
									}
								}
								dbObject.put(fieldName, arg);
							}
						}
					}
				}
			}
		}

		return dbObject;
	}
	
	public static void beanCopy(Object src, Object des) 
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		beanCopy(src, des, false);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
  public static void beanCopy(Object src, Object des, boolean withParent) 
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> c = src.getClass();
		List<Field> fields = Arrays.asList(c.getDeclaredFields());
		
		if (withParent){
			fields = getFields(c);
		}

		for (Field field : fields) {
			String fieldName = field.getName();
			if ("serialVersionUID".equals(fieldName)) continue;
			Class<?> fieldType = field.getType();
			String set = "set";
			String get = fieldType.equals(Boolean.class) ? "is" : "get";
			// dbObject.put(fieldName, arg1);
			Method setM = c.getMethod(getMethodName(set, fieldName), fieldType);
			Method getM = c.getMethod(getMethodName(get, fieldName));
			
			Object item = getM.invoke(src, new Object[]{});

			if (null != item) {
				if (item instanceof Map) {
					Map srcMap = (Map)item;
					Map desMap = (Map)getM.invoke(des, new Object[]{});
					for (Object key : srcMap.keySet()) {
						desMap.put(key, srcMap.get(key));
					}
				} else {
					setM.invoke(des, item);
				}
			}
		}
	}

	private static String getMethodName(String prefix, String fieldName) {
		return new StringBuilder(prefix).append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1)).toString();
	}
	/**
	 * 获取属性(包含直接父类的属性)
	 * @param cls
	 * @return
	 */
	public static List<Field> getFields(Class<?> cls){
		List<Field> ms = new ArrayList<Field>();
		ms.addAll(Arrays.asList(cls.getDeclaredFields()));
		Class<?> superClass = cls.getSuperclass();
		ms.addAll(Arrays.asList(superClass.getDeclaredFields()));
		return ms;
	}
	
	/**
	 * 获取方法（包含直接父类的方法）
	 * @param cls
	 * @return
	 */
	public static List<Method> getMethods(Class<?> cls){
		List<Method> ms = new ArrayList<Method>();
		ms.addAll(Arrays.asList(cls.getDeclaredMethods()));
		Class<?> superClass = cls.getSuperclass();
		ms.addAll(Arrays.asList(superClass.getDeclaredMethods()));
		return ms;
	}
}
