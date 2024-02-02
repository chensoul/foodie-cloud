package com.chensoul.infrastructure.lock;

import java.util.Collections;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Getter
@Setter
public class RedisLock {

	private final RedisTemplate redisTemplate;
	private final DefaultRedisScript<Long> lockScript;
	private final DefaultRedisScript<Object> unlockScript;

	public RedisLock(final RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
        this.lockScript = new DefaultRedisScript<>();
        this.lockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lock.lua")));
        this.lockScript.setResultType(Long.class);
        this.unlockScript = new DefaultRedisScript<>();
        this.unlockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("unlock.lua")));
	}

	/**
	 * 获取锁
	 *
	 * @param lockName    锁名称
	 * @param releaseTime 超时时间(单位:秒)
	 * @return key 解锁标识
	 */
	public String tryLock(final String lockName, final long releaseTime) {
		// 存入的线程信息的前缀，防止与其它JVM中线程信息冲突
		final String key = UUID.randomUUID().toString();

		// 执行脚本
		final Long result = (Long) this.redisTemplate.execute(
                this.lockScript,
			Collections.singletonList(lockName),
			key + Thread.currentThread().getId(), releaseTime);

		// 判断结果
		if (result != null && result.intValue() == 1) return key;
        else return null;
	}

	/**
	 * 释放锁
	 *
	 * @param lockName 锁名称
	 * @param key      解锁标识
	 */
	public void unlock(final String lockName, final String key) {
		// 执行脚本
        this.redisTemplate.execute(
                this.unlockScript,
			Collections.singletonList(lockName),
			key + Thread.currentThread().getId(), null);
	}
}
