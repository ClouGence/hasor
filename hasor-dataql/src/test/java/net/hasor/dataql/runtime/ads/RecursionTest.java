package net.hasor.dataql.runtime.ads;
import net.hasor.core.Hasor;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.Query;
import net.hasor.dataql.UDF;
import net.hasor.dataql.extend.binder.DataQL;
import net.hasor.dataql.runtime.InstructRuntimeException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RecursionTest extends AbstractTestResource {
    @Test
    public void recursion() throws IOException, InstructRuntimeException {
        ArrayList<Object> finalData = new ArrayList<>();
        UDF addToArray = (params, readOnly) -> {
            finalData.add(params[0]);
            return null;
        };
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        String recursionScript = getScript("/net_hasor_dataql_adv/recursion.ql");
        Query query = dataQL.createQuery(recursionScript);
        query.execute(new HashMap<String, Object>() {{
            put("addToArray", addToArray);
        }});
        //
        assert finalData.size() == 10;
    }
}
