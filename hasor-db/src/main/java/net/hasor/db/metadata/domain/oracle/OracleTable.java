package net.hasor.db.metadata.domain.oracle;
import net.hasor.db.metadata.TableDef;

/**
 * Oracle 的 Table see: ALL_TABLES
 * @version : 2021-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class OracleTable implements TableDef {
    private String          schema;
    private String          table;
    private String          tableSpace;
    private Boolean         readOnly;
    private OracleTableType tableType;
    private String          materializedLog;
    private String          comment;

    @Override
    public String getCatalog() {
        return null;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTableSpace() {
        return this.tableSpace;
    }

    public void setTableSpace(String tableSpace) {
        this.tableSpace = tableSpace;
    }

    public Boolean getReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public OracleTableType getTableType() {
        return this.tableType;
    }

    public void setTableType(OracleTableType tableType) {
        this.tableType = tableType;
    }

    public String getMaterializedLog() {
        return this.materializedLog;
    }

    public void setMaterializedLog(String materializedLog) {
        this.materializedLog = materializedLog;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
