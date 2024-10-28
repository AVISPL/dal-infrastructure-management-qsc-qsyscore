/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.lang.reflect.Method;

/**
 * EnumTypeHandler class provides functions using for any class
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 7/4/2023
 * @since 1.0.0
 */
public class EnumTypeHandler {

	/**
	 * Get metric name of enum by name
	 *
	 * @param enumType the enumType is enum class
	 * @param propertyName is String
	 * @return T is metric instance
	 */
	public static <T extends Enum<T>> T getMetricByPropertyName(Class<T> enumType, String propertyName) {
		T enumTypeValue = null;
		try {
			for (T c : enumType.getEnumConstants()) {
				Method methodName = c.getClass().getMethod("getProperty");
				String nameMetric = String.valueOf(methodName.invoke(c));
				String[] splitProperty = nameMetric.split("%s");
				if (splitProperty.length < 2 && nameMetric.equalsIgnoreCase(propertyName)) {
					enumTypeValue = c;
					break;
				}
				try {
					Integer.parseInt(propertyName.replace(splitProperty[0], QSYSCoreConstant.EMPTY).replace(splitProperty[1], QSYSCoreConstant.EMPTY));
					enumTypeValue = c;
					break;
				} catch (Exception e) {
					continue;
				}
			}
			return enumTypeValue;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get metric name of enum by name
	 *
	 * @param enumType the enumType is enum class
	 * @param name is String
	 * @return T is metric instance
	 */
	public static <T extends Enum<T>> T getMetricByName(Class<T> enumType, String name) {
		try {
			for (T c : enumType.getEnumConstants()) {
				Method methodName = c.getClass().getMethod("getProperty");
				String nameMetric = String.valueOf(methodName.invoke(c));
				if (nameMetric.equalsIgnoreCase(name)) {
					return c;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	/**
	 * Get metric name of enum by name
	 *
	 * @param enumType the enumType is enum class
	 * @param name is String
	 * @return T is metric instance
	 */
	public static <T extends Enum<T>> T getPropertiesByName(Class<T> enumType, String name) {
		try {
			for (T c : enumType.getEnumConstants()) {
				Method methodName = c.getClass().getMethod("getMetric");
				String nameMetric = String.valueOf(methodName.invoke(c));
				if (nameMetric.equalsIgnoreCase(name)) {
					return c;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}