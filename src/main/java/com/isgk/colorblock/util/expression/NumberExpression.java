package com.isgk.colorblock.util.expression;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.isgk.colorblock.util.client.component.ClientMessageUtil;
import com.isgk.colorblock.util.matrix.Matrix;
import com.isgk.colorblock.util.string.StringUtil;

public class NumberExpression implements IExpression<Matrix, Matrix> {

	private String expression;
	private String[][] var;
	private IExpression<Matrix, Matrix>[][] expressionMatrix;
	private boolean[][] isVar;
	private int rows;
	private int cols;
	private Matrix value;
	private boolean isFinal;

	public NumberExpression(String key) {
		key = StringUtil.removeBracket(key.replaceAll("\\s+", ""));
		expression = key;
		try {
			if (key.equals("")) {
				value = new Matrix(0D);
			} else {
				try {
					value = new Matrix(Double.parseDouble(key));
				} catch (RuntimeException e) {
					value = new Matrix(key);
				}
			}
			rows = value.getRows();
			cols = value.getCols();
			isFinal = true;
		} catch (RuntimeException e) {
			String[] rowss = key.split(",,");
			rows = rowss.length;
			cols = -1;
			var = new String[rows][];
			expressionMatrix = new IExpression[rows][];
			isVar = new boolean[rows][];
			for (int i = 0; i < rows; i++) {
				String[] colss = split(rowss[i]);
				if (cols == -1) {
					cols = colss.length;
				} else if (cols != colss.length) {
					throw new RuntimeException("NumberExpression Create Error:" + key);
				}
				var[i] = new String[cols];
				expressionMatrix[i] = new IExpression[cols];
				isVar[i] = new boolean[cols];
				for (int j = 0; j < cols; j++) {
					if (tryParse(colss[j])) {
						isVar[i][j] = false;
						try {
							expressionMatrix[i][j] = new NumericalExpression(colss[j]);
						} catch (RuntimeException e1) {
							try {
								expressionMatrix[i][j] = new FunctionExpression(colss[j]);
							} catch (RuntimeException e2) {
								expressionMatrix[i][j] = new NumberExpression(colss[j]);
							}
						}
					} else {
						isVar[i][j] = true;
					}
					var[i][j] = colss[j];
				}
			}
			isFinal = false;
		}
	}

	private String[] split(String key) {
		List<String> result = Lists.newArrayList();
		int preIndex = 0;
		int bracket = 0;
		for (int i = 0; i < key.length(); i++) {
			char ch = key.charAt(i);
			if (ch == '(') {
				bracket++;
			} else if (ch == ')') {
				bracket--;
			} else if (ch == ',' && bracket == 0) {
				result.add(key.substring(preIndex, i));
				preIndex = i + 1;
			}
		}
		result.add(key.substring(preIndex));
		return result.toArray(new String[0]);
	}

	private boolean tryParse(String key) {
		for (Operator operator : Operator.values()) {
			if (key.indexOf(operator.getOperator()) != -1) {
				return true;
			}
		}
		if (key.indexOf('(') != -1 && key.indexOf(')') != -1) {
			return true;
		}
		try {
			Double.parseDouble(key);
			return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	@Override
	public Matrix invoke(Map<String, Matrix> args) {
		if (isFinal) {
			return value;
		} else {
			if (rows == 1 && cols == 1) {
				if (isVar[0][0]) {
					if (args.containsKey(var[0][0])) {
						return args.get(var[0][0]);
					} else {
						ClientMessageUtil.addChatMessage("Var Undefine:" + expression);
						throw new RuntimeException(expression);
					}
				} else {
					return expressionMatrix[0][0].invoke(args);
				}
			} else {
				Matrix[][] matrixs = new Matrix[rows][cols];
				boolean allIsNumber = true;
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						if (isVar[i][j]) {
							if (args.containsKey(var[i][j])) {
								matrixs[i][j] = args.get(var[i][j]);
							} else {
								ClientMessageUtil.addChatMessage("Var Undefine:" + var[i][j]);
								throw new RuntimeException(expression);
							}
						} else {
							matrixs[i][j] = expressionMatrix[i][j].invoke(args);
						}
						if (!matrixs[i][j].isNumber()) {
							allIsNumber = false;
						}
					}
				}
				double[][] result;
				if (allIsNumber) {
					result = new double[rows][cols];
					for (int i = 0; i < rows; i++) {
						for (int j = 0; j < cols; j++) {
							result[i][j] = matrixs[i][j].getNumber();
						}
					}
				} else {
					int newCols = 0;
					int newRows = 0;
					for (int i = 0; i < rows; i++) {
						int sRows = matrixs[i][0].getRows();
						newRows += sRows;
						for (int j = 1; j < cols; j++) {
							if (matrixs[i][j].getRows() != sRows) {
								ClientMessageUtil.addChatMessage("Var Type Error:" + var[i][j]);
								throw new RuntimeException(expression);
							}
						}
					}
					for (int j = 0; j < cols; j++) {
						int sCols = matrixs[0][j].getCols();
						newCols += sCols;
						for (int i = 1; i < rows; i++) {
							if (matrixs[i][j].getCols() != sCols) {
								ClientMessageUtil.addChatMessage("Var Type Error:" + var[i][j]);
								throw new RuntimeException(expression);
							}
						}
					}
					result = new double[newRows][newCols];
					for (int i = 0; i < rows; i++) {
						for (int j = 0; j < cols; j++) {
							for (int k = 0; k < matrixs[i][j].getRows(); k++) {
								for (int l = 0; l < matrixs[i][j].getCols(); l++) {
									result[i + k][j + l] = matrixs[i][j].get(k, l);
								}
							}
						}
					}
				}
				return new Matrix(result);
			}
		}
	}

	public Matrix setKeyValue(Map<String, Matrix> args, Matrix matrix) {
		if (isFinal) {
			ClientMessageUtil.addChatMessage("Not A Var:" + expression);
			throw new RuntimeException(expression);
		}
		if (rows == matrix.getRows() && cols == matrix.getCols()) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					if (isVar[i][j]) {
						args.put(var[i][j], matrix.getMatrix(i, j));
					} else {
						ClientMessageUtil.addChatMessage("Not A Var:" + var[i][j]);
						throw new RuntimeException(expression);
					}
				}
			}
			return matrix;
		} else if (rows == 1 && cols == 1) {
			if (isVar[0][0]) {
				args.put(var[0][0], matrix);
			} else {
				ClientMessageUtil.addChatMessage("Not A Var:" + var[0][0]);
				throw new RuntimeException(expression);
			}
			return matrix;
		} else {
			ClientMessageUtil.addChatMessage("Var Type Error:" + expression);
			throw new RuntimeException(expression);
		}
	}

}
