package com.github.mwedgwood;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Importer<T> {

    private final Class<T> klass;

    public Importer(Class<T> klass) {
        this.klass = klass;
    }

    public Collection<T> parse(InputStream inputStream) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inputStream));

        Sheet sheet1 = wb.getSheetAt(0);
        HashMap<Integer, Method> columnIndexToSetter = parseMetaData(sheet1.getRow(1));

        sheet1.removeRow(sheet1.getRow(0));
        sheet1.removeRow(sheet1.getRow(1));

        Stream<Row> rowStream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(sheet1.rowIterator(), Spliterator.ORDERED),
                false
        );

        return rowStream
                .map(new RowMapper<>(klass, columnIndexToSetter)::map)
                .collect(Collectors.toList());
    }

    HashMap<Integer, Method> parseMetaData(Row row) {
        MetaDataCache.MetaData metaData = MetaDataCache.getInstance().getMetaDataForClass(klass);

        HashMap<Integer, Method> columnIndexToSetter = new HashMap<>();
        for (Cell cell : row) {
            String columnType = extractColumnType(cell.getRichStringCellValue().getString());
            Method setter = metaData.setterForColumn(columnType);
            if (setter == null)
                throw new IllegalArgumentException(klass.getName() + " has no property '" + columnType + "'");

            columnIndexToSetter.put(cell.getColumnIndex(), setter);
        }
        return columnIndexToSetter;
    }

    String extractColumnType(String columnType) {
        if (StringUtils.isBlank(columnType))
            throw new IllegalStateException("Header column type definition must have a value");

        return columnType.replaceAll("\\[|\\]", "");
    }

}
