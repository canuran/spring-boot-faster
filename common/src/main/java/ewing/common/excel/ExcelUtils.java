package ewing.common.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel工具类。
 *
 * @author caiyouyuan
 * @since 2019年06月15日
 */
public class ExcelUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 导出单Sheet页的Excel。
     *
     * @return XSSF格式的Workbook，即xlsx格式。
     */
    public static <T> Workbook exportExcel(Class<T> type, List<T> data) {
        Workbook workbook = new XSSFWorkbook();
        writeDataToSheet(workbook.createSheet(), type, data);
        return workbook;
    }

    /**
     * 导出单Sheet页的Excel。
     */
    public static <T> void exportExcel(Class<T> type, List<T> data, OutputStream outputStream) {
        Workbook workbook = exportExcel(type, data);
        try {
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Write excel failure", e);
        }
    }

    /**
     * 导出单Sheet页的Excel。
     */
    public static <T> void exportExcel(Class<T> type, List<T> data, String fileName, HttpServletResponse response) {
        Workbook workbook = exportExcel(type, data);
        writeWorkBookToResponse(fileName, response, workbook);
    }

    /**
     * 导出多Sheet页的Excel。
     *
     * @param type 具有多个带@ExcelSheet注解的List属性的类。
     * @return XSSF格式的Workbook，即xlsx格式。
     */
    @SuppressWarnings("unchecked")
    public static <T> Workbook exportMultiSheetExcel(Class<T> type, T data) {
        Workbook workbook = new XSSFWorkbook();
        if (type == null || data == null) {
            return workbook;
        }
        try {
            for (Field field : type.getDeclaredFields()) {
                // 需要是List属性
                if (List.class.isAssignableFrom(field.getType())) {
                    // 需要有泛型参数
                    Type genericType = field.getGenericType();
                    if (!(genericType instanceof ParameterizedType)) {
                        continue;
                    }

                    Type[] fieldTypes = ((ParameterizedType) genericType).getActualTypeArguments();
                    if (fieldTypes.length < 1 || !(fieldTypes[0] instanceof Class)) {
                        continue;
                    }

                    // 需要获取ExcelSheet注解
                    ExcelSheet excelSheet = field.getAnnotation(ExcelSheet.class);
                    if (excelSheet == null) {
                        continue;
                    }

                    // 创建Sheet设置默认值
                    Sheet sheet = excelSheet.value().isEmpty() ? workbook.createSheet() :
                            workbook.createSheet(excelSheet.value());
                    sheet.setDefaultColumnWidth(excelSheet.columnWidth());
                    if (excelSheet.freezeFirstRow() || excelSheet.freezeFirstColumn()) {
                        sheet.createFreezePane(excelSheet.freezeFirstColumn() ? 1 : 0,
                                excelSheet.freezeFirstRow() ? 1 : 0,
                                excelSheet.freezeFirstColumn() ? 1 : 0,
                                excelSheet.freezeFirstRow() ? 1 : 0);
                    }

                    field.setAccessible(true);
                    writeDataToSheet(sheet, (Class) fieldTypes[0], (List) field.get(data));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Generate multi sheet excel failure", e);
        }
        return workbook;
    }

    /**
     * 导出多Sheet页的Excel。
     *
     * @param type 具有多个带@ExcelSheet注解的List<T>属性的类。
     */
    public static <T> void exportMultiSheetExcel(Class<T> type, T data, String fileName, HttpServletResponse response) {
        Workbook workbook = exportMultiSheetExcel(type, data);
        writeWorkBookToResponse(fileName, response, workbook);
    }

    /**
     * 导出多Sheet页的Excel。
     *
     * @param type 具有多个带@ExcelSheet注解的List<T>属性的类。
     */
    public static <T> void exportMultiSheetExcel(Class<T> type, T data, OutputStream outputStream) {
        Workbook workbook = exportMultiSheetExcel(type, data);
        try {
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Write multi sheet excel failure", e);
        }
    }

    private static <T> void writeDataToSheet(Sheet sheet, Class<T> type, List<T> data) {
        try {
            if (sheet == null || type == null) {
                return;
            }
            // 读取并写入标题字段
            Row titleRow = sheet.createRow(0);

            List<Field> dataFields = new ArrayList<>();
            Field[] fields = type.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                // 需要获取ExcelColumn注解
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                if (excelColumn == null) {
                    continue;
                }
                if (excelColumn.columnWidth() > 0) {
                    sheet.setColumnWidth(i, excelColumn.columnWidth() * 256);
                }

                titleRow.createCell(i).setCellValue(excelColumn.value());
                field.setAccessible(true);
                dataFields.add(field);
            }

            // 读取并写入Excel数据
            if (dataFields.isEmpty() || data == null || data.isEmpty()) {
                return;
            }

            int rowIndex = 1;
            for (Object rowData : data) {
                Row row = sheet.createRow(rowIndex++);
                for (int i = 0; i < dataFields.size(); i++) {
                    Field dataField = dataFields.get(i);
                    Object value = dataField.get(rowData);
                    row.createCell(i).setCellValue(value == null ? "" : value.toString());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Generate excel sheet failure", e);
        }
    }

    private static void writeWorkBookToResponse(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ServletOutputStream outputStream = null;
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Write excel to response failure", e);
        }
    }

}
