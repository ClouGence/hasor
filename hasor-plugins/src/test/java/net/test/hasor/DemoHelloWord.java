//package net.test.hasor;
//import net.hasor.core.*;
//import net.hasor.dataql.binder.DataQL;
//import net.hasor.db.JdbcModule;
//import net.hasor.db.Level;
//import net.hasor.db.jdbc.core.JdbcTemplate;
//import net.hasor.plugins.autoscan.Command;
//import net.hasor.plugins.freemarker.render.FreemarkerRender;
//import net.hasor.rsf.RsfApiBinder;
//import net.hasor.tconsole.CommandExecutor;
//import net.hasor.tconsole.ConsoleApiBinder;
//import net.hasor.tconsole.launcher.CmdRequest;
//import net.hasor.web.WebApiBinder;
//import net.hasor.web.WebController;
//import net.hasor.web.annotation.Get;
//import net.hasor.web.annotation.MappingTo;
//import net.hasor.web.annotation.Render;
//
//import javax.sql.DataSource;
//public class DemoHelloWord {
//    public static void main(String[] args) {
//        Hasor.createAppContext("xxxx", new Module() {
//            @Override
//            public void loadModule(ApiBinder apiBinder) throws Throwable {
//                //
//                DataSource dataSource = null;
//                apiBinder.installModule(new JdbcModule(Level.Full, dataSource));
//                //
//                apiBinder.tryCast(WebApiBinder.class).loadMappingTo(apiBinder.findClass(MappingTo.class));
//                apiBinder.tryCast(WebApiBinder.class).loadRender(apiBinder.findClass(Render.class));
//                //
//                apiBinder.tryCast(ConsoleApiBinder.class).addCommand(new String[] { "devops" }, DevOpsCommand.class);
//                //
//                apiBinder.tryCast(RsfApiBinder.class).rsfService(ServiceFace.class).to(ManagerBean.class).register();
//                //
//
//            }
//        });
//    }
//}
////
//@Singleton
//@MappingTo("/hello.html")
//class HelloAction extends WebController {
//    @Inject
//    private ServiceFace serviceFace;
//    @Get
//    public void doGet() {
//        //
//    }
//}
//@Render("html")
//class UserFreemarkerRender extends FreemarkerRender{}
//@ImplBy(ManagerBean.class)
//interface ServiceFace {
//    public void remoteFace();
//}
//class ManagerBean implements ServiceFace {
//    @Inject
//    private JdbcTemplate jdbcTemplate;
//    @Inject
//    private DataQL qlTemplate;
//    @Override
//    public void remoteFace() {
//        //
//    }
//}
//@Command()
//class DevOpsCommand implements CommandExecutor {
//    @Override
//    public String helpInfo() {
//        return null;
//    }
//    @Override
//    public boolean inputMultiLine(CmdRequest request) {
//        return false;
//    }
//    @Override
//    public String doCommand(CmdRequest request) throws Throwable {
//        return null;
//    }
//}
//@WatchTo("/sssss/sss")
//class TTTT implements Watch {
//    @Override
//    public void initValue(Object initValue) throws Throwable {
//        //
//    }
//    @Override
//    public void onChange(Object newValue) throws Throwable {
//        //
//    }
//}