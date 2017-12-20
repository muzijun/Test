package com.zy.phone.net;

public interface Integral {
	/**
	 * 查看
	 * @param retcode
	 * @param integral
	 */
	public void retCheckIntegral(String retcode, String integral);
	/**
	 * 扣除
	 * @param retcode
	 * @param integral
	 */
	public void retMinusIntegral(String retcode, String integral);
	/**
	 * 增加
	 * @param retcode
	 * @param integral
	 */
	public void retAddIntegral(String retcode, String integral);

}
