控制指令
------------------------------------
IF
'''''''
条件判断。如果条件判断失败那么 GOTO 到指定位置，否则继续往下执行

* 参数说明：共1参数；参数1：GOTO 的位置
* 栈行为：消费1，产出0
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：if (1 == 1) return true; else return false
    指令：
      ...(部分略)...
      #02  DO        ==
      #03  IF        7   <- 如果判断失败那么跳转到 #07 行指令
      ...(部分略)...
      #07  LABEL     7   <- LABEL 7 之后的相当于 else 部分
      ...(部分略)...

GOTO
'''''''
执行跳转，指令执行序列指针跳转，DataQL 语言本身并不支持 goto 语句。goto 的产生会在 if/数组类型结果转换时产生。

* 参数说明：共1参数；参数1：GOTO 的位置
* 栈行为：消费0，产出0
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：if (1 == 1) return true; else return false
    指令：
      ...(部分略)...
      #06  GOTO      11  <- 通常 goto 会跳转到一个 LABEL 指令上
      ...(部分略)...
      #11  LABEL     11

HINT
'''''''
设置 Hint，影响执行引擎的参数选项。

* 参数说明：共2参数；参数1：选项Key；参数2：选项Value
* 栈行为：消费2，产出0
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：hint a = 'abc' return 1
    指令：
      #0  LDC_S     a
      #1  LDC_S     abc
      #2  HINT
      ...(部分略)...

CAST_I
'''''''
类型转换指令(转换为迭代器)。将栈顶元素转换为迭代器，作为迭代器有三个特殊操作：data(数据)、next(移动到下一个，如果成功返回true)

* 参数说明：共0参数
* 栈行为：消费1，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return a => [ field1 ]
    指令：
      ...(部分略)...
      #01  GET       a
      #02  CAST_I
      #03  E_PUSH
      ...(部分略)...

CAST_O
'''''''
类型转换指令(转换为对象)，将栈顶元素转换为一个对象，如果是集合那么取第一条记录（可以通过CAST_I方式解决，但会多消耗大约8条左右的指令）

* 参数说明：共0参数
* 栈行为：消费1，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return a => { 'field1','field2' }
    指令：
      ...(部分略)...
      #01  GET       a
      #02  CAST_O
      #03  E_PUSH
      ...(部分略)...

LOAD_C
'''''''
加载用户数据集，通过 CustomizeScope 接口获取用户数据集

* 参数说明：共1参数；参数1：[@#$]符号之一
* 栈行为：消费0，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return ${a}.b
    指令：
      #0  LOAD_C    $   <- 可能的符号参数有三个"@#$"，例如：${a} 产生 $，#{a} 产生 #
      #1  GET       a
      #2  GET       b
      #3  RETURN    0

POP
'''''''
丢弃栈顶数据。丢弃栈顶数据，该指令目前在编译结果转换时会通过转换语句优化产生，平常语句不会编译出该指令。

* 参数说明：共0参数；
* 栈行为：消费1，产出0
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return a => []
    指令：
      #0  E_LOAD    #
      #1  GET       a
      #2  POP
      #3  NEW_A
      #4  RETURN    0

E_PUSH
'''''''
数据交换到环境栈。取出当前栈顶数据，并压入环境栈。通常和 CAST_I与CAST_O联合出现，每一个E_PUSH都会存在一个对应的E_POP。

* 参数说明：共0参数；
* 栈行为：消费1，产出0
* 环境栈行为：消费0，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return a => { 'field1','field2' }
    指令：
      ...(部分略)...
      #01  GET       a
      #02  CAST_O
      #03  E_PUSH
      ...(部分略)...
      #11  E_POP
      #12  RETURN    0

E_POP
'''''''
丢弃环境栈元素。

* 参数说明：共0参数；
* 栈行为：消费0，产出0
* 环境栈行为：消费1，产出0
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return a => [ field1 ]
    指令：
      ...(部分略)...
      #01  GET       a
      #02  CAST_I
      #03  E_PUSH
      ...(部分略)...
      #18  E_POP
      #19  RETURN    0

E_LOAD
'''''''
数据从环境栈交换到数据栈，加载环境栈顶的数据到数据栈，一个有效的 E_LOAD 前面肯定会有一个 E_PUSH；除此之外E_LOAD也有单独出现的时候，但通常都不具备运行上的任何意义。
例如："return a => []" 的编译结果中的 E_LOAD 就拿不到任何数据，除了编译毫无任何意义。因为根层的 E_LOAD 数据肯定是空的。

* 参数说明：共1参数；参数1：操作符号@#$
* 栈行为：消费0，产出1
* 环境栈行为：消费0，产出0
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return a => { 'field1','field2' }
    指令：
      ...(部分略)...
      #03  E_PUSH
      #04  NEW_O
      #05  E_LOAD    #
      #06  GET       field1
      #07  PUT       field1
      ...(部分略)...
