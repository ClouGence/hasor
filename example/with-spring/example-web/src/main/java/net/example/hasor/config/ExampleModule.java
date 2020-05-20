package net.example.hasor.config;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.DimModule;
import net.hasor.dataway.DatawayService;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.spring.SpringModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@DimModule
@Component
public class ExampleModule implements SpringModule {
    @Autowired
    private DataSource dataSource = null;

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // .DataSource form Spring boot into Hasor
        apiBinder.installModule(new JdbcModule(Level.Full, this.dataSource));
        // .custom DataQL
        //apiBinder.tryCast(QueryApiBinder.class).loadUdfSource(apiBinder.findClass(DimUdfSource.class));
        //
        //        apiBinder.bindSpiListener(PreExecuteChainSpi.class, (apiInfo, future) -> {
        //            apiInfo.getParameterMap().put("self", "me");
        //            if (apiInfo.getApiPath().equals("/api/demos/find_user_by_name")) {
        //                future.completed(new HashMap<String, Object>() {{
        //                    put("status", false);
        //                    put("message", "no power");
        //                }});
        //            }
        //            // future.failed(new StatusMessageException(401, "not power"));
        //        });
        //        apiBinder.bindSpiListener(ResultProcessChainSpi.class, new ResultProcessChainSpi() {
        //            public Object callAfter(boolean formPre, ApiInfo apiInfo, Object result) {
        //                if (formPre) {
        //                    //为了避免和 PreExecuteChainSpi 的冲突，如果前置拦截器处理了。那么后置拦截器就不处理。
        //                    return result;
        //                }
        //                if (apiInfo.getApiPath().equals("/demos/mock")) {
        //                    throw new StatusMessageException(401, "not power2222");
        //                }
        //                return new HashMap<String, Object>() {{
        //                    put("method", apiInfo.getMethod());
        //                    put("path", apiInfo.getApiPath());
        //                    put("result", result);
        //                }};
        //            }
        //
        //            public Object callError(boolean formPre, ApiInfo apiInfo, Throwable e) {
        //                e.printStackTrace();
        //                return new HashMap<String, Object>() {{
        //                    put("method", apiInfo.getMethod());
        //                    put("path", apiInfo.getApiPath());
        //                    put("errorMessage", e.getMessage());
        //                }};
        //            }
        //        });
        //        {
        //
        //
        //        apiBinder.bindSpiListener(SerializationChainSpi.class, (apiInfo, mimeType, result) -> {
        //            String jsonString = JSON.toJSONString(result);
        //            return SerializationChainSpi.SerializationInfo.ofString("abc/text/plain", jsonString); //JSON.toJSONString(result);
        //        });
        //
        //        apiBinder.bindSpiListener(SerializationChainSpi.class, (apiInfo, mimeType, result) -> {
        //            //
        //            try {
        //                BufferedImage bi = new BufferedImage(150, 70, BufferedImage.TYPE_INT_RGB);
        //                Graphics2D g2 = (Graphics2D) bi.getGraphics();
        //                // background color
        //                g2.fillRect(0, 0, 150, 70);
        //                g2.setColor(Color.WHITE);
        //                // text
        //                g2.setFont(new Font("宋体", Font.BOLD, 18));
        //                g2.setColor(Color.BLACK);
        //                g2.drawString(String.valueOf(result), 3, 50);
        //                // save to bytes
        //                ByteArrayOutputStream oat = new ByteArrayOutputStream();
        //                ImageIO.write(bi, "JPEG", oat);
        //                //
        //                return SerializationChainSpi.SerializationInfo.ofBytes(//
        //                        mimeType.getMimeType("jpeg"),   // response context-type
        //                        oat.toByteArray()                       // response body
        //                );
        //            } catch (Exception e) {
        //                throw ExceptionUtils.toRuntimeException(e);
        //            }
        //        });
    }

    @Override
    public void onStart(AppContext appContext) throws Throwable {
        DatawayService datawayService = appContext.getInstance(DatawayService.class);
        //        Map<String, Object> objectMap = datawayService.invokeApi("post", "/api/demos/find_user_by_name", new HashMap<String, Object>() {{
        //            put("userName", "1");
        //        }});
        //        System.out.println(JSONObject.toJSONString(objectMap));
    }
}