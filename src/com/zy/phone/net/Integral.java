package com.zy.phone.net;

public interface Integral {
	/**
	 * �鿴
	 * @param retcode
	 * @param integral
	 */
	public void retCheckIntegral(String retcode, String integral);
	/**
	 * �۳�
	 * @param retcode
	 * @param integral
	 */
	public void retMinusIntegral(String retcode, String integral);
	/**
	 * ����
	 * @param retcode
	 * @param integral
	 */
	public void retAddIntegral(String retcode, String integral);

}
