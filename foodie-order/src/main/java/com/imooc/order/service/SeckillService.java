package com.imooc.order.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.entity.Diner;
import com.imooc.commons.model.entity.SeckillVoucher;
import com.imooc.commons.model.entity.VoucherOrder;
import com.imooc.order.mapper.VoucherOrderMapper;
import com.imooc.order.model.RedisLock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * 秒杀业务逻辑层
 */
@Service
public class SeckillService {
	@Resource
	private VoucherOrderMapper voucherOrderMapper;
	@Value("${service.name.foodie-oauth-server}")
	private String oauthServerName;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private RedisTemplate redisTemplate;
	@Resource
	private DefaultRedisScript defaultRedisScript;
	@Resource
	private RedisLock redisLock;
	@Resource
	private RedissonClient redissonClient;

	@Resource
	private ObjectMapper objectMapper;

	/**
	 * 抢购代金券
	 *
	 * @param voucherId   代金券 ID
	 * @param accessToken 登录token
	 * @Para path 访问路径
	 */
	@Transactional(rollbackFor = Exception.class)
	public void doSeckill(final Integer voucherId, final String accessToken) throws JsonProcessingException {
		final String key = RedisKeyConstant.seckill_voucher.getKey() + voucherId;
		final SeckillVoucher seckillVoucher = this.objectMapper.readValue((String) this.redisTemplate.opsForValue().get(key), SeckillVoucher.class);

		final Date now = new Date();
		Assert.isTrue(now.after(seckillVoucher.getStartTime()), "该抢购还未开始");
		Assert.isTrue(now.before(seckillVoucher.getEndTime()), "该抢购已结束");
		Assert.isTrue(seckillVoucher.getAmount() < 1, "该券已经卖完了");

		final String url = this.oauthServerName + "user/me?access_token={accessToken}";
		final R resultInfo = this.restTemplate.getForObject(url, R.class, accessToken);
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new IllegalArgumentException("获取用户信息失败");
		}
		final Diner dinerInfo = null;
		final VoucherOrder order = this.voucherOrderMapper.findDinerOrder(dinerInfo.getId(), seckillVoucher.getVoucherId());
		Assert.isTrue(order != null, "该用户已抢到该代金券，无需再抢");

		// 使用 Redis 锁一个账号只能购买一次
		final String lockName = RedisKeyConstant.lock_key.getKey() + dinerInfo.getId() + ":" + voucherId;
		final long expireTime = seckillVoucher.getEndTime().getTime() - now.getTime();

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
				voucherOrder.setDinerId(dinerInfo.getId());
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
		final Date now = new Date();
		Assert.isNull(seckillVoucher.getStartTime(), "请输入开始时间");
		// 生产环境下面一行代码需放行，这里注释方便测试
		// AssertUtil.isTrue(now.after(seckillVouchers.getStartTime()), "开始时间不能早于当前时间");
		Assert.isNull(seckillVoucher.getEndTime(), "请输入结束时间");
		Assert.isTrue(now.before(seckillVoucher.getEndTime()), "结束时间不能早于当前时间");
		Assert.isTrue(seckillVoucher.getStartTime().before(seckillVoucher.getEndTime()), "开始时间不能晚于结束时间");

		// 采用 Redis 实现
		final String key = RedisKeyConstant.seckill_voucher.getKey() +
						   seckillVoucher.getVoucherId();
		// 验证 Redis 中是否已经存在该券的秒杀活动
		final Map<String, Object> map = this.redisTemplate.opsForHash().entries(key);
		Assert.isTrue(!map.isEmpty() && (int) map.get("amount") > 0, "该券已经拥有了抢购活动");

		seckillVoucher.setCreateTime(LocalDateTime.now());
		seckillVoucher.setUpdateTime(LocalDateTime.now());
		this.redisTemplate.opsForValue().set(key, seckillVoucher);
	}

}
