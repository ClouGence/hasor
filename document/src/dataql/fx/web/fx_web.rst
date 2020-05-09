--------------------
Web函数库
--------------------
引入集合函数库的方式为：``import 'net.hasor.dataql.fx.web.WebUdfSource' as webData;``

cookieMap
------------------------------------
函数定义：``Map cookieMap()``

- **参数定义：无
- **返回类型：** ``Map``
- **作用：** 获取 Cookie 并且以 Map 形式返回。

**例子**

.. code-block:: js
    :linenos:

    var cookie = webData.cookieMap() // 例如：JSESSIONID
    // 结果：
    // {
    //   "JSESSIONID": "EA23D2E1AC1CAE5F8BD17191EF80EE7C"
    // }

cookieArrayMap
------------------------------------
函数定义：``Map cookieArrayMap()``

- **参数定义：无
- **返回类型：** ``Map``
- **作用：** 获取 Cookie 并且以数组形式返回。

**例子**

.. code-block:: js
    :linenos:

    var cookie = webData.cookieMap() // 例如：JSESSIONID
    // 结果：
    // {
    //   "JSESSIONID": [
    //     "EA23D2E1AC1CAE5F8BD17191EF80EE7C"
    //   ]
    // }

getCookie
------------------------------------
函数定义：``String getCookie(cookieName)``

- **参数定义：** ``cookieName`` 类型：String
- **返回类型：** ``String``
- **作用：** 获取 Cookie。

**例子**

.. code-block:: js
    :linenos:

    // 例如：JSESSIONID = "EA23D2E1AC1CAE5F8BD17191EF80EE7C"
    webData.getCookie("JSESSIONID") = "EA23D2E1AC1CAE5F8BD17191EF80EE7C"
    webData.getCookie("dddd")       =  null

getCookieArray
------------------------------------
函数定义：``List getCookieArray(cookieName)``

- **参数定义：** ``cookieName`` 类型：String
- **返回类型：** ``List``
- **作用：** 获取Cookie数组形态。

**例子**

.. code-block:: js
    :linenos:

    // 例如：JSESSIONID = "EA23D2E1AC1CAE5F8BD17191EF80EE7C"
    webData.getCookieArray("JSESSIONID") = [ "EA23D2E1AC1CAE5F8BD17191EF80EE7C" ]
    webData.getCookieArray("dddd")       =  null

tempCookie
------------------------------------
函数定义：``Boolean tempCookie(cookieName, cookieValue)``

- **参数定义：** ``cookieName`` 类型：String，``cookieValue`` 类型：String
- **返回类型：** ``Boolean``
- **作用：** 临时 Cookie，临时 Cookie 的 MaxAge = -1。

**例子**

.. code-block:: js
    :linenos:

    // 第一次执行获取 不存在的 Cookie 返回为空
    webData.getCookie("dddd")           =  null

    // 设置新 Cookie
    webData.tempCookie("dddd","aaaa")   = true

    // 第二次查询 Cookie，可以得到上一次设置的值
    webData.getCookie("dddd")           =  "aaaa"

tempCookieAll
------------------------------------
函数定义：``Boolean tempCookieAll(cookieMap)``

- **参数定义：** ``cookieMap`` 类型：Map
- **返回类型：** ``Boolean``
- **作用：** 批量设置临时 Cookie。临时 Cookie 的 MaxAge = -1。

**例子**

.. code-block:: js
    :linenos:

    // 第一次执行获取 不存在的 Cookie 返回为空
    webData.getCookie("dddd")    =  null

    // 批量设置临时 Cookie
    webData.tempCookieAll({
        "dddd","aaaa"
    })

    // 第二次查询 Cookie，可以得到上一次设置的值
    webData.getCookie("dddd")   =  "aaaa"

storeCookie
------------------------------------
函数定义：``Boolean storeCookie(cookieName, cookieValue, maxAge)``

- **参数定义：** ``cookieName`` 类型：String，``cookieValue`` 类型：String，``maxAge`` 类型：Number
- **返回类型：** ``Boolean``
- **作用：** 存储 Cookie，Cookie 的有效期通过maxAge 参数指定。

**例子**

.. code-block:: js
    :linenos:

    // 第一次执行获取 不存在的 Cookie 返回为空
    webData.getCookie("dddd")           =  null

    // 设置新 Cookie
    webData.storeCookie("dddd","aaaa", 10)   = true

    // 第二次查询 Cookie，可以得到上一次设置的值
    webData.getCookie("dddd")           =  "aaaa"

storeCookieAll
------------------------------------
函数定义：``Boolean storeCookieAll(cookieMap, maxAge)``

- **参数定义：** ``cookieMap`` 类型：Map
- **返回类型：** ``Boolean``
- **作用：** 批量设置临时 Cookie。临时 Cookie 的 MaxAge = -1。

**例子**

.. code-block:: js
    :linenos:

    // 第一次执行获取 不存在的 cookie 返回为空
    webData.getCookie("dddd")    =  null

    // 批量设置 Cookie
    webData.storeCookieAll({
        "dddd","aaaa"
    }, 10)

    // 第二次查询 Cookie，可以得到上一次设置的值
    webData.getCookie("dddd")   =  "aaaa"

removeCookie
------------------------------------
函数定义：``Boolean removeCookie(cookieName)``

- **参数定义：** ``cookieMap`` 类型：Map
- **返回类型：** ``Boolean``
- **作用：** 删除 Cookie。

**例子**

.. code-block:: js
    :linenos:

    webData.removeCookie("dddd")    =  null

headerMap
------------------------------------
函数定义：``Map headerMap()``

- **参数定义：无
- **返回类型：** ``Map``
- **作用：** 获取请求 Header 并且以 Map 形式返回。

**例子**

.. code-block:: js
    :linenos:

    var header = webData.headerMap() // 例如
    // 结果：
    // {
    //   "sec-fetch-mode": "cors",
    //   "content-length": "603",
    //   "referer": "http://127.0.0.1:8080/interface-ui/",
    //   "sec-fetch-site": "same-origin",
    //   "accept-language": "zh-CN,zh;q=0.9",
    //   "cookie": "dddd=aaaa; JSESSIONID=EA23D2E1AC1CAE5F8BD17191EF80EE7C",
    //   "origin": "http://127.0.0.1:8080",
    //   "accept": "application/json",
    //   "host": "127.0.0.1:8080",
    //   "connection": "keep-alive",
    //   "content-type": "application/json; charset=UTF-8",
    //   "accept-encoding": "gzip, deflate, br",
    //   "user-agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36",
    //   "sec-fetch-dest": "empty"
    // }

headerArrayMap
------------------------------------
函数定义：``Map headerArrayMap()``

- **参数定义：无
- **返回类型：** ``Map``
- **作用：** 获取请求 Header 并且以 数组 形式返回。

**例子**

.. code-block:: js
    :linenos:

    var header = webData.headerArrayMap() // 例如
    // 结果：
    // {
    //   "sec-fetch-mode": [ "cors" ],
    //   "content-length": [ "608" ],
    //   "referer": [ "http://127.0.0.1:8080/interface-ui/" ],
    //   "sec-fetch-site": [ "same-origin" ],
    //   "accept-language": [ "zh-CN,zh;q=0.9" ],
    //   "cookie": [ "dddd=aaaa; JSESSIONID=EA23D2E1AC1CAE5F8BD17191EF80EE7C" ],
    //   "origin": [ "http://127.0.0.1:8080" ],
    //   "accept": [ "application/json" ],
    //   "host": [ "127.0.0.1:8080" ],
    //   "connection": [ "keep-alive" ],
    //   "content-type": [ "application/json; charset=UTF-8" ],
    //   "accept-encoding": [ "gzip, deflate, br" ],
    //   "user-agent": [ "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36" ],
    //   "sec-fetch-dest": [ "empty" ]
    // }

getHeader
------------------------------------
函数定义：``String getHeader(headerName)``

- **参数定义：** ``headerName`` 类型：String
- **返回类型：** ``String``
- **作用：** 获取 Header。

**例子**

.. code-block:: js
    :linenos:

    webData.getHeader("origin")         = "http://127.0.0.1:8080"
    webData.getHeader("content-type")   = "application/json; charset=UTF-8"

getHeaderArray
------------------------------------
函数定义：``List getHeaderArray(headerName)``

- **参数定义：** ``headerName`` 类型：String
- **返回类型：** ``List``
- **作用：** 获取所有名字相同的 Header。

**例子**

.. code-block:: js
    :linenos:

    webData.getHeaderArray("origin")       = [ "http://127.0.0.1:8080" ]
    webData.getHeaderArray("content-type") = [ "application/json; charset=UTF-8" ]

setHeader
------------------------------------
函数定义：``Boolean setHeader(headerName, headerValue)``

- **参数定义：** ``headerName`` 类型：String，``headerValue`` 类型：String
- **返回类型：** ``Boolean``
- **作用：** 设置 response Header。

**例子**

.. code-block:: js
    :linenos:

    webData.setHeader("abc", "ss")

setHeaderAll
------------------------------------
函数定义：``Boolean setHeaderAll(headerMap)``

- **参数定义：** ``headerMap`` 类型：Map
- **返回类型：** ``Boolean``
- **作用：** 批量设置 Header。

**例子**

.. code-block:: js
    :linenos:

    webData.setHeaderAll({
        "abc1", "ss",
        "abc2", "ss"
    })

addHeader
------------------------------------
函数定义：``Boolean addHeader(headerName, headerValue)``

- **参数定义：** ``headerName`` 类型：String，``headerValue`` 类型：String
- **返回类型：** ``Boolean``
- **作用：** 添加 Header。

**例子**

.. code-block:: js
    :linenos:

    webData.addHeader("abc", "ss")

addHeaderAll
------------------------------------
函数定义：``Boolean addHeaderAll(headerMap)``

- **参数定义：** ``headerMap`` 类型：Map
- **返回类型：** ``Boolean``
- **作用：** 批量添加 Header。

**例子**

.. code-block:: js
    :linenos:

    webData.addHeaderAll({
        "abc1", "ss",
        "abc2", "ss"
    })

sessionKeys
------------------------------------
函数定义：``List sessionKeys()``

- **参数定义：** 无
- **返回类型：** ``List``
- **作用：** 获得 session Keys。

**例子**

.. code-block:: js
    :linenos:

    webData.sessionKeys()

getSession
------------------------------------
函数定义：``Object getSession(key)``

- **参数定义：** ``key`` 类型：String
- **返回类型：** ``Object``
- **作用：** 获取 Session 中的属性

**例子**

.. code-block:: js
    :linenos:

    webData.getSession("xx")  // 相当于 httpSession.getAttribute("xx")

setSession
------------------------------------
函数定义：``Object setSession(key, newValue)``

- **参数定义：** ``key`` 类型：String，``newValue`` 类型：Object
- **返回类型：** ``Object``
- **作用：** 设置 Session 属性

功效：
setSession 在把 newValue 设置到对应的 Session 中时。会事先把已经存在的同名属性先拿出来，然后在更新 session 中的值。
当把 Session 更新好之后会返回之前 session 中已经存在的值

**例子**

.. code-block:: js
    :linenos:

    // 例如：xx = null 的前提下
    var res = webData.setSession("xx", "abc")  // res = null
    var res = webData.setSession("xx", "abc")  // res = abc

removeSession
------------------------------------
函数定义：``Boolean removeSession(key)``

- **参数定义：** ``key`` 类型：String
- **返回类型：** ``Boolean``
- **作用：** 根据 key 值删除Session

**例子**

.. code-block:: js
    :linenos:

    webData.removeSession("xx")  // return true or false

cleanSession
------------------------------------
函数定义：``Boolean cleanSession()``

- **参数定义：** 无
- **返回类型：** ``Boolean``
- **作用：** 删除所有Key

**例子**

.. code-block:: js
    :linenos:

    webData.cleanSession()

sessionInvalidate
------------------------------------
函数定义：``Boolean sessionInvalidate()``

- **参数定义：** 无
- **返回类型：** ``Boolean``
- **作用：** Invalidates this session then unbinds any objects bound to it.

**例子**

.. code-block:: js
    :linenos:

    webData.sessionInvalidate()

sessionId
------------------------------------
函数定义：``String sessionId()``

- **参数定义：** 无
- **返回类型：** ``String``
- **作用：** 获取 Session ID

**例子**

.. code-block:: js
    :linenos:

    webData.sessionId()

sessionLastAccessedTime
------------------------------------
函数定义：``Number sessionLastAccessedTime()``

- **参数定义：** 无
- **返回类型：** ``Number``
- **作用：** 返回客户端发送与之关联的请求的最后一次时间

**例子**

.. code-block:: js
    :linenos:

    webData.sessionLastAccessedTime()
