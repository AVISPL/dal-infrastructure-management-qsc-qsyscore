package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;

/**
 * LoginInfo
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/14/2023
 * @since 1.0.0
 */
public class LoginInfo {

	private static LoginInfo loginInfo;
	private long loginDateTime = 0;
	private String token;

	public static LoginInfo createLoginInfoInstance() {
		if (loginInfo == null) {
			loginInfo = new LoginInfo();
		}
		return loginInfo;
	}

	/**
	 * Retrieves {@code {@link #loginDateTime}}
	 *
	 * @return value of {@link #loginDateTime}
	 */
	public long getLoginDateTime() {
		return loginDateTime;
	}

	/**
	 * Sets {@code loginDateTime}
	 *
	 * @param loginDateTime the {@code long} field
	 */
	public void setLoginDateTime(long loginDateTime) {
		this.loginDateTime = loginDateTime;
	}

	/**
	 * Retrieves {@code {@link #token}}
	 *
	 * @return value of {@link #token}
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets {@code token}
	 *
	 * @param token the {@code java.lang.String} field
	 */
	public void setToken(String token) {
		this.token = token;
	}


	/**
	 * Check token expiry time
	 * Token is timeout when elapsed > 55 min
	 *
	 * @return boolean
	 */
	public boolean isTimeout() {
		long elapsed = (System.currentTimeMillis() - loginDateTime) / 60000;
		return elapsed > QSYSCoreConstant.TIMEOUT;
	}

}