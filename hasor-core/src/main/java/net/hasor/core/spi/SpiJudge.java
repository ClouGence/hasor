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
package net.hasor.core.spi;
import java.util.EventListener;
import java.util.List;

/**
 * SPI 仲裁：当同一个 SPI bind 了多个 SpiListener 时，仲裁可以决定哪些 SPI 会被调用。
 * 带返回值的 SPI 调用在仲裁的帮助下可以决定，使用具体的哪个 SpiListener 返回值。
 * @version : 2020-04-19
 * @author 赵永春 (zyc@hasor.net)
 */
public interface SpiJudge {
    /** 默认裁决：1.所有 SPI 监听器，全部执行；2.结果只取最后一个。*/
    public final static SpiJudge DEFAULT = new SpiJudge() {
    };

    /** 调用仲裁 */
    public default <T extends EventListener> boolean judgeSpiCall(T spiListener) {
        return true;
    }

    /** 结果仲裁 */
    public default <R> R judgeSpiResult(List<R> result, R defaultResult) {
        return (result == null || result.isEmpty()) ? defaultResult : result.get(result.size() - 1);
    }
}