package hlf.ssconvert.ssconvert;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SsSequenceDs implements SequenceDs {

  private final String filename;
  private final String[] columnNames;

  public SsSequenceDs(String filename, String... columns) {
    this.filename = filename;
    this.columnNames = columns;
  }

  private XSSFWorkbook getWorkbook(String filename) throws Exception {
    XSSFWorkbook workbook = null;
    File file = new File(filename);
    InputStream fileInputStream = new FileInputStream(file);
    workbook = new XSSFWorkbook(fileInputStream);

    return workbook;
  }

  Map<String, Object> convertRow(XSSFSheet sheet, int row) {
    Map<String, Object> rowMap = new HashMap<>();
    XSSFRow ssRow = sheet.getRow(row);
    for (int i = 0; i < columnNames.length; i++) {
      XSSFCell cell = ssRow.getCell(i);
      rowMap.put(columnNames[i], getCellValueAsString(cell));
    }

    return rowMap;
  }

  static String getCellValueAsString(XSSFCell cell) {
    if (cell == null) {
      return "";
    } else if (CellType.NUMERIC == cell.getCellType()) {
      return new String(cell.getRawValue());
    } else {
      return cell.getStringCellValue();
    }
  }

  @Override
  public Iterable<Map<String, Object>> read() throws Exception {
    XSSFWorkbook workbook = getWorkbook(filename);
    XSSFSheet sheet = workbook.getSheetAt(0);
    int first = sheet.getFirstRowNum();
    int last = sheet.getLastRowNum();

    List<Map<String, Object>> rows = new ArrayList<>();

    for (int i = first; i <= last; i++) {
      rows.add(convertRow(sheet, i));
    }

    workbook.close();
    return rows;
  }
}
