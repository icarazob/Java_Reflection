/**
 * 
 */
package com.hums.therapy.services.mdif.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hums.therapy.config.YAMLExportConfig;
import com.hums.therapy.model.database.mdif.Inventory;
import com.hums.therapy.services.mdif.GenerateReport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author israel.carazo
 *
 */

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class GenerateReportImpl implements GenerateReport 
{
	private static final Logger logger = LoggerFactory.getLogger(GenerateReportImpl.class);
	
	private final YAMLExportConfig yamlExportConfig;
	
	/**
	 * Sets the excel file type.
	 * 
	 * How to use it:
	 * <p>ExcelType type = ExcelType.XLSX;</p>
	 * 
	 * <p>setExcelFileType(type);</p>
	 * 
	 * <p>Supported type: xlsx, xls</p>
	 *
	 * @param type the new excel file type
	 * 
	 * @return String with type
	 */
	public String setExcelFileType(ExcelType type)
	{
		switch(type)
		{
	      case XLS:
	        return ".xls";
	      case XLSX:
	    	return ".xlsx";
	      default:
	    	return "";
	    }
	}

	/**
	 * Generate report with multiple sheets.
	 *TODO: Terminar de implementar
	 * @param fileName the file name
	 * @param sheetsName the sheets name
	 */
	@Override
	public void generateReportWithMultipleSheets(StringBuilder fileName, final List<String> sheetsName, 
			Map<Integer, String> titles, final int titlesPosition,
			final List<?> rows) 
	{
		String excelExtensionFile = "";
		if("".equals(excelExtensionFile) || null == excelExtensionFile)
		{
			logger.error("Please before continue the proccess set an extension file type with method: setExcelFileType");
			return;
		}
		
        XSSFWorkbook workbook = new XSSFWorkbook();
        List<XSSFSheet> listSheets = new ArrayList<XSSFSheet>();
        for(String sheetName : sheetsName)
        {
        	XSSFSheet sheet = workbook.createSheet(sheetName);
        	listSheets.add(sheet);
        }

        Object[][] bookData = 
        {
                {"Head First Java", "Kathy Serria", 79},
                {"Effective Java", "Joshua Bloch", 36},
                {"Clean Code", "Robert martin", 42},
                {"Thinking in Java", "Bruce Eckel", 35},
        }; //TODO: Left adapt it
 
        int rowCount = 0;
         
        for (Object[] aBook : bookData) 
        {
            //Row row = sheet.createRow(++rowCount);
        	for(XSSFSheet sheet : listSheets)
        	{
	        	Row row = sheet.createRow(rowCount);             
	            int columnCount = 0;
	             
	            for (Object field : aBook)
	            {
	                //Cell cell = row.createCell(++columnCount);
	            	Cell cell = row.createCell(columnCount);
	                if (field instanceof String)
	                {
	                    cell.setCellValue((String) field);
	                } 
	                else if (field instanceof Integer)
	                {
	                    cell.setCellValue((Integer) field);
	                }
	            }
        	}
        }
        fileName.append(excelExtensionFile);
        try (FileOutputStream outputStream = new FileOutputStream(fileName.append(excelExtensionFile).toString()))
        {
            workbook.write(outputStream);
        }
        catch (FileNotFoundException e)
        {
        	logger.error("Error creating the excel file: ");
			logger.error(e.toString());
		} 
        catch (IOException e)
        {
        	logger.error("Error writing on excel file: ");
        	logger.error(e.toString());
		}
        finally
        {
        	try 
        	{
				workbook.close();
			} 
        	catch (IOException e)
        	{
        		logger.error("Error closing the excel file: ");
        		logger.error(e.toString());
			}
        }
	}

	/**
	 * Generate report.
	 *
	 * @param fileName the file name
	 * @param sheetName the sheet name
	 * @param titles the titles
	 */
	@Override
	public void generateReport(StringBuilder fileName, final String sheetName, 
			Map<Integer, String> titles, final int titlesPosition,
			final List<?> rows, final List<String> subSheetsName,
			ExcelType type)
	{
		String excelExtensionFile = setExcelFileType(type);
		if("".equals(excelExtensionFile) || null == excelExtensionFile)
		{
			logger.error("Please before continue the proccess set an extension file	type with method: setExcelFileType");
			return;
		}
		
		logger.info("Generating excel file");
		
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        
        if(null != subSheetsName && !subSheetsName.isEmpty())
        {
        	for(String name : subSheetsName)
        	{
        		newSheet(name, workbook);
        	}
        }
        
        int rowCount = 0;
        if(null != titles && !titles.isEmpty())
        {
        	generateTitles(titles, sheet, titlesPosition);
        	rowCount = titlesPosition + 1;
        }
        
        try
        {
			makeExcel(rows, sheet, rowCount);
		}
        catch (Exception e)
        {
			e.printStackTrace();
		}
        fileName.append(excelExtensionFile);
        saveExcelFile("C:\\Users\\israel.carazo\\Documents\\", fileName.toString(),
        		workbook);
	}
	
	@Override
	public File generateReportAndReturnFile(StringBuilder fileName, final String sheetName, 
			Map<Integer, String> titles, final int titlesPosition,
			final List<?> rows, final List<String> subSheetsName,
			ExcelType type)
	{
		File excelFile = null;
		String excelExtensionFile = setExcelFileType(type);
		if("".equals(excelExtensionFile) || null == excelExtensionFile)
		{
			logger.error("Please before continue the proccess set an extension file	type with method: setExcelFileType");
			return excelFile;
		}
		
		logger.info("Generating excel file");
		
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        
        if(null != subSheetsName && !subSheetsName.isEmpty())
        {
        	for(String name : subSheetsName)
        	{
        		newSheet(name, workbook);
        	}
        }
        
        int rowCount = 0;
        if(null != titles && !titles.isEmpty())
        {
        	generateTitles(titles, sheet, titlesPosition);
        	rowCount = titlesPosition + 1;
        }
        
        try
        {
			makeExcel(rows, sheet, rowCount);
		}
        catch (Exception e)
        {
			e.printStackTrace();
		}
        fileName.append(excelExtensionFile);
        return generateExcelFileAsObjectFile("C:\\Users\\israel.carazo\\Documents\\", fileName.toString(), workbook);
	}

	/**
	 * Generate titles.
	 * <p>Using the row parameter as position in the excel file</p>
	 * <p>The key of the map represent the cell</p>
	 * <p>The value of the map is the value of the cell</p>
	 *
	 * @param titles the titles
	 * @param sheet the sheet
	 * @param row used as title
	 */
	@Override
	public void generateTitles(Map<Integer, String> titles, XSSFSheet sheet, final int row)
	{
		logger.info("Generating Dynamically the titles in the Excel");
		
		Integer[] arrKeys = new Integer[titles.size()];
		int arrSize = 0;
		for(Integer key : titles.keySet())
		{
			arrKeys[arrSize] = key;
			arrSize = arrSize+1;
		}
		
        //Dynamic Titles
        int mapSize = 0;
        XSSFRow titlesRow = sheet.createRow(row);
        while(mapSize != titles.size())
        {
        	titlesRow.createCell(arrKeys[mapSize]).setCellValue(
        			titles.get(arrKeys[mapSize]));
        	mapSize++;
        }
	}
	
	public void newSheet(String sheetName, XSSFWorkbook exceldoc)
	{
        exceldoc.createSheet(sheetName);
	}
	
	/**
	 * Make an excel from a List of any type.
	 *
	 * @param rows the rows
	 * @param sheet the sheet
	 * @param rowCount the row count
	 * @throws Exception the exception
	 */
	@Override
	public void makeExcel(List<?> rows, XSSFSheet sheet,
			int rowCount) throws Exception
	{
		//Will convert the object from the call of the method
		int columnCount = 0;
        for (Object rowObject : rows)
        {
        	Row row = sheet.createRow(rowCount);
            if (rowObject instanceof Inventory)
            {
            	Inventory aircraft = (Inventory) rowObject;
            	//fillCellsWithAircraftConfig(aircraft, row, columnCount);
            	rowCount++;
            }
            else
            {
            	logger.error("Type of Object not controlled");
            	throw new Exception("Type of Object not controlled");
            }
        }
	}
	
	/**
	 * Save excel file.
	 *
	 * @param path the path
	 * @param filename the filename
	 * @param exceldoc the exceldoc
	 * @return true, if successful
	 */
	@Override
	public boolean saveExcelFile(String path, String filename, XSSFWorkbook exceldoc)
	{
		boolean hasError = true;
		try (FileOutputStream out = new FileOutputStream(path + filename))
		{
		    exceldoc.write(out);
		    hasError = false;
		}
        catch (FileNotFoundException e)
        {
        	logger.error("Error creating the excel file: ");
			logger.error(e.toString());
		} 
        catch (IOException e)
        {
        	logger.error("Error writing on excel file: ");
        	logger.error(e.toString());
		}
        finally
        {
        	try
        	{
				exceldoc.close();
				hasError = false;
			}
        	catch (IOException e)
        	{
        		hasError = true;
        		logger.error("Error closing the excel file: ");
        		logger.error(e.toString());
			}
        }
		return hasError;
	}
	
	/**
	 * Generate excel file as object file.
	 *
	 * @param path the path
	 * @param filename the filename
	 * @param exceldoc the exceldoc
	 * @return the file
	 */
	@Override
	public File generateExcelFileAsObjectFile(String path, String filename, XSSFWorkbook exceldoc)
	{
		File excelFile = null;
		try (FileOutputStream out = new FileOutputStream(path + filename))
		{
		    exceldoc.write(out);
		}
        catch (FileNotFoundException e)
        {
        	logger.error("Error creating the excel file: ");
			logger.error(e.toString());
		} 
        catch (IOException e)
        {
        	logger.error("Error writing on excel file: ");
        	logger.error(e.toString());
		}
        finally
        {
        	try
        	{
				exceldoc.close();
				new File(path + filename);
			}
        	catch (IOException e)
        	{
        		logger.error("Error closing the excel file: ");
        		logger.error(e.toString());
			}
        }
		return excelFile;
	}
	
	
	public File createExcelWithFormat(final String fileName, final List<?> rows,
			final String sheetName, int headerRow, List<String> headers,
			final String[] methodsName) throws ClassNotFoundException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException
	{
        try (FileOutputStream outputStream = new FileOutputStream(fileName))
        {

            XSSFWorkbook workbook = new XSSFWorkbook();

            XSSFSheet sheet = workbook.createSheet(sheetName);//"Aircraft Configuration Report");

            XSSFFont font = workbook.createFont();
            Row header = sheet.createRow(headerRow);
            CellStyle csTitle = workbook.createCellStyle();
            font.setBold(true);
            csTitle.setWrapText(true);            
            csTitle.setFont(font);
 
            if(!headers.isEmpty())
            {
                int i = 0;
                for (String str : headers) //yamlExportConfig.getAcConfigReportHeader())
                {
                    Cell cell = header.createCell(i++);
                    cell.setCellValue(str);
                    cell.setCellStyle(csTitle);
                }  
            }
            
            //TODO: How to do it??
            AtomicInteger j = new AtomicInteger(headerRow + 1);
            rows.forEach(inventory -> {
            	int cellIndex = 0;
                //TODO To generic method
                Row row = sheet.createRow(j.getAndIncrement());
                Cell ataCell = row.createCell(cellIndex);
                Cell acPositionCell = row.createCell(cellIndex + 1);
                Cell equipmentPositionCell = row.createCell(cellIndex + 1);
                Cell serialNumberCell = row.createCell(cellIndex + 1);
                Cell partNumberCell = row.createCell(cellIndex + 1);
                Cell partDescriptionCell = row.createCell(cellIndex + 1);
                Cell installationDateCell = row.createCell(cellIndex + 1);
                
                //Value by method
                List<String> values = new ArrayList<String>();
                for(String methodName : methodsName)
                {
					try 
					{
						Object value = invokeMethodFromObjectInitialized("com.hums.therapy.model.database.mdif.Inventory", 
								methodName);
						values.add((String) value);
					} 
					catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException | NoSuchMethodException
							| SecurityException e)
					{
						e.printStackTrace();
					}
                }
                
                //TODO:
                /*
                ataCell.setCellValue(inventory.getAta());
                acPositionCell.setCellValue(inventory.getAircraftPosition());
                equipmentPositionCell.setCellValue(inventory.getEquipmentPosition());
                serialNumberCell.setCellValue(inventory.getSerialNumber());
                partNumberCell.setCellValue(inventory.getPartNumber());
                partDescriptionCell.setCellValue(inventory.getPartDescription());
                installationDateCell.setCellValue(inventory.getInstallationDate().format(DateTimeFormatter.ISO_DATE));
                */
            });
            
            workbook.write(outputStream);
            workbook.close();

        } 
        catch (IOException ignored)
        {
        }
        return new File(fileName);
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
	
	
	public Object createNewObjectAndInvokeMethod(String packageAndClassName, String methodName) throws 
		IllegalAccessException, IllegalArgumentException, 
		InvocationTargetException, ClassNotFoundException, InstantiationException,
		NoSuchMethodException, SecurityException
	{
       //String packageAndClassName = "com.mypackage.bean.Dog";
       Class<?> objectClass = Class.forName(packageAndClassName); // convert string classname to class
       Object objectMapped = objectClass.getDeclaredConstructor().newInstance(); // invoke empty constructor
       //String methodName = "";
       Method setNameMethod = null;
       // with single parameter, return void
       setNameMethod = objectClass.getClass().getMethod(methodName);
       return setNameMethod.invoke(objectMapped);
	}
	
	/**
	 * Invoke method from object initialized.
	 *
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
	 */
	public Object invokeMethodFromObjectInitialized(String packageAndClassName,
			String methodName) throws 
		ClassNotFoundException, InstantiationException, 
		IllegalAccessException, IllegalArgumentException, 
		InvocationTargetException, NoSuchMethodException, SecurityException
	{
		Class<?> objectClass = Class.forName(packageAndClassName);
		Object objectMapped = objectClass.getDeclaredConstructor();
		Method setNameMethod = objectClass.getClass().getMethod(methodName);
		return setNameMethod.invoke(objectMapped);
	}
}
