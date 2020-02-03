package net.hasor.dataql.domain;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.Udf;
import net.hasor.test.dataql.udfs.DataBean;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ObjectDomainTest extends AbstractTestResource {
    private HashMap hashMap = new HashMap() {{
        put("data_str", "abc");
        put("data_boolean", true);
        put("data_int", 1234);
        put("data_null", null);
        put("data_big", new BigInteger("1234"));
        put("data_udf", PowerMockito.mock(Udf.class));
        put("data_list", new ArrayList<Object>() {{
            add("abc0");
            add("abc1");
            add("abc2");
            add(new ArrayList<String>() {{
                add("bcd0");
                add("bcd1");
                add("bcd2");
                add("bcd3");
            }});
            add(PowerMockito.mock(Udf.class));
        }});
        put("data_array", Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        put("data_bean", new DataBean());
        put("data_beans", Arrays.asList(new DataBean(), new DataBean(), new DataBean()));
    }};

    @Test
    public void ok_test() {
        ObjectModel objectModel = (ObjectModel) DomainHelper.convertTo(hashMap);
        assert objectModel.isValue("data_str");
        assert objectModel.isValue("data_boolean");
        assert objectModel.isValue("data_int");
        assert objectModel.isValue("data_null");
        assert objectModel.isValue("data_big");
        //
        assert !objectModel.isList("data_str");
        assert objectModel.isList("data_array");
        assert objectModel.isList("data_list");
        //
        assert !objectModel.isObject("data_big");
        assert !objectModel.isObject("data_list");
        assert objectModel.isObject("data_bean");
        //
        assert objectModel.isUdf("data_udf");
        assert objectModel.getUdf("data_udf") != null;
        //
        //
        //
        assert objectModel.getValue("data_str").isString();
        assert objectModel.getValue("data_boolean").isBoolean();
        assert objectModel.getValue("data_int").isInt();
        assert objectModel.getValue("data_null").isNull();
        //
        assert objectModel.getList("data_array").getValue(0).asInt() == 1;
        assert objectModel.getList("data_array").getValue(1).asInt() == 2;
        assert objectModel.getList("data_array").getValue(2).asInt() == 3;
        assert objectModel.getList("data_array").getValue(3).asInt() == 4;
        //
        assert objectModel.getList("data_list").isList(3);
        assert objectModel.getList("data_list").getList(3).isValue(0);
        assert objectModel.getList("data_list").getList(3).getValue(0).asString().equals("bcd0");
        assert objectModel.getList("data_list").getList(3).isValue(1);
        assert objectModel.getList("data_list").getList(3).getValue(1).asString().equals("bcd1");
        assert objectModel.getList("data_list").getList(3).isValue(2);
        assert objectModel.getList("data_list").getList(3).getValue(2).asString().equals("bcd2");
        assert objectModel.getList("data_list").isUdf(4);
        assert objectModel.getList("data_list").getUdf(4) != null;
        //
        assert objectModel.isObject("data_bean");
        assert objectModel.getObject("data_bean").isValue("name");
        assert objectModel.getObject("data_bean").getValue("name").asString().equals("马三");
        //
        assert objectModel.getList("data_beans").isObject(0);
        assert objectModel.getList("data_beans").getObject(0).isValue("name");
        assert objectModel.getList("data_beans").getObject(0).getValue("name").asString().equals("马三");
    }

    @Test
    public void failed_test() {
        ObjectModel objectModel = (ObjectModel) DomainHelper.convertTo(hashMap);
        try {
            objectModel.getValue("data_list");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to ValueModel.");
        }
        try {
            objectModel.getList("data_big");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to ListModel.");
        }
        try {
            objectModel.getObject("data_big");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to ObjectModel.");
        }
        try {
            objectModel.getUdf("data_big");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to UdfModel.");
        }
        //
        try {
            objectModel.getList("data_list").getObject(0);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to ObjectModel.");
        }
        try {
            objectModel.getList("data_list").getList(0);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to ListModel.");
        }
        try {
            objectModel.getList("data_list").getValue(3);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to ValueModel.");
        }
        try {
            objectModel.getList("data_list").getUdf(0);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to UdfModel.");
        }
    }
}