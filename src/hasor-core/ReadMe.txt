Hasor-Core

    该项目是 Hasor 体系的核心，共分为三个部分。
“net.hasor.core.*” Hasor 的微内核，这一部分是整个 Hasor 的核心。
“net.hasor.plugins.*” 作为 Hasor 核心项目对外提供的一些插件。
“org.more.*”，这部分包含了 Hasor 依赖的一些第三方工具类；
以及我以前写的一些独立的工具包，有很大一部分工具 Hasor 没有使用它。
    目前最新版本 v0.0.3 是 Hasor 的核心软件包，几乎所有 Hasor 
扩展模块都会依赖到它。该软件包中包含了：模块生命周期管理、配置文件服务、
事件服务、环境变量、IoC/Aop、Bean。并且通过 Guice提供 JSR-330 标准的兼容。
