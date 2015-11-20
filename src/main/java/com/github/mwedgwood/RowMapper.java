package com.github.mwedgwood;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Method;
import java.util.HashMap;

public class RowMapper<T> {

    private final Class<T> klass;
    private final HashMap<Integer, Method> columnIndexToSetter;

    public RowMapper(Class<T> klass, HashMap<Integer, Method> columnIndexToSetter) {
        this.klass = klass;
        this.columnIndexToSetter = columnIndexToSetter;
    }

    public T map(Row row) {
        try {
            T instance = klass.newInstance();

            for (Cell cell : row) {
                Method setter = columnIndexToSetter.get(cell.getColumnIndex());
                if (setter == null) continue;

                Class<?> aClass = setter.getParameterTypes()[0];
                setter.invoke(instance, aClass.cast(getCellValue(cell)));
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString();
            case Cell.CELL_TYPE_NUMERIC:
                return DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell.getNumericCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            default:
                throw new IllegalStateException("Unknown cell type");
        }
    }
}
