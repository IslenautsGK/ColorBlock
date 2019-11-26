package com.isgk.colorblock.util.expression;

import com.isgk.colorblock.core.ColorBlockCore;
import com.isgk.colorblock.util.commoninterface.IExecutable;
import com.isgk.colorblock.util.jsengine.ParticleScriptEngine;

public final class ExpressionUtil {

	public static IExecutable prase(String expression, boolean enableReturn) {
		if (expression == null || expression.equals("null")) {
			return null;
		}
		IExecutable exe;
		if (expression.startsWith("js:")) {
			exe = new ParticleScriptEngine(expression.substring(3));
		} else {
			if (enableReturn) {
				if(ColorBlockCore.jniParticleUpdate) {
					exe = new ParticleExpression(expression).jni();
				}else {
					exe = new ParticleExpression(expression);
				}
			} else {
				if(ColorBlockCore.jniParticleUpdate) {
					exe = new ParticleExpressions(expression).jni();
				}else {
					exe = new ParticleExpressions(expression);
				}
			}
		}
		return exe;
	}

}
