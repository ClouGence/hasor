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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.hasor.core.Hasor;
import org.more.FormatException;
import org.more.logger.LoggerHelper;
import org.more.util.StringUtils;
/**
 * 服务地址例：“rsf://127.0.0.1:8000/unit”
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class InterAddress {
    public static final String SECHMA = "rsf";
    private final String       formUnit;       //所属单元
    private final String       hostAddress;    //地址
    private final int          hostAddressData; //地址数值表现形式
    private final int          hostPort;       //端口
    private final URI          uriFormat;
    //
    public InterAddress(String newAddressURL) throws URISyntaxException {
        this(new URI(newAddressURL));
    }
    public InterAddress(URI newAddressURL) {
        if (checkFormat(newAddressURL) == false) {
            throw new FormatException(newAddressURL + " format error.");
        }
        this.uriFormat = Hasor.assertIsNotNull(newAddressURL, "address URL is null.");
        this.hostAddress = newAddressURL.getHost();
        this.hostPort = newAddressURL.getPort();
        String formPath = newAddressURL.getPath();
        if (formPath.startsWith("/")) {
            formPath = formPath.substring(1);
        }
        this.formUnit = formPath.split("/")[0];
        this.hostAddressData = this.initIP(this.hostAddress);
    }
    public InterAddress(String hostAddress, int hostPort, String formUnit) throws URISyntaxException {
        this.hostAddress = Hasor.assertIsNotNull(hostAddress, "hostAddress is null.");
        this.hostPort = hostPort;
        this.formUnit = Hasor.assertIsNotNull(formUnit, "formUnit is null.");
        this.uriFormat = this.createURL();
        this.hostAddressData = this.initIP(this.hostAddress);
    }
    private int initIP(String hostIP) {
        int ipInt = 0;
        String[] ipParts = hostIP.split("\\.");
        for (int i = 0; i < ipParts.length; i++) {
            int ipPartData = Integer.parseInt(ipParts[i]);
            ipInt = ipInt | (ipPartData << ((3 - i) * 8));
        }
        return ipInt;
    }
    //
    /** @return 地址*/
    public String getHostAddress() {
        return this.hostAddress;
    }
    /** @return 端口*/
    public int getHostPort() {
        return this.hostPort;
    }
    /** @return 所属单元*/
    public String getFormUnit() {
        return this.formUnit;
    }
    /** @return 获取IP的int值*/
    public int getHostAddressData() {
        return this.hostAddressData;
    }
    /**转换地址为URL形式*/
    public URI toURI() throws URISyntaxException {
        return this.uriFormat;
    }
    /**
     * 两个 Address 可以比较是否相等
     * @param obj 另一个对象
     * @return 返回结果。
     */
    public boolean equals(Object obj) {
        String diffHost = "";
        int diffProt = 0;
        if (obj instanceof InterAddress) {
            diffHost = ((InterAddress) obj).getHostAddress();
            diffProt = ((InterAddress) obj).getHostPort();
        } else {
            return false;
        }
        //
        if (StringUtils.equalsBlankIgnoreCase(diffHost, this.hostAddress)) {
            return diffProt == this.hostPort;
        }
        return false;
    }
    //
    //
    public String toString() {
        return String.format("rsf://%s:%s/%s", this.hostAddress, this.hostPort, this.formUnit);
    }
    protected URI createURL() throws URISyntaxException {
        return new URI(SECHMA, null, this.getHostAddress(), this.getHostPort(), "/" + this.formUnit, null, null);
    }
    protected boolean checkFormat(URI rsfAddress) {
        if (StringUtils.equalsBlankIgnoreCase(SECHMA, rsfAddress.getScheme()) == true) {
            if (StringUtils.isBlank(rsfAddress.getHost()) == false) {
                if (rsfAddress.getPort() != 0) {
                    String REG = "[A-Za-z0-9_\\.]+";
                    Matcher mat = Pattern.compile("/(" + REG + ")").matcher(rsfAddress.getPath());
                    mat.find();
                    String formUnit = mat.group(1);
                    if (StringUtils.isBlank(formUnit) == false) {
                        return Pattern.matches(REG, formUnit);
                    }
                }
            }
        }
        LoggerHelper.logFinest("'%s' rsfAddress format error.", rsfAddress);
        return false;
    }
}