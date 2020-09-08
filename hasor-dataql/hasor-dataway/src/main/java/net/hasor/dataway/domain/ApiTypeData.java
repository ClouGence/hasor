package net.hasor.dataway.domain;
import java.util.List;

public class ApiTypeData {
    private String           exampleData; // 数据样本（Body or Params）
    private List<HeaderData> headerData;  // 头参数（Header 信息）
    private String           jsonSchema;  // JsonSchema
    private String           comment;     // 备注

    public String getExampleData() {
        return exampleData;
    }

    public void setExampleData(String exampleData) {
        this.exampleData = exampleData;
    }

    public List<HeaderData> getHeaderData() {
        return headerData;
    }

    public void setHeaderData(List<HeaderData> headerData) {
        this.headerData = headerData;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(String jsonSchema) {
        this.jsonSchema = jsonSchema;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}