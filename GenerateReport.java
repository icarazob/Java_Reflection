/**
 * 
 */
package com.hums.therapy.services.mdif;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hums.therapy.services.mdif.GenerateReport.ExcelType;

/**
 * @author israel.carazo
 *
 */
public interface GenerateReport 
{
	
	enum ExcelType 
	{
		XLS,
		XLSX
	}
	
	void generateReportWithMultipleSheets(StringBuilder fileName, final List<String> sheetsName, 
			Map<Integer, String> titles, final int titlesPosition,
			final List<?> rows);

	void generateReport(StringBuilder fileName, final String sheetName, 
			Map<Integer, String> titles, final int titlesPosition,
			final List<?> rows, final List<String> subSheetsName,
			ExcelType type);
	
	File generateReportAndReturnFile(StringBuilder fileName, final String sheetName, 
			Map<Integer, String> titles, final int titlesPosition,
			final List<?> rows, final List<String> subSheetsName,
			ExcelType type);
	
	void generateTitles(Map<Integer, String> titles, XSSFSheet sheet, final int row);
	
	void newSheet(String sheetName, XSSFWorkbook exceldoc);
	
	String setExcelFileType(ExcelType type);
	
	void makeExcel(List<?> rows, XSSFSheet sheet,
			int rowCount) throws Exception;
	
	boolean saveExcelFile(String path, String filename, XSSFWorkbook exceldoc);
	
	File generateExcelFileAsObjectFile(String path, String filename, XSSFWorkbook exceldoc);

}
