package net.hasor.dataql.fx.foos;
import net.hasor.core.Hasor;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.dataql.fx.AbstractTestResource;
import net.hasor.dataql.runtime.InstructRuntimeException;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

public class IdentifierTest extends AbstractTestResource {
    @Test
    public void uuid() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.fx.basic.StateUdfSource' as state;";
        qlString = qlString + "return state.uuid()";
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
        qlString = qlString + "import 'net.hasor.dataql.fx.basic.StateUdfSource' as state;";
        qlString = qlString + "return state.uuidToShort();";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        String uuid = ((ValueModel) dataModel).asString();
        assert uuid.length() == UUID.randomUUID().toString().replace("-", "").length();
    }
}
