package com.imooc.commons.utils;

import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.exception.ParameterException;
import org.apache.commons.lang3.StringUtils;

/**
 * 断言工具类
 */
public class AssertUtil {

<<<<<<< HEAD
	/**
	 * 必须登录
	 *
	 * @param accessToken
	 */
	public static void mustLogin(final String accessToken) {
		if (StringUtils.isBlank(accessToken)) {
			throw new ParameterException(ApiConstant.NO_LOGIN_CODE, ApiConstant.NO_LOGIN_MESSAGE);
		}
	}
||||||| 4176292
    /**
     * 判断字符串非空
     *
     * @param str
     * @param message
     */
    public static void isNotEmpty(String str, String... message) {
        if (StrUtil.isBlank(str)) {
            execute(message);
        }
    }
=======
	/**
	 * 判断字符串非空
	 *
	 * @param str
	 * @param message
	 */
	public static void isNotEmpty(final String str, final String... message) {
		if (StringUtils.isBlank(str)) {
			execute(message);
		}
	}
>>>>>>> feature/04-seckill

<<<<<<< HEAD
	/**
	 * 判断字符串非空
	 *
	 * @param str
	 * @param message
	 */
	public static void isNotEmpty(final String str, final String... message) {
		if (StringUtils.isBlank(str)) {
			execute(message);
		}
	}
||||||| 4176292
    /**
     * 判断对象非空
     *
     * @param obj
     * @param message
     */
    public static void isNotNull(Object obj, String... message) {
        if (obj == null) {
            execute(message);
        }
    }
=======
	/**
	 * 判断对象非空
	 *
	 * @param obj
	 * @param message
	 */
	public static void isNotNull(final Object obj, final String... message) {
		if (obj == null) {
			execute(message);
		}
	}
>>>>>>> feature/04-seckill

<<<<<<< HEAD
	/**
	 * 判断对象非空
	 *
	 * @param obj
	 * @param message
	 */
	public static void isNotNull(final Object obj, final String... message) {
		if (obj == null) {
			execute(message);
		}
	}
||||||| 4176292
    /**
     * 判断结果是否为真
     *
     * @param isTrue
     * @param message
     */
    public static void isTrue(boolean isTrue, String... message) {
        if (isTrue) {
            execute(message);
        }
    }
=======
	/**
	 * 判断结果是否为真
	 *
	 * @param isTrue
	 * @param message
	 */
	public static void isTrue(final boolean isTrue, final String... message) {
		if (isTrue) {
			execute(message);
		}
	}
>>>>>>> feature/04-seckill

<<<<<<< HEAD
	/**
	 * 判断结果是否为真
	 *
	 * @param isTrue
	 * @param message
	 */
	public static void isTrue(final boolean isTrue, final String... message) {
		if (isTrue) {
			execute(message);
		}
	}
||||||| 4176292
    /**
     * 最终执行方法
     *
     * @param message
     */
    private static void execute(String... message) {
        String msg = ApiConstant.ERROR_MESSAGE;
        if (message != null && message.length > 0) {
            msg = message[0];
        }
        throw new ParameterException(msg);
    }
=======
	/**
	 * 最终执行方法
	 *
	 * @param message
	 */
	private static void execute(final String... message) {
		String msg = ApiConstant.ERROR_MESSAGE;
		if (message != null && message.length > 0) {
			msg = message[0];
		}
		throw new ParameterException(msg);
	}
>>>>>>> feature/04-seckill

<<<<<<< HEAD
	/**
	 * 最终执行方法
	 *
	 * @param message
	 */
	private static void execute(final String... message) {
		String msg = ApiConstant.ERROR_MESSAGE;
		if (message != null && message.length > 0) {
			msg = message[0];
		}
		throw new ParameterException(msg);
	}

}
||||||| 4176292
}
=======
}
>>>>>>> feature/04-seckill
