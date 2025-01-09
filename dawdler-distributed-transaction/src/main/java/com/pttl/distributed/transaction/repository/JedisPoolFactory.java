package com.pttl.distributed.transaction.repository;

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

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.pttl.distributed.transaction.util.PropertiesUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.util.Pool;

/**
 * @author jackson.song
 * @version V1.0
 * @Title JedisPoolFactory.java
 * @Description jedispool 改变之前的单个jedis的配置 支持多个
 * @date 2021年6月18日
 * @email suxuan696@gmail.com
 */
public final class JedisPoolFactory {

	public static final Map<String, Pool<Jedis>> POOLS = new ConcurrentHashMap<>();

	private static Pool<Jedis> createJedisPool(String fileName) throws Exception {
		Pool<Jedis> jedisPool = null;
		Properties ps = PropertiesUtil.loadAllProperties(fileName);
		String auth = ps.getProperty("auth");
		String userName = ps.getProperty("userName");
		String clientName = ps.getProperty("clientName");
		String sentinelUser = ps.getProperty("sentinelUser");
		String sentinelPassword = ps.getProperty("sentinelPassword");
		String sentinelClientName = ps.getProperty("sentinelClientName");
		int database = PropertiesUtil.getIfNullReturnDefaultValueInt("database", 0, ps);
		int maxIdle = PropertiesUtil.getIfNullReturnDefaultValueInt("max_idle", JedisPoolConfig.DEFAULT_MAX_IDLE, ps);
		long maxWait = PropertiesUtil.getIfNullReturnDefaultValueLong("max_wait",
				JedisPoolConfig.DEFAULT_MAX_WAIT_MILLIS, ps);
		int maxActive = PropertiesUtil.getIfNullReturnDefaultValueInt("max_active", JedisPoolConfig.DEFAULT_MAX_TOTAL,
				ps);
		int timeout = PropertiesUtil.getIfNullReturnDefaultValueInt("timeout", Protocol.DEFAULT_TIMEOUT, ps);
		Object testOnBorrowObj = ps.get("test_on_borrow");
		boolean testOnBorrow = JedisPoolConfig.DEFAULT_TEST_ON_BORROW;
		if (testOnBorrowObj != null) {
			testOnBorrow = Boolean.parseBoolean(testOnBorrowObj.toString());
		}

		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(maxActive);
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMaxWaitMillis(maxWait);
		poolConfig.setTestOnBorrow(testOnBorrow);
		String masterName = (String) ps.get("masterName");
		String sentinels = (String) ps.get("sentinels");
		if (masterName != null && sentinels != null) {
			String[] sentinelsArray = sentinels.split(",");
			Set<String> sentinelsSet = Arrays.stream(sentinelsArray).collect(Collectors.toSet());
			jedisPool = new JedisSentinelPool(masterName, sentinelsSet,
			poolConfig,
			timeout, Protocol.DEFAULT_TIMEOUT,0,
			userName,auth,database, clientName,
			timeout, Protocol.DEFAULT_TIMEOUT, sentinelUser,
			sentinelPassword, sentinelClientName);
		} else {
			String addr = ps.getProperty("addr");
			int port = PropertiesUtil.getIfNullReturnDefaultValueInt("port", 5672, ps);
			jedisPool = new JedisPool(poolConfig, addr, port, timeout, auth, database);
		}
		return jedisPool;

	}

	public static Pool<Jedis> getJedisPool(String fileName) throws Exception {
		Pool<Jedis> pool = POOLS.get(fileName);
		if (pool != null) {
			return pool;
		}
			
		if (pool == null) {
			synchronized (POOLS) {
				pool = POOLS.get(fileName);
				if (pool == null) {
					POOLS.put(fileName, createJedisPool(fileName));
				}
			}
		}
		return POOLS.get(fileName);
	}

}