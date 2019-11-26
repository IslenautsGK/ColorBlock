package com.isgk.colorblock.util.expression;

import java.util.Map;

import com.isgk.colorblock.util.matrix.Matrix;
import com.isgk.colorblock.util.string.StringUtil;

public class ConditionExpression implements IExpression<Boolean, Matrix> {

	private static final Operator[][] SUCCESSOPERATOR = {
			{ Operator.EQU, Operator.NEQ, Operator.GRE, Operator.LES, Operator.GREEQU, Operator.LESEQU },
			{ Operator.NOT }, { Operator.AND }, { Operator.OR } };

	private IExpression<Boolean, Matrix> leftcExpression = null;
	private IExpression<Boolean, Matrix> rightcExpression = null;
	private IExpression<Matrix, Matrix> leftExpression = null;
	private IExpression<Matrix, Matrix> rightExpression = null;
	private Operator operator = null;
	private String expression;

	public ConditionExpression(String expression) {
		expression.replaceAll(" ", "");
		this.expression = expression;
		parse(expression);
		if (operator == null) {
			throw new RuntimeException(expression);
		}
	}

	@Override
	public Boolean invoke(Map<String, Matrix> args) {
		boolean result = false;
		switch (operator) {
		case EQU:
			result = leftExpression.invoke(args).equals(rightExpression.invoke(args));
			break;
		case NEQ:
			result = !leftExpression.invoke(args).equals(rightExpression.invoke(args));
			break;
		case GRE:
			result = leftExpression.invoke(args).gre(rightExpression.invoke(args));
			break;
		case LES:
			result = leftExpression.invoke(args).les(rightExpression.invoke(args));
			break;
		case GREEQU:
			result = leftExpression.invoke(args).greequ(rightExpression.invoke(args));
			break;
		case LESEQU:
			result = leftExpression.invoke(args).lesequ(rightExpression.invoke(args));
			break;
		case NOT:
			result = !rightcExpression.invoke(args);
			break;
		case AND:
			result = leftcExpression.invoke(args) && rightcExpression.invoke(args);
			break;
		case OR:
			result = leftcExpression.invoke(args) || rightcExpression.invoke(args);
			break;
		default:
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
		} else {
			boolean equal = expression.charAt(operatorIndex + 1) == '=';
			if (operatorGrade == 0) {
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
					rightExpression = new NumericalExpression(
							expression.substring(operatorIndex + (equal ? 2 : 1), expression.length()));
				} catch (RuntimeException e1) {
					try {
						rightExpression = new FunctionExpression(
								expression.substring(operatorIndex + (equal ? 2 : 1), expression.length()));
					} catch (RuntimeException e2) {
						rightExpression = new NumberExpression(
								expression.substring(operatorIndex + (equal ? 2 : 1), expression.length()));
					}
				}
			} else {
				leftcExpression = operatorIndex == 0 ? null
						: new ConditionExpression(expression.substring(0, operatorIndex));
				rightcExpression = new ConditionExpression(
						expression.substring(operatorIndex + (equal ? 2 : 1), expression.length()));
			}
		}
	}

}
