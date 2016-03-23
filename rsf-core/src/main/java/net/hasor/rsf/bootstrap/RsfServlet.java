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
package net.hasor.rsf.bootstrap;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.hasor.core.Inject;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.rpc.caller.remote.RemoteRsfCaller;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.transform.codec.ProtocolUtils;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
/**
 * Rsf，Http方式部署。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfServlet extends HttpServlet {
    private static final long  serialVersionUID = -486309970684132898L;
    @Inject
    private AbstractRsfContext rsfContext       = null;
    //
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String length = req.getHeader("Content-Length");
        int dataSize = Integer.parseInt(length);
        if (dataSize <= 30) {
            resp.sendError(ProtocolStatus.ProtocolError, "rsf packet error.");
            return;
        }
        //
        String rsfURL = req.getParameter("rsf");
        String type = req.getParameter("type");
        req.getRemotePort();
        InterAddress target = null;
        try {
            target = new InterAddress(req.getRemoteHost(), req.getRemotePort(), "");
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //
        ServletInputStream inStream = req.getInputStream();
        ByteBuf dataBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        dataBuf.writeBytes(inStream, dataSize);
        byte rsfHead = dataBuf.getByte(0);
        RequestInfo rsfRequest = ProtocolUtils.buildRequestInfo(rsfHead, dataBuf);
        //
        RemoteRsfCaller caller = rsfContext.getRsfCaller();
        ResponseInfo rsfResponse = caller.doRequest(target, rsfRequest);
        //
        super.service(req, resp);
    }
}