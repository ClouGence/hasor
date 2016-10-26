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
package net.hasor.neuron.node;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.address.InterAddress;
import org.more.RepeateException;

import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
/**
 * 集群管理器
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class ClusterManager {
    @Inject
    private RsfContext     rsfContext      = null;
    private List<NodeData> allServiceNodes = null; //所有服务器节点
    //
    @Init
    public void init() throws UnknownHostException, URISyntaxException {
        this.allServiceNodes = new ArrayList<NodeData>();
    }
    //
    /** 动态添加节点 */
    public synchronized void addNode(InterAddress target) {
        AskNameService wrapper = this.rsfContext.getRsfClient(target).wrapper(AskNameService.class);
        //
        String targetServerID = wrapper.askServerID();
        if (this.allServiceNodes.containsKey(targetServerID)) {
            NodeData nodeData = this.allServiceNodes.get(targetServerID);
            if (!nodeData.getInterAddress().equalsHost(target)) {
                throw new RepeateException(target.toString());
            }
        }
        //
        this.allServiceNodes.add(new NodeData(target));
    }
    public List<NodeData> getAllServiceNodes() {
        return allServiceNodes;
    }
}