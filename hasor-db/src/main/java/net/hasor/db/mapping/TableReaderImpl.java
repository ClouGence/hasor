package net.hasor.db.mapping;
import net.hasor.db.metadata.CaseSensitivityType;
import net.hasor.db.types.TypeHandler;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
import net.hasor.utils.ref.LinkedCaseInsensitiveMap;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

class TableReaderImpl<T> implements TableReader<T> {
    private final Class<T>                  mapperClass;
    private final TableMapping              tableMapping;
    //
    private final List<String>              columnNames;
    private final Map<String, List<String>> columnPropertyMapping;
    private final Map<String, String>       propertyForWriteByColumn;

    /** Create a new TableReader.*/
    public TableReaderImpl(Class<T> mapperClass, TableMapping tableMapping) {
        this.mapperClass = mapperClass;
        this.tableMapping = tableMapping;
        this.columnNames = new ArrayList<>();
        this.columnPropertyMapping = new HashMap<>();
        this.propertyForWriteByColumn = new HashMap<>();
        //
        List<ColumnMapping> properties = tableMapping.getProperties();
        for (ColumnMapping property : properties) {
            String propertyName = property.getPropertyName();
            String columnName = property.getName();
            this.columnNames.add(property.getName());
            List<String> propertyNames = this.columnPropertyMapping.computeIfAbsent(columnName, k -> new ArrayList<>());
            propertyNames.add(propertyName);
            //
            if (property.isInsert() || property.isUpdate()) {
                if (this.propertyForWriteByColumn.containsKey(columnName)) {
                    String differentProperty = "'" + propertyName + "','" + this.propertyForWriteByColumn.get(columnName) + "'";
                    throw new IllegalStateException("mapping different property " + differentProperty + " write the same column '" + columnName + "'.");
                } else {
                    this.propertyForWriteByColumn.put(columnName, propertyName);
                }
            }
        }
    }

    @Override
    public Class<T> getMapperClass() {
        return this.mapperClass;
    }

    @Override
    public TableMapping getTableMapping() {
        return this.tableMapping;
    }

    public ColumnMapping getPropertyForWriteByColumn(String columnName) {
        String propertyName = this.propertyForWriteByColumn.get(columnName);
        if (StringUtils.isBlank(propertyName)) {
            return null;
        }
        return this.getTableMapping().getMapping(propertyName);
    }

    @Override
    public T readRow(ResultSet rs, int rowNum) throws SQLException {
        T targetObject;
        try {
            targetObject = this.mapperClass.newInstance();
            return this.tranResultSet(rs, targetObject);
        } catch (ReflectiveOperationException e) {
            throw new SQLException(e);
        }
    }

    private T tranResultSet(final ResultSet rs, final T targetObject) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int nrOfColumns = rsmd.getColumnCount();
        List<String> resultColumns = new ArrayList<>();
        Map<String, Integer> resultColumnIndexMap = this.tableMapping.getCaseSensitivity() == CaseSensitivityType.Fuzzy ? new LinkedCaseInsensitiveMap<>() : new LinkedHashMap<>();
        for (int i = 1; i <= nrOfColumns; i++) {
            String colName = rsmd.getColumnName(i);
            if (!resultColumnIndexMap.containsKey(colName)) {
                resultColumnIndexMap.put(colName, i);
                resultColumns.add(colName);
            }
        }
        //
        for (String columnName : this.columnNames) {
            if (!resultColumnIndexMap.containsKey(columnName)) {
                continue;
            }
            int realIndex = resultColumnIndexMap.get(columnName);
            List<String> propertyNames = this.columnPropertyMapping.get(columnName);
            for (String propertyName : propertyNames) {
                ColumnMapping mapping = this.tableMapping.getMapping(propertyName);
                TypeHandler<?> realHandler = mapping.getTypeHandler();
                Object result = realHandler.getResult(rs, realIndex);
                //
                Class<?> propertyType = BeanUtils.getPropertyOrFieldType(this.mapperClass, propertyName);
                Object convert = ConverterUtils.convert(propertyType, result);
                BeanUtils.writePropertyOrField(targetObject, propertyName, convert);
            }
        }
        return targetObject;
    }
}
