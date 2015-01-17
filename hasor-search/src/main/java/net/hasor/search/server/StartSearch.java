package net.hasor.search.server;
import net.hasor.core.Hasor;
import net.hasor.search.server.query.SorlQuery;
public class StartSearch {
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        System.setProperty("SEARCH-HOME", "%RUN_PATH%/src/search-home");
        Hasor.createAppContext(new SorlQuery());
        //
        //
        //
        //        SolrRequestParsers.DEFAULT.buildRequestFrom(core, params, streams);
        //
        Thread.sleep(100000L);
    }
}