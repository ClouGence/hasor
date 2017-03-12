&emsp;&emsp;ApiBinder 和 Module 一样，都是 Hasor 的基础。也是您接触 Hasor 必然接触到的东西。本小节就简单讲一下 ApiBinder 都能做什么。

- Apibinder 是您模块在 init 阶段唯一可以接触到的接口。
- 它仅在 init 阶段有效，如果您不可以在 onStart 阶段使用这个接口。
- ApiBinder 可以提供给你 Settings 接口用来读取配置文件。
- 它可以让您使用代码形式进行依赖注入。
- 您可以通过 ApiBinder 获取 Environment 接口来操作环境变量。

&emsp;&emsp;说了这么多 ApiBinder 是个什么？，它就是你通往 Hasor 的一个重要入口。