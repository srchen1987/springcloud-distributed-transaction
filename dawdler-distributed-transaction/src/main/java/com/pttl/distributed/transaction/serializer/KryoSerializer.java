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

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.unsafe.UnsafeInput;
import com.esotericsoftware.kryo.unsafe.UnsafeOutput;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;

/**
 * 
 * @Title: KryoSerializer.java
 * @Description: kroy实现的序列化 目前升级到最新版本 4x
 * @author: jackson.song
 * @date: 2014年12月22日
 * @version V1.0
 * @email: suxuan696@gmail.com
 */
//@Component
public class KryoSerializer implements Serializer {
	private static final ThreadLocal<KryoLocal> kryos = new ThreadLocal<KryoLocal>() {
		protected KryoLocal initialValue() {
			KryoLocal kryoLocal = new KryoLocal();
			return kryoLocal;
		};
	};

	public static class KryoLocal {
		private Kryo kryo;

//		private Input input;
//		private Output out;
		public KryoLocal() {
			kryo = new Kryo();
			kryo.setReferences(true);
			kryo.setRegistrationRequired(false);
			kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
//			  ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
//              .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
//			input = new UnsafeInput();
//			out = new UnsafeOutput(2048,-1);
		}

		public Kryo getKryo() {
			kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
			return kryo;
		}

		public void setKryo(Kryo kryo) {
			this.kryo = kryo;
		}
//		public Input getInput() {
//			return input;
//		}
//		public void setInput(Input input) {
//			this.input = input;
//		}
//		public Output getOut() {
//			return out;
//		}
//		public void setOut(Output out) {
//			this.out = out;
//		}
	}

	@Override
	public Object deserialize(byte[] bytes) throws Exception {
		Kryo kryo = kryos.get().getKryo();
//		Input input = kryos.get().input;
		Input input = new UnsafeInput();
		input.setBuffer(bytes);
		Object obj;
		try {
			obj = kryo.readClassAndObject(input);
		} finally {
			input.close();
		}
		return obj;
	}

	@Override
	public byte[] serialize(Object object) throws Exception {
		Kryo kryo = kryos.get().kryo;
//		Output out = kryos.get().out;
		Output out = new UnsafeOutput(2048, -1);
		byte[] datas = null;
		try {
			kryo.writeClassAndObject(out, object);
			datas = out.toBytes();
		} finally {
			out.flush();
			out.close();
		}
		return datas;
	}
}
