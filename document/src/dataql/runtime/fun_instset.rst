函数指令
------------------------------------
CALL
'''''''
发起 UDF 调用。发起服务调用（例：CALL,2）

* 参数说明：共1参数；参数1：发起调用时需要用到的调用参数个数 n
* 栈行为：消费：n + 1（n是参数，1是函数入口），产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return abs(-1)
    指令：
      ...(部分略)...
      #2  M_DEF
      #3  LDC_D     -1
      #4  CALL      1   <- 在 CALL 发起函数调用之前通常会有 M_DEF、M_TYP、M_REF 三种之一的函数定义行为。
      ...(部分略)...

M_DEF
'''''''
函数定义，将栈顶元素转换为 UDF

* 参数说明：共0参数；
* 栈行为：消费1，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return abs(-1)
    指令：
      #0  E_LOAD    #
      #1  GET       abs
      #2  M_DEF         <= 前面 GET 出来的 abs 定义为函数
      ...(部分略)...

M_TYP
'''''''
加载一个对象到栈顶。通过 Finder 的 findBean 方法加载一个对象到栈顶。一般情况下对应的操作是 import。

* 参数说明：共1参数；参数为要加载的Bean名
* 栈行为：消费0，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：import 'net.hasor.test.dataql.udfs.DemoUdf' as foo; return foo().name
    指令：
      #0  M_TYP     net.hasor.test.dataql.udfs.DemoUdf
      #1  STORE     0
      #2  LOAD      0, 0
      #3  M_DEF
      #4  CALL      0
      ...(部分略)...

M_FRAG
'''''''
加载代码执行片段执行器，片段执行器允许通过扩展方式集成一个非 DataQL 语法的脚本块。

* 参数说明：共1参数；参数1：片段类型
* 栈行为：消费0，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    [1]
      ...(部分略)...
      #03  M_FRAG    sql     <- 加载类型为 sql 的外部片段执行器。
      #04  LOAD      0, 0
      ...(部分略)...

M_REF
'''''''
将指令序列作为函数。引用另一处的指令序列地址，并将其作为 UDF 形态存放到栈顶。lambda 被调用的开始会像栈顶放入一个入参数组。

* 参数说明：共1参数；参数1：内置lambda函数的入口地址
* 栈行为：消费0，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：var foo = () -> return {"name" : true}; return foo().name
    指令：
    [0]
      #0  M_REF     1    <- 引用 [1] 指令序列作为函数
      #1  STORE     0
      #2  LOAD      0, 0
      #3  M_DEF
      #4  CALL      0
      ...(部分略)...
    [1]
      ...(部分略)...

LOCAL
'''''''
参数入堆。将入参存入堆，也用于标记变量名称。

* 参数说明：共3参数；参数1：调用时的入参位置；参数2：存储到堆中的位置；参数3：参数名助记符；
* 栈行为：消费0，产出0
* 堆行为：存入数据

.. code-block:: text
    :linenos:

    代码：var foo = (a,b,c) -> return {"name" : a}; return foo().name
    指令：
    [0]
      ...(部分略)...
    [1]
      #0  LOCAL     0, 0, a
      #1  LOCAL     1, 1, b
      #2  LOCAL     2, 2, c
      ...(部分略)...
