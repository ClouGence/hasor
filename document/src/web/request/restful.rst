RESTful
------------------------------------
Hasor Web 框架支持 RESTful 形式的 URL 映射配置，例如：

.. code-block:: java
    :linenos:

    @MappingTo("/user/info/${userID}")
    public class HelloAction {
        public void execute(@PathParameter("userID") long userID) {
            ...
        }
    }


下面还可以通过请求类型区分操作：

.. code-block:: java
    :linenos:

    @MappingTo("/user/info/${userID}")
    public class HelloAction {
        @Post
        public void updateUser(@PathParameter("userID") long userID) {
            ...
        }
        @Get
        public void queryByID(@PathParameter("userID") long userID) {
            ...
        }
    }


或者通过两个 RESTful 参数来简化接口定义：

.. code-block:: java
    :linenos:

    @MappingTo("/user/info/${userID}.${action}")
    public class HelloAction {
        public void execute(@PathParam("userID") long userID,
                            @PathParameter("action") String action) {
            if ("update".equals(action)){
                ...
            } else if ("delete".equals(action)){
                ...
            } else {
                ...
            }
        }
    }
