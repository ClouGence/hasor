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
package net.hasor.rsf.runtime.client.netty;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.protocol.message.ResponseMsg;
import net.hasor.rsf.runtime.context.RsfContext;
/**
 * 调用服务的Handler（只处理ResponseMsg）
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private RsfContext manager = null;
    public ClientHandler(RsfContext manager) {
        this.manager = manager;
    }
    //
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ResponseMsg == false)
            return;
        //
    }
    //
    //
    //
    //
    //
    //
    //
    //
    //    private Map<ProtocolStatus, ResponseProcess> processMapping = new HashMap<ProtocolStatus, ResponseProcess>();
    //    private static interface ResponseProcess {
    //        public void exec(ResponseMsg response, ServerRsfContext manager);
    //    }
    //    /**协议错误。*/
    //    private static class ProtocolError_Process implements ResponseProcess {}
    //    /**调用服务执行出错，通常是遭到异常抛出。*/
    //    private static class InternalServerError_Process implements ResponseProcess {}
    //    /**序列化异常。*/
    //    private static class SerializeError_Process implements ResponseProcess {}
    //    /**调用服务超时*/
    //    private static class RequestTimeout_Process implements ResponseProcess {}
    //    /**找不到服务*/
    //    private static class NotFound_Process implements ResponseProcess {}
    //    /**服务资源不可用。*/
    //    private static class Forbidden_Process implements ResponseProcess {}
    //    /**未定义*/
    //    private static class Unknown_Process implements ResponseProcess {}
    //    //
    //    //
    //    /**服务器要求客户端选择其它服务提供者处理该请求。*/
    //    private static class ChooseOther_Process implements ResponseProcess {}
    //    /**服务重定向。*/
    //    private static class MovedPermanently_Process implements ResponseProcess {}
    //    /**正确调用了服务方法，但服务器有意丢弃了响应信息。*/
    //    private static class Ignore_Process implements ResponseProcess {}
    //    /**在请求响应中间传递的消息。*/
    //    private static class Message_Process implements ResponseProcess {}
    //    /**已经接受请求处理正在进行中。*/
    //    private static class Accepted_Process implements ResponseProcess {}
    //    /**内容正确返回。*/
    //    private static class OK_Process implements ResponseProcess {}
    //    /**试图调用受保护的服务。*/
    //    private static class Unauthorized_Process implements ResponseProcess {}
    //
    //
    //
    //
    //
    public void tt() {
        manager.getCallExecute("aa").execute(new Runnable() {
            public void run() {
                while (true) {
                    aa();
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {}
                }
            }
        });
    }
    private long sendCount        = 0;
    private long acceptedCount    = 0;
    private long chooseOtherCount = 0;
    private long serializeError   = 0;
    private long requestTimeout   = 0;
    private long okCount          = 0;
    private long start            = System.currentTimeMillis();
    public void aa() {
        long duration = System.currentTimeMillis() - start;
        System.out.println("send QPS  :" + (sendCount * 1000 / duration));
        System.out.println("accept QPS:" + ((acceptedCount - chooseOtherCount) * 1000 / duration));
        System.out.println("send      :" + sendCount);
        System.out.println("accept    :" + (acceptedCount - chooseOtherCount));
        System.out.println("choose    :" + chooseOtherCount);
        System.out.println("serialize :" + serializeError);
        System.out.println("timeout   :" + requestTimeout);
        System.out.println("ok(%)     :" + okCount);
        System.out.println();
    }
    //
    public void atMsg(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResponseMsg response = (ResponseMsg) msg;
        //
        if (response.getStatus() == ProtocolStatus.Accepted)
            acceptedCount++;
        else if (response.getStatus() == ProtocolStatus.ChooseOther)
            chooseOtherCount++;
        else if (response.getStatus() == ProtocolStatus.OK)
            okCount++;
        else if (response.getStatus() == ProtocolStatus.SerializeError)
            serializeError++;
        else if (response.getStatus() == ProtocolStatus.RequestTimeout)
            requestTimeout++;
        else {
            int a = 0;
            a++;
        }
    }
}