package com.pttl.distributed.transaction.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.pttl.distributed.transaction.context.ApplicationContextHolder;
import com.pttl.distributed.transaction.context.DistributedTransactionContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.Pool;

/**
 * 
 * @ClassName: RedisRepository
 * @Description: 基于redis的实现
 * @author: srchen
 * @date: 2019年11月01日 下午10:19:32
 */
public class RedisRepository extends TransactionRepository implements InitializingBean {
	@Autowired
	ApplicationContextHolder applicationContextHolder;

	private Pool<Jedis> jedisPool = null;

	private static final String PREFIX = "gtid_";

	private static int expireTime = 259200;

	@Override
	public int create(DistributedTransactionContext transaction) throws Exception {
		byte[] datas = serializer.serialize(transaction);
		Map<byte[], byte[]> map = new HashMap<>();
		map.put(transaction.getBranchTxId().getBytes(), datas);
		return execute(jedisPool, new JedisExecutor<Integer>() {
			@Override
			public Integer execute(Jedis jedis) {
				byte[] globalKey = (PREFIX + transaction.getGlobalTxId()).getBytes();
				jedis.hmset(globalKey, map);
				jedis.expire(globalKey, expireTime);
				return 1;
			}
		});
	}

	@Override
	public int update(DistributedTransactionContext transaction) throws Exception {
		return execute(jedisPool, new JedisExecutor<Integer>() {
			@Override
			public Integer execute(Jedis jedis) throws Exception {
				byte[] datas = serializer.serialize(transaction);
				Map<byte[], byte[]> map = jedis.hgetAll(transaction.getGlobalTxId().getBytes());
				if (map != null) {
					map.put(transaction.getBranchTxId().getBytes(), datas);
					jedis.hmset((PREFIX + transaction.getGlobalTxId()).getBytes(), map);
					return 1;
				}
				return 0;
			}
		});
	}

	@Override
	public int deleteByBranchTxId(String globalTxId, String branchTxId) throws Exception {
		return execute(jedisPool, new JedisExecutor<Integer>() {
			@Override
			public Integer execute(Jedis jedis) {
				jedis.hdel((PREFIX + globalTxId), branchTxId);
				return 1;
			}
		});
	}

	@Override
	public int deleteByGlobalTxId(String globalTxId) throws Exception {
		return execute(jedisPool, new JedisExecutor<Integer>() {
			@Override
			public Integer execute(Jedis jedis) {
				jedis.del(globalTxId);
				return 1;
			}
		});
	}

	@Override
	public List<DistributedTransactionContext> findAllByGlobalTxId(String globalTxId) throws Exception {
		List list = new ArrayList();
		return execute(jedisPool, new JedisExecutor<List<DistributedTransactionContext>>() {
			@Override
			public List<DistributedTransactionContext> execute(Jedis jedis) throws Exception {
				Collection<byte[]> collection = jedis.hgetAll((PREFIX + globalTxId).getBytes()).values();
				for (byte[] bs : collection) {
					list.add(serializer.deserialize(bs));
				}
				return list;
			}
		});
	}

	@Override
	public int updateDataByGlobalTxId(String globalTxId, Map<String, Object> data) throws Exception {
		String status = (String) data.get("status");
		Map map = new HashMap();
		return execute(jedisPool, new JedisExecutor<Integer>() {
			@Override
			public Integer execute(Jedis jedis) throws Exception {
				Collection<byte[]> collection = jedis.hgetAll((PREFIX + globalTxId).getBytes()).values();
				for (byte[] bs : collection) {
					DistributedTransactionContext context = (DistributedTransactionContext) serializer.deserialize(bs);
					if (status != null) {
						context.setStatus(status);
					}
					bs = serializer.serialize(context);
					map.put(context.getBranchTxId().getBytes(), bs);
				}
				if (!map.isEmpty()) {
					String res = jedis.hmset((PREFIX + globalTxId).getBytes(), map);
				}
				return 1;
			}
		});
	}

	@Override
	public List<DistributedTransactionContext> findALLBySecondsLater(int seconds) throws Exception {
		List list = new ArrayList();
		return execute(jedisPool, new JedisExecutor<List<DistributedTransactionContext>>() {
			@Override
			public List<DistributedTransactionContext> execute(Jedis jedis) throws Exception {
				Set<byte[]> mkeys = jedis.keys((PREFIX + "*").getBytes());
				for (byte[] keys : mkeys) {
					Collection<byte[]> collection = jedis.hgetAll(keys).values();
					for (byte[] bs : collection) {
						DistributedTransactionContext dc = (DistributedTransactionContext) serializer.deserialize(bs);
						int now = (int) (System.currentTimeMillis() / 1000);
						if ((now - dc.getAddtime()) > seconds) {
							list.add(dc);
						}
					}
				}
				return list;
			}
		});
	}

	private <T> T execute(Pool<Jedis> jedisPool, JedisExecutor<T> executor) throws Exception {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return executor.execute(jedis);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static interface JedisExecutor<T> {
		public T execute(Jedis jedis) throws Exception;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String[] profiles = applicationContextHolder.getApplicationContext().getEnvironment().getActiveProfiles();
		String activeProfile = profiles.length > 0 ? profiles[0] : "";
		String fileName = "distributed-transaction-redis";
		fileName = fileName + (activeProfile != null ? "-" + activeProfile : "") + ".properties";
		jedisPool = JedisPoolFactory.getJedisPool(fileName);
	}
}
