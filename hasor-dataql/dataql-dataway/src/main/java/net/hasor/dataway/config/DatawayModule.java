package net.hasor.dataway.config;
import net.hasor.core.Environment;
import net.hasor.dataway.web.*;
import net.hasor.utils.StringUtils;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DatawayModule implements WebModule {
    protected static Logger     logger        = LoggerFactory.getLogger(DatawayModule.class);
    /** 使用 findClass 虽然可以降低代码复杂度，但是会因为引入代码扫描而增加初始化时间。 */
    private          Class<?>[] controllerSet = new Class<?>[] {//
            ApiDetailController.class,  //
            ApiHistoryController.class, //
            ApiInfoController.class,    //
            ApiListController.class,    //
            GetHistoryController.class, //
            //
            DisableController.class,    //
            ExecuteController.class,    //
            SmokeController.class,      //
            SaveApiController.class,    //
            PublishController.class,    //
            PerformController.class,    //
            ModifyPathController.class, //
    };

    @Override
    public void loadModule(WebApiBinder apiBinder) {
        // .是否启用 Dataway
        Environment environment = apiBinder.getEnvironment();
        if (!Boolean.parseBoolean(environment.getVariable("HASOR_DATAQL_DATAWAY"))) {
            logger.info("dataway is disable.");
            return;
        }
        // .Api接口
        //
        //
        // .Dataway 后台管理界面
        if (!Boolean.parseBoolean(environment.getVariable("HASOR_DATAQL_DATAWAY_ADMIN"))) {
            logger.info("dataway admin is disable.");
            return;
        }
        String baseUri = environment.getVariable("HASOR_DATAQL_DATAWAY_UI_URL");
        if (StringUtils.isBlank(baseUri)) {
            baseUri = "/interface-ui/";
        }
        logger.info("dataway admin workAt " + baseUri);
        for (Class<?> aClass : controllerSet) {
            MappingToUrl toUrl = aClass.getAnnotation(MappingToUrl.class);
            apiBinder.mappingTo(fixUrl(baseUri + "/" + toUrl.value())).with(aClass);
        }
        //
        apiBinder.setEncodingCharacter("UTF-8", "UTF-8");
        apiBinder.filter(fixUrl(baseUri + "/*")).through(new InterfaceUiFilter(baseUri));
    }

    static String fixUrl(String url) {
        return url.replaceAll("/+", "/");
    }
}