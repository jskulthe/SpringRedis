package com.rediscachedemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	UserRepository userRepository;
	
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
	    return new JedisConnectionFactory();
	}
	 
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
	    RedisTemplate<String, Object> template = new RedisTemplate<>();
	    template.setConnectionFactory(jedisConnectionFactory());
	    return template;
	}	
/*
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
	    JedisConnectionFactory jedisConFactory
	      = new JedisConnectionFactory();
	    jedisConFactory.setHostName("localhost");
	    jedisConFactory.setPort(6379);
	    return jedisConFactory;
	}
	*/
	
	@Cacheable(value = "users", key = "#userId", unless = "#result.followers < 12000")
	public User getUser(String userId) {
		LOG.info("Getting user with ID {}.", userId);
		return userRepository.findOne(Long.valueOf(userId));
	}

	@CachePut(value = "users", key = "#user.id")
	public User save(User user) {
		userRepository.save(user);
		return user;
	}

	@CacheEvict(value = "users", allEntries = true)
	public void delete(Long userId) {
		LOG.info("deleting person with id {}", userId);
		userRepository.delete(userId);
	}

}
