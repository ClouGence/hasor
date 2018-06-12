感谢理解
-------------------
商业软件的背后往往是有公司资源，他们可以雇佣人员，针对产品撰写一些文稿、做宣传、甚至活动。在各个论坛甚至答疑也会非常及时。
而 Hasor 是个人作品，编写 Hasor 是一个十分耗体力、耗精力的运动，相信走在开源道路上的所有开源作者都有这种感悟。

因此也请你在提问之前充分准备，下面列出一些不受欢迎的问题：

- **1.Hasor 支持 xxx 功能嘛？**
    - 建议在问问题之前请仔细查阅手册，研究代码

- **2.xxx 项目也有一个类似的功能，请问和 Hasor 有什么区别？**
    - 首先Hasor作者没有义务帮您评测各类产品。如果您是在做技术选型，功能产品对比是您必须具备的一种能力。

- **3.Hasor 的 xxx 是怎么实现的？**
    - 我们非常欢迎您是原理性的讨论，而不是简单的询问。如果您想知道 Hasor 是如何实现的，那么查看手册和翻阅相关代码是您最快的途径。

- **4.xxx 是开源的嘛？**
    - 目前 Hasor 是 Apache 2.0 协议授权。

在提出问题之前，我们希望您做一些深入的了解。我相信但凡您做一点功课都不会问出像 “Hasor 是开源的嘛？” 这类问题。要知道大家的时间都是宝贵的。


贡献
-------------------
做开源项目，往往是利用工作时间之余来做这些事情，需要耗费很大精力和时间。Hasor 不要求您一定有所回报，但是还是希望您有一些回报以帮助 Hasor 可以更好的前进。

帮助 Hasor 可以有很多方式，资金的捐赠只是其中一种。我们希望看到的是，您在 Hasor 上学到东西之余能够让我们知道您、让更多人知道 Hasor。帮助 Hasor 包括但不限下面这些途径：

1. 在社区向 Hasor 提问或解答问题。
2. 分享您在使用 Hasor 过程中的心得和体会，您也可以撰写Blog或者微博，向更多的人介绍 Hasor。
3. 向 Hasor 递交 Issues，报告 Bug 和建议。[http://git.oschina.net/teams/hasor/issues]
4. 贡献您的智慧，为 Hasor 递交代码
5. 捐赠金钱
6. 提供服务器空间带宽资源
7. 提供不同语言的翻译
8. 为 Hasor 提供不同的插件

如果您觉得上面这些帮助 Hasor 的方式对于您来说仍然很难做到，那么您可以在
`Github <https://github.com/zycgit/hasor>`__ 或 `码云 <http://git.oschina.net/zycgit/hasor>`__
上 fork、start 一下，这也算是为 Hasor 进行了贡献。


编译本手册
-------------------
1.安装pip：

- https://pip.pypa.io/en/stable/installing/
- https://bootstrap.pypa.io/3.2/get-pip.py
- python get-pip.py
- pip install -U pip

2.安装sphinx

- pip install sphinx  --upgrade --ignore-installed six
- pip install sphinx_rtd_theme（https://sphinx-rtd-theme.readthedocs.io/en/latest/configuring.html）
- pip install recommonmark（http://www.sphinx-doc.org/en/master/markdown.html）
- sphinx-quickstart

3.编译手册

- make html
