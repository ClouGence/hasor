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
package org.more.remote.assembler.creater;
import java.rmi.Remote;
import org.more.hypha.anno.assembler.AnnoMetaDataUtil;
import org.more.hypha.anno.define.Bean;
import org.more.remote.RemoteService;
import org.more.remote.assembler.AbstractRmiBeanCreater;
/**
 * 注解代理
 * @version : 2011-8-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class AnnoRmiBeanCreater extends AbstractRmiBeanCreater {
    private RemoteService remoteService = null;
    private Class<?>[]    faces         = null;
    private Class<?>      classType     = null;
    //
    public AnnoRmiBeanCreater(Class<?>[] faces, Class<?> classType, RemoteService remoteService) {
        this.remoteService = remoteService;
        this.faces = faces;
        this.classType = classType;
    };
    public Class<?>[] getFaces() throws Throwable {
        return this.getRemoteFaces(this.faces);
    };
    public Remote create() throws Throwable {
        Object obj = null;
        //1.创建对象
        Bean annoBean = this.classType.getAnnotation(Bean.class);
        if (annoBean != null) {
            String id = AnnoMetaDataUtil.getBeanID(annoBean, this.classType);
            obj = this.remoteService.getApplicationContext().getBean(id);
        } else
            obj = this.classType.newInstance();
        //2.生成Remote代理
        return super.getRemoteByFaces(obj, this.getFaces());
    };
}