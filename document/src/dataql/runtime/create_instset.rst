构造指令
------------------------------------
LDC_D
'''''''
定义数字，将数字压入栈（例：LDC_D 12345）

* 参数说明：共1参数；参数1：数据；
* 栈行为：消费0，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return 134
    指令：
      #00  LDC_D     134
      #01  RETURN    0

LDC_B
'''''''
定义布尔，将布尔数据压入栈（例：LDC_B true）

* 参数说明：共1参数；参数1：数据；
* 栈行为：消费0，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return true
    指令：
      #00  LDC_B     true
      #01  RETURN    0

LDC_S
'''''''
定义字符串，将字符串数据压入栈（例：LDC_S "ssssss"）

* 参数说明：共1参数；参数1：数据；
* 栈行为：消费0，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return "ssss"
    指令：
      #00  LDC_S     ssss
      #01  RETURN    0

LDC_N
'''''''
定义 NULL，将null压入栈（例：LDC_N）

* 参数说明：共0参数；
* 栈行为：消费0，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码：return null
    指令：
      #00  LDC_N
      #01  RETURN    0

NEW_O
'''''''
定义对象，构造一个键值对对象并压入栈

* 参数说明：共0参数；
* 栈行为：消费0，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码1：return {}
    指令1：
      #00  NEW_O
      #01  RETURN    0

.. code-block:: text
    :linenos:

    代码2：return {'field_1':'f1','field_2':'f2'}
    指令2：
      #0  NEW_O
      ...(部分略)...
      #5  RETURN    0

NEW_A
'''''''
定义集合，构造一个集合对象并压入栈

* 参数说明：共0参数；
* 栈行为：消费0，产出1
* 堆行为：无

.. code-block:: text
    :linenos:

    代码1：return []
    指令1：
      #00  NEW_A
      #01  RETURN    0

.. code-block:: text
    :linenos:

    代码2：return [1,2,3]
    指令2：
      #0  NEW_A
      ...(部分略)...
      #7  RETURN    0
