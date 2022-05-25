import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 */

/**
 * @author icarazob
 *
 */
public class CheckValues {
	
	/**
	 * Invoke method from object initialized.
	 * 	Method must be public
	 * @param packageAndClassName the package and class name
	 * @param methodName the method name
	 * @return the object
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * 	How to use it:
	 * 	String packageAndClassName = "org.xxxx.classname";
	 * 	String methodName = "sayHello"
	 * 	Object[] params = {property, age};
		Class[] parameterTypes = {String.class, Integer.class};
	 */
	public Object invokeMethodFromObjectInitialized(String packageAndClassName,
			String methodName, Object[] params, Class<?>... parameterTypes) throws 
		ClassNotFoundException, InstantiationException, 
		IllegalAccessException, IllegalArgumentException, 
		InvocationTargetException, NoSuchMethodException, SecurityException
	{
		Class<?> objectClass = Class.forName(packageAndClassName);
		Object objectMapped = objectClass.getDeclaredConstructor();
		Method setNameMethod = objectClass.getClass().getMethod(methodName, parameterTypes);
		Object result = setNameMethod.invoke(objectMapped, params);
		return result;
	}
	
	/**
	 * Creates the new object and invoke method with params.
	 * Method must be public
	 * @param packageAndClassName the package and class name
	 * @param methodName the method name
	 * @param params the params
	 * @param parameterTypes the parameter types
	 * @return the object
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InstantiationException the instantiation exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * 
	 * 	How to use it:
	 * 	String packageAndClassName = "org.xxxx.classname";
	 * 	String methodName = "sayHello"
	 * 	Object[] params = {property, age};
		Class[] parameterTypes = {String.class, Integer.class};
	 */
	public Object createNewObjectAndInvokeMethodWithParams(String packageAndClassName, String methodName, Object[] params,
			 Class<?>... parameterTypes) throws 
		IllegalAccessException, IllegalArgumentException, 
		InvocationTargetException, ClassNotFoundException, InstantiationException,
		NoSuchMethodException, SecurityException
	{
		Object result = null;
        //String packageAndClassName = "com.mypackage.bean.Dog";
        Class<?> objectClass = Class.forName(packageAndClassName); // convert string classname to class
        Object objectMapped = objectClass.getDeclaredConstructor().newInstance(); // invoke empty constructor
        //String methodName = "";
        Method setNameMethod = null;
        // with single parameter, return void
        setNameMethod = objectClass.getMethod(methodName, parameterTypes); //With parameters   
        result = setNameMethod.invoke(objectMapped, params); // pass args
        return result;
	}
	
	public boolean isNotBlankOrNull(String val)
	{
		if(!"".equals(val) && null != val)
		{
			return true;
		}
		return false;
	}
	
	public String testString(String val)
	{
		return "str";
	}
	
	public void sayHello()
	{
		System.out.println("Hiii");
	}
	
	public boolean isZero(String val)
	{
		if("0".equals(val))
		{
			return true;
		}
		return false;
	}
	
	public boolean isNotZero(String val)
	{
		if(!"0".equals(val))
		{
			return true;
		}
		return false;
	}

}
