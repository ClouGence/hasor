package net.hasor.dataql.runtime.basic;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.OptionValue;
import net.hasor.dataql.Query;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.domain.ObjectModel;
import org.junit.Test;

public class ObjectRuntimeTest extends AbstractTestResource implements OptionValue {
    @Test
    public void obj_1_Test() throws Exception {
        Query compilerQL = compilerQL("return {'a':'abc-true','b':'bcd-false'}");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isObjectModel();
        assert ((ObjectModel) dataModel).asValueModel("a").asString().equals("abc-true");
        assert ((ObjectModel) dataModel).asValueModel("b").asString().equals("bcd-false");
    }

    @Test
    public void obj_2_Test() throws Exception {
        Query compilerQL = compilerQL("return {'a':'abc-true','b': { 'c' :'bcd-false','d':true }}");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isObjectModel();
        assert ((ObjectModel) dataModel).asValueModel("a").asString().equals("abc-true");
        assert ((ObjectModel) dataModel).isObjectModel("b");
        assert ((ObjectModel) dataModel).asObjectModel("b").asValueModel("c").asString().equals("bcd-false");
        assert ((ObjectModel) dataModel).asObjectModel("b").asValueModel("d").asBoolean();
    }

    @Test
    public void list_1_Test() throws Exception {
        Query compilerQL = compilerQL("return [{},true,123,0x123]");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isListModel();
        assert ((ListModel) dataModel).isObjectModel(0);
        assert ((ListModel) dataModel).asObjectModel(0).size() == 0;
        assert ((ListModel) dataModel).asValueModel(1).asBoolean();
        assert ((ListModel) dataModel).asValueModel(2).asInt() == 123;
        assert ((ListModel) dataModel).asValueModel(3).asInt() == 0x123;
    }

    @Test
    public void list_2_Test() throws Exception {
        Query compilerQL = compilerQL("return {'a':'abc-true','b': [ {'c' :'bcd-false'},true]}");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isObjectModel();
        assert ((ObjectModel) dataModel).asValueModel("a").asString().equals("abc-true");
        assert ((ObjectModel) dataModel).isListModel("b");
        assert ((ObjectModel) dataModel).asListModel("b").isObjectModel(0);
        assert ((ObjectModel) dataModel).asListModel("b").asObjectModel(0).asValueModel("c").asString().equals("bcd-false");
        assert ((ObjectModel) dataModel).asListModel("b").isValueModel(1);
        assert ((ObjectModel) dataModel).asListModel("b").asValueModel(1).asBoolean();
    }
}