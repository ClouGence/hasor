package net.hasor.dataql.functions.foos;
import net.hasor.core.Hasor;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.dataql.functions.AbstractTestResource;
import net.hasor.dataql.runtime.InstructRuntimeException;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

public class IdentifierTest extends AbstractTestResource {
    @Test
    public void uuid() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.udfs.IdentifierUdfSource' as ids;";
        qlString = qlString + "return ids.uuid()";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        String uuid = ((ValueModel) dataModel).asString();
        assert uuid.length() == UUID.randomUUID().toString().length();
    }

    @Test
    public void uuid2() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.udfs.IdentifierUdfSource' as ids;";
        qlString = qlString + "return ids.uuid2()";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        String uuid = ((ValueModel) dataModel).asString();
        assert uuid.length() == UUID.randomUUID().toString().replace("-", "").length();
    }
}
