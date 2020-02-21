package net.hasor.dataql.functions.foos;
import net.hasor.core.Hasor;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.dataql.functions.AbstractTestResource;
import net.hasor.dataql.runtime.InstructRuntimeException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class CollectionTest extends AbstractTestResource {
    @Test
    public void merge() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.udfs.CollectionUdfSource' as collect;";
        qlString = qlString + "return collect.merge(0,[1,2],[3,4],5,6,[7,8],[9])";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        assert dataModel.isList();
        assert ((ListModel) dataModel).size() == 10;
        assert ((ListModel) dataModel).getValue(5).asInt() == 5;
    }

    @Test
    public void filter() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.udfs.CollectionUdfSource' as collect;";
        qlString = qlString + "var dat = [0,1,2,3,4,5,6,7,8,9]; return collect.filter(dat,(obj) -> { return (obj >5) ? true : false })";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        assert dataModel.isList();
        assert ((ListModel) dataModel).size() == 4;
        assert ((ListModel) dataModel).getValue(0).asInt() == 6;
        assert ((ListModel) dataModel).getValue(1).asInt() == 7;
        assert ((ListModel) dataModel).getValue(2).asInt() == 8;
        assert ((ListModel) dataModel).getValue(3).asInt() == 9;
    }

    @Test
    public void limit() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.udfs.CollectionUdfSource' as collect;";
        qlString = qlString + "var dat = [0,1,2,3,4,5,6,7,8,9]; return collect.limit(dat,3,3)";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
        //
        assert dataModel.isList();
        assert ((ListModel) dataModel).size() == 3;
        assert ((ListModel) dataModel).getValue(0).asInt() == 3;
        assert ((ListModel) dataModel).getValue(1).asInt() == 4;
        assert ((ListModel) dataModel).getValue(2).asInt() == 5;
    }

    @Test
    public void list2map() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.udfs.CollectionUdfSource' as collect;";
        qlString = qlString + "import 'net.hasor.test.dataql.beans.UserOrderUdfSource' as data;";
        qlString = qlString + "return collect.list2map(data.userList(),'userID')";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        ObjectModel dataModel = (ObjectModel) dataQL.createQuery(qlString).execute().getData();
        //
        assert dataModel.size() == 4;
        Set<String> strings = dataModel.asOri().keySet();
        assert strings.contains(String.valueOf(1));
        assert strings.contains(String.valueOf(2));
        assert strings.contains(String.valueOf(3));
        assert strings.contains(String.valueOf(4));
    }

    @Test
    public void map2list() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.udfs.CollectionUdfSource' as collect;";
        qlString = qlString + "import 'net.hasor.test.dataql.beans.UserOrderUdfSource' as data;";
        qlString = qlString + "return collect.map2list(data.userList()[0])";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        ListModel dataModel = (ListModel) dataQL.createQuery(qlString).execute().getData();
        //
        assert dataModel.size() == 8;
        List<Object> unwrap = dataModel.unwrap();
        //
        assert unwrap.size() == 8;
    }

    @Test
    public void empty() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.udfs.CollectionUdfSource' as collect;";
        qlString = qlString + "return collect.isEmpty([])";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        ValueModel dataModel = (ValueModel) dataQL.createQuery(qlString).execute().getData();
        //
        assert dataModel.asBoolean();
    }

    @Test
    public void empty2() throws IOException, InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.dataql.udfs.CollectionUdfSource' as collect;";
        qlString = qlString + "if (collect.isEmpty([])) return true else return false;";
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        ValueModel dataModel = (ValueModel) dataQL.createQuery(qlString).execute().getData();
        //
        assert dataModel.asBoolean();
    }
}
