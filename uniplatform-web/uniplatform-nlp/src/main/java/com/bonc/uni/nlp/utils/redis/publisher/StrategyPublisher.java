package com.bonc.uni.nlp.utils.redis.publisher;

import com.bonc.uni.nlp.utils.redis.RedisConstant;
import com.bonc.uni.nlp.utils.redis.RedisManager;
import com.bonc.usdp.odk.redis.IRedisHandler;
import com.bonc.usdp.odk.redis.cluster.ClusterHandler;
import com.bonc.usdp.odk.redis.standalone.StandaloneHandler;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class StrategyPublisher {

	public void publishStrategyDelMsg(String strategyName) {
		
		IRedisHandler redisHandler = RedisManager.getInstance().getRedisHandler();
		
		if (redisHandler instanceof StandaloneHandler) {
			Jedis publishJedis = ((StandaloneHandler) redisHandler).getJedis();
			
			publishJedis.publish(RedisConstant.STRATEGY_DEL_REDIS_SUBSCRIBE_CHANNEL, strategyName);
		} else {
			JedisCluster publishJedis = ((ClusterHandler) redisHandler).getJedis();
			
			publishJedis.publish(RedisConstant.STRATEGY_DEL_REDIS_SUBSCRIBE_CHANNEL, strategyName);
		}
		
		if(redisHandler instanceof StandaloneHandler) {
			((StandaloneHandler) redisHandler).close();
		}
	}
	
}
