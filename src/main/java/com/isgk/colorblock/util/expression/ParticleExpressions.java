package com.isgk.colorblock.util.expression;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import com.isgk.colorblock.util.client.component.ClientMessageUtil;
import com.isgk.colorblock.util.commoninterface.IExecutable;
import com.isgk.colorblock.util.matrix.Matrix;

public class ParticleExpressions implements IExecutable {

	private static final Map<String, NumericalExpression[]> numericalExpressionsBuf = Maps.newHashMap();

	IExpression<?, Matrix>[] expressions;
	Map<String, Matrix> args;

	public ParticleExpressions(String expression) {
		NumericalExpression[] nes;
		if (numericalExpressionsBuf.containsKey(expression)) {
			nes = numericalExpressionsBuf.get(expression);
		} else {
			String[] expressionsString = expression.split(";");
			nes = new NumericalExpression[expressionsString.length];
			for (int i = 0; i < expressionsString.length; i++) {
				nes[i] = new NumericalExpression(expressionsString[i]);
			}
			numericalExpressionsBuf.put(expression, nes);
		}
		this.expressions = nes;
		this.args = new HashMap<>();
	}

	public ParticleExpressions(IExpression<?, Matrix>[] expressions) {
		this.expressions = expressions;
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
		for (IExpression<?, Matrix> expression : expressions) {
			expression.invoke(args);
		}
		return null;
	}

	public JNI jni() {
		return new JNI(this);
	}

	public class JNI implements IExecutable {

		public JNI(ParticleExpressions particleExpressions) {
			init(particleExpressions);
		}

		private native void init(ParticleExpressions pe);

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
