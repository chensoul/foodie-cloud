package com.chensoul.order.infrastructure.persistence;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * Custom MetaObject Handler
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
@Component
@Slf4j
public class CustomMetaObjecthandler implements MetaObjectHandler {
	protected static void fillValIfNullByName(final String fieldName, final Object fieldVal, final MetaObject metaObject,
											  final boolean isCover) {
		// 1. 没有 set 方法
		if (!metaObject.hasSetter(fieldName)) {
			return;
		}
		// 2. 如果用户有手动设置的值
		if (metaObject.getValue(fieldName) != null && !isCover) {
			return;
		}
		// 3. field 类型相同时设置
		final Class<?> getterType = metaObject.getGetterType(fieldName);
		if (ClassUtils.isAssignableValue(getterType, fieldVal)) {
			metaObject.setValue(fieldName, fieldVal);
		}
	}

	/**
	 * 插入操作，自动填充
	 *
	 * @param metaObject
	 */
	@Override
	public void insertFill(final MetaObject metaObject) {
		final LocalDateTime now = LocalDateTime.now();
		final String username = SecurityContextHolder.getContext().getAuthentication().getName();

		log.info("公共字段自动填充[insert]: {},{}", now, username);

		fillValIfNullByName("createTime", now, metaObject, false);
		fillValIfNullByName("createUser", username, metaObject, false);
	}

	/**
	 * 更新操作，自动填充
	 *
	 * @param metaObject
	 */
	@Override
	public void updateFill(final MetaObject metaObject) {
		final LocalDateTime now = LocalDateTime.now();
		final String username = SecurityContextHolder.getContext().getAuthentication().getName();

		log.info("公共字段自动填充[update]: {},{}", now, username);

		fillValIfNullByName("updateTime", now, metaObject, true);
		fillValIfNullByName("updateUser", username, metaObject, true);
	}
}
