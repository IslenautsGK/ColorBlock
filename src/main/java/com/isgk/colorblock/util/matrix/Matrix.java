package com.isgk.colorblock.util.matrix;

import com.isgk.colorblock.util.client.component.ClientMessageUtil;

public class Matrix extends Number {

	private static final long serialVersionUID = 7729902854802325412L;

	public static Matrix E3 = new Matrix();
	public static Matrix N0 = new Matrix(0D);

	private double[][] data;
	private int rows;
	private int cols;

	public Matrix() {
		this((String) null);
	}

	public Matrix(String str) {
		if (str == null || str.length() == 0 || str.equals("E3")) {
			data = new double[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
			rows = 3;
			cols = 3;
			return;
		} else if (str.equals("N0")) {
			this.rows = 1;
			this.cols = 1;
			this.data = new double[1][1];
			data[0][0] = 0;
			return;
		}
		String[] rowstr = str.split(",,");
		rows = rowstr.length;
		data = new double[rows][];
		this.cols = -1;
		for (int i = 0; i < rows; i++) {
			String[] colstr = rowstr[i].split(",");
			if (this.cols == -1) {
				this.cols = colstr.length;
			} else if (this.cols != colstr.length) {
				throw new RuntimeException("Matrix Create Error:" + str);
			}
			data[i] = new double[this.cols];
			for (int j = 0; j < cols; j++) {
				data[i][j] = Double.parseDouble(colstr[j]);
			}
		}
	}

	public Matrix(double[][] matrix) {
		this.rows = matrix.length;
		this.cols = -1;
		for (int i = 0; i < rows; i++) {
			if (this.cols == -1) {
				this.cols = matrix[i].length;
			} else if (this.cols != matrix[i].length) {
				throw new RuntimeException("Matrix Create Error");
			}
		}
		this.data = matrix;
	}

	public Matrix(double number) {
		this.rows = 1;
		this.cols = 1;
		this.data = new double[1][1];
		data[0][0] = number;
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	public double get(int row, int col) {
		return data[row][col];
	}

	public Matrix getMatrix(int row, int col) {
		return new Matrix(data[row][col]);
	}

	public boolean isNumber() {
		return rows == 1 && cols == 1;
	}

	public boolean isVector() {
		return cols == 1;
	}

	public boolean isRowVector() {
		return rows == 1;
	}

	public double getNumber() {
		if (!isNumber()) {
			ClientMessageUtil.addChatMessage("Matrix Not A Number");
			throw new RuntimeException();
		}
		return data[0][0];
	}

	public double[] getVector() {
		if (!isVector()) {
			ClientMessageUtil.addChatMessage("Matrix Not A Vector");
			throw new RuntimeException();
		}
		double[] result = new double[rows];
		for (int i = 0; i < rows; i++) {
			result[i] = data[i][0];
		}
		return result;
	}

	public double[] getRowVector() {
		if (!isRowVector()) {
			ClientMessageUtil.addChatMessage("Matrix Not A Row Vector");
			throw new RuntimeException();
		}
		double[] result = new double[cols];
		for (int i = 0; i < cols; i++) {
			result[i] = data[0][i];
		}
		return result;
	}

	public double[] transform(double... pos) {
		if (!canTransform(pos)) {
			ClientMessageUtil.addChatMessage("Pos Transform Error");
			throw new RuntimeException();
		}
		double[] result = new double[rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result[i] += pos[j] * data[i][j];
			}
		}
		return result;
	}

	public Matrix transform(Matrix matrix) {
		if (!canTransform(matrix)) {
			ClientMessageUtil.addChatMessage("Matrix Transform Error");
			throw new RuntimeException();
		}
		if (matrix.isNumber()) {
			if (isNumber()) {
				return new Matrix(getNumber() * matrix.getNumber());
			} else {
				double[][] result = new double[rows][cols];
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						result[i][j] = data[i][j] * matrix.getNumber();
					}
				}
				return new Matrix(result);
			}
		}
		double[][] result = new double[rows][matrix.cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < matrix.cols; j++) {
				for (int k = 0; k < cols; k++) {
					result[i][j] += data[i][k] * matrix.data[k][j];
				}
			}
		}
		return new Matrix(result);
	}

	public boolean canTransform(double... pos) {
		return pos.length == cols;
	}

	public boolean canTransform(Matrix matrix) {
		return cols == matrix.rows || matrix.isNumber();
	}

	public Matrix add(Matrix matrix) {
		if (!canOperation(matrix)) {
			ClientMessageUtil.addChatMessage("Matrix Add Error");
			throw new RuntimeException();
		}
		if (matrix.isNumber()) {
			if (isNumber()) {
				return new Matrix(getNumber() + matrix.getNumber());
			} else {
				double[][] result = new double[rows][cols];
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						result[i][j] = data[i][j] + matrix.getNumber();
					}
				}
				return new Matrix(result);
			}
		}
		double result[][] = new double[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result[i][j] = data[i][j] + matrix.data[i][j];
			}
		}
		return new Matrix(result);
	}

	public Matrix sub(Matrix matrix) {
		if (!canOperation(matrix)) {
			ClientMessageUtil.addChatMessage("Matrix Add Error");
			throw new RuntimeException();
		}
		if (matrix.isNumber()) {
			if (isNumber()) {
				return new Matrix(getNumber() - matrix.getNumber());
			} else {
				double[][] result = new double[rows][cols];
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						result[i][j] = data[i][j] - matrix.getNumber();
					}
				}
				return new Matrix(result);
			}
		}
		double result[][] = new double[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result[i][j] = data[i][j] - matrix.data[i][j];
			}
		}
		return new Matrix(result);
	}

	public Matrix neg() {
		if (isNumber()) {
			return new Matrix(-getNumber());
		}
		double result[][] = new double[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result[i][j] = -data[i][j];
			}
		}
		return new Matrix(result);
	}

	public boolean canOperation(Matrix matrix) {
		return rows == matrix.rows && cols == matrix.cols || matrix.isNumber();
	}

	public Matrix getConfactor(int h, int v) {
		double[][] result = new double[rows - 1][cols - 1];
		for (int i = 0; i < rows - 1; i++) {
			if (i < h - 1) {
				for (int j = 0; j < cols - 1; j++) {
					if (j < v - 1) {
						result[i][j] = data[i][j];
					} else {
						result[i][j] = data[i][j + 1];
					}
				}
			} else {
				for (int j = 0; j < result[i].length; j++) {
					if (j < v - 1) {
						result[i][j] = data[i + 1][j];
					} else {
						result[i][j] = data[i + 1][j + 1];
					}
				}
			}
		}
		return new Matrix(result);
	}

	public double getMatrixResult() {
		if (isNumber()) {
			return getNumber();
		}
		return getMatrixResult(this);
	}

	private double getMatrixResult(Matrix matrix) {
		if (matrix.rows == 2 && matrix.cols == 2) {
			return matrix.data[0][0] * matrix.data[1][1] - matrix.data[0][1] * matrix.data[1][0];
		}
		double result = 0;
		double[] nums = new double[matrix.rows];
		for (int i = 0; i < matrix.rows; i++) {
			if (i % 2 == 0) {
				nums[i] = matrix.data[0][i] * getMatrixResult(getConfactor(1, i + 1));
			} else {
				nums[i] = -matrix.data[0][i] * getMatrixResult(getConfactor(1, i + 1));
			}
		}
		for (int i = 0; i < matrix.rows; i++) {
			result += nums[i];
		}
		return result;
	}

	public Matrix getReverseMartrix() {
		if (!canReverse()) {
			ClientMessageUtil.addChatMessage("Matrix Reverse Error");
			throw new RuntimeException();
		}
		if (isNumber()) {
			return new Matrix(1D / getNumber());
		}
		double[][] result = new double[rows][cols];
		double abs = getMatrixResult(this);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if ((i + j) % 2 == 0) {
					result[i][j] = getMatrixResult(getConfactor(i + 1, j + 1)) / abs;
				} else {
					result[i][j] = -getMatrixResult(getConfactor(i + 1, j + 1)) / abs;
				}

			}
		}
		result = trans(result);
		return new Matrix(result);
	}

	public boolean canReverse() {
		return rows == cols;
	}

	private double[][] trans(double[][] data) {
		double[][] result = new double[data[0].length][data.length];
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[0].length; j++) {
				result[j][i] = data[i][j];
			}
		return result;
	}

	public Matrix pow(int n) {
		Matrix result = this;
		for (int i = 0; i < n; i++) {
			result = result.transform(this);
		}
		return result;
	}

	public Matrix pow(double n) {
		return new Matrix(Math.pow(getNumber(), n));
	}

	public Matrix pow(Matrix n) {
		return pow(n.getNumber());
	}

	public boolean gre(Matrix matrix) {
		if (isNumber() && matrix.isNumber()) {
			return getNumber() > matrix.getNumber();
		}
		return false;
	}

	public boolean les(Matrix matrix) {
		if (isNumber() && matrix.isNumber()) {
			return getNumber() < matrix.getNumber();
		}
		return false;
	}

	public boolean greequ(Matrix matrix) {
		if (isNumber() && matrix.isNumber()) {
			return getNumber() >= matrix.getNumber();
		}
		return false;
	}

	public boolean lesequ(Matrix matrix) {
		if (isNumber() && matrix.isNumber()) {
			return getNumber() <= matrix.getNumber();
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				sb.append(data[i][j]);
				if (j != cols - 1) {
					sb.append(",");
				}
			}
			if (i != rows - 1) {
				sb.append(",,");
			}
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Matrix) {
			Matrix matrix = (Matrix) obj;
			if (isNumber() && matrix.isNumber()) {
				return getNumber() == matrix.getNumber();
			}
			if (canOperation(matrix)) {
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						if (data[i][j] != matrix.data[i][j]) {
							return false;
						}
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public double doubleValue() {
		if (isNumber()) {
			return getNumber();
		}
		return 0;
	}

	@Override
	public float floatValue() {
		if (isNumber()) {
			return (float) getNumber();
		}
		return 0;
	}

	@Override
	public int intValue() {
		if (isNumber()) {
			return (int) getNumber();
		}
		return 0;
	}

	@Override
	public long longValue() {
		if (isNumber()) {
			return (long) getNumber();
		}
		return 0;
	}

}
