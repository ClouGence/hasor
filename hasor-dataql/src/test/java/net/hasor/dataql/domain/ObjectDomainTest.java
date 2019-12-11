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
        assert objectModel.isValueModel("data_str");
        assert objectModel.isValueModel("data_boolean");
        assert objectModel.isValueModel("data_int");
        assert objectModel.isValueModel("data_null");
        assert objectModel.isValueModel("data_big");
        //
        assert !objectModel.isListModel("data_str");
        assert objectModel.isListModel("data_array");
        assert objectModel.isListModel("data_list");
        //
        assert !objectModel.isObjectModel("data_big");
        assert !objectModel.isObjectModel("data_list");
        assert objectModel.isObjectModel("data_bean");
        //
        assert objectModel.isUdfModel("data_udf");
        assert objectModel.asUdfModel("data_udf") != null;
        //
        //
        //
        assert objectModel.asValueModel("data_str").isString();
        assert objectModel.asValueModel("data_boolean").isBoolean();
        assert objectModel.asValueModel("data_int").isInt();
        assert objectModel.asValueModel("data_null").isNull();
        //
        assert objectModel.asListModel("data_array").asValueModel(0).asInt() == 1;
        assert objectModel.asListModel("data_array").asValueModel(1).asInt() == 2;
        assert objectModel.asListModel("data_array").asValueModel(2).asInt() == 3;
        assert objectModel.asListModel("data_array").asValueModel(3).asInt() == 4;
        //
        assert objectModel.asListModel("data_list").isListModel(3);
        assert objectModel.asListModel("data_list").asListModel(3).isValueModel(0);
        assert objectModel.asListModel("data_list").asListModel(3).asValueModel(0).asString().equals("bcd0");
        assert objectModel.asListModel("data_list").asListModel(3).isValueModel(1);
        assert objectModel.asListModel("data_list").asListModel(3).asValueModel(1).asString().equals("bcd1");
        assert objectModel.asListModel("data_list").asListModel(3).isValueModel(2);
        assert objectModel.asListModel("data_list").asListModel(3).asValueModel(2).asString().equals("bcd2");
        assert objectModel.asListModel("data_list").isUdfModel(4);
        assert objectModel.asListModel("data_list").asUdfModel(4) != null;
        //
        assert objectModel.isObjectModel("data_bean");
        assert objectModel.asObjectModel("data_bean").isValueModel("name");
        assert objectModel.asObjectModel("data_bean").asValueModel("name").asString().equals("马三");
        //
        assert objectModel.asListModel("data_beans").isObjectModel(0);
        assert objectModel.asListModel("data_beans").asObjectModel(0).isValueModel("name");
        assert objectModel.asListModel("data_beans").asObjectModel(0).asValueModel("name").asString().equals("马三");
    }

    @Test
    public void failed_test() {
        ObjectModel objectModel = (ObjectModel) DomainHelper.convertTo(hashMap);
        try {
            objectModel.asValueModel("data_list");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to ValueModel.");
        }
        try {
            objectModel.asListModel("data_big");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to ListModel.");
        }
        try {
            objectModel.asObjectModel("data_big");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to ObjectModel.");
        }
        try {
            objectModel.asUdfModel("data_big");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to UdfModel.");
        }
        //
        try {
            objectModel.asListModel("data_list").asObjectModel(0);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to ObjectModel.");
        }
        try {
            objectModel.asListModel("data_list").asListModel(0);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to ListModel.");
        }
        try {
            objectModel.asListModel("data_list").asValueModel(3);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to ValueModel.");
        }
        try {
            objectModel.asListModel("data_list").asUdfModel(0);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" not Cast to UdfModel.");
        }
    }
}