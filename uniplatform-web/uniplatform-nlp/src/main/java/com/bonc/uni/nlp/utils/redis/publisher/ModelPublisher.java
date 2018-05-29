package com.bonc.uni.nlp.utils.redis.publisher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.entity.model.ModelInfo;
import com.bonc.uni.nlp.utils.redis.RedisConstant;
import com.bonc.uni.nlp.utils.redis.RedisManager;
import com.bonc.usdp.odk.logmanager.LogManager;
import com.bonc.usdp.odk.redis.IRedisHandler;
import com.bonc.usdp.odk.redis.cluster.ClusterHandler;
import com.bonc.usdp.odk.redis.standalone.StandaloneHandler;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @author : GaoQiuyuer
 * @version: 2018年1月4日 下午7:35:02
 */
public class ModelPublisher {
	
	public void publishModel(ModelInfo modelInfo, boolean delete) {

		modelInfo.setIp(SystemConfig.ADMIN_SERVER_IP);
		modelInfo.setPort(SystemConfig.ADMIN_SERVER_SSH_PORT);
		modelInfo.setUsername(SystemConfig.ADMIN_SERVER_USERNAME);
		modelInfo.setPassword(SystemConfig.ADMIN_SERVER_PASSWORD);

		String message = JSON.toJSONString(modelInfo, SerializerFeature.DisableCircularReferenceDetect,
				SerializerFeature.WriteNullStringAsEmpty);
		
		
		LogManager.debug("Publish :" + message);

		IRedisHandler redisHandler = RedisManager.getInstance().getRedisHandler();
		
		if (redisHandler instanceof StandaloneHandler) {
			Jedis publishJedis = ((StandaloneHandler) redisHandler).getJedis();

			if (delete) {
				publishJedis.publish(RedisConstant.MODEL_DEL_REDIS_SUBSCRIBE_CHANNEL, message);
			} else {
				publishJedis.publish(RedisConstant.MODEL_UPLOAD_REDIS_SUBSCRIBE_CHANNEL, message);
			}
		} else {
			JedisCluster publishJedis = ((ClusterHandler) redisHandler).getJedis();

			if (delete) {
				publishJedis.publish(RedisConstant.MODEL_DEL_REDIS_SUBSCRIBE_CHANNEL, message);
			} else {
				publishJedis.publish(RedisConstant.MODEL_UPLOAD_REDIS_SUBSCRIBE_CHANNEL, message);
			}
		}

		if (redisHandler instanceof StandaloneHandler) {
			((StandaloneHandler) redisHandler).close();
		}
	}
}
