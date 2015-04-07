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
package net.hasor.rsf.address;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.hasor.core.Hasor;
import org.more.FormatException;
import org.more.util.StringUtils;
/**
 * 服务地址例：“rsf://127.0.0.1:8000/unit/group/name/version” <br/>
 * --unit   :单元名称<br/>
 * --group  :服务分组<br/>
 * --name   :服务名称<br/>
 * --version:服务版<br/>
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class InterServiceAddress extends InterAddress {
    private String group   = null; //:服务分组
    private String name    = null; //:服务名称
    private String version = null; //:服务版
    //
    //
    public InterServiceAddress(String serviceURL) throws URISyntaxException {
        this(new URI(serviceURL));
    }
    public InterServiceAddress(URI serviceURL) {
        super(serviceURL);
        if (checkFormat(serviceURL) == false) {
            throw new FormatException(serviceURL + " format error.");
        }
        String servicePath = serviceURL.getPath();
        if (servicePath.startsWith("/")) {
            servicePath = servicePath.substring(1);
        }
        String[] arrays = servicePath.split("/");
        this.group = arrays[1];
        this.name = arrays[2];
        this.version = arrays[3];
    }
    public InterServiceAddress(String hostAddress, int hostPort, String formUnit, String group, String name, String version) throws URISyntaxException {
        super(hostAddress, hostPort, formUnit);
        this.group = Hasor.assertIsNotNull(group, "group is null.");
        this.name = Hasor.assertIsNotNull(name, "name is null.");
        this.version = Hasor.assertIsNotNull(version, "version is null.");
    }
    //
    /** @return 服务分组*/
    public String getGroup() {
        return this.group;
    }
    /** @return 服务名*/
    public String getName() {
        return this.name;
    }
    /** @return 服务版本*/
    public String getVersion() {
        return this.version;
    }
    //
    /**
     * 两个 Address 可以比较是否相等
     * @param obj 另一个对象
     * @return 返回结果。
     */
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            String group = ((InterServiceAddress) obj).group;
            String name = ((InterServiceAddress) obj).name;
            String version = ((InterServiceAddress) obj).version;
            return StringUtils.equals(group, this.group) && StringUtils.equals(name, this.name) && StringUtils.equals(version, this.version);
        }
        //
        return false;
    }
    public String toString() {
        return super.toString() + String.format("/%s/%s/%s", this.group, this.name, this.version);
    }
    protected URI createURL() throws URISyntaxException {
        String path = String.format("/%s/%s/%s/%s", this.getFormUnit(), this.getGroup(), this.getName(), this.getVersion());
        return new URI(SECHMA, null, this.getHostAddress(), this.getHostPort(), "/" + path, null, null);
    }
    protected boolean checkFormat(URI serviceURL) {
        if (super.checkFormat(serviceURL)) {
            String REG = "[A-Za-z0-9_\\.]+";
            Matcher mat = Pattern.compile("(/" + REG + ")").matcher(serviceURL.getPath());
            List<String> result = new ArrayList<String>();
            while (mat.find()) {
                result.add(mat.group());
            }
            if (result.size() >= 4) {
                return true;
            }
        }
        return false;
    }
}