package com.chensoul.domain.order.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.chensoul.client.UserClient;
import com.chensoul.constant.RedisKeyConstant;
import com.chensoul.domain.order.entity.SeckillVoucher;
import com.chensoul.domain.order.entity.VoucherOrder;
import com.chensoul.domain.order.mapper.VoucherOrderMapper;
import com.chensoul.domain.user.entity.User;
import com.chensoul.infrastructure.lock.RedisLock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;

/**
 * 秒杀业务逻辑层
 */
@Service
@AllArgsConstructor
public class SeckillService {
	private VoucherOrderMapper voucherOrderMapper;
	private RedisTemplate redisTemplate;
	private DefaultRedisScript defaultRedisScript;
	private RedisLock redisLock;
	private RedissonClient redissonClient;
	private ObjectMapper objectMapper;
	private UserClient userClient;

	/**
	 * 抢购代金券
	 *
	 * @param voucherId 代金券 ID
	 * @Para path 访问路径
	 */
	@Transactional(rollbackFor = Exception.class)
	public void doSeckill(final Integer voucherId) throws JsonProcessingException {
		final String key = RedisKeyConstant.SECKILL_VOUCHER.getKey() + voucherId;
		final SeckillVoucher seckillVoucher = this.objectMapper.readValue((String) this.redisTemplate.opsForValue().get(key), SeckillVoucher.class);

		final LocalDateTime now = LocalDateTime.now();
		Assert.isTrue(now.isAfter(seckillVoucher.getStartTime()), "该抢购还未开始");
		Assert.isTrue(now.isBefore(seckillVoucher.getEndTime()), "该抢购已结束");
		Assert.isTrue(seckillVoucher.getAmount() < 1, "该券已经卖完了");

		final User userInfo = this.userClient.getCurrentUser().getData();
		final VoucherOrder order = this.voucherOrderMapper.findUserOrder(userInfo.getId(), seckillVoucher.getVoucherId());
		Assert.isTrue(order != null, "该用户已抢到该代金券，无需再抢");

		// 使用 Redis 锁一个账号只能购买一次
		final String lockName = RedisKeyConstant.LOCK_KEY.getKey() + userInfo.getId() + ":" + voucherId;
		final long expireTime = seckillVoucher.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
								- now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		// 自定义 Redis 分布式锁
		//String lockKey = redisLock.tryLock(lockName, expireTime);

		// Redisson 分布式锁
		final RLock lock = this.redissonClient.getLock(lockName);

		try {
			// Redisson 分布式锁处理
			final boolean isLocked = lock.tryLock(expireTime, TimeUnit.MILLISECONDS);
			if (isLocked) {
				// 下单
				final VoucherOrder voucherOrder = new VoucherOrder();
				voucherOrder.setUserId(userInfo.getId());
				// Redis 中不需要维护外键信息
				// voucherOrders.setseckillId(seckillVouchers.getId());
				voucherOrder.setVoucherId(seckillVoucher.getVoucherId());
				final String orderNo = IdWorker.getIdStr();
				voucherOrder.setOrderNo(orderNo);
				voucherOrder.setOrderType(1);
				voucherOrder.setStatus(0);
				final long count = this.voucherOrderMapper.save(voucherOrder);
				Assert.isTrue(count != 0, "用户抢购失败");

				final List<String> keys = new ArrayList<>();
				keys.add(key);
				keys.add("amount");
				final Long amount = (Long) this.redisTemplate.execute(this.defaultRedisScript, keys);
				Assert.isTrue(amount != null && amount >= 1, "该券已经卖完了");
			}
		} catch (final Exception e) {
			// 手动回滚事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			// Redisson 解锁
			lock.unlock();
		}
	}

	/**
	 * 添加需要抢购的代金券
	 *
	 * @param seckillVoucher
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addSeckillVouchers(final SeckillVoucher seckillVoucher) {
		// 非空校验
		Assert.isTrue(seckillVoucher.getVoucherId() != null, "请选择需要抢购的代金券");
		Assert.isTrue(seckillVoucher.getAmount() != 0, "请输入抢购总数量");
		final LocalDateTime now = LocalDateTime.now();
		Assert.isNull(seckillVoucher.getStartTime(), "请输入开始时间");
		// 生产环境下面一行代码需放行，这里注释方便测试
		// AssertUtil.isTrue(now.after(seckillVouchers.getStartTime()), "开始时间不能早于当前时间");
		Assert.isNull(seckillVoucher.getEndTime(), "请输入结束时间");
		Assert.isTrue(now.isBefore(seckillVoucher.getEndTime()), "结束时间不能早于当前时间");
		Assert.isTrue(seckillVoucher.getStartTime().isBefore(seckillVoucher.getEndTime()), "开始时间不能晚于结束时间");

		// 采用 Redis 实现
		final String key = RedisKeyConstant.SECKILL_VOUCHER.getKey() +
						   seckillVoucher.getVoucherId();
		// 验证 Redis 中是否已经存在该券的秒杀活动
		final Map<String, Object> map = this.redisTemplate.opsForHash().entries(key);
		Assert.isTrue(!map.isEmpty() && (int) map.get("amount") > 0, "该券已经拥有了抢购活动");

		seckillVoucher.setCreateTime(LocalDateTime.now());
		seckillVoucher.setUpdateTime(LocalDateTime.now());
        this.redisTemplate.opsForValue().set(key, seckillVoucher);
	}

}
