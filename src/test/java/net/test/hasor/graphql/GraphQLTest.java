package net.test.hasor.graphql;
import net.hasor.graphql.dsl.GraphQL;
import net.hasor.graphql.dsl.GraphQuery;
import net.hasor.graphql.dsl.parser.GraphParser;
import net.hasor.graphql.dsl.parser.ParseException;
import org.junit.Test;
/**
 * Created by yongchun.zyc on 2017/3/21.
 */
public class GraphQLTest {
    @Test
    public void main1() {

    /*
-- 查询服务，并返回查询一条结果（如果服务返回一个List，那么取第一个元素）
findUserByID ( userID = 12345 , status = false) {
    name,
    age,
    nick
}
    */
        GraphQuery graphQuery = GraphQL.createQuery()//
                .byUDF("findUserByID")//
                .addParam(//
                        GraphQL.createParam("userID").withNumber(12345)//
                )//
                .addParam(//
                        GraphQL.createParam("status").withBoolean(true)//
                )//
                //-----------------------------------------------------------------------
                .asObject()//
                .addField(//
                        GraphQL.createField("name").withMapping("name2")//
                )//
                .addField(//
                        GraphQL.createField("age").withMapping("age")//
                )//
                .addField(//
                        GraphQL.createField("nick").withMapping("nick")//
                )//----------------------------------------------------------------------
                .buildQuery();
        //
        printQuery(graphQuery);
    }
    @Test
    public void main2() {
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
    source : "GraphQL"
}
    */
        GraphQuery graphQuery = GraphQL.createQuery()//
                //-----------------------------------------------------------------------
                .asObject()//
                .addField(//
                        GraphQL.createField("userInfo")//
                                //---------------------------------------------------------------------------
                                .asObject()//
                                .addField(//
                                        GraphQL.createField("info")//
                                                .withUDF("findUserByID")//
                                                .addParam(//
                                                        GraphQL.createParam("userID").withNumber(12345)//
                                                )//------------------------------------------------------------------------------
                                                .asObject()//
                                                .addField(//
                                                        GraphQL.createField("name").withMapping("name")//
                                                )//
                                                .addField(//
                                                        GraphQL.createField("age").withMapping("age")//
                                                )//
                                                .addField(//
                                                        GraphQL.createField("nick").withMapping("nick")//
                                                )//------------------------------------------------------------------------------
                                                .asField()//
                                )//
                                .addField(//
                                        GraphQL.createField("nick").withMapping("info.nick")//
                                )//--------------------------------------------------------------------------
                                .asField()//
                )//
                .addField(//
                        GraphQL.createField("source").withString("GraphQL")//
                )
                //----------------------------------------------------------------------
                .buildQuery();
        //
        printQuery(graphQuery);
    }
    @Test
    public void main3() {
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
        GraphQuery graphQuery = GraphQL.createQuery()//
                .byUDF("findUserByID")//
                .addParam(//
                        GraphQL.createParam("userID").withNumber(12345)//
                )//----------------------------------------------------------------------
                .asListObject()//
                //
                .addField(//
                        GraphQL.createField("name").withMapping("name")//
                )//
                .addField(//
                        GraphQL.createField("age").withMapping("age")//
                )//
                .addField(//
                        GraphQL.createField("nick").withMapping("nick")//
                )//----------------------------------------------------------------------
                .buildQuery();
        //
        printQuery(graphQuery);
    }
    @Test
    public void main4() {
    /*
-- 查询服务，并返回所有名称集合
findUserByID ( userID = 12345 ) [
    name
]
    */
        GraphQuery graphQuery = GraphQL.createQuery()//
                .byUDF("findUserByID")//
                .addParam(//
                        GraphQL.createParam("userID").withNumber(12345)//
                )//----------------------------------------------------------------------
                .asListValue()//
                //
                .addField(//
                        GraphQL.createField("name").withMapping("name2")//
                )//
                .buildQuery();
        //
        printQuery(graphQuery);
    }
    @Test
    public void main5() {
    /*
-- 查询服务的同时构造另两个 orderList 属性，属性来源是另一个服务，另外参数 userID、status 也可以使用引号阔起来
findUserByIDAndType ( "userID" = uid, "status" = 1 ) {
    userID,
    nick,
    orderList : queryOrder ( "accountID" = uid) [
        {
            orderID,
            itemID,
            itemName
        }
    ]
}
    */
        GraphQuery graphQuery = GraphQL.createQuery()//
                .byUDF("findUserByID")//
                .addParam(//
                        GraphQL.createParam("userID").withParam("uid")//
                ).addParam(//
                        GraphQL.createParam("status").withNumber(1)//
                )//----------------------------------------------------------------------
                .asObject()//
                .addField(//
                        GraphQL.createField("userID").withMapping("userID")//
                )//
                .addField(//
                        GraphQL.createField("nick").withMapping("nick")//
                )//
                .addField(//
                        GraphQL.createField("orderList")//
                                .withUDF("queryOrder")//
                                .addParam(//
                                        GraphQL.createParam("accountID").withParam("uid")//
                                )//--------------------------------------------------------------------------
                                .asListObject()//
                                .addField(//
                                        GraphQL.createField("orderID").withMapping("orderID")//
                                )//
                                .addField(//
                                        GraphQL.createField("itemID").withMapping("itemID")//
                                )//
                                .addField(//
                                        GraphQL.createField("itemName").withMapping("itemName")//
                                )//--------------------------------------------------------------------------
                                .asField()//
                )//
                .buildQuery();
        //
        printQuery(graphQuery);
    }
    @Test
    public void main6() {
    /*
-- 参数可以是另一个函数的返回值
findUserByID ( "userID" = foo( "sessionID" = sid ), "status" = 1 ) {
    "userID",
    "nick"
}
    */
        GraphQuery graphQuery = GraphQL.createQuery()//
                .byUDF("findUserByID")//
                .addParam(//
                        GraphQL.createParam("userID")//
                                .withUDF("foo")//
                                .addParam(//
                                        GraphQL.createParam("sessionID").withParam("sid")//
                                )//
                                //----------------------------------------------------------------------
                                .asOriginal()//
                                .asParam()//
                )//
                .addParam(//
                        GraphQL.createParam("status").withNumber(1) //
                )//
                //----------------------------------------------------------------------
                .asObject()//
                .addField(//
                        GraphQL.createField("userID").withMapping("userID")//
                )//
                .addField(//
                        GraphQL.createField("nick").withMapping("nick")//
                )//
                .buildQuery();
        //
        printQuery(graphQuery);
    }
    @Test
    public void main7() {
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
        GraphQuery graphQuery = GraphQL.createQuery()//
                .asObject()//
                .addField(//
                        GraphQL.createField("user")//
                                .withUDF("findUserByID")//
                                .addParam(//
                                        GraphQL.createParam("userID").withParam("uid")//
                                )//
                                .asObject()//
                                .addField(//
                                        GraphQL.createField("uid").withMapping("userID")//
                                )//
                                .addField(//
                                        GraphQL.createField("name").withMapping("name")//
                                )//
                                .addField(//
                                        GraphQL.createField("age").withMapping("age")//
                                )//
                                .addField(//
                                        GraphQL.createField("nick").withMapping("nick")//
                                )//
                                .asField()//
                )//
                .addField(//
                        GraphQL.createField("orderList")//
                                .withUDF("queryOrder")//
                                .addParam(//
                                        GraphQL.createParam("accountID").withParam("user.uid")//
                                )//
                                .asListObject()//
                                .addField(//
                                        GraphQL.createField("orderID").withMapping("orderID")//
                                )//
                                .addField(//
                                        GraphQL.createField("itemID").withMapping("itemID")//
                                )//
                                .addField(//
                                        GraphQL.createField("itemName").withMapping("itemName")//
                                )//
                                .asField()//
                )//
                .buildQuery();
        //
        printQuery(graphQuery);
    }
    @Test
    public void main8() {
    /*
-- 使用查询片段优化 GraphQL 语句结构，以便于阅读
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
        GraphQuery fUserQL = GraphQL.createQuery("fUserQL")//
                .byUDF("findUserByID")//
                .addParam(//
                        GraphQL.createParam("userID").withParam("uid")//
                )//
                .asObject()//
                .addField(//
                        GraphQL.createField("userID").withMapping("userID")//
                )//
                .addField(//
                        GraphQL.createField("name").withMapping("name")//
                )//
                .addField(//
                        GraphQL.createField("age").withMapping("age")//
                )//
                .addField(//
                        GraphQL.createField("nick").withMapping("nick")//
                )//
                .buildQuery();
        //
        GraphQuery fOrderQL = GraphQL.createQuery("fOrderQL")//
                .byUDF("queryOrder")//
                .addParam(//
                        GraphQL.createParam("accountID").withParam("uid")//
                )//
                .asListObject()//
                .addField(//
                        GraphQL.createField("orderID").withMapping("orderID")//
                )//
                .addField(//
                        GraphQL.createField("itemID").withMapping("itemID")//
                )//
                .addField(//
                        GraphQL.createField("itemName").withMapping("itemName")//
                )//
                .buildQuery();
        //
        GraphQuery graphQuery = GraphQL.createQuery()//
                .asObject()//
                .addField(//
                        GraphQL.createField("user").withFragment(fUserQL)//
                ).addField(//
                        GraphQL.createField("orderList").withFragment(fOrderQL)//
                )//
                .buildQuery();
        //
        printQuery(graphQuery);
    }
    @Test
    public void main9() {
    /*
-- 查询片段的集中样式
fragment fUser on {
    userInfo : findUserByID ( userID = uid ) {
        name,
        age,
        nick
    },
    source : "GraphQL"
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
        GraphQuery fUserQL = GraphQL.createQuery("fUser")//
                .asObject()//
                .addField(GraphQL.createField("userInfo")//
                        .withUDF("findUserByID")//
                        .addParam(//
                                GraphQL.createParam("userID").withParam("uid")//
                        )//
                        .asField()//
                ).addField(//
                        GraphQL.createField("source").withString("GraphQL")//
                )//
                .buildQuery();
        //
        GraphQuery fOrder = GraphQL.createQuery("fOrder")//
                .byUDF("queryOrder")//
                .addParam(//
                        GraphQL.createParam("accountID").withParam("uid")//
                )//
                .asListObject()//
                .addField(//
                        GraphQL.createField("orderID").withMapping("orderID")//
                )//
                .addField(//
                        GraphQL.createField("itemID").withMapping("itemID")//
                )//
                .addField(//
                        GraphQL.createField("itemName").withMapping("itemName")//
                )//
                .buildQuery();
        //
        GraphQuery graphQuery = GraphQL.createQuery()//
                .asObject()//
                .addField(//
                        GraphQL.createField("user").withFragment(fUserQL)//
                ).addField(//
                        GraphQL.createField("orderList").withFragment(fOrder)//
                )//
                .buildQuery();
        //
        printQuery(graphQuery);
    }
    private void printQuery(GraphQuery graphQL1) {
        try {
            String query1 = graphQL1.buildQuery();
            System.out.println(query1);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            GraphQuery graphQL2 = GraphParser.parserGraphQL(query1);
            String query2 = graphQL2.buildQuery();
            System.out.println(query2);
            System.out.println("EQ:" + query1.equals(query2));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}