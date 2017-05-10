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
package net.test.hasor.db._07_ql;
import net.hasor.data.ql.dsl.DataQL;
import net.hasor.data.ql.dsl.QueryModel;
import net.hasor.data.ql.dsl.domain.EqType;
import net.hasor.data.ql.dsl.parser.DataQLParser;
import net.hasor.data.ql.dsl.parser.ParseException;
import org.junit.Test;
/**
 * @version : 2014-7-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class GraphQLTest {
    public QueryModel main1() {

    /*
-- 查询服务，并返回查询一条结果（如果服务返回一个List，那么取第一个元素）
findUserByID ( userID = 12345 , status > 2) {
    name,
    age,
    nick
}
    */
        QueryModel queryModel = DataQL.createQuery()//
                .byUDF("findUserByID")//
                .addParam(//
                        DataQL.createParam("userID").withNumber(12345)//
                )//
                .addParam(//
                        DataQL.createParam("status").withNumber(2), EqType.GT//
                )//
                //-----------------------------------------------------------------------
                .asObject()//
                .addField(//
                        DataQL.createField("name").withMapping("name2")//
                )//
                .addField(//
                        DataQL.createField("age").withMapping("age")//
                )//
                .addField(//
                        DataQL.createField("nick").withMapping("nick")//
                )//----------------------------------------------------------------------
                .buildQuery();
        //
        return printQuery(queryModel);
    }
    public QueryModel main2() {
    /*
-- 查询服务，并返回查询一条结果（如果服务返回一个List，那么取第一个元素）
{
    userInfo : {
        info :findUserByID ( userID = 12345 ) {
            name,
            age,
            nick
        },
        nick : info.nick
    },
    source : "DataQL"
}
    */
        QueryModel queryModel = DataQL.createQuery()//
                //-----------------------------------------------------------------------
                .asObject()//
                .addField(//
                        DataQL.createField("userInfo")//
                                //---------------------------------------------------------------------------
                                .asObject()//
                                .addField(//
                                        DataQL.createField("info")//
                                                .withUDF("findUserByID")//
                                                .addParam(//
                                                        DataQL.createParam("userID").withNumber(12345)//
                                                )//------------------------------------------------------------------------------
                                                .asObject()//
                                                .addField(//
                                                        DataQL.createField("name").withMapping("name")//
                                                )//
                                                .addField(//
                                                        DataQL.createField("age").withMapping("age")//
                                                )//
                                                .addField(//
                                                        DataQL.createField("nick").withMapping("nick")//
                                                )//------------------------------------------------------------------------------
                                                .asField()//
                                )//
                                .addField(//
                                        DataQL.createField("nick").withMapping("info.nick")//
                                )//--------------------------------------------------------------------------
                                .asField()//
                )//
                .addField(//
                        DataQL.createField("source").withString("DataQL")//
                )
                //----------------------------------------------------------------------
                .buildQuery();
        //
        return printQuery(queryModel);
    }
    public QueryModel main3() {
    /*
-- 查询服务，并返回一组结果（如果服务只返回一个对象，那么以 List 形式返回）
findUserByID ( userID = 12345 ) [
    {
        name,
        age,
        nick
    }
]
    */
        QueryModel queryModel = DataQL.createQuery()//
                .byUDF("findUserByID")//
                .addParam(//
                        DataQL.createParam("userID").withNumber(12345)//
                )//----------------------------------------------------------------------
                .asListObject()//
                //
                .addField(//
                        DataQL.createField("name").withMapping("name")//
                )//
                .addField(//
                        DataQL.createField("age").withMapping("age")//
                )//
                .addField(//
                        DataQL.createField("nick").withMapping("nick")//
                )//----------------------------------------------------------------------
                .buildQuery();
        //
        return printQuery(queryModel);
    }
    public QueryModel main4() {
    /*
-- 查询服务，并返回所有名称集合
findUserByID ( userID = 12345 ) [
    name
]
    */
        QueryModel queryModel = DataQL.createQuery()//
                .byUDF("findUserByID")//
                .addParam(//
                        DataQL.createParam("userID").withNumber(12345)//
                )//----------------------------------------------------------------------
                .asListValue()//
                //
                .addField(//
                        DataQL.createField("name").withMapping("name2")//
                )//
                .buildQuery();
        //
        return printQuery(queryModel);
    }
    public QueryModel main5() {
    /*
-- 查询服务的同时构造另两个 orderList 属性，属性来源是另一个服务，另外参数 userID、status 也可以使用引号阔起来
findUserByID ("userID" = uid, "status" = 1, "oriData" =  {
        "self" : true,
        "testID" : 222
    }) {
    "info" :  {
        "userID" : ~.userID,
        "nick" : ~.nick
    },
    "orderList" : queryOrder ("accountID" = $.info.userID) [
        {
            "orderID",
            "itemID",
            "itemName",
            "nick" : $.nick
        }
    ]
}
    */
        QueryModel queryModel = DataQL.createQuery()//
                .byUDF("findUserByID")//
                .addParam(//
                        DataQL.createParam("userID").withParam("uid")//
                ).addParam(//
                        DataQL.createParam("status").withNumber(1)//
                ).addParam(//
                        DataQL.createParam("oriData").withFragment(//
                                DataQL.createQuery()//
                                        .asObject()//
                                        .addField(//
                                                DataQL.createField("self").withBoolean(true)//
                                        )//
                                        .addField(//
                                                DataQL.createField("testID").withNumber(222)//
                                        ).buildQuery()//
                        )//
                )//----------------------------------------------------------------------
                .asObject()//
                .addField(
                        //
                        DataQL.createField("info")//
                                .asObject()//
                                .addField(//
                                        DataQL.createField("userID").withMapping("userID")//
                                ).addField(//
                                DataQL.createField("nick").withMapping("~.nick")//
                        ).asField()//
                )//
                .addField(//
                        DataQL.createField("orderList")//
                                .withUDF("queryOrder")//
                                .addParam(//
                                        DataQL.createParam("accountID").withParam("$.info.userID")//
                                )//--------------------------------------------------------------------------
                                .asListObject()//
                                .addField(//
                                        DataQL.createField("orderID").withMapping("orderID")//
                                )//
                                .addField(//
                                        DataQL.createField("itemID").withMapping("itemID")//
                                )//
                                .addField(//
                                        DataQL.createField("itemName").withMapping("itemName")//
                                )//
                                .addField(//
                                        DataQL.createField("nick").withMapping("$.nick")//
                                )//--------------------------------------------------------------------------
                                .asField()//
                )//
                .buildQuery();
        //
        return printQuery(queryModel);
    }
    public QueryModel main6() {
    /*
-- 参数可以是另一个函数的返回值
findUserByID ( "userID" = foo( "sessionID" = sid ), "status" = 1 ) {
    "userID",
    "nick"
}
    */
        QueryModel queryModel = DataQL.createQuery()//
                .byUDF("findUserByID")//
                .addParam(//
                        DataQL.createParam("userID")//
                                .withUDF("foo")//
                                .addParam(//
                                        DataQL.createParam("sessionID").withParam("sid")//
                                )//
                                //----------------------------------------------------------------------
                                .asOriginal()//
                                .asParam()//
                )//
                .addParam(//
                        DataQL.createParam("status").withNumber(1) //
                )//
                //----------------------------------------------------------------------
                .asObject()//
                .addField(//
                        DataQL.createField("userID").withMapping("userID")//
                )//
                .addField(//
                        DataQL.createField("nick").withMapping("nick")//
                )//
                .buildQuery();
        //
        return printQuery(queryModel);
    }
    public QueryModel main7() {
    /*
{
    user : findUserByID( "userID" = uid ,... ) {
        uid : "userID",
        name,
        age,
        nick
    },
    orderList : queryOrder( "accountID" = user.uid , ... ) [
        {
            orderID,
            itemID,
            itemName
        }
    ]
}
    */
        QueryModel queryModel = DataQL.createQuery()//
                .asObject()//
                .addField(//
                        DataQL.createField("user")//
                                .withUDF("userManager.findUserByID")//
                                .addParam(//
                                        DataQL.createParam("userID").withParam("uid")//
                                )//
                                .asObject()//
                                .addField(//
                                        DataQL.createField("uid").withMapping("userID")//
                                )//
                                .addField(//
                                        DataQL.createField("name").withMapping("name")//
                                )//
                                .addField(//
                                        DataQL.createField("age").withMapping("age")//
                                )//
                                .addField(//
                                        DataQL.createField("nick").withMapping("nick")//
                                )//
                                .asField()//
                )//
                .addField(//
                        DataQL.createField("orderList")//
                                .withUDF("queryOrder")//
                                .addParam(//
                                        DataQL.createParam("accountID").withParam("user.uid")//
                                )//
                                .asListObject()//
                                .addField(//
                                        DataQL.createField("orderID").withMapping("orderID")//
                                )//
                                .addField(//
                                        DataQL.createField("itemID").withMapping("itemID")//
                                )//
                                .addField(//
                                        DataQL.createField("itemName").withMapping("itemName")//
                                )//
                                .asField()//
                )//
                .buildQuery();
        //
        return printQuery(queryModel);
    }
    public QueryModel main8() {
    /*
-- 使用查询片段优化 DataQL 语句结构，以便于阅读
fragment fUser on findUserByID( "userID" = uid ) {
    userID,
    name,
    age,
    nick,
}
fragment fOrder on queryOrder( "accountID" = uid , ... ) [
    {
        orderID,
        itemID,
        itemName
    }
]

{
    user      : fUser{},
    orderList : fOrder{},
}
    */
        QueryModel fUserQL = DataQL.createQuery("fUserQL")//
                .byUDF("findUserByID")//
                .addParam(//
                        DataQL.createParam("userID").withParam("uid")//
                )//
                .asObject()//
                .addField(//
                        DataQL.createField("userID").withMapping("userID")//
                )//
                .addField(//
                        DataQL.createField("name").withMapping("name")//
                )//
                .addField(//
                        DataQL.createField("age").withMapping("age")//
                )//
                .addField(//
                        DataQL.createField("nick").withMapping("nick")//
                )//
                .buildQuery();
        //
        QueryModel fOrderQL = DataQL.createQuery("fOrderQL")//
                .byUDF("queryOrder")//
                .addParam(//
                        DataQL.createParam("accountID").withParam("uid")//
                )//
                .asListObject()//
                .addField(//
                        DataQL.createField("orderID").withMapping("orderID")//
                )//
                .addField(//
                        DataQL.createField("itemID").withMapping("itemID")//
                )//
                .addField(//
                        DataQL.createField("itemName").withMapping("itemName")//
                )//
                .buildQuery();
        //
        QueryModel queryModel = DataQL.createQuery()//
                .asObject()//
                .addField(//
                        DataQL.createField("user").withFragment(fUserQL)//
                ).addField(//
                        DataQL.createField("orderList").withFragment(fOrderQL)//
                )//
                .buildQuery();
        //
        return printQuery(queryModel);
    }
    public QueryModel main9() {
    /*
-- 查询片段的集中样式
fragment fUser on {
    userInfo : findUserByID ( userID = uid ) {
        name,
        age,
        nick
    },
    source : "DataQL"
}
fragment fOrder on queryOrder( "accountID" = uid , ... ) [
    {
        orderID,
        itemID,
        itemName
    }
]

{
    user      : fUser,
    orderList : fOrder,
}
    */
        QueryModel fUserQL = DataQL.createQuery("fUser")//
                .asObject()//
                .addField(DataQL.createField("userInfo")//
                        .withUDF("findUserByID")//
                        .addParam(//
                                DataQL.createParam("userID").withParam("uid")//
                        )//
                        .asField()//
                ).addField(//
                        DataQL.createField("source").withString("DataQL")//
                )//
                .buildQuery();
        //
        QueryModel fOrder = DataQL.createQuery("fOrder")//
                .byUDF("queryOrder")//
                .addParam(//
                        DataQL.createParam("accountID").withParam("uid")//
                )//
                .asListObject()//
                .addField(//
                        DataQL.createField("orderID").withMapping("orderID")//
                )//
                .addField(//
                        DataQL.createField("itemID").withMapping("itemID")//
                )//
                .addField(//
                        DataQL.createField("itemName").withMapping("itemName")//
                )//
                .buildQuery();
        //
        QueryModel queryModel = DataQL.createQuery()//
                .asObject()//
                .addField(//
                        DataQL.createField("user").withFragment(fUserQL)//
                ).addField(//
                        DataQL.createField("orderList").withFragment(fOrder)//
                )//
                .buildQuery();
        //
        return printQuery(queryModel);
    }
    private QueryModel printQuery(QueryModel graphQL1) {
        try {
            String query1 = graphQL1.buildQuery();
            System.out.println(query1);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            QueryModel graphQL2 = DataQLParser.parserQL(query1);
            String query2 = graphQL2.buildQuery();
            System.out.println(query2);
            System.out.println("EQ:" + query1.equals(query2));
            assert query1.equals(query2);
            return graphQL2;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void _main1() {
        main1();
    }
    @Test
    public void _main2() {
        main2();
    }
    @Test
    public void _main3() {
        main3();
    }
    @Test
    public void _main4() {
        main4();
    }
    @Test
    public void _main5() {
        main5();
    }
    @Test
    public void _main6() {
        main6();
    }
    @Test
    public void _main7() {
        main7();
    }
    @Test
    public void _main8() {
        main8();
    }
    @Test
    public void _main9() {
        main9();
    }
}