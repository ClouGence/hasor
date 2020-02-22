package net.hasor.dataql.fx.foos;
import net.hasor.core.Hasor;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.fx.AbstractTestResource;
import net.hasor.dataql.runtime.InstructRuntimeException;
import org.junit.Test;

import java.io.IOException;

public class StateTest extends AbstractTestResource {
    @Test
    public void decInt() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.fx.StateUdfSource' as state;";
        qlString = qlString + "var decNum = state.decInt(0); return [ decNum(),decNum(),decNum() ]";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        assert ((ListModel) dataModel).getValue(0).asLong() == 1;
        assert ((ListModel) dataModel).getValue(1).asLong() == 2;
        assert ((ListModel) dataModel).getValue(2).asLong() == 3;
    }

    @Test
    public void decLong() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.fx.StateUdfSource' as state;";
        qlString = qlString + "var decNum = state.decLong(0); return [ decNum(),decNum(),decNum() ]";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        assert ((ListModel) dataModel).getValue(0).asLong() == 1;
        assert ((ListModel) dataModel).getValue(1).asLong() == 2;
        assert ((ListModel) dataModel).getValue(2).asLong() == 3;
    }

    @Test
    public void incInt() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.fx.StateUdfSource' as state;";
        qlString = qlString + "var decNum = state.incInt(0); return [ decNum(),decNum(),decNum() ]";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        assert ((ListModel) dataModel).getValue(0).asLong() == -1;
        assert ((ListModel) dataModel).getValue(1).asLong() == -2;
        assert ((ListModel) dataModel).getValue(2).asLong() == -3;
    }

    @Test
    public void incLong() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.fx.StateUdfSource' as state;";
        qlString = qlString + "var decNum = state.incLong(0); return [ decNum(),decNum(),decNum() ]";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        assert ((ListModel) dataModel).getValue(0).asLong() == -1;
        assert ((ListModel) dataModel).getValue(1).asLong() == -2;
        assert ((ListModel) dataModel).getValue(2).asLong() == -3;
    }
}
