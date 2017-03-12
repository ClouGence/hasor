&emsp;&emsp;在上一个小节我们对 ApiBinder 实现了一个自定义的扩展，并且使用了它。那么下面将会更加详细的讲解，ApiBinder的转换机制。让我们回顾一下代码：
```java
TestBinder myBinder = (TestBinder)apiBinder;
```

&emsp;&emsp;在上一个例子中我们使用的强制类型转换来得到我们的自定义 ApiBinder，倘若我们的扩展模块没有部署进Hasor。那么这个代码就会发生类型转换异常。

&emsp;&emsp;为了避免此类问题，在强制类型转换之前您应该先进行一下类型判断，例如：
```java
if (apibindr instanceof TestBinder){
   ...
}
```

&emsp;&emsp;除此之外，您还可以这样来避免强制类型转换：
```java
TestBinder myBinder = apiBinder.tryCast(TestBinder.class);
```

&emsp;&emsp;如果 ApiBinder 的扩展模块部署到了 Hasor 中那么 tryCast 就会正确返回给您一个 TestBinder。如果没有正确配置，它会返回一个 null。