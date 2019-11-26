package com.isgk.colorblock.util.expression;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import com.isgk.colorblock.util.client.component.ClientMessageUtil;
import com.isgk.colorblock.util.commoninterface.IExecutable;
import com.isgk.colorblock.util.matrix.Matrix;

public class ParticleExpression implements IExecutable {

	private static final Map<String, ConditionExpression> conditionExpressionBuf = Maps.newHashMap();

	IExpression<?, Matrix> expression;
	Map<String, Matrix> args;

	public ParticleExpression(String expression) {
		ConditionExpression ce;
		if (conditionExpressionBuf.containsKey(expression)) {
			ce = conditionExpressionBuf.get(expression);
		} else {
			ce = new ConditionExpression(expression);
			conditionExpressionBuf.put(expression, ce);
		}
		this.expression = ce;
		this.args = new HashMap<>();
	}

	public ParticleExpression(IExpression<?, Matrix> expression) {
		this.expression = expression;
		this.args = new HashMap<>();
	}

	@Override
	public void put(String key, Object value) {
		if (value instanceof Number) {
			args.put(key, new Matrix(((Number) value).doubleValue()));
		} else if (value instanceof Matrix) {
			args.put(key, (Matrix) value);
		} else {
			ClientMessageUtil.addChatMessage("Put Args Error:" + value.toString());
			throw new RuntimeException();
		}
	}

	@Override
	public Object get(String key) {
		Object obj = args.get(key);
		if (obj instanceof Matrix) {
			return ((Matrix) obj).getNumber();
		} else if (obj instanceof Number) {
			return ((Number) obj).doubleValue();
		}
		ClientMessageUtil.addChatMessage("Get Args Error:" + obj.toString());
		throw new RuntimeException();
	}

	@Override
	public Object invoke() {
		return expression.invoke(args);
	}

	public JNI jni() {
		return new JNI(this);
	}

	public class JNI implements IExecutable {

		public JNI(ParticleExpression pe) {
			init(pe);
		}

		private native void init(ParticleExpression pe);

		@Override
		public native void put(String key, Object value);

		@Override
		public native Object get(String key);

		@Override
		public native Object invoke();

		@Override
		protected void finalize() throws Throwable {
			super.finalize();
			destroy();
		}

		private native void destroy();

	}

}
