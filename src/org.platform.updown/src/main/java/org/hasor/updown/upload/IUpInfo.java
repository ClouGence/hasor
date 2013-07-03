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
package org.hasor.updown.upload;
import java.util.List;
/**
 * 上传服务获取信息接口，从该接口可以获取到上传服务的所有请求数据。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IUpInfo {
    //    /**获取{@link HttpServletRequest}对象。*/
    //    public HttpServletRequest getHttpRequest();
    //    /**获取{@link HttpServletResponse}对象。*/
    //    public HttpServletResponse getHttpResponse();
    /**获取所有上传条目。*/
    public List<IFileItem> getItems();
    /**获取指定的上传条目。*/
    public IFileItem getItem(String name);
    /**设置返回值*/
    public void setReturnData(Object returnData);
}