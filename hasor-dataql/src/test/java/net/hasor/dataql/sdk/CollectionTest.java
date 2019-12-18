package net.hasor.dataql.sdk;
import net.hasor.core.Hasor;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.binder.DataQL;
import net.hasor.dataql.runtime.InstructRuntimeException;
import org.junit.Test;

import java.io.IOException;

public class CollectionTest extends AbstractTestResource {
    @Test
    public void merge() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;";
        qlString = qlString + "return collect.merge(0,[1,2],[3,4],5,6,[7,8],[9])";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        assert dataModel.isListModel();
        assert ((ListModel) dataModel).size() == 10;
        assert ((ListModel) dataModel).asValueModel(5).asInt() == 5;
    }

    @Test
    public void filter() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;";
        qlString = qlString + "var dat = [0,1,2,3,4,5,6,7,8,9]; return collect.filter(dat,(obj) -> { return (obj >5) ? true : false })";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        assert dataModel.isListModel();
        assert ((ListModel) dataModel).size() == 4;
        assert ((ListModel) dataModel).asValueModel(0).asInt() == 6;
        assert ((ListModel) dataModel).asValueModel(1).asInt() == 7;
        assert ((ListModel) dataModel).asValueModel(2).asInt() == 8;
        assert ((ListModel) dataModel).asValueModel(3).asInt() == 9;
    }

    @Test
    public void limit() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;";
        qlString = qlString + "var dat = [0,1,2,3,4,5,6,7,8,9]; return collect.limit(dat,3,3)";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        assert dataModel.isListModel();
        assert ((ListModel) dataModel).size() == 3;
        assert ((ListModel) dataModel).asValueModel(0).asInt() == 3;
        assert ((ListModel) dataModel).asValueModel(1).asInt() == 4;
        assert ((ListModel) dataModel).asValueModel(2).asInt() == 5;
    }
}
