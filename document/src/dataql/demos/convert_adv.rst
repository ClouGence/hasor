.. HINT::
    高阶转换是一些场景的集合，设计到的内容较为广泛。相当于综合运用 DataQL 各方面的能力。

生成 Tree 结构
------------------------------------
DataQL 查询语句

.. code-block:: js
    :linenos:

    // 层次化：把带有 parent 属性的数据转换成 tree 结构
    //  - 需要用到集合包的 filter 函数
    //  - 实现思路是：递归
    import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;

    var dataSet = [
        {'id': 1, 'parent_id':null, 'label' : 't1'},
        {'id': 2, 'parent_id':1   , 'label' : 't2'},
        {'id': 3, 'parent_id':1   , 'label' : 't3'},
        {'id': 4, 'parent_id':2   , 'label' : 't4'},
        {'id': 5, 'parent_id':null, 'label' : 't5'}
    ]
    var nodeFmt = (dat) -> {
        return dat => {
            "id",
            "label",
            "children" : collect.filter(dataSet, (test)-> { return (test.parent_id == dat.id); }) => [ nodeFmt(#) ]
        }
    }
    return collect.filter(dataSet, (test)-> { return (test.parent_id == null); }) => [ nodeFmt(#) ]

查询结果：

.. code-block:: json
    :linenos:

    [
        {
            "id":1,
            "label":"t1",
            "children":[
                {
                    "id":2,
                    "label":"t2",
                    "children":[
                        {
                            "id":4,
                            "label":"t4",
                            "children":[]
                        }
                    ]
                },
                {
                    "id":3,
                    "label":"t3",
                    "children":[]
                }
            ]
        },
        {
            "id":5,
            "label":"t5",
            "children":[]
        }
    ]

Tree 到 Tree 的变换
------------------------------------
`样本数据 <../../_static/test_json_2020-01-14-11-29.json>`_ ，对样本数据进行结构变化，变化后依然保留 Tree 结构。

DataQL 查询

.. code-block:: js
    :linenos:

    var fmt = (dat)-> {
        return {
            'id'    : dat.value,
            'text'  : dat.text,
            'child' : dat.ChildNodes => [ fmt(#) ] }
    }
    return ${result} => [ fmt(#) ]

.. HINT::
    ``${result}`` 是访问执行 DataQL 时程序传入的参数。

高维数组转为一维
------------------------------------

.. code-block:: js
    :linenos:

    // 递归：利用有状态集合，把一个多维数组打平成为一维数组
    import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;
    var data = [
        [1,2,3,[4,5]],
        [6,7,8,9,0]
    ]
    var foo = (dat, arrayObj) -> {
        var tmpArray = dat => [ # ];  // 符号 '#' 相当于在循环 dat 数组期间的，当前元素。
        if (tmpArray[0] == dat) {
            run arrayObj.addLast(dat);// 末级元素直接加到最终的集合中，否则就继续遍历集合
        } else {
            run tmpArray => [ foo(#,arrayObj) ];
        }
        return arrayObj;
    }
    return foo(data,collect.new()).data();

查询结果

.. code-block:: json
    :linenos:

    [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 ]


模拟 SQL 的 left join
------------------------------------
有两个数据集 ``year2019``、``year2018`` 通过 mapJoin 的方式将两个数据联合在一起，并计算同比

.. code-block:: js
    :linenos:

    hint MAX_DECIMAL_DIGITS = 4
    import "net.hasor.dataql.sdk.CollectionUdfSource" as collect

    var year2019 = [
        { "pt":2019, "item_code":"code_1", "sum_price":2234 },
        { "pt":2019, "item_code":"code_2", "sum_price":234 },
        { "pt":2019, "item_code":"code_3", "sum_price":12340 },
        { "pt":2019, "item_code":"code_4", "sum_price":2344 }
    ];

    var year2018 = [
        { "pt":2018, "item_code":"code_1", "sum_price":1234.0 },
        { "pt":2018, "item_code":"code_2", "sum_price":1234.0 },
        { "pt":2018, "item_code":"code_3", "sum_price":1234.0 },
        { "pt":2018, "item_code":"code_4", "sum_price":1234.0 }
    ];

    // 求同比
    return collect.mapJoin(year2019,year2018, { "item_code":"item_code" }) => [
        {
            "商品Code": data1.item_code,
            "去年同期": data2.sum_price,
            "今年总额": data1.sum_price,
            "环比去年增长": ((data1.sum_price - data2.sum_price) / data2.sum_price * 100) + "%"
        }
    ]

查询执行结果

.. code-block:: js
    :linenos:

    [
        {
            "商品Code":"code_1",
            "去年同期":1234.0,
            "今年总额":2234,
            "环比去年增长":"81.04%"
        },
        {
            "商品Code":"code_2",
            "去年同期":1234.0,
            "今年总额":234,
            "环比去年增长":"-81.04%"
        },
        {
            "商品Code":"code_3",
            "去年同期":1234.0,
            "今年总额":12340,
            "环比去年增长":"900.0%"
        },
        {
            "商品Code":"code_4",
            "去年同期":1234.0,
            "今年总额":2344,
            "环比去年增长":"89.95%"
        }
    ]
