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
package net.hasor.rsf;
import net.hasor.rsf.utils.NetworkUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 服务地址例：“rsf://127.0.0.1:8000/unit”
 * @version : 2014年9月12日
 * @author 赵永春 (zyc@hasor.net)
 */
public class InterAddress {
    protected static    Logger logger         = LoggerFactory.getLogger(InterAddress.class);
    public static final String DEFAULT_SECHMA = "rsf";
    private final       String sechma;                                              //协议
    private final       String formUnit;                                            //所属单元
    private final       String hostAddress;                                         //地址
    private final       int    hostPort;                                            //端口
    private final       String hostSchema;

    public InterAddress(String newAddressURL) throws URISyntaxException {
        this(new URI(newAddressURL));
    }

    public InterAddress(URI newAddressURL) {
        if (!checkFormat(newAddressURL)) {
            throw new IllegalStateException(newAddressURL + " format error.");
        }
        String formPath = newAddressURL.getPath();
        if (formPath.startsWith("/")) {
            formPath = formPath.substring(1);
        }
        this.sechma = newAddressURL.getScheme().toLowerCase();
        this.formUnit = formPath.split("/")[0];
        this.hostAddress = newAddressURL.getHost();//
        this.hostPort = newAddressURL.getPort();
        this.hostSchema = String.format("%s://%s:%s/%s", this.sechma, this.hostAddress, this.hostPort, this.formUnit);
    }

    public InterAddress(String hostAddress, int hostPort, String formUnit) {
        this(DEFAULT_SECHMA, hostAddress, hostPort, formUnit);
    }

    public InterAddress(String sechma, String hostAddress, int hostPort, String formUnit) {
        this.sechma = Objects.requireNonNull(sechma, "sechma is null.").toLowerCase();
        this.formUnit = Objects.requireNonNull(formUnit, "formUnit is null.");
        this.hostAddress = Objects.requireNonNull(hostAddress, "hostAddress is null.");
        this.hostPort = hostPort;
        this.hostSchema = String.format("%s://%s:%s/%s", this.sechma, this.hostAddress, this.hostPort, this.formUnit);
    }

    /** 返回协议头 */
    public String getSechma() {
        return this.sechma;
    }

    /** 返回地址所属单元 */
    public String getFormUnit() {
        return this.formUnit;
    }

    /** 返回目标IP地址 */
    public String getHost() {
        if ("local".equalsIgnoreCase(this.hostAddress)) {
            List<String> localIpAddr = NetworkUtils.localIpAddr();
            if (localIpAddr.isEmpty()) {
                try {
                    return InetAddress.getLocalHost().getHostName();
                } catch (Exception e) {
                    return "localhost";
                }
            } else {
                return localIpAddr.get(0);
            }
        }
        return this.hostAddress;
    }

    /** 返回IPv4地址 */
    public String getIp() throws UnknownHostException {
        return InetAddress.getByName(getHost()).getHostAddress();
    }

    /** 返回目标地址的端口号 */
    public int getPort() {
        return this.hostPort;
    }

    /** 返回IP地址和端口，格式为：192.168.25.33:8000 */
    public String getHostPort() {
        return this.getHost() + ":" + this.getPort();
    }

    /** 返回IP地址和端口，格式为：192.168.25.33:8000 */
    public String getIpPort() throws UnknownHostException {
        return getIp() + ":" + this.getPort();
    }

    /** 转换地址为URL形式 */
    public URI toURI() throws URISyntaxException {
        return new URI(this.getSechma(), null, this.getHost(), this.getPort(), "/" + this.formUnit, null, null);
    }

    /** 返回RSF协议形式表述的主机地址。格式为：“rsf://127.0.0.1:8000/unit” */
    public String toHostSchema() {
        return this.hostSchema;
    }

    /** 转换成{@link SocketAddress}类型对象 */
    public InetSocketAddress toSocketAddress() throws UnknownHostException {
        return new InetSocketAddress(getIp(), getPort());
    }

    /**
     * 两个 Address 可以比较是否相等
     * @param obj 另一个对象
     * @return 返回结果。
     */
    public boolean equals(Object obj) {
        String diffURI = "";
        if (obj instanceof InterAddress) {
            diffURI = ((InterAddress) obj).toHostSchema();
            return diffURI.equalsIgnoreCase(this.toHostSchema());
        } else {
            return false;
        }
    }

    /** 判断连接地址是否是同一个。判断依据是参数的{@link #getHostPort()}返回值和该对象的{@link #getHostPort()}返回值做比较 */
    public boolean equalsHost(InterAddress evalResult) throws UnknownHostException {
        return evalResult != null && equalsHost(evalResult.getIpPort());
    }

    /** 判断连接地址是否是同一个。判断依据是参数值和{@link #getHostPort()}返回值做比较 */
    public boolean equalsHost(String evalResult) throws UnknownHostException {
        return evalResult != null && this.getIpPort().equals(evalResult);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((formUnit == null) ? 0 : formUnit.hashCode());
        result = prime * result + ((formUnit == null) ? 0 : formUnit.hashCode());
        result = prime * result + ((hostAddress == null) ? 0 : hostAddress.hashCode());
        result = prime * result + this.hostAddress.hashCode();
        result = prime * result + hostPort;
        return result;
    }

    public String toString() {
        return toHostSchema();
    }

    public static boolean checkFormat(URI serviceURL) {
        if (serviceURL == null) {
            return false;
        }
        //        if (StringUtils.equalsBlankIgnoreCase(SECHMA, serviceURL.getScheme())) {
        if (!StringUtils.isBlank(serviceURL.getHost())) {
            if (serviceURL.getPort() != 0) {
                if (StringUtils.isBlank(serviceURL.getPath())) {
                    return false;
                }
                String REG = "[A-Za-z0-9_\\.]+";
                Matcher mat = Pattern.compile("/(" + REG + ")").matcher(serviceURL.getPath());
                mat.find();
                String formUnit = mat.group(1);
                if (!StringUtils.isBlank(formUnit)) {
                    return Pattern.matches(REG, formUnit);
                }
            }
        }
        //        }
        if (logger.isDebugEnabled()) {
            logger.debug("'{}' rsfAddress format error.", serviceURL);
        }
        return false;
    }
}