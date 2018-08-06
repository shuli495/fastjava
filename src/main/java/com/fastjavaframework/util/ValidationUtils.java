package com.fastjavaframework.util;

import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import com.fastjavaframework.exception.ThrowPrompt;

/**
 * 校验实体数据规范
 *
 * @author wangshuli
 */
public class ValidationUtils {

	private static final Validator VALIDATOR = Validation
			.buildDefaultValidatorFactory().getValidator();

	/**
	 * 校验obj对象
	 * @param bean
	 */
	public static void bean(Object obj) {
		validationType(obj, false);
	}

	/**
	 * 只校验实体属性是否为null
	 * @param bean
	 */
	public static void onlyNull(Object obj) {
		validationType(obj, true);
	}
	
	/**
	 * 判断数据类型
	 * @param obj 数据对象
	 * @param isOnlyChkNull 是否只校验null
	 */
	private static void validationType(Object obj, boolean isOnlyChkNull) {
		if(obj instanceof java.util.List) {
			for (Object subObj : (List)obj) {
				validation(subObj, isOnlyChkNull);
			}
		} else {
			validation(obj, isOnlyChkNull);
		}
	}

	/**
	 * 校验实体
	 * @param bean 实体对象
	 * @param isOnlyChkNull 是否只校验null
	 */
	private static void validation(Object bean, boolean isOnlyChkNull) {
		for (ConstraintViolation<Object> constraintViolation : VALIDATOR.validate(bean)) {
			// 只校验null
			if (isOnlyChkNull && !(constraintViolation.getConstraintDescriptor().getAnnotation() instanceof NotNull)) {
				continue;
			}

			throw new ThrowPrompt(constraintViolation.getMessage());
		}
	}
}
