- `doStart` ：执行 start 阶段的起始标志。
- `ContextEvent_Started` ：通过事件机制发送 `AppContext#ContextEvent_Started` 事件。
- `doStartCompleted` ：执行 start 阶段的终止标志。
---

&emsp;&emsp;如一开始在 `0. 生命周期` 小结中所介绍，Start 阶段一共有三个扩展点（0.生命周期：图右侧）它们分别是 `ContextStartListener` 接口的 `doStart`、`doStartCompleted` 方法，以及 LifeModule 接口的 `onStart` 方法。

### A. 意义

&emsp;&emsp;下面这张图为我们展现了这三个节点对我们的重要意义。

![作用](http://files.hasor.net/uploader/20170305/155728/CC2_040A_B4A8_396.jpg "作用")

&emsp;&emsp;对于框架而言，加载 `LifeModule` 的先后顺序没有多大重要性，但是对于开发者而言可能有着很重要的意义，例如：先初始化数据库连接，在创建测试数据。我们知道通常在同一个应用中，创建数据库连接的 Module 和 创建测试数据的 Module 它们的加载顺序我们是可以控制的。但是当引入一个基于 Hasor Module 的第三方组件时。我们就无法保证 Module 的加载顺序，因为框架不会对它们进行排序的，同时开发者也无法进行控制。

&emsp;&emsp;这时候 `ContextStartListener` 接口的两个方法就会为我们提供一些帮助。这一组方法执行的时机相当于给所有 `LifeModule` 在执行它们的 `onStart` 方法前后加了一个大的全局拦截器。

### B. 什么时候使用 ContextStartListener？

&emsp;&emsp;严格意义上说，ContextStartListener 的设计初衷并不是给业务系统准备的。但当您被此类问题困扰时候，可以尝试使用这个接口来解决问题。当然这个接口也不是万能的，它也有一些限制。

- 第一个限制：ContextStartListener，即便可以在所有 `LifeModule` 执行 onStart 之前执行，但是它依然无法控制 LifeModule 具体的执行顺序。
- 第二个限制：ContextStartListener，无法知道哪些 `LifeModule` 被加载。同样的 `ContextShutdownListener` 也无法知道哪些 LifeModule 没有成功执行 onStop。