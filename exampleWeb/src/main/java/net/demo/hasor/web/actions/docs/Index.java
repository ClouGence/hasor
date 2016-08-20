/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.demo.hasor.web.actions.docs;
import net.demo.hasor.core.Action;
import net.demo.hasor.domain.VersionInfoDO;
import net.demo.hasor.manager.EnvironmentConfig;
import net.demo.hasor.manager.VersionInfoManager;
import net.hasor.core.Inject;
import net.hasor.restful.api.MappingTo;
import net.hasor.restful.api.ReqParam;
import org.more.util.StringUtils;

import java.util.List;
/**
 *
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
@MappingTo("/docs/index.htm")
public class Index extends Action {
    @Inject
    private EnvironmentConfig  envConfig;
    @Inject
    private VersionInfoManager versionManager;
    //
    public void execute(@ReqParam("apiFrame") String apiFrame) throws Exception {
        if (StringUtils.isBlank(apiFrame)) {
            apiFrame = envConfig.getCurentVersion();
        }
        VersionInfoDO curVersion = versionManager.queryByVersion(apiFrame);
        List<VersionInfoDO> allVersion = versionManager.queryListOrerByVersion();
        //
        this.putData("curVersion", curVersion);
        this.putData("allVersion", allVersion);
        //
        //
        //        Parser parser = Parser.builder().build();
        //        Node document = parser.parse("### 一、基本IoC\n" +
        //                "&emsp;&emsp;首先开始一个项目中会有各种类，类与类之间还有复杂的依赖关系。如果没有 IoC 框架的情况下，我们需要手工的创建它们。通常在你脑海里会呈现一张庞大的对象图，你必须要保证它们依赖没有错误。但这是十分繁琐而且是很容易出错的。\n" +
        //                "\n" +
        //                "&emsp;&emsp;有了 Spring、Guice 这一类优秀的 IoC 框架之后，你再也不需要去描绘这样对象图。整个对象的初始化工作全部交给 IoC 容器去完成。你只需要简单的在配置文件上声明这个属性的来源是哪里即可，而后者 Guice 采用了纯粹的注解方式，这使得依赖注入的配置更加简单和直观。\n" + 
        //                "\n" +
        //                "&emsp;&emsp;在 IoC 方面 Hasor 的做法和 Guice 比较类似，不同的是 Hasor 的依赖注入有着更为灵活的方式来控制要注入的对象，而并不是按照注解或者配置文件上指定的方式死板的工作。开发者可以根据自己的意愿，甚至某一个具体的业务逻辑来实现动态注入。这一个特性会在后面的章节中得以体现。\n" +
        //                "\n" +
        //                "&emsp;&emsp;为了说明如何使用 Hasor 我们启动一个 TradeService 类用来实现下单操作，而该类会依赖到 InventoryService 库存服务，PayService 付款服务以及 ItemService 商品服务。\n" + 
        //                "```\n" +
        //                "public class TradeService {\n" +
        //                "    @Inject\n" +
        //                "    private PayService      payService;\n" +
        //                "    @Inject\n" + 
        //                "    private ItemService     itemService;\n" +
        //                "\n" + 
        //                "    public boolean payItem(long itemId , CreditCard creditCard){\n" + 
        //                "        ....\n" + 
        //                "    }\n" + 
        //                "}\n" + 
        //                "```\n" + 
        //                "\n" +
        //                "&emsp;&emsp;接下来我们创建 AppContext 并通过它将 TradeService 所需要的服务对象注入进去。此时倘若 InventoryService、PayService、ItemService 为具体的实现类，那么您只需要通过下面这个代码完成 TradeService 对象的构建。\n" +
        //                "```\n" +
        //                "AppContext appContext = Hasor.createAppContext();\n" +
        //                "TradeService tradeService = appContext.getInstance(TradeService.class);\n" +
        //                "....\n" + 
        //                "tradeService.payItem(......);\n" +
        //                "```\n" +
        //                "\n" +
        //                "&emsp;&emsp;如果 PayService 为一个接口而非具体的实现类，那么您可以简单的在 PayService 接口上通过 @ImplBy 注释标记出它的实现在哪里。同理 InventoryService、ItemService 两个服务也是一样。\n" +
        //                "```\n" +
        //                "@ImplBy(PayServiceImpl.class)\n" +
        //                "public interface PayService {\n" + 
        //                "    ....\n" +
        //                "}\n" +
        //                "```\n" + 
        //                "&emsp;&emsp;在实际场景中，通常这种重量级的服务对象都是单例的，在 Hasor 中要想实现单例只需要简单的在对象上增加一个 @Singleton 注解即可。例如：TradeService 是一个单实例服务。\n" +
        //                "```\n" +
        //                "@Singleton\n" +
        //                "public class TradeService {\n" +
        //                "    @Inject\n" +
        //                "    private PayService      payService;\n" + 
        //                "    @Inject\n" + 
        //                "    private ItemService     itemService;\n" +
        //                "    \n" +
        //                "    public boolean payItem(long itemId , CreditCard creditCard){\n" +
        //                "        ....\n" + 
        //                "    }\n" + 
        //                "}\n" +
        //                "```\n" + 
        //                "\n" +
        //                "&emsp;&emsp;对于这样一种关键服务，有的时候我们需要在项目启动的时让它自己做一些初始化的工作。在 Spring 中需要我们配置 xml，而 Guice 你需要实现 TypeListener 接口。在 Hasor 中你只需要简单的在方法上标注上 @Init 注释即可。\n" +
        //                "```\n" +
        //                "@Singleton\n" + 
        //                "public class TradeService {\n" +
        //                "    @Inject\n" +
        //                "    private PayService      payService;\n" +
        //                "    @Inject\n" + 
        //                "    private ItemService     itemService;\n" + 
        //                "    \n" +
        //                "    public boolean payItem(long itemId , CreditCard creditCard){\n" +
        //                "        ....\n" +
        //                "    }\n" + "    @Init\n" + 
        //                "    public void initMethod(){\n" + 
        //                "        ....\n" + 
        //                "    }\n" + 
        //                "}\n" + 
        //                "```\n" + 
        //                "&emsp;&emsp;同样的起步例子，我们还可以通过 Module 的方式进行实现。首先我们列出完整的业务程序原始代码，然后通过下面的 Hasor 代码实现完全 `无侵入` 的依赖注入。\n" +
        //                "```\n" +
        //                "public class TradeService {\n" +
        //                "    private PayService       payService;\n" + 
        //                "    private ItemService      itemService;\n" + 
        //                "    public boolean payItem(long itemId , CreditCard creditCard){\n" +
        //                "        ....\n" +
        //                "    }\n" +
        //                "    public void initMethod(){\n" + 
        //                "        ....\n" + 
        //                "    }\n" +
        //                "}\n" + 
        //                "```\n" +
        //                "```\n" + 
        //                "AppContext appContext = Hasor.createAppContext(new Module() {\n" +
        //                "    public void loadModule(ApiBinder apiBinder) throws Throwable {\n" +
        //                "        apiBinder.bindType(TradeService.class)\n" + 
        //                "                .inject(\"payService\", apiBinder.bindType(PayService.class).toInfo())\n" +
        //                "                .inject(\"itemService\", apiBinder.bindType(ItemService.class).toInfo())\n" +
        //                "                .initMethod(\"initMethod\")\n" + 
        //                "                .asEagerSingleton();\n" +
        //                "    }\n" + 
        //                "});\n" +
        //                "```");
        //        HtmlRenderer renderer = HtmlRenderer.builder().build();
        //        String htmlData = renderer.render(document );
        //        data.put("htmlData",htmlData);
    }
} 