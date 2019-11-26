package com.isgk.colorblock.util.expression;

import java.util.Map;

import com.isgk.colorblock.util.client.component.ClientMessageUtil;
import com.isgk.colorblock.util.matrix.Matrix;
import com.isgk.colorblock.util.string.StringUtil;

public class NumericalExpression implements IExpression<Matrix, Matrix> {

	private static final Operator[][] SUCCESSOPERATOR = { { Operator.POW }, { Operator.MUL, Operator.DIV },
			{ Operator.ADD, Operator.SUB }, { Operator.ASS }, { Operator.SPL, Operator.SPLLIN } };

	private IExpression<Matrix, Matrix> leftExpression = null;
	private IExpression<Matrix, Matrix> rightExpression = null;
	private Operator operator = null;
	private String expression;

	public NumericalExpression(String expression) {
		expression.replaceAll(" ", "");
		this.expression = expression;
		parse(expression);
		if (operator == null) {
			throw new RuntimeException(expression);
		}
	}

	@Override
	public Matrix invoke(Map<String, Matrix> args) {
		Matrix result = Matrix.N0;
		if (operator == null) {
			ClientMessageUtil.addChatMessage("Operator Error:" + expression);
			throw new RuntimeException(expression);
		}
		switch (operator) {
		case ADD:
			result = leftExpression.invoke(args).add(rightExpression.invoke(args));
			break;
		case SUB:
			result = leftExpression.invoke(args).sub(rightExpression.invoke(args));
			break;
		case NEG:
			result = rightExpression.invoke(args).neg();
			break;
		case MUL:
			result = leftExpression.invoke(args).transform(rightExpression.invoke(args));
			break;
		case DIV:
			result = leftExpression.invoke(args).transform(rightExpression.invoke(args).getReverseMartrix());
			break;
		case POW:
			result = leftExpression.invoke(args).pow(rightExpression.invoke(args));
			break;
		case ASS:
			if (leftExpression instanceof NumberExpression) {
				return ((NumberExpression) leftExpression).setKeyValue(args, rightExpression.invoke(args));
			} else {
				ClientMessageUtil.addChatMessage("Assignment Error:" + expression);
				throw new RuntimeException(expression);
			}
		default:
			ClientMessageUtil.addChatMessage("Operator Error:" + expression);
			throw new RuntimeException(expression);
		}
		return result;
	}

	private void parse(String expression) {
		expression = StringUtil.removeBracket(expression);
		char[] chs = expression.toCharArray();
		int operatorIndex = -1;
		int operatorGrade = -1;
		int bracket = 0;
		for (int i = 0; i < chs.length; i++) {
			switch (chs[i]) {
			case '(':
				bracket++;
				break;
			case ')':
				bracket--;
				break;
			default:
				if (bracket == 0) {
					for (int j = 0; j < SUCCESSOPERATOR.length; j++) {
						for (int k = 0; k < SUCCESSOPERATOR[j].length; k++) {
							boolean equal = true;
							for (int l = 0; l < SUCCESSOPERATOR[j][k].getOperator().length(); l++) {
								if (chs[i + l] != SUCCESSOPERATOR[j][k].getOperator().charAt(l)) {
									equal = false;
									break;
								}
							}
							if (equal && j >= operatorGrade) {
								operatorIndex = i;
								operatorGrade = j;
								operator = SUCCESSOPERATOR[j][k];
							}
						}
					}
				}
				break;
			}
		}
		if (operatorIndex == -1) {
			throw new RuntimeException(expression);
		} else if (operatorIndex == 0 && operator == Operator.SUB) {
			operator = Operator.NEG;
			try {
				rightExpression = new NumericalExpression(expression.substring(operatorIndex + 1, expression.length()));
			} catch (RuntimeException e1) {
				try {
					rightExpression = new FunctionExpression(
							expression.substring(operatorIndex + 1, expression.length()));
				} catch (RuntimeException e2) {
					rightExpression = new NumberExpression(
							expression.substring(operatorIndex + 1, expression.length()));
					if (((NumberExpression) rightExpression).getRows() != 1
							|| ((NumberExpression) rightExpression).getRows() != 1) {
						throw new RuntimeException(expression);
					}
				}
			}
		} else if (operator == Operator.SPL || operator == Operator.SPLLIN) {
			throw new RuntimeException(expression);
		} else {
			if (operator == Operator.SUB && expression.charAt(operatorIndex - 1) == ',') {
				throw new RuntimeException(expression);
			}
			try {
				leftExpression = new NumericalExpression(expression.substring(0, operatorIndex));
			} catch (RuntimeException e1) {
				try {
					leftExpression = new FunctionExpression(expression.substring(0, operatorIndex));
				} catch (RuntimeException e2) {
					leftExpression = new NumberExpression(expression.substring(0, operatorIndex));
				}
			}
			try {
				rightExpression = new NumericalExpression(expression.substring(operatorIndex + 1, expression.length()));
			} catch (RuntimeException e1) {
				try {
					rightExpression = new FunctionExpression(
							expression.substring(operatorIndex + 1, expression.length()));
				} catch (RuntimeException e2) {
					rightExpression = new NumberExpression(
							expression.substring(operatorIndex + 1, expression.length()));
				}
			}
		}
	}

}
