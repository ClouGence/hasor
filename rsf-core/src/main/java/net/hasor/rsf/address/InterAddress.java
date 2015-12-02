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
import org.more.FormatException;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.Hasor;
/**
 * 服务地址例：“rsf://127.0.0.1:8000/unit”
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class InterAddress {
    protected static Logger    logger = LoggerFactory.getLogger(InterAddress.class);
    public static final String SECHMA = "rsf";
    private final String       formUnit;                                            //所属单元
    private final String       hostAddress;                                         //地址
    private final int          hostAddressData;                                     //地址数值表现形式
    private final int          hostPort;                                            //端口
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
    /** 返回目标IP地址*/
    public String getHost() {
        return this.hostAddress;
    }
    /** 返回目标地址的端口号*/
    public int getPort() {
        return this.hostPort;
    }
    /** 返回IP地址和端口，格式为：192.168.25.33:8000*/
    public String getHostPort() {
        return this.hostAddress + ":" + this.hostPort;
    }
    /** 返回地址所属单元*/
    public String getFormUnit() {
        return this.formUnit;
    }
    /** 返回IPv4地址的int表达形式。转换方法：字符串表达形式下可以分为4个字节对象，在由于int数据占有4个字节，彼此一一对应。
     * <p>例如：192.168.34.22 -&nbsp;&gt;&nbsp; 11000000.10101000.00100010.00010110 -&nbsp;&gt;&nbsp; 11000000101010000010001000010110 -&nbsp;&gt;&nbsp; -1062723050*/
    public int getHostIPValue() {
        return this.hostAddressData;
    }
    /**转换地址为URL形式*/
    public URI toURI() {
        return this.uriFormat;
    }
    /**返回RSF协议形式表述的主机地址。格式为：“rsf://127.0.0.1:8000/unit”*/
    public String toHostSchema() {
        return String.format("rsf://%s:%s/%s", this.hostAddress, this.hostPort, this.formUnit);
    }
    //
    /**
     * 两个 Address 可以比较是否相等
     * @param obj 另一个对象
     * @return 返回结果。
     */
    public boolean equals(Object obj) {
        String diffURI = "";
        if (obj instanceof InterAddress) {
            diffURI = ((InterAddress) obj).toHostSchema();
            return StringUtils.equalsBlankIgnoreCase(diffURI, this.toHostSchema());
        } else {
            return false;
        }
    }
    /**判断连接地址是否是同一个。判断依据是参数的{@link #getHostPort()}返回值和该对象的{@link #getHostPort()}返回值做比较。*/
    public boolean equalsHost(InterAddress evalResult) {
        return evalResult == null ? false : equalsHost(evalResult.getHostPort());
    }
    /**判断连接地址是否是同一个。判断依据是参数值和{@link #getHostPort()}返回值做比较。*/
    public boolean equalsHost(String evalResult) {
        return evalResult == null ? false : this.getHostPort().equals(evalResult);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((formUnit == null) ? 0 : formUnit.hashCode());
        result = prime * result + ((hostAddress == null) ? 0 : hostAddress.hashCode());
        result = prime * result + hostAddressData;
        result = prime * result + hostPort;
        result = prime * result + ((uriFormat == null) ? 0 : uriFormat.hashCode());
        return result;
    }
    //
    public String toString() {
        return toHostSchema();
    }
    protected URI createURL() throws URISyntaxException {
        return new URI(SECHMA, null, this.getHost(), this.getPort(), "/" + this.formUnit, null, null);
    }
    public static boolean checkFormat(URI rsfAddress) {
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
        if (logger.isDebugEnabled()) {
            logger.debug("'{}' rsfAddress format error.", rsfAddress);
        }
        return false;
    }
}