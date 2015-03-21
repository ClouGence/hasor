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
package net.hasor.rsf;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.more.future.BasicFuture;
import org.more.future.FutureCallback;
/**
 * RSF Future
 * @version : 2014年11月14日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfFuture extends BasicFuture<RsfResponse> {
    private RsfRequest rsfRequest = null;
    public RsfFuture(RsfRequest rsfRequest) {
        this.rsfRequest = rsfRequest;
    }
    public RsfFuture(RsfRequest rsfRequest, FutureCallback<RsfResponse> listener) {
        super(listener);
        this.rsfRequest = rsfRequest;
    }
    /** @return 获取发起请求的Request*/
    public RsfRequest getRequest() {
        return this.rsfRequest;
    }
    /**
     * 获取响应的结果。
     * @return 获取响应的结果。
     * @throws InterruptedException wait方法可能引发的异常。
     * @throws ExecutionException 远程方法在调用过程中发生异常。
     */
    public Object getData() throws InterruptedException, ExecutionException {
        return this.get().getResponseData();
    }
    /**
     * 等待执行结果的返回。
     * @param timeout 超时时间
     * @param unit 超时时间单位
     * @return 返回执行结果。
     * @throws InterruptedException wait方法可能引发的异常。
     * @throws ExecutionException 远程方法在调用过程中发生异常。
     * @throws TimeoutException 超时时间到达
     */
    public Object getData(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.get(timeout, unit).getResponseData();
    }
}