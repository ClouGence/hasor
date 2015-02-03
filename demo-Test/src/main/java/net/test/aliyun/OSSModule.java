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
package net.test.aliyun;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import com.aliyun.oss.OSSClient;
/**
 * 阿里云OSS配置
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class OSSModule implements Module {
	@Override
	public void loadModule(ApiBinder apiBinder) throws Throwable {
		String accessKeyId = "nodwX1vbYpYLyJBP";
		String accessKeySecret = "HpO1p9zLHHAGAinulrEuys80dBAW2X";
		String endpoint = "http://oss-cn-beijing.aliyuncs.com";
		// 初始化一个OSSClient
		final OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		apiBinder.bindType(OSSClient.class).toInstance(client);
	}
}