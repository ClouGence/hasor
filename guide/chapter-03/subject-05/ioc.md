&emsp;&emsp;“依赖注入(DI)”有时候也被称为“控制反转(IoC)”本质上它们是同一个概念。具体是指，当某个类调用另外一个类的时候通常需要调用者来创建被调用者。但在控制反转的情况下调用者不在主动创建被调用者，而是改为由容器注入，因此而得名。这里的“创建”强调的是调用者的主动性。而依赖注入则不在需要调用者主动创建被调用者。举个例子通常情况下调用者（ClassA），会先创建好被调用者（FunBean），然后在调用方法callFoo中调用被调用者（FunBean）的foo方法：
```java
public class ClassA {
    private FunBean funBean = new FunBean();
    public void callFoo() {
        this.funBean.foo();
    }
}
public class FunBean {
    public void foo() {
        System.out.println("say ...");
    }
}
```

&emsp;&emsp;使用了依赖注入的情况恰恰相反，调用者(ClassA)事先并不知道要创建哪个被调用 者(FunBean)。ClassA 调用的是被注入进来的 FunBean，通常我们会为需要依赖注入的 对象留有 set 方法，在调用 callFoo 方法之前是需要先将 funBean 对象通过 setFunBean 方法设置进来的。例如:
```java
public class ClassA {
    private FunBean funBean = null;
    public void setFunBean(FunBean funBean) {
        this.funBean = funBean;
    }
    public void callFoo() {
        this.funBean.foo();
    }
}
public class FunBean {
    ......
}
```

---
&emsp;&emsp;严格意义上来说注入的形式分为两种，它们是“构造方法注入”和“set 属性注入”。我们经常听到有第三种注入方式叫“接口注入”。其实它只是“set 属性注入”的一种接口 表现形式。

#### A.构造方法注入
&emsp;&emsp;这种方式是指被注入的对象通过构造方法传入，例如下面代码:
```java
public class ClassA {
    private FunBean funBean = null;
    public ClassA(FunBean funBean) {
        this.funBean = funBean;
    }
    public void callFoo() {
        this.funBean.foo();
    }
}
```

#### B.set 属性注入
&emsp;&emsp;是指被注入的对象通过其 get/set 读写属性方法注入进来，例如:
```java
public class ClassA {
    private FunBean funBean = null;
    public void setFunBean(FunBean funBean) {
        this.funBean = funBean;
    }
    public void callFoo() {
        this.funBean.foo();
    }
}
```

#### C.接口注入
&emsp;&emsp;是指通过某个接口的 set 属性方法来注入，大家可以看到其本质还是 set 属性注入。只不过调用者(ClassA)，需要实现某个注入接口。
```java
public interface IClassA {
    public void setFunBean(FunBean funBean);
}
public class ClassA implements IClassA {
    private FunBean funBean = null;
    public void setFunBean(FunBean funBean) {
        this.funBean = funBean;
    }
    public void callFoo() {
        this.funBean.foo();
    }
}
```

&emsp;&emsp;上面我们讲解了什么是依赖注入，并且举例了一些依赖注入的表现形式。现在我们来看看 Hasor 的依赖如何帮助你进行快速开发。