存储指令
------------------------------------
STORE
'''''''
堆保存操作，栈顶数据存储到堆（例：STORE，2）

* 参数说明：共1参数；参数1：存入堆的位置；
* 栈行为：消费1，产出0
* 堆行为：存入数据

.. code-block:: text
    :linenos:

    代码：var a = 123 ; return a
    指令：
      #0  LDC_D     123
      #1  STORE     0
      #2  LOAD      0, 0
      #3  RETURN    0

LOAD
'''''''
堆读取操作，从指定深度的堆中加载n号元素到栈（例：LOAD 1 ,1 ）

* 参数说明：共2参数；参数1：堆深度(栈顶深度为0)；参数2：元素序号；
* 栈行为：消费0，产出1
* 堆行为：取出数据（不删除）

.. code-block:: text
    :linenos:

    代码：var a = 123 ; return a
    指令：
      #0  LDC_D     123
      #1  STORE     0
      #2  LOAD      0, 0
      #3  RETURN    0

GET
'''''''
从对象中读取数据，获取栈顶对象元素的属性（例：GET,"xxxx"）

* 参数说明：共1参数；参数1：属性名称（Map的Key 或 对象的属性名）
* 栈行为：消费1，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：var a = {'abc':true} ; return a.abc
    指令：  ...(部分略)...
      #4  LOAD      0, 0
      #5  GET       abc
      #6  RETURN    0

PUT
'''''''
向对象写入数据，将栈顶对象元素放入对象元素中（例：PUT,"xxxx"）

* 参数说明：共1参数；参数1：属性名称（Map的Key 或 对象的属性名）
* 栈行为：消费1，产出0
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return {'field_1':'f1','field_2':'f2'}
    指令：
      #0  NEW_O
      #1  LDC_S     f1
      #2  PUT       field_1
      #3  LDC_S     f2
      #4  PUT       field_2
      #5  RETURN    0

PULL
'''''''
从集合中获取数据，栈顶元素是一个集合类型，获取集合的指定索引元素。（例：PULL 123）

* 参数说明：共1参数；参数1：元素位置(负数表示从后向前，正数表示从前向后)
* 栈行为：消费1，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：var a = [1,2,3,4,5] ; return a指令：
      ...(部分略)...
      #11  STORE     0
      #12  LOAD      0, 0
      #13  PULL      2
      #14  RETURN    0

PUSH
'''''''
向对象中写入数据，将栈顶元素压入集合（例：PUSH）

* 参数说明：共0参数；
* 栈行为：消费1，产出0
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return [1,2,3]
    指令：
      ...(部分略)...
      #1  LDC_D     1
      #2  PUSH
      #3  LDC_D     2
      #4  PUSH
      ...(部分略)...