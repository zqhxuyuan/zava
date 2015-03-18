package com.shansun.sparrow.base;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-14
 */
public class MoreToStringBuilder extends ReflectionToStringBuilder {

	/**
	 * Which field names to include from output. Intended for fields like
	 * <code>"id"</code>.
	 */
	private String[]	includeFieldNames;

	public String toStringInclude(Object object, String[] includeFieldNames) {
		return new MoreToStringBuilder(object).setIncludeFieldNames(includeFieldNames).toString();
	}

	public MoreToStringBuilder(Object object, ToStringStyle style, StringBuffer buffer) {
		super(object, style, buffer);
	}

	public MoreToStringBuilder(Object object, ToStringStyle style) {
		super(object, style);
	}

	public MoreToStringBuilder(Object object) {
		super(object);
	}

	public String[] getIncludeFieldNames() {
		return includeFieldNames;
	}

	public MoreToStringBuilder setIncludeFieldNames(String[] includeFieldNames) {
		this.includeFieldNames = includeFieldNames;
		return this;
	}

	@Override
	protected boolean accept(Field field) {
		if (field.getName().indexOf(ClassUtils.INNER_CLASS_SEPARATOR_CHAR) != -1) {
			// Reject field from inner class.
			return false;
		}
		if (Modifier.isTransient(field.getModifiers()) && !this.isAppendTransients()) {
			// Reject transient fields.
			return false;
		}
		if (Modifier.isStatic(field.getModifiers()) && !this.isAppendStatics()) {
			// Rject static fields.
			return false;
		}
		if (this.getIncludeFieldNames() != null && this.getIncludeFieldNames().length > 0) {
			if (Arrays.binarySearch(this.getIncludeFieldNames(), field.getName()) >= 0) {
				return true;
			} else {
				// Reject fields which not in the getIncludeFieldNames list.
				return false;
			}
		}
		if (this.getExcludeFieldNames() != null && Arrays.binarySearch(this.getExcludeFieldNames(), field.getName()) >= 0) {
			// Reject fields from the getExcludeFieldNames list.
			return false;
		}
		return true;
	}
}
