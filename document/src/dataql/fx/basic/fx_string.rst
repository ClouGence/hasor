--------------------
字符串函数库
--------------------
引入字符串函数库的方式为：``import 'net.hasor.dataql.fx.basic.StringUdfSource' as string;``

startsWith
------------------------------------
函数定义：``boolean startsWith(str, prefix)``

- **参数定义：** ``str`` 类型：String；``prefix`` 类型：String
- **返回类型：** ``Boolean``
- **作用：** Check if a String starts with a specified prefix.

**例子**

.. code-block:: js
    :linenos:

    string.startsWith(null, null)      = true
    string.startsWith(null, "abc")     = false
    string.startsWith("abcdef", null)  = false
    string.startsWith("abcdef", "abc") = true
    string.startsWith("ABCDEF", "abc") = false

startsWithIgnoreCase
------------------------------------
函数定义：``boolean startsWithIgnoreCase(str, prefix)``

- **参数定义：** ``str`` 类型：String；``prefix`` 类型：String
- **返回类型：** ``Boolean``
- **作用：** Case insensitive check if a String starts with a specified prefix.

**例子**

.. code-block:: js
    :linenos:

    string.startsWithIgnoreCase(null, null)      = true
    string.startsWithIgnoreCase(null, "abc")     = false
    string.startsWithIgnoreCase("abcdef", null)  = false
    string.startsWithIgnoreCase("abcdef", "abc") = true
    string.startsWithIgnoreCase("ABCDEF", "abc") = true   <- different startsWith

endsWith
------------------------------------
函数定义：``boolean endsWith(str, prefix)``

- **参数定义：** ``str`` 类型：String；``prefix`` 类型：String
- **返回类型：** ``Boolean``
- **作用：** check if a String ends with a specified suffix.

**例子**

.. code-block:: js
    :linenos:

    string.endsWith(null, null)      = true
    string.endsWith(null, "def")     = false
    string.endsWith("abcdef", null)  = false
    string.endsWith("abcdef", "def") = true
    string.endsWith("ABCDEF", "def") = false
    string.endsWith("ABCDEF", "cde") = false

endsWithIgnoreCase
------------------------------------
函数定义：``boolean endsWithIgnoreCase(str, prefix)``

- **参数定义：** ``str`` 类型：String；``prefix`` 类型：String
- **返回类型：** ``Boolean``
- **作用：** Case insensitive check if a String ends with a specified suffix.

**例子**

.. code-block:: js
    :linenos:

    string.endsWithIgnoreCase(null, null)      = true
    string.endsWithIgnoreCase(null, "def")     = false
    string.endsWithIgnoreCase("abcdef", null)  = false
    string.endsWithIgnoreCase("abcdef", "def") = true
    string.endsWithIgnoreCase("ABCDEF", "def") = true   <- different startsWith
    string.endsWithIgnoreCase("ABCDEF", "cde") = false

lineToHump
------------------------------------
函数定义：``String lineToHump(str)``

- **参数定义：** ``str`` 类型：String；
- **返回类型：** ``String``
- **作用：** 下划线转驼峰，规则为遇到下划线后字母转为大写并删除下划线。

**例子**

.. code-block:: js
    :linenos:

    string.lineToHump(null)      = null
    string.lineToHump("def")     = "def"
    string.lineToHump("_def")    = "Def"
    string.lineToHump("ABC")     = "abc"
    string.lineToHump("_ABC")    = "Abc"
    string.lineToHump("ABC_ABC") = "abcAbc"

humpToLine
------------------------------------
函数定义：``String humpToLine(str)``

- **参数定义：** ``str`` 类型：String；
- **返回类型：** ``String``
- **作用：** 驼峰转下划线，规则为遇到大写字母后，转为小写并前面加下滑线。

**例子**

.. code-block:: js
    :linenos:

    string.humpToLine(null)      = null
    string.humpToLine("def")     = "def"
    string.humpToLine("defAbc")  = "def_abc"
    string.humpToLine("ABC")     = "_a_b_c"
    string.humpToLine("_ABC")    = "__a_b_c"
    string.humpToLine("ABC_ABC") = "_a_b_c__a_b_c"

firstCharToUpperCase
------------------------------------
函数定义：``String firstCharToUpperCase(str)``

- **参数定义：** ``str`` 类型：String；
- **返回类型：** ``String``
- **作用：** 转换首字母大写。

**例子**

.. code-block:: js
    :linenos:

    string.firstCharToUpperCase(null)     = null
    string.firstCharToUpperCase("def")    = "Def"
    string.firstCharToUpperCase("defAbc") = "DefAbc"
    string.firstCharToUpperCase("ABC")    = "ABC"

firstCharToLowerCase
------------------------------------
函数定义：``String firstCharToLowerCase(str)``

- **参数定义：** ``str`` 类型：String；
- **返回类型：** ``String``
- **作用：** 转换首字母小写。

**例子**

.. code-block:: js
    :linenos:

    string.firstCharToLowerCase(null)     = null
    string.firstCharToLowerCase("def")    = "def"
    string.firstCharToLowerCase("defAbc") = "defAbc"
    string.firstCharToLowerCase("ABC")    = "aBC"

toUpperCase
------------------------------------
函数定义：``String toUpperCase(str)``

- **参数定义：** ``str`` 类型：String；
- **返回类型：** ``String``
- **作用：** 转换大写。

**例子**

.. code-block:: js
    :linenos:

    string.toUpperCase(null)     = null
    string.toUpperCase("def")    = "DEF"
    string.toUpperCase("defAbc") = "DEFABC"
    string.toUpperCase("ABC")    = "ABC"

toLowerCase
------------------------------------
函数定义：``String toLowerCase(str)``

- **参数定义：** ``str`` 类型：String；
- **返回类型：** ``String``
- **作用：** 转换小写。

**例子**

.. code-block:: js
    :linenos:

    string.toLowerCase(null)     = null
    string.toLowerCase("def")    = "def"
    string.toLowerCase("defAbc") = "defabc"
    string.toLowerCase("ABC")    = "abc"

indexOf
------------------------------------
函数定义：``String indexOf(str, searchStr)``

- **参数定义：** ``str`` 类型：String；``searchStr`` 类型：String；
- **返回类型：** ``String``
- **作用：** Finds the first index within a String, handling ``null``. This method uses String.indexOf(String).

**例子**

.. code-block:: js
    :linenos:

    string.indexOf(null, *)         = -1
    string.indexOf("", *)           = -1
    string.indexOf("aabaabaa", 'a') = 0
    string.indexOf("aabaabaa", 'b') = 2

indexOfWithStart
------------------------------------
函数定义：``String indexOfWithStart(str, searchStr, startPos)``

- **参数定义：** ``str`` 类型：String；``searchStr`` 类型：String；``startPos`` 类型：Number；
- **返回类型：** ``String``
- **作用：** Finds the first index within a String from a start position, handling ``null``. This method uses String#indexOf(String, int).

**例子**

.. code-block:: js
    :linenos:

    string.indexOfWithStart(null,"", 1)         = -1
    string.indexOfWithStart("","", 2)           = -1
    string.indexOfWithStart("aabaabaa", 'a', 3) = 3
    string.indexOfWithStart("aabaabaa", 'b' , 3)= 5

indexOfIgnoreCase
------------------------------------
函数定义：``String indexOfIgnoreCase(str, searchStr)``

- **参数定义：** ``str`` 类型：String；``searchStr`` 类型：String；
- **返回类型：** ``String``
- **作用：** Case in-sensitive find of the first index within a String.

**例子**

.. code-block:: js
    :linenos:

    string.indexOfWithStart(null,"", 1)          = -1
    string.indexOfWithStart("","", 2)            = -1
    string.indexOfWithStart("aabaabaa", 'a', 3)  = 3
    string.indexOfWithStart("aabaabaa", 'b' , 3) = 5
    string.indexOfWithStart("aabaabaa", 'B' , 3) = -1

indexOfIgnoreCaseWithStart
------------------------------------
函数定义：``String indexOfIgnoreCaseWithStart(str, searchStr, startPos)``

- **参数定义：** ``str`` 类型：String；``searchStr`` 类型：String；``startPos`` 类型：Number；
- **返回类型：** ``String``
- **作用：** Case in-sensitive find of the first index within a String from the specified position.

**例子**

.. code-block:: js
    :linenos:

    string.indexOfIgnoreCaseWithStart(null,"", 1)          = -1
    string.indexOfIgnoreCaseWithStart("","", 2)            = -1
    string.indexOfIgnoreCaseWithStart("aabaabaa", 'a', 3)  = 3
    string.indexOfIgnoreCaseWithStart("aabaabaa", 'b' , 3) = 5
    string.indexOfIgnoreCaseWithStart("aabaabaa", 'B' , 3) = 5

lastIndexOf
------------------------------------
函数定义：``String lastIndexOf(str, searchStr)``

- **参数定义：** ``str`` 类型：String；``searchStr`` 类型：String；
- **返回类型：** ``String``
- **作用：** Finds the last index within a String, handling ``null``. This method uses String#lastIndexOf(String).

**例子**

.. code-block:: js
    :linenos:

    string.lastIndexOf(null, *)         = -1
    string.lastIndexOf("", *)           = -1
    string.lastIndexOf("aabaabaa", 'a') = 7
    string.lastIndexOf("aabaabaa", 'b') = 5

lastIndexOfWithStart
------------------------------------
函数定义：``String lastIndexOfWithStart(str, searchStr, startPos)``

- **参数定义：** ``str`` 类型：String；``searchStr`` 类型：String；``startPos`` 类型：Number；
- **返回类型：** ``String``
- **作用：** Finds the last index within a String from a start position, handling ``null``. This method uses String#lastIndexOf(String, int).

**例子**

.. code-block:: js
    :linenos:

    string.lastIndexOf(null, *, *)          = -1
    string.lastIndexOf("", *,  *)           = -1
    string.lastIndexOf("aabaabaa", 'b', 8)  = 5
    string.lastIndexOf("aabaabaa", 'b', 4)  = 2
    string.lastIndexOf("aabaabaa", 'b', 0)  = -1
    string.lastIndexOf("aabaabaa", 'b', 9)  = 5
    string.lastIndexOf("aabaabaa", 'b', -1) = -1
    string.lastIndexOf("aabaabaa", 'a', 0)  = 0

lastIndexOfIgnoreCase
------------------------------------
函数定义：``String lastIndexOf(str, searchStr)``

- **参数定义：** ``str`` 类型：String；``searchStr`` 类型：String；
- **返回类型：** ``String``
- **作用：** Case in-sensitive find of the last index within a String from the specified position.

**例子**

.. code-block:: js
    :linenos:

    string.lastIndexOf(null, *)         = -1
    string.lastIndexOf("", *)           = -1
    string.lastIndexOf("aabaabaa", 'a') = 7
    string.lastIndexOf("aabaabaa", 'b') = 5

lastIndexOfIgnoreCaseWithStart
------------------------------------
函数定义：``String lastIndexOfIgnoreCaseWithStart(str, searchStr)``

- **参数定义：** ``str`` 类型：String；``searchStr`` 类型：String；``startPos`` 类型：Number；
- **返回类型：** ``String``
- **作用：** Case in-sensitive find of the last index within a String from the specified position.

**例子**

.. code-block:: js
    :linenos:

    string.lastIndexOfIgnoreCase(null, *, *)          = -1
    string.lastIndexOfIgnoreCase(*, null, *)          = -1
    string.lastIndexOfIgnoreCase("aabaabaa", "A", 8)  = 7
    string.lastIndexOfIgnoreCase("aabaabaa", "B", 8)  = 5
    string.lastIndexOfIgnoreCase("aabaabaa", "AB", 8) = 4
    string.lastIndexOfIgnoreCase("aabaabaa", "B", 9)  = 5
    string.lastIndexOfIgnoreCase("aabaabaa", "B", -1) = -1
    string.lastIndexOfIgnoreCase("aabaabaa", "A", 0)  = 0
    string.lastIndexOfIgnoreCase("aabaabaa", "B", 0)  = -1

contains
------------------------------------
函数定义：``String contains(str, searchStr)``

- **参数定义：** ``str`` 类型：String；``searchStr`` 类型：String；
- **返回类型：** ``String``
- **作用：** Checks if String contains a search String, handling ``null``. This method uses String#indexOf(String).

**例子**

.. code-block:: js
    :linenos:

    string.contains(null, *)    = false
    string.contains("", *)      = false
    string.contains("abc", 'a') = true
    string.contains("abc", 'z') = false

containsIgnoreCase
------------------------------------
函数定义：``String containsIgnoreCase(str, searchStr)``

- **参数定义：** ``str`` 类型：String；``searchStr`` 类型：String；
- **返回类型：** ``String``
- **作用：** Checks if String contains a search String irrespective of case, handling ``null``. Case-insensitivity is defined as by String#equalsIgnoreCase(String).

**例子**

.. code-block:: js
    :linenos:

    string.contains(null, *)    = false
    string.contains(*, null)    = false
    string.contains("", "")     = true
    string.contains("abc", "")  = true
    string.contains("abc", "a") = true
    string.contains("abc", "z") = false
    string.contains("abc", "A") = true
    string.contains("abc", "Z") = false

containsAny
------------------------------------
函数定义：``String containsAny(str, searchStrArray)``

- **参数定义：** ``str`` 类型：String；``searchStrArray`` 类型：List；
- **返回类型：** ``String``
- **作用：** Checks if the String contains any character in the given set of string.

**例子**

.. code-block:: js
    :linenos:

    string.containsAny(null, *)                = false
    string.containsAny("", *)                  = false
    string.containsAny(*, null)                = false
    string.containsAny(*, [])                  = false
    string.containsAny("zzabyycdxx",['z','a']) = true
    string.containsAny("zzabyycdxx",['b','y']) = true
    string.containsAny("aba", ['z'])           = false
    string.containsAny("zzabyycdxx",['Z','A']) = false
    string.containsAny("zzabyycdxx",['B','Y']) = false

containsAnyIgnoreCase
------------------------------------
函数定义：``String containsAnyIgnoreCase(str, searchStrArray)``

- **参数定义：** ``str`` 类型：String；``searchStrArray`` 类型：List；
- **返回类型：** ``String``
- **作用：** Case in-sensitive Checks if the String contains any character in the given set of string.

**例子**

.. code-block:: js
    :linenos:

    string.containsAnyIgnoreCase(null, *)                = false
    string.containsAnyIgnoreCase("", *)                  = false
    string.containsAnyIgnoreCase(*, null)                = false
    string.containsAnyIgnoreCase(*, [])                  = false
    string.containsAnyIgnoreCase("zzabyycdxx",['z','a']) = true
    string.containsAnyIgnoreCase("zzabyycdxx",['b','y']) = true
    string.containsAnyIgnoreCase("aba", ['z'])           = false
    string.containsAnyIgnoreCase("zzabyycdxx",['Z','A']) = true
    string.containsAnyIgnoreCase("zzabyycdxx",['B','Y']) = true

trim
------------------------------------
函数定义：``String trim(str)``

- **参数定义：** ``str`` 类型：String；
- **返回类型：** ``String``
- **作用：** 截断两边空格，如果为空返回为空。

**例子**

.. code-block:: js
    :linenos:

    string.trim(null)          = null
    string.trim("")            = ""
    string.trim("     ")       = ""
    string.trim("abc")         = "abc"
    string.trim("    abc    ") = "abc"

sub
------------------------------------
函数定义：``String sub(str, start, end)``

- **参数定义：** ``str`` 类型：String；``start`` 类型：Number；``end`` 类型：Number；
- **返回类型：** ``String``
- **作用：** Gets a substring from the specified String avoiding exceptions.

**例子**

.. code-block:: js
    :linenos:

    string.substring(null, *, *)    = null
    string.substring("", * ,  *)    = "";
    string.substring("abc", 0, 2)   = "ab"
    string.substring("abc", 2, 0)   = ""
    string.substring("abc", 2, 4)   = "c"
    string.substring("abc", 4, 6)   = ""
    string.substring("abc", 2, 2)   = ""
    string.substring("abc", -2, -1) = "b"
    string.substring("abc", -4, 2)  = "ab"

left
------------------------------------
函数定义：``String left(str, len)``

- **参数定义：** ``str`` 类型：String；``len`` 类型：Number；
- **返回类型：** ``String``
- **作用：** Gets the leftmost ``len`` characters of a String.

**例子**

.. code-block:: js
    :linenos:

    string.left(null, *)    = null
    string.left(*, -ve)     = ""
    string.left("", *)      = ""
    string.left("abc", 0)   = ""
    string.left("abc", 2)   = "ab"
    string.left("abc", 4)   = "abc"

right
------------------------------------
函数定义：``String right(str, len)``

- **参数定义：** ``str`` 类型：String；``len`` 类型：Number；
- **返回类型：** ``String``
- **作用：** Gets the rightmost ``len`` characters of a String.

**例子**

.. code-block:: js
    :linenos:

    string.right(null, *)    = null
    string.right(*, -ve)     = ""
    string.right("", *)      = ""
    string.right("abc", 0)   = ""
    string.right("abc", 2)   = "bc"
    string.right("abc", 4)   = "abc"

alignRight
------------------------------------
函数定义：``String alignRight(str, padChar, len)``

- **参数定义：** ``str`` 类型：String；``padChar`` 类型：String；``len`` 类型：Number；
- **返回类型：** ``String``
- **作用：** 字符串在指定长度下进行右对齐，空出来的字符使用padChar补齐。如果传入多个字符将会取第一个字符。

**例子**

.. code-block:: js
    :linenos:

    string.alignRight(null, *, *)     = null
    string.alignRight("", 3, 'z')     = "zzz"
    string.alignRight("bat", 3, 'z')  = "bat"
    string.alignRight("bat", 5, 'z')  = "batzz"
    string.alignRight("bat", 1, 'z')  = "bat"
    string.alignRight("bat", -1, 'z') = "bat"

alignLeft
------------------------------------
函数定义：``String alignLeft(str, padChar, len)``

- **参数定义：** ``str`` 类型：String；``padChar`` 类型：String；``len`` 类型：Number；
- **返回类型：** ``String``
- **作用：** 字符串在指定长度下进行左对齐，空出来的字符使用padChar补齐。如果传入多个字符将会取第一个字符。

**例子**

.. code-block:: js
    :linenos:

    string.alignLeft(null, *, *)     = null
    string.alignLeft("", 3, 'z')     = "zzz"
    string.alignLeft("bat", 3, 'z')  = "bat"
    string.alignLeft("bat", 5, 'z')  = "zzbat"
    string.alignLeft("bat", 1, 'z')  = "bat"
    string.alignLeft("bat", -1, 'z') = "bat"

alignCenter
------------------------------------
函数定义：``String alignCenter(str, padChar, len)``

- **参数定义：** ``str`` 类型：String；``padChar`` 类型：String；``len`` 类型：Number；
- **返回类型：** ``String``
- **作用：** 字符串在指定长度下进行剧中对齐，空出来的字符使用padChar补齐。如果传入多个字符将会取第一个字符。

**例子**

.. code-block:: js
    :linenos:

    string.alignCenter(null, *, *)     = null
    string.alignCenter("", 4, ' ')     = "    "
    string.alignCenter("ab", -1, ' ')  = "ab"
    string.alignCenter("ab", 4, ' ')   = " ab"
    string.alignCenter("abcd", 2, ' ') = "abcd"
    string.alignCenter("a", 4, ' ')    = " a  "
    string.alignCenter("a", 4, 'y')    = "yayy"

join
------------------------------------
函数定义：``String join(str, padChar, len)``

- **参数定义：** ``array`` 类型：List；``separator`` 类型：String；
- **返回类型：** ``String``
- **作用：** Joins the elements of the provided array into a single String containing the provided list of elements.

**例子**

.. code-block:: js
    :linenos:

    string.join(null, *)               = null
    string.join([], *)                 = ""
    string.join([null], *)             = ""
    string.join(["a", "b", "c"], ';')  = "a;b;c"
    string.join(["a", "b", "c"], null) = "abc"
    string.join([null, "", "a"], ';')  = ";;a"

isEmpty
------------------------------------
函数定义：``boolean isEmpty(str)``

- **参数定义：** ``str`` 类型：String；
- **返回类型：** ``Boolean``
- **作用：** Checks if a String is empty ("") or null.

**例子**

.. code-block:: js
    :linenos:

    string.isEmpty(null)      = true
    string.isEmpty("")        = true
    string.isEmpty(" ")       = false
    string.isEmpty("bob")     = false
    string.isEmpty("  bob  ") = false

equalsIgnoreCase
------------------------------------
函数定义：``boolean equalsIgnoreCase(str1, str2)``

- **参数定义：** ``str1`` 类型：String；``str2`` 类型：String；
- **返回类型：** ``Boolean``
- **作用：** 忽略大小写比较相等

**例子**

.. code-block:: js
    :linenos:

    string.equalsIgnoreCase(null, null)   = true
    string.equalsIgnoreCase(null, "abc")  = false
    string.equalsIgnoreCase("abc", null)  = false
    string.equalsIgnoreCase("abc", "abc") = true
    string.equalsIgnoreCase("abc", "ABC") = true

split
------------------------------------
函数定义：``List split(str, separatorChars)``

- **参数定义：** ``str`` 类型：String；``separatorChars`` 类型：String
- **返回类型：** ``List``
- **作用：** Splits the provided text into an array, separators specified. This is an alternative to using StringTokenizer.

**例子**

.. code-block:: js
    :linenos:

    string.split(null, *)         = null
    string.split("", *)           = []
    string.split("abc def", null) = ["abc", "def"]
    string.split("abc def", " ")  = ["abc", "def"]
    string.split("abc  def", " ") = ["abc", "def"]
    string.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
