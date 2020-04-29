--------------------
时间日历函数库
--------------------
引入时间日历函数库的方式为：``import 'net.hasor.dataql.fx.basic.DateTimeUdfSource' as time;``

now
------------------------------------
函数定义：``long now()``

- **参数定义：无
- **返回类型：** ``Number``
- **作用：** 返回当前系统时间戳。

**作用**

- 获取当前时间，类似 ``System.currentTimeMillis()``

**例子**

.. code-block:: js
    :linenos:

    time.now() = 1588040924445

year
------------------------------------
函数定义：``Number year(time)``

- **参数定义：** ``timeNumber`` 类型：Number
- **返回类型：** ``Number``
- **作用：** 返回当前系统时区的：年。

**作用**

- timeNumber代表一个时间戳，year 函数可以获取这个时间戳中的年份。

**例子**

.. code-block:: js
    :linenos:

    time.year(1588040924445) = 2020 // 将三个集合合并成一个集合。
    time.year(time.now())    = 2020 // 系统时间中，当前的年份

month
------------------------------------
函数定义：``Number month(time)``

- **参数定义：** ``timeNumber`` 类型：Number
- **返回类型：** ``Number``
- **作用：** 返回当前系统时区的：月。

**作用**

- timeNumber代表一个时间戳，month 函数可以获取这个时间戳中的月份。

**例子**

.. code-block:: js
    :linenos:

    time.month(1588040924445) = 4 // 将三个集合合并成一个集合。
    time.month(time.now())    = 4 // 系统时间中，当前的月份（从 1 开始）

day、dayOfMonth
------------------------------------
函数定义：``Number dayOfMonth(time)`` 或 ``Number day(time)``

- **参数定义：** ``timeNumber`` 类型：Number
- **返回类型：** ``Number``
- **作用：** 这个日期在这一月中是第几天，起始数为：1。

**作用**

- timeNumber代表一个时间戳，dayOfMonth/day 函数可以获取这个时间戳中当前日。

**例子**

.. code-block:: js
    :linenos:

    time.dayOfMonth(1588040924445) = 28
    time.dayOfMonth(time.now())    = ...
    time.day(1588040924445)        = 28
    time.day(time.now())           = ...

hour
------------------------------------
函数定义：``Number hour(time)``

- **参数定义：** ``timeNumber`` 类型：Number
- **返回类型：** ``Number``
- **作用：** 返回当前系统时区的：小时。

**作用**

- timeNumber代表一个时间戳，hour 函数可以获取这个时间戳中的小时数。

**例子**

.. code-block:: js
    :linenos:

    time.hour(1588040924445) = 10
    time.hour(time.now())    = ...

minute
------------------------------------
函数定义：``Number minute(time)``

- **参数定义：** ``timeNumber`` 类型：Number
- **返回类型：** ``Number``
- **作用：** 返回当前系统时区的：分钟。

**作用**

- timeNumber代表一个时间戳，minute 函数可以获取这个时间戳中的分钟数。

**例子**

.. code-block:: js
    :linenos:

    time.minute(1588040924445) = 28
    time.minute(time.now())    = ...

second
------------------------------------
函数定义：``Number second(time)``

- **参数定义：** ``timeNumber`` 类型：Number
- **返回类型：** ``Number``
- **作用：** 返回当前系统时区的：秒。

**作用**

- timeNumber代表一个时间戳，second 函数可以获取这个时间戳中的秒数。

**例子**

.. code-block:: js
    :linenos:

    time.second(1588040924445) = 44
    time.second(time.now())    = ...

dayOfYear
------------------------------------
函数定义：``Number dayOfYear(time)``

- **参数定义：** ``timeNumber`` 类型：Number
- **返回类型：** ``Number``
- **作用：** 这个日期在这一年中是第几天，起始数为：1。

**作用**

- timeNumber代表一个时间戳，dayOfYear 函数可以获取这个时间戳中在全年的第几天数。

**例子**

.. code-block:: js
    :linenos:

    time.dayOfYear(1588040924445)                           = 119
    time.dayOfYear(time.parser('2020-01-01','yyyy-MM-dd'))  = 1

dayOfWeek
------------------------------------
函数定义：``Number dayOfWeek(time)``

- **参数定义：** ``timeNumber`` 类型：Number
- **返回类型：** ``Number``
- **作用：** 这个日期在这一周中是第几天：1。

**作用**

- timeNumber代表一个时间戳，dayOfWeek 函数可以获取这个时间戳中在其所在周的第几天。

**例子**

.. code-block:: js
    :linenos:

    time.dayOfWeek(1588040924445)                           = 3 // 周二
    time.dayOfWeek(time.parser('2020-01-01','yyyy-MM-dd'))  = 4 // 周三


返回值和星期数表：

+-------------+-----------+
| Name        | Value     |
+-------------+-----------+
| SUNDAY      | 1         |
+-------------+-----------+
| MONDAY      | 2         |
+-------------+-----------+
| TUESDAY     | 3         |
+-------------+-----------+
| WEDNESDAY   | 4         |
+-------------+-----------+
| THURSDAY    | 5         |
+-------------+-----------+
| FRIDAY      | 6         |
+-------------+-----------+
| SATURDAY    | 7         |
+-------------+-----------+

format
------------------------------------
函数定义：``String format(time, pattern)``

- **参数定义：** ``time`` 类型：Number，待处理的数据；``pattern`` 类型：String
- **返回类型：** ``String``
- **作用：** 格式化指定时间。

**作用**

- 对 Number 类型的时间戳进行时间日期格式化。底层使用 ``java.text.SimpleDateFormat`` 进行格式化。

**例子**

.. code-block:: js
    :linenos:

    time.format(1588040924445, "yyyy-MM-dd hh:mm:ss")
    // result 2020-04-28 10:28:44

parser
------------------------------------
函数定义：``Number parser(time, pattern)``

- **参数定义：** ``time`` 类型：Number，待处理的数据；``pattern`` 类型：String
- **返回类型：** ``String``
- **作用：** 解析一个时间日期数据为 long。

**作用**

- 对 Number 类型的时间戳进行时间日期格式化。底层使用 ``java.text.SimpleDateFormat`` 进行解析。

**例子**

.. code-block:: js
    :linenos:

    time.parser("2020-04-28 10:28:44", "yyyy-MM-dd hh:mm:ss")
    // 1588040924000
