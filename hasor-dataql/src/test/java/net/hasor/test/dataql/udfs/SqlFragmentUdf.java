package net.hasor.test.dataql.udfs;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Hints;

import java.util.HashMap;
import java.util.Map;

public class SqlFragmentUdf implements FragmentProcess {
    private int index;

    public SqlFragmentUdf(int index) {
        this.index = index;
    }

    @Override
    public Object runFragment(Hints hint, Map<String, Object> params, String fragmentString) throws Throwable {
        return new HashMap<String, Object>() {{
            put("id", "id_" + index);
            put("name", "name_" + index);
            put("code", "code_" + index);
            put("body", fragmentString.trim());
            put("params", params);
        }};
    }
}