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
package net.hasor.rsf.utils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
/**
 * 
 * @version : 2014年12月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class URLUtils {
    public static URL toURL(String hostIP, int hostPort) throws MalformedURLException {
        return new URL("rsf", hostIP, hostPort, "", new RsfURLStreamHandler());
    }
    public static URL toURL(String hostIP, int hostPort, String servicePath) throws MalformedURLException {
        return new URL("rsf", hostIP, hostPort, servicePath, new RsfURLStreamHandler());
    }
    public static URL toHostURL(URL rsfURL) throws MalformedURLException {
        if ("rsf".equals(rsfURL.getProtocol()) == false) {
            throw new MalformedURLException(rsfURL.getProtocol() + " Protocol error.");
        }
        return new URL("rsf", rsfURL.getHost(), rsfURL.getPort(), "", new RsfURLStreamHandler());
    }
    //
    private static class RsfURLStreamHandler extends URLStreamHandler {
        protected URLConnection openConnection(URL u) throws IOException {
            throw new UnsupportedOperationException("Method not implemented.");
        }
    }
}