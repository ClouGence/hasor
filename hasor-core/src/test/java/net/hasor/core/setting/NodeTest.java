/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.core.setting;
import net.hasor.core.setting.data.TreeNode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class NodeTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void treeNode_01() {
        TreeNode treeNode = new TreeNode("DDDD", "");
        //
        treeNode.addValue("settings.abc", "123");
        assert treeNode.getSubNode("settings").getSubNode("abc").getValue().equals("123");
        assert treeNode.getFullName().equals("");
        assert treeNode.getSubNode("settings").getFullName().equals("settings");
        assert treeNode.getSubNode("settings").getSubNode("abc").getFullName().equals("settings.abc");
        assert treeNode.findNode("settings.abc").getValue().equals("123");
        assert treeNode.getSubNode("settings").getParent() == treeNode;
        assert treeNode.getSpace().equals("DDDD");
    }

    @Test
    public void treeNode_02() {
        TreeNode treeNode = new TreeNode();
        //
        treeNode.addValue("settings.abc", "1");
        treeNode.addValue("settings.abc", "2");
        treeNode.addValue("settings.abc", "3");
        assert treeNode.findNode("settings.abc").getValue().equals("3");
        assert treeNode.findNode("settings.abc").getValues().length == 3;
        assert treeNode.findNode("settings.abc").getValues()[0].equals("1");
        assert treeNode.findNode("settings.abc").getValues()[1].equals("2");
        assert treeNode.findNode("settings.abc").getValues()[2].equals("3");
        //
        treeNode.setValue("settings.abc", "4");
        assert treeNode.findNode("settings.abc").getValues().length == 1;
        assert treeNode.findNode("settings.abc").getValues()[0].equals("4");
        assert treeNode.findNode("settings.abc").getValue().equals("4");
        //
        treeNode.findNode("settings.abc").clearValue();
        assert treeNode.findNode("settings.abc").getValues().length == 0;
        //
        treeNode.clear();
        assert treeNode.findNode("settings.abc") == null;
    }

    @Test
    public void treeNode_03() {
        TreeNode treeNode = new TreeNode();
        //
        treeNode.addValue("settings.abc", "1");
        treeNode.addValue("settings.abc", "2");
        treeNode.addValue("settings.abc", "3");
        assert treeNode.getSubNode("settings").getValues().length == 0;
        assert treeNode.getSubNode("settings.abc") == null;
        List<String> stringStream = Arrays.stream(treeNode.getSubNodes()).map(SettingNode::getName).collect(Collectors.toList());
        assert stringStream.contains("settings");
        assert stringStream.size() == 1;
        //
        TreeNode settings = treeNode.findNode("settings");
        assert settings.findNodes("abc").size() == 1;
    }

    @Test
    public void treeNode_04() {
        TreeNode treeNode = new TreeNode();
        //
        treeNode.addValue("settings.a", "1");
        treeNode.addValue("settings.b", "2-1");
        treeNode.addValue("settings.b", "2-2");
        treeNode.addValue("settings.b", "2-3");
        treeNode.addValue("settings.c", "3-1");
        treeNode.addValue("settings.c", "3-2");
        //
        assert treeNode.getSubNode("settings.abc") == null;
        assert treeNode.getSubValue("settings.abc") == null;
        assert treeNode.getSubValues("settings.abc").length == 0;
        //
        treeNode = treeNode.getSubNode("settings");
        assert treeNode.getSubValue("abc") == null;
        assert treeNode.getSubValue("a").equals("1");
        assert treeNode.getSubValue("b").equals("2-3");
        assert treeNode.getSubValue("c").equals("3-2");
        //
        assert treeNode.getSubValues("a").length == 1;
        assert treeNode.getSubValues("b").length == 3;
        assert treeNode.getSubValues("c").length == 2;
        //
        List<String> collect = Arrays.stream(treeNode.getSubNodes()).map(SettingNode::getName).collect(Collectors.toList());
        assert collect.size() == 3;
        assert collect.contains("a");
        assert collect.contains("b");
        assert collect.contains("c");
    }

    @Test
    public void treeNode_05() {
        TreeNode treeNode1 = new TreeNode();
        treeNode1.addSubNode(new TreeNode("a")).setValue("1");
        treeNode1.addSubNode(new TreeNode("a")).setValue("2");
        treeNode1.addSubNode(new TreeNode("b")).setValue("3");
        //
        assert treeNode1.getSubNodes("a").length == 2;
        assert treeNode1.getSubNodes("b").length == 1;
        assert treeNode1.getSubNodes("c").length == 0;
        //
        assert treeNode1.getSubNodes("a", n -> n.getValue().equals("2")).length == 1;
        assert treeNode1.getSubNodes("a", n -> n.getValue().equals("2"))[0].getValue().equals("2");
        assert treeNode1.getSubNodes("a", n -> n.getValue().equals("3")).length == 0;
    }

    @Test
    public void treeNode_06() {
        TreeNode dataNode = new TreeNode();
        dataNode.addSubNode(new TreeNode("a")).setValue("1");
        dataNode.addSubNode(new TreeNode("a")).setValue("2");
        dataNode.addSubNode(new TreeNode("b")).setValue("3");
        //
        try {
            TreeNode root = new TreeNode();
            root.addSubNode(dataNode);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("elementName must not blank.");
        }
        //
        try {
            TreeNode root = new TreeNode();
            root.addSubNode("dat", dataNode);
            //
            assert root.getSubNodes("dat").length == 1;
            assert root.getSubNodes("dat")[0].getSubNodes("a").length == 2;
            assert root.getSubNodes("dat")[0].getSubNodes("a")[0].getValue().equals("1");
            assert root.getSubNodes("dat")[0].getSubNodes("a")[1].getValue().equals("2");
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    public void treeNode_07() {
        TreeNode dataNode = new TreeNode();
        dataNode.addSubNode(new TreeNode("a")).setValue("1");
        dataNode.addSubNode(new TreeNode("a")).setValue("2");
        dataNode.addSubNode(new TreeNode("A")).setValue("3");
        //
        assert dataNode.getSubValues("a").length == 2;
        assert dataNode.getSubValues("a")[0].equals("1");
        assert dataNode.getSubValues("a")[1].equals("2");
        assert dataNode.getSubValues("A")[0].equals("3");
        //
        assert dataNode.getSubNodes().length == 3;
        assert dataNode.getSubKeys().length == 2;
        assert Arrays.asList(dataNode.getSubKeys()).contains("a");
        assert Arrays.asList(dataNode.getSubKeys()).contains("A");
        //
        for (SettingNode sn : dataNode.getSubNodes()) {
            assert sn.getValues().length == 1;
        }
    }

    @Test
    public void treeNode_08() {
        TreeNode treeNode = new TreeNode();
        //
        assert treeNode.getSubNodes() != null;
        assert treeNode.getSubNodes().length == 0;
        //
        assert treeNode.getSubNodes("a", s -> true) != null;
        assert treeNode.getSubNodes("a", s -> true).length == 0;
    }

    @Test
    public void treeNode_09() {
        TreeNode treeNode = new TreeNode();
        treeNode.addValue("a", "1");
        treeNode.addValue("a", "2");
        assert treeNode.getSubValue("a").equals("2");
        assert treeNode.getSubValues("a").length == 2;
        assert treeNode.getSubValues("a")[0].equals("1");
        assert treeNode.getSubValues("a")[1].equals("2");
        assert treeNode.getSubNodes().length == 1;
        assert treeNode.getSubNodes()[0].getValue().equals("2");
        assert treeNode.getSubNodes()[0].getValues().length == 2;
        assert treeNode.getSubNodes()[0].getValues()[0].equals("1");
        assert treeNode.getSubNodes()[0].getValues()[1].equals("2");
        //
        treeNode.addSubNode(new TreeNode("a"), true).addValue("3");
        assert treeNode.getSubValue("a").equals("3");
        assert treeNode.getSubValues("a").length == 3;
        assert treeNode.getSubValues("a")[0].equals("1");
        assert treeNode.getSubValues("a")[1].equals("2");
        assert treeNode.getSubValues("a")[2].equals("3");
        assert treeNode.getSubNodes().length == 2;
        assert treeNode.getSubNodes()[0].getValue().equals("2");
        assert treeNode.getSubNodes()[0].getValues().length == 2;
        assert treeNode.getSubNodes()[0].getValues()[0].equals("1");
        assert treeNode.getSubNodes()[0].getValues()[1].equals("2");
        assert treeNode.getSubNodes()[1].getValue().equals("3");
        assert treeNode.getSubNodes()[1].getValues().length == 1;
        assert treeNode.getSubNodes()[1].getValues()[0].equals("3");
        //
        treeNode.addValue("a", "4");
        assert treeNode.getSubValue("a").equals("4");
        assert treeNode.getSubValues("a").length == 4;
        assert treeNode.getSubValues("a")[0].equals("1");
        assert treeNode.getSubValues("a")[1].equals("2");
        assert treeNode.getSubValues("a")[2].equals("3");
        assert treeNode.getSubValues("a")[3].equals("4");
        assert treeNode.getSubNodes().length == 2;
        assert treeNode.getSubNodes()[0].getValue().equals("2");
        assert treeNode.getSubNodes()[0].getValues().length == 2;
        assert treeNode.getSubNodes()[0].getValues()[0].equals("1");
        assert treeNode.getSubNodes()[0].getValues()[1].equals("2");
        assert treeNode.getSubNodes()[1].getValue().equals("4");
        assert treeNode.getSubNodes()[1].getValues().length == 2;
        assert treeNode.getSubNodes()[1].getValues()[0].equals("3");
        assert treeNode.getSubNodes()[1].getValues()[1].equals("4");
    }

    @Test
    public void treeNode_10() {
        TreeNode treeNode = new TreeNode();
        treeNode.newSubNode("a").addValue("1");
        treeNode.addValue("a", "2");
        //
        treeNode.addSubNode(new TreeNode("a"), true).addValue("3");
        treeNode.addValue("a", "4");
        //
        assert treeNode.getSubKeys().length == 1;
        assert treeNode.getSubKeys()[0].equals("a");
        assert treeNode.getSubNodes().length == 2;
        //
        assert treeNode.getSubNodes()[0].getValue().equals("2");
        assert treeNode.getSubNodes()[0].getValues()[0].equals("1");
        assert treeNode.getSubNodes()[0].getValues()[1].equals("2");
        //
        assert treeNode.getSubNodes()[1].getValue().equals("4");
        assert treeNode.getSubNodes()[1].getValues()[0].equals("3");
        assert treeNode.getSubNodes()[1].getValues()[1].equals("4");
    }

    @Test
    public void treeNode_11() {
        // conf.a = 1
        TreeNode conf = new TreeNode("conf");
        conf.addValue("a", "1");
        //
        // root.dat.my = conf
        TreeNode root = new TreeNode();
        root.addValue("root.dat.my.a", "2");
        //
        assert root.findNode("root.dat.my.a").getValues().length == 1;
        assert root.findNode("root.dat.my.a").getValue().equals("2");
        //
        root.setNode("root.dat.my", conf);
        assert root.findNode("root.dat.my.a").getValues().length == 1;
        assert root.findNode("root.dat.my.a").getValue().equals("1");
    }

    @Test
    public void treeNode_12() {
        // conf.a = 1
        TreeNode conf = new TreeNode("conf");
        conf.addValue("a", "1");
        //
        // root.dat.my = conf
        TreeNode root = new TreeNode();
        root.addValue("root.dat.my.a", "2");
        //
        assert root.findNode("root.dat.my.a").getValues().length == 1;
        assert root.findNode("root.dat.my.a").getValue().equals("2");
        //
        root.addNode("root.dat.my", conf);
        assert root.findValues("root.dat.my.a").length == 2;
        assert root.findValue("root.dat.my.a").equals("1");
        assert root.findValues("root.dat.my.a")[0].equals("2");
        assert root.findValues("root.dat.my.a")[1].equals("1");
    }

    @Test
    public void treeNode_13() {
        TreeNode conf = new TreeNode();
        conf.addValue("a", "1");
        conf.addValue("root.dat.my.a", "2");
        //
        assert conf.findNode(".") == conf;
        assert conf.findNode("") == conf;
        //
        assert conf.findNodes(".").size() == 1;
        assert conf.findNodes(".").get(0) == conf;
        assert conf.findNodes("").size() == 1;
        assert conf.findNodes("").get(0) == conf;
    }

    @Test
    public void treeNode_14() {
        TreeNode conf = new TreeNode();
        conf.addValue("a", "1");
        conf.addValue("b", "2");
        conf.addValue("abc.a", "3");
        conf.addValue("abc.a", "4");
        //
        Map<String, String> toMap = conf.toMap();
        assert toMap.size() == 3;
        assert toMap.get("a").equals("1");
        assert toMap.get("b").equals("2");
        assert toMap.get("abc.a").equals("4");
    }

    @Test
    public void treeNode_15() {
        TreeNode conf = new TreeNode();
        conf.addValue("a", "1");
        conf.addValue("b", "2");
        conf.addValue("abc.a", "3");
        conf.addValue("abc.a", "4");
        //
        Map<String, List<String>> toMap = conf.toMapList();
        assert toMap.size() == 3;
        assert toMap.get("a").size() == 1;
        assert toMap.get("a").get(0).equals("1");
        assert toMap.get("b").size() == 1;
        assert toMap.get("b").get(0).equals("2");
        assert toMap.get("abc.a").size() == 2;
        assert toMap.get("abc.a").get(0).equals("3");
        assert toMap.get("abc.a").get(1).equals("4");
    }

    @Test
    public void treeNode_16() {
        TreeNode conf = new TreeNode();
        conf.addValue("a", "1");
        conf.addValue("b", "2");
        conf.addValue("abc.a", "3");
        conf.addValue("abc.a", "4");
        assert conf.getSubKeys().length == 3;
        //
        conf.clearSub("abc.a");
        assert conf.getSubKeys().length == 3;
        //
        conf.clearSub("abc");
        assert conf.getSubKeys().length == 2;
    }

    @Test
    public void treeNode_17() {
        TreeNode conf = new TreeNode();
        conf.newSubNode("a").setValue("1");
        conf.newSubNode("b").setValue("2");
        //
        TreeNode abcNode1 = conf.newSubNode("abc");
        abcNode1.newSubNode("a").setValue("3");
        abcNode1.newSubNode("a").setValue("4");
        //
        TreeNode abcNode2 = conf.newSubNode("abc");
        abcNode2.newSubNode("a").setValue("5");
        abcNode2.newSubNode("a").setValue("6");
        //
        assert conf.getSubKeys().length == 3;
        //
        conf.findClear("abc.a");
        assert conf.getSubKeys().length == 2;
        assert conf.toMap().containsKey("a");
        assert conf.toMap().containsKey("b");
    }

    @Test
    public void treeNode_18() {
        TreeNode conf = new TreeNode();
        assert conf.toString().equals("TreeNode{space='', name='', value=null, dataSize=0, subKeysSize=0, subSize=0}");
        //
        conf.newSubNode("a").setValue("1");
        assert conf.toString().equals("TreeNode{space='', name='', value=null, dataSize=0, subKeysSize=1, subSize=1}");
        conf.newSubNode("b").setValue("2");
        assert conf.toString().equals("TreeNode{space='', name='', value=null, dataSize=0, subKeysSize=2, subSize=2}");
        //
        TreeNode abcNode1 = conf.newSubNode("abc");
        assert conf.toString().equals("TreeNode{space='', name='', value=null, dataSize=0, subKeysSize=3, subSize=3}");
        abcNode1.newSubNode("a").setValue("3");
        abcNode1.newSubNode("a").setValue("4");
        assert conf.toString().equals("TreeNode{space='', name='', value=null, dataSize=0, subKeysSize=3, subSize=3}");
        //
        conf.newSubNode("b").setValue("5");
        assert conf.toString().equals("TreeNode{space='', name='', value=null, dataSize=0, subKeysSize=3, subSize=3}");
    }

    @Test
    public void treeNode_19() {
        TreeNode conf = new TreeNode();
        conf.newSubNode("a").setValue("1");
        conf.newSubNode("b").setValue("2");
        TreeNode abcNode1 = conf.newSubNode("abc");
        abcNode1.newSubNode("a").setValue("3");
        abcNode1.newSubNode("a").setValue("4");
        TreeNode abcNode2 = conf.newSubNode("abc");
        abcNode2.newSubNode("a").setValue("5");
        abcNode2.newSubNode("a").setValue("6");
        //
        Map<String, String> toMap = conf.toMap();
        assert toMap.size() == 3;
        assert toMap.get("a").equals("1");
        assert toMap.get("b").equals("2");
        assert toMap.get("abc.a").equals("6");
        //
        Map<String, List<String>> toMapList = conf.toMapList();
        assert toMapList.size() == 3;
        assert toMapList.get("a").size() == 1;
        assert toMapList.get("a").get(0).equals("1");
        assert toMapList.get("b").size() == 1;
        assert toMapList.get("b").get(0).equals("2");
        assert toMapList.get("abc.a").size() == 4;
        assert toMapList.get("abc.a").get(0).equals("3");
        assert toMapList.get("abc.a").get(1).equals("4");
        assert toMapList.get("abc.a").get(2).equals("5");
        assert toMapList.get("abc.a").get(3).equals("6");
    }

    @Test
    public void treeNode_20() {
        TreeNode conf = new TreeNode();
        conf.newSubNode("a").setValue("1");
        conf.newSubNode("b").setValue("2");
        TreeNode abcNode1 = conf.newSubNode("abc");
        abcNode1.newSubNode("a").setValue("3");
        abcNode1.newSubNode("a").setValue("4");
        TreeNode abcNode2 = conf.newSubNode("abc");
        abcNode2.newSubNode("a").setValue("5");
        abcNode2.newSubNode("a").setValue("6");
        //
        conf.update((dataNode, context) -> {
            if (dataNode.getValues().length > 0) {
                dataNode.setValue("1");
            }
        }, null);
        //
        Map<String, String> toMap = conf.toMap();
        assert toMap.size() == 3;
        assert toMap.get("a").equals("1");
        assert toMap.get("b").equals("1");
        assert toMap.get("abc.a").equals("1");
        //
        Map<String, List<String>> toMapList = conf.toMapList();
        assert toMapList.size() == 3;
        assert toMapList.get("a").size() == 1;
        assert toMapList.get("a").get(0).equals("1");
        assert toMapList.get("b").size() == 1;
        assert toMapList.get("b").get(0).equals("1");
        assert toMapList.get("abc.a").size() == 4;
        assert toMapList.get("abc.a").get(0).equals("1");
        assert toMapList.get("abc.a").get(1).equals("1");
        assert toMapList.get("abc.a").get(2).equals("1");
        assert toMapList.get("abc.a").get(3).equals("1");
    }

    @Test
    public void treeNode_21() {
        TreeNode conf = new TreeNode();
        conf.newSubNode("a").setValue("1");
        conf.newSubNode("b").setValue("2");
        //
        conf.update((dataNode, context) -> {
            String[] values = dataNode.getValues();
            for (int i = 0; i < values.length; i++) {
                dataNode.replace(i, "aac");
            }
        }, null);
        //
        Map<String, String> toMap = conf.toMap();
        assert toMap.size() == 2;
        assert toMap.get("a").equals("aac");
        assert toMap.get("b").equals("aac");
    }
}
