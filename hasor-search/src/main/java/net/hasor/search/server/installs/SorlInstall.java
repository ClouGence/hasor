/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.search.server.installs;
import java.io.File;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrException.ErrorCode;
import org.apache.solr.core.ConfigSolr;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.more.logger.LoggerHelper;
/**
 * Sorl容器启动
 * @version : 2015年1月13日
 * @author 赵永春(zyc@hasor.net)
 */
public class SorlInstall implements Module {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        LoggerHelper.logInfo("Solr.init...");
        try {
            String searchHome = apiBinder.getEnvironment().envVar("SEARCH-HOME");
            CoreContainer cores = createCoreContainer(searchHome);
            //
            apiBinder.bindType(CoreContainer.class).toInstance(cores);
            LoggerHelper.logInfo("SEARCH-HOME = " + searchHome);
        } catch (Throwable t) {
            // catch this so our filter still works
            LoggerHelper.logSevere("Could not start Solr. Check SEARCH-HOME property and the logs");
            SolrCore.log(t);
            if (t instanceof Error) {
                throw (Error) t;
            }
        }
        LoggerHelper.logInfo("Solr.init done.");
    }
    /**
     * 初始化 {@link CoreContainer}对象。
     * @return a CoreContainer to hold this server's cores
     */
    protected CoreContainer createCoreContainer(String searchHome) {
        SolrResourceLoader loader = new SolrResourceLoader(searchHome);
        //
        if (new File(searchHome, ConfigSolr.SOLR_XML_FILE).exists() == false) {
            throw new SolrException(ErrorCode.SERVER_ERROR, "Bad solr.solrxml.location set: " + searchHome + " - should be 'SEARCH-HOME'");
        }
        //
        ConfigSolr config = ConfigSolr.fromSolrHome(loader, searchHome);
        CoreContainer cores = new CoreContainer(loader, config);
        cores.load();
        return cores;
    }
}