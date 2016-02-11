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
package net.hasor.plugins.mimetype;
import javax.servlet.ServletContext;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2015年2月11日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerMimeTypeWebContext extends InnerMimeTypeContext {
    private static final long serialVersionUID = -174159036966050326L;
    private ServletContext    sc               = null;
    public InnerMimeTypeWebContext(Object content) {
        super(content);
        this.sc = (ServletContext) content;
    }
    public String getMimeType(String suffix) {
        String mimeType = sc.getMimeType(suffix);
        if (StringUtils.isBlank(mimeType) == false)
            return mimeType;
        return super.getMimeType(suffix);
    }
}