/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pttl.distributed.transaction.serializer;

import org.springframework.stereotype.Component;

import com.pttl.distributed.transaction.context.DistributedTransactionContext;
import com.pttl.distributed.transaction.util.JsonUtils;

/**
 * 
 * @Title: JsonSerializer.java
 * @Description: json实现的序列化
 * @author: jackson.song
 * @date: 2021年10月26日
 * @version V1.0
 * @email: suxuan696@gmail.com
 */
@Component
public class JsonSerializer implements Serializer {
	@Override
	public Object deserialize(byte[] bytes) throws Exception {
		return JsonUtils.jsonToClass(bytes, DistributedTransactionContext.class);
	}

	@Override
	public byte[] serialize(Object object) throws Exception {
		return JsonUtils.objectToJsonByte(object);
	}
}
