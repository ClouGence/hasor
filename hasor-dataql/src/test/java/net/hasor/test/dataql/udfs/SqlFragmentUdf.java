package net.hasor.test.dataql.udfs;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;

import java.util.HashMap;

public class SqlFragmentUdf implements Udf {
    private int index;

    public SqlFragmentUdf(int index) {
        this.index = index;
    }

    @Override
    public Object call(Hints readOnly, Object[] params) {
        return new HashMap<String, Object>() {{
            put("id", "id_" + index);
            put("name", "name_" + index);
            put("code", "code_" + index);
            put("body", params[0]);
        }};
    }
}