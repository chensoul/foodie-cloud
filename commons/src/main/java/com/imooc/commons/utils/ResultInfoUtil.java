package com.imooc.commons.utils;


import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.model.domain.ResultInfo;

/**
 * 公共返回对象工具类
 */
public class ResultInfoUtil {

	/**
	 * 请求出错返回
	 *
	 * @param <T>
	 * @return
	 */
	public static <T> ResultInfo<T> buildError() {
		final ResultInfo<T> resultInfo = build(ApiConstant.ERROR_CODE,
			ApiConstant.ERROR_MESSAGE, null);
		return resultInfo;
	}

	/**
	 * 请求出错返回
	 *
	 * @param errorCode 错误代码
	 * @param message   错误提示信息
	 * @param <T>
	 * @return
	 */
	public static <T> ResultInfo<T> buildError(final int errorCode, final String message) {
		final ResultInfo<T> resultInfo = build(errorCode, message, null);
		return resultInfo;
	}

	/**
	 * 请求成功返回
	 *
	 * @param <T>
	 * @return
	 */
	public static <T> ResultInfo<T> buildSuccess() {
		final ResultInfo<T> resultInfo = build(ApiConstant.SUCCESS_CODE,
			ApiConstant.SUCCESS_MESSAGE, null);
		return resultInfo;
	}

	/**
	 * 请求成功返回
	 *
	 * @param data 返回数据对象
	 * @param <T>
	 * @return
	 */
	public static <T> ResultInfo<T> buildSuccess(final T data) {
		final ResultInfo<T> resultInfo = build(ApiConstant.SUCCESS_CODE,
			ApiConstant.SUCCESS_MESSAGE, data);
		return resultInfo;
	}

	/**
	 * 构建返回对象方法
	 *
	 * @param code
	 * @param message
	 * @param data
	 * @param <T>
	 * @return
	 */
	public static <T> ResultInfo<T> build(Integer code, String message, final T data) {
		if (code == null) code = ApiConstant.SUCCESS_CODE;
		if (message == null) message = ApiConstant.SUCCESS_MESSAGE;
		final ResultInfo resultInfo = new ResultInfo();
		resultInfo.setCode(code);
		resultInfo.setMessage(message);
		resultInfo.setData(data);
		return resultInfo;
	}

}
