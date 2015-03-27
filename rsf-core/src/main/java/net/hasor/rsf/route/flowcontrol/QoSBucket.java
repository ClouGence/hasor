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
package net.hasor.rsf.route.flowcontrol;
import java.util.concurrent.atomic.AtomicInteger;
import org.more.logger.LoggerHelper;
/**
 * 描述：线程安全的令牌桶限流器，时间窗刷新误差为毫秒级。 定义有效的限流器需要满足：
 * <ol>
 * <li>速率rate > 0，单位为次数/秒
 * <li>时间窗timeWindow >= 1，单位为毫秒
 * <li>峰值peak >= rate * timeWindow / 1000.0
 * </ol>
 */
public class QoSBucket {
    private static final int DEFAULT_RATE       = 50;
    private static final int DEFAULT_PEAK       = 100;
    private static final int DEFAULT_TIMEWINDOW = 1000;
    private int              rate;                     // 稳态中，每秒允许的调用次数
    private int              peak;                     // 突发调用峰值的上限，即令牌桶容量
    private int              timeWindow;               // 令牌桶刷新最小间隔，单位毫秒
    private AtomicInteger    tokens;                   // 当前可用令牌数量
    private volatile long    lastRefreshTime;          // 下一次刷新令牌桶的时间
    private volatile double  leftDouble;
    //
    //
    public QoSBucket() {
        this(DEFAULT_RATE, DEFAULT_PEAK, DEFAULT_TIMEWINDOW);
    }
    public QoSBucket(int rate, int peak, int timeWindow) {
        this.rate = rate;
        this.peak = peak;
        this.timeWindow = timeWindow;
        double initialToken = rate * timeWindow / 1000d;
        //初始的token为零不合理， 改为1。
        this.tokens = initialToken >= 1 ? new AtomicInteger((int) initialToken) : new AtomicInteger(1);
        //增加此保存值，是为了double转int时候的不精确；如果不累及这个误差，累计的接过会非常大。
        this.leftDouble = initialToken - Math.floor(initialToken);
        this.lastRefreshTime = System.currentTimeMillis();
    }
    /** 检查令牌前，首先更新令牌数量 */
    public boolean check() {
        long now = System.currentTimeMillis();
        if (now > lastRefreshTime + timeWindow) {// 尝试更新令牌数量
            int currentValue = tokens.get();
            double interval = (now - lastRefreshTime) / 1000d;
            double addedDouble = interval * rate;
            int added = (int) addedDouble; // 最大值为Integer.MAX_VALUE
            if (added > 0) {
                double addedPlusDouble = leftDouble + (addedDouble - added);
                int addPlus = (int) addedPlusDouble;
                added += addPlus;
                int newValue = currentValue + added;
                newValue = (newValue > currentValue && newValue < peak) ? newValue : peak;
                if (tokens.compareAndSet(currentValue, newValue)) {
                    lastRefreshTime = now;// 更新成功后，设置新的刷新时间
                    leftDouble = addedPlusDouble - addPlus;
                    LoggerHelper.logFinest("[QoSBucket] Updated done: [%s] -> [%s], refresh time: %s.", currentValue, newValue, now);
                }
            }
        }
        int value = tokens.get();// 尝试获得一个令牌
        boolean flag = false; // 是否获得到一个令牌
        while (value > 0 && !flag) {
            flag = tokens.compareAndSet(value, value - 1);
            value = tokens.get();
        }
        if (LoggerHelper.isEnableFinestLoggable()) {
            if (!flag) {
                LoggerHelper.logFinest("QoSBucket: get token failed, tokens[" + tokens.get() + "]");
            }
        }
        return flag;
    }
    @Override
    public String toString() {
        return "QoSBucket [tokens=" + tokens + ", rate=" + rate + ", peak=" + peak + ", timeWindow=" + timeWindow + "]";
    }
    /**
     * 限流器有效性验证。限流器的配置必须满足以下条件：
     * <ol>
     * <li>速率rate、峰值peak配置为大于0
     * <li>时间窗timeWindow不小于1
     * <li>峰值不小于速率与时间窗的乘积
     * </ol>
     * @return true/false
     */
    public boolean validate() {
        if (rate <= 0 || peak <= 0 || timeWindow < 1) {
            return false;
        }
        if (peak < (rate * timeWindow / 1000F)) {
            return false;
        }
        return true;
    }
}