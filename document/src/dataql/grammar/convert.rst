--------------------
结果转换
--------------------
语法为：``expr => fmt``，**fmt** 有两种转换结果分别为：对象和数组。

DataQL 在对表达式结果转换到 fmt 表示的格式时，会对表达式结果做检测。

    - 如果结果是一个数组，那么在转换为数组的时候就直接使用这个数组来做处理。如果转换的目标是一个对象，那么就会取数组的第一个元素。
    - 如果结果是一个对象，那么在转换为数组的时候就会将这个对象放进只有一个元素的数组中在做处理。如果转换的目标是一个对象，那么就直接使用这个对象。

后面的语法说名都会用到这个结构，因此写在最前面：

.. code-block:: java
    :linenos:

    public class User {
        long    userID = 1234567890;
        int     age    = 31;
        String  name   = "this is name.";
        String  name2  = "this is name2.";
        String  nick   = "my name is nick.";
        SexEnum sex    = SexEnum.F; // 枚举 enum SexEnum { F, M }
        boolean status = true;
    }


对象到数组
------------------------------------
例如：有一个函数接口 ``findUser`` 返回 User 对象。通过 DataQL 查询这个接口并且把对象转换为 List。

.. code-block:: js
    :linenos:

    return findUser() => [ # ]

查询结果为

.. code-block:: json
    :linenos:

    [
        {
            "userID" : 1234567890,
            "age"    : 31,
            "name"   : "this is name.",
            "name2"  : "this is name2.",
            "nick"   : "my name is nick.",
            "sex"    : "F",
            "status" : true
        }
    ]

.. HINT::
    ``#`` 是一个特殊的访问符，在访问符章节会有更加详细的介绍。单独使用 ``#`` 访问符的意义类似于 ``for (Object obj : objects) { ... }`` 中的 ``obj``


数组到数组
------------------------------------
有一个函数接口 ``findUserList`` 返回 ``List<User>`` 对象集合。通过 DataQL 查询这个接口并且只返回 ``name`` 和 ``age`` 两个字段的集合。

.. code-block:: js
    :linenos:

    return findUserList() => [  // <= 数组到数组的转换
        {                       // <= 每一个数组元素都是一个对象
            "age",              // <= 保留 name
            "name"              // <= 保留 age
        }
    ]

查询结果为

.. code-block:: json
    :linenos:

    [
        {
            "age"    : 31,
            "name"   : "this is name."
        },{
            "age"    : 31,
            "name"   : "this is name."
        }
    ]


数组到对象
------------------------------------
将接口 ``findUserList`` 的返回只转换为对象。DataQL 在处理数组到对象转换的原则是取第一条元素作为结果。

因此对于接口返回的 ``List<User>`` 对象集合，相当于执行了 ``list.get(0)`` 操作。如果集合为空，那么转换结果也是空。


.. code-block:: js
    :linenos:

    return findUserList() => {  // <= 转换成为对象
        "age",                  // <= 保留 name
        "name"                  // <= 保留 age
    }


查询结果为

.. code-block:: json
    :linenos:

    {
        "age"  : 31,
        "name" : "this is name."
    }

.. CAUTION::
    注意这种写法是错误的 ``findUserList() => { # }``，原因是 ``{...}`` 花括号中必须要明确指明具体的要保留的字段。


多维数组的转换
------------------------------------
多维数组转换处理中，处理第一维转换期间需要获取整个当前元素，因此需要使用访问符 `#`。下面是一将一个二维数值数组做一个转换，为每一个数组元素都变为 ``值：xxx`` 形式

假定数据为：

.. code-block:: js
    :linenos:

    var data = [
        [1,2,3],
        [4,5,6],
        [7,8,9]
    ]

DataQL 查询语句为

.. code-block:: js
    :linenos:

    return data => [
        # => [
            "值：" + #
        ]
    ]

查询结果：

.. code-block:: json
    :linenos:

    [
        ["值：1","值：2","值：3"],
        ["值：4","值：5","值：6"],
        ["值：7","值：8","值：9"]
    ]


对象数组转换基础类型数组
------------------------------------
例如通过 ``findUserList()`` 函数，查询所有用户的 id 集合。

DataQL 查询语句为

.. code-block:: js
    :linenos:

    return findUserList() => [ userID ]

查询结果：

.. code-block:: json
    :linenos:

    [
        1234567890,
        1234567890,
        1234567890
    ]


对象到对象
------------------------------------
对象到对象的转换一般都是为了两个目的：

    - 1.对象属性值的变换。
    - 2.对象结构的变化。

**对象属性值的变换**

例如：查询结果中，将年龄表示的数值转换为 ``xx岁``。将性别表示的 ``F/M``,转换为：``男/女``

.. code-block:: js
    :linenos:

    return findUser() => {
        "name",
        "age" : age + "岁",
        "sex" : (sex == 'F') ? '男' : '女'
    }

查询结果为

.. code-block:: json
    :linenos:

    {
        "name" : "this is name.",
        "age"  : "31岁",
        "sex"  : "男"
    }


**对象结构的变化**

例如：查询结果中，将年龄和性别放入一个新的结构中返回给前端。

.. code-block:: js
    :linenos:

    return findUser() => {
        "name",
        "info" : {
            "age" : age + "岁",
            "sex" : (sex == 'F') ? '男' : '女'
        }
    }

查询结果为

.. code-block:: json
    :linenos:

    {
        "name" : "this is name.",
        "info" : {
            "age"  : "31岁",
            "sex"  : "男"
        }
    }
