package com.isgk.colorblock.util.expression;

import java.util.Map;

import com.isgk.colorblock.util.client.component.ClientMessageUtil;
import com.isgk.colorblock.util.matrix.Matrix;
import com.isgk.colorblock.util.string.StringUtil;

public class FunctionExpression implements IExpression<Matrix, Matrix> {

	private IExpression<Matrix, Matrix> expression1 = null;
	private IExpression<Matrix, Matrix> expression2 = null;
	private Function function = null;
	private String expressionStr;

	public FunctionExpression(String expression) {
		expression = expression.replaceAll("\\s+", "");
		this.expressionStr = expression;
		parse(expression);
		if (function == null) {
			throw new RuntimeException(expression);
		}
	}

	@Override
	public Matrix invoke(Map<String, Matrix> args) {
		if (function == null) {
			ClientMessageUtil.addChatMessage("Function Error:" + expressionStr);
			throw new RuntimeException(expressionStr);
		}
		try {
			if (expression2 == null) {
				return new Matrix(function.invoke(expression1.invoke(args).getNumber()));
			} else {
				return new Matrix(
						function.invoke(expression1.invoke(args).getNumber(), expression2.invoke(args).getNumber()));
			}
		} catch (RuntimeException e) {
			ClientMessageUtil.addChatMessage(e.getMessage());
			throw new RuntimeException(expressionStr);
		}
	}

	private void parse(String expression) {
		expression = StringUtil.removeBracket(expression);
		int bracketBegin = expression.indexOf('(');
		int bracketEnd = expression.lastIndexOf(')');
		if (bracketBegin == -1 || bracketEnd == -1) {
			throw new RuntimeException(expression);
		} else {
			String funName = expression.substring(0, bracketBegin);
			boolean isFunc = false;
			for (Function fun : Function.values()) {
				if (fun.getName().equals(funName)) {
					function = fun;
					isFunc = true;
					break;
				}
			}
			if (!isFunc) {
				throw new RuntimeException(expression);
			}
			int index = -1;
			int bracket = 0;
			for (int i = bracketBegin + 1; i < bracketEnd; i++) {
				char ch = expression.charAt(i);
				switch (ch) {
				case '(':
					bracket++;
					break;
				case ')':
					bracket--;
					break;
				case ',':
					if (bracket == 0) {
						index = i;
					}
				}
				if (bracket < 0) {
					throw new RuntimeException(expression);
				}
			}
			if (index == -1) {
				try {
					this.expression1 = new NumericalExpression(expression.substring(bracketBegin + 1, bracketEnd));
				} catch (RuntimeException e1) {
					try {
						this.expression1 = new FunctionExpression(expression.substring(bracketBegin + 1, bracketEnd));
					} catch (RuntimeException e2) {
						this.expression1 = new NumberExpression(expression.substring(bracketBegin + 1, bracketEnd));
					}
				}
			} else {
				try {
					this.expression1 = new NumericalExpression(expression.substring(bracketBegin + 1, index));
				} catch (RuntimeException e1) {
					try {
						this.expression1 = new FunctionExpression(expression.substring(bracketBegin + 1, index));
					} catch (RuntimeException e2) {
						this.expression1 = new NumberExpression(expression.substring(bracketBegin + 1, index));
					}
				}
				try {
					this.expression2 = new NumericalExpression(expression.substring(index + 1, bracketEnd));
				} catch (RuntimeException e1) {
					try {
						this.expression2 = new FunctionExpression(expression.substring(index + 1, bracketEnd));
					} catch (RuntimeException e2) {
						this.expression2 = new NumberExpression(expression.substring(index + 1, bracketEnd));
					}
				}
			}
		}
	}

}
