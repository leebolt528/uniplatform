package com.bonc.uni.nlp.utils.redis.publisher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.utils.database.dictionary.DicDBOperator;
import com.bonc.uni.nlp.utils.redis.RedisConstant;
import com.bonc.uni.nlp.utils.redis.RedisManager;
import com.bonc.uni.nlp.utils.redis.entity.RedisPublishDic;
import com.bonc.usdp.odk.logmanager.LogManager;
import com.bonc.usdp.odk.redis.IRedisHandler;
import com.bonc.usdp.odk.redis.cluster.ClusterHandler;
import com.bonc.usdp.odk.redis.standalone.StandaloneHandler;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
/**
 * @ClassName: DicPublisher
 * @Package: com.bonc.uni.nlp.utils.redis.publisher
 * @Description: 词库发布类
 * @author: Chris
 * @date: 2017年12月28日 下午8:06:28
 */
public class DicPublisher {
	/**
	 * <p>Title: publishDicStart</p>
	 * <p>Description: 发布词库启用/停用</p>
	 * @param dicId 词库id
	 * @param functionId 功能id
	 * @param start 启用词库
	 */
	public void publishDic(String dicId, String functionId, boolean start) {
		RedisPublishDic dic = DicDBOperator.getDicInfoById(dicId);
		
		LogManager.debug("发布的词信息：" + dic);
		
		if(null == dic) {
			return;
		}
		dic.setDicFunction(DicDBOperator.getFunctionNameById(functionId));
		
		String message = JSON.toJSONString(dic,
				SerializerFeature.DisableCircularReferenceDetect,
				SerializerFeature.WriteNullStringAsEmpty);
		
		LogManager.debug("Publish :" + message);
		
		IRedisHandler redisHandler = RedisManager.getInstance().getRedisHandler();
		
		if (redisHandler instanceof StandaloneHandler) {
			Jedis publishJedis = ((StandaloneHandler) redisHandler).getJedis();
			
			if(start) {
				publishJedis.publish(RedisConstant.DIC_START_REDIS_SUBSCRIBE_CHANNEL, message);
			} else {
				publishJedis.publish(RedisConstant.DIC_STOP_REDIS_SUBSCRIBE_CHANNEL, message);
			}
		} else {
			JedisCluster publishJedis = ((ClusterHandler) redisHandler).getJedis();
			
			if(start) {
				publishJedis.publish(RedisConstant.DIC_START_REDIS_SUBSCRIBE_CHANNEL, message);
			} else {
				publishJedis.publish(RedisConstant.DIC_STOP_REDIS_SUBSCRIBE_CHANNEL, message);
			}
		}
		
		if(redisHandler instanceof StandaloneHandler) {
			((StandaloneHandler) redisHandler).close();
		}
	}
	
}
