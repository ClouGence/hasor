# DataQL 数据查询引擎

&emsp;&emsp;DataQL（Data Query Language）DataQL 是一种查询语言。旨在通过提供直观、灵活的语法来描述客户端应用程序的数据需求和交互。
            
&emsp;&emsp;数据的存储根据其业务形式通常是较为简单的，并不适合直接在页面上进行展示。因此开发页面的前端工程师需要为此做大量的工作，这就是 DataQL 极力解决的问题。
            
&emsp;&emsp;例如：下面这个 DataQL 从 user 函数中查询 id 为 4 的用户相关信息并返回给应用。

```js
return userByID({'id': 4}) => {
    'name',
    'sex' : (sex == 'F') ? '男' : '女' ,
    'age' : age + '岁'
}
```

返回结果：
```json
{
  'name' : '马三',
  'sex'  : '男',
  'age'  : '25岁'
}
```

----------
## 特性
01. **层次结构**：多数产品都涉及数据的层次结构，为了保证结构的一致性 DataQL 结果也是分层的。
02. **数据为中心**：前端工程是一个比较典型的场景，但是 DataQL 不局限于此（后端友好性）。
03. **弱类型定义**：语言中不会要求声明任何形式的类型结构。
04. **简单逻辑**：具备简单逻辑处理能力：表达式计算、对象取值、条件分支、lambda和函数。
05. **编译运行**：查询的执行是基于编译结果的。
06. **混合语言**：允许查询中混合任意的其它语言代码，典型的场景是查询中混合 SQL 查询语句。
07. **类 JS 语法**：类JS语法设计，学习成本极低。

## 样例

```java
public class UserByIdUdf implements Udf {
    public UserInfo call(Hints readOnly, Object[] params) {
        ...
    }
}

public class ConsoleDemo {
    public static void main(String[] args) {
        AppContext appContext = Hasor.create().build((QueryModule) apiBinder -> {
            apiBinder.addShareVarInstance("userByID", new UserByIdUdf());

        });
        DataQL dataQL = appContext.getInstance(DataQL.class);
        QueryResult queryResult = dataQL.createQuery("return userByID({'id': 4}) => {" +
                                                     "    'name'," +
                                                     "    'sex' : (sex == 'F') ? '男' : '女' ," +
                                                     "    'age' : age + '岁'" +
                                                     "}").execute();
        DataModel dataModel = queryResult.getData();
    }
}
```
