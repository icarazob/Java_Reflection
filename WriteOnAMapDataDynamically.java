import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WriteOnAMapDataDynamically {

	private String packageAndClassName = "";
	CheckValues cv = new CheckValues();
	
	public WriteOnAMapDataDynamically(String packageAndClassName)
	{
		if(isBlankOrNull(packageAndClassName))
		{
			this.packageAndClassName = this.getClass().getSimpleName();//MyClass.class;
			return;
		}
		this.packageAndClassName = packageAndClassName;
	}
	
	public WriteOnAMapDataDynamically()
	{
		
	}
	
	//This is for Properties files configurables
	String[] arrParamNames = {"INVOICENR", "TOTALPAY", "TAXES"}; //Properties with all names separated by commas.
	String[] arrParamValuesCsv = {"0897890", "20.34", ""}; //From Csv file
	String[] arrParamTypes = {"String", "Double", "String"}; //Parameter type
	String[] arrParamMethodsToCall = {"isNotBlankOrNull", "isNotZero", ""}; //Params method for type of check
	String[] ParamAllMethodsAvailables = {"isBlankOrNull", "isNotZero", ""}; //Params methods for checking data

	//This is by my project. It's optional.
	@SuppressWarnings("rawtypes")
	Map hmJasper = new HashMap(); //For Jasper Report
	
	public void executeTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException, 
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		String[] arrParamNames = {"INVOICENR", "TOTALPAY"}; //Properties with all names separated by commas.
		String[] arrCsvFileLines = {"098765;20.34"};
		String[] arrParamMethodsToCall = {"isNotBlankOrNull", "isNotZero"}; //Params method for type of check
		@SuppressWarnings("unused")
		String[] ParamAllMethodsAvailables = {"isBlankOrNull", "isNotZero"};
		//this.packageAndClassName = "CheckValues";
		/*
		Properties propConfig = new Properties();
		String splitBy = propConfig.getProperty("SPLITBY");
		String[] arrParamNames = propConfig.getProperty("PARAMNAMES").split(splitBy);
		String[] arrParamTypes = propConfig.getProperty("PARAMTYPES").split(splitBy);
		String[] arrParamMethods = propConfig.getProperty("PARAMMETHODS").split(splitBy);
		
		//Data from csv file
		String[] arrCsvValues = {"987654321"};
		*/
		
		for(String line : arrCsvFileLines)
		{
			String[] arrParamValuesCsv = line.split(";");
			int index = 0;
			for(String property : arrParamValuesCsv)
			{
				Object[] params = {property};
				Class[] parameterTypes = {String.class};
				if(!checkParameter(this.packageAndClassName, arrParamMethodsToCall[index], params,
						parameterTypes))
				{
					System.out.println("Error checking the csv value: " + arrParamNames[index] + 
							" with method: " + arrParamMethodsToCall[index]);
					break;
				}
				System.out.println("Csv value: " + arrParamNames[index] + 
						", checked with method: " + arrParamMethodsToCall[index] +
						" result is OK");
				hmJasper.put(arrParamNames[index], property);
				index = index+1;
				//String invoiceNr = arrParamValuesCsv[0];
			}

		}
	}
	
	@SuppressWarnings("unused")
	private String getTypeOfObject(Object o)
	{
		String type = o.getClass().getName();
		return type;
	}
	
	@SuppressWarnings("unused")
	private String getTypeOfObject(Class<?> c)
	{
		String type = c.getClass().getName();
		return type;
	}
	
	/**
		Example:
		String sToBeConvert = "2";
		Integer iconverted = new Integer("0");
        iconverted = (Integer) convertFromStringToOtherType(sToBeConvert, iconverted);
	 * @return 
	*/
	@SuppressWarnings("unused")
	private Object convertFromStringToOtherType(String val, Object type)
	{
	    Object o = null;
		
		if (type instanceof Integer)
		{
		    o = Integer.parseInt(val);
		    System.out.println("The type is Integer");
		}
		return o;
	}
	
	@SuppressWarnings("unused")
	private void loadPropertiesParams(Properties prop, String paramNameProperties, String[] arrParams, 
		String splitby)
	{
		String lineProperties = prop.getProperty(paramNameProperties);
		arrParams = lineProperties.split(splitby);
	}

	private Boolean checkParameter(String packageAndClassName, String methodNameToCheckValue,
			Object[] params, Class<?>... parameterTypes)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
			SecurityException
	{
		//call by reflection the method and get the return value
		Object returnObj = cv.createNewObjectAndInvokeMethodWithParams(packageAndClassName, methodNameToCheckValue,
				params, parameterTypes);
		
		Object returnObjStr = cv.createNewObjectAndInvokeMethodWithParams(packageAndClassName, "testString",
				params, parameterTypes);
		if(null == returnObj)
		{
			return true;
		}
		if(returnObj instanceof java.lang.Boolean)
		{
			System.out.println("El retorno es un boolean");
		}
		if(returnObjStr instanceof java.lang.String)
		{
			System.out.println("El retorno es una str");
		}
		return (Boolean) cv.createNewObjectAndInvokeMethodWithParams(packageAndClassName, methodNameToCheckValue,
				params, parameterTypes);
	}

	@SuppressWarnings("unused")
	public static boolean isNotBlankOrNull(String val)
	{
		if(!"".equals(val) || null != val)
		{
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	public boolean isNotZero(String val)
	{
		if(!"0".equals(val))
		{
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean isZero(String val)
	{
		if("0".equals(val))
		{
			return true;
		}
		return false;
	}
	
	private boolean isBlankOrNull(String val)
	{
		if("".equals(val) || null == val)
		{
			return true;
		}
		return false;
	}
	
	public String testString(String val)
	{
		return "str";
	}
	
	/**
	 * Creates the new object and invoke method with params.
	 *
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
        if(params.length > 0)
        {
        	setNameMethod = objectClass.getClass().getMethod(methodName, parameterTypes); //With parameters        	
        	result = setNameMethod.invoke(objectMapped, params); // pass args
        }
        else
        {
            setNameMethod = objectClass.getClass().getMethod(methodName);
            result = setNameMethod.invoke(objectMapped);
        }
        return result;
	}
     
     public static void main(String[] args)
     {
    	 WriteOnAMapDataDynamically woamdd = new WriteOnAMapDataDynamically("CheckValues");
    	 try 
    	 {
			woamdd.executeTest();
    	 }
    	 catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e)
    	 {
			e.printStackTrace();
    	 }
     }
	

}