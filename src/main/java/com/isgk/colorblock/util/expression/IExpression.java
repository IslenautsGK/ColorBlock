package com.isgk.colorblock.util.expression;

import java.util.Map;
import java.util.Random;

public interface IExpression<Output, Input extends Number> {

	Output invoke(Map<String, Input> args);

	public static enum Operator {

		ADD("+"), SUB("-"), NEG("-"), MUL("*"), DIV("/"), POW("^"), ASS("="), EQU("=="), NEQ("!="), GRE(">"), LES("<"),
		GREEQU(">="), LESEQU("<="), AND("&"), OR("|"), NOT("!"), SPL(","), SPLLIN(",,");

		private String operator;

		private Operator(String operator) {
			this.operator = operator;
		}

		public String getOperator() {
			return operator;
		}

	}

	public static enum Function {

		SIN("sin", Math::sin), COS("cos", Math::cos), TAN("tan", Math::tan), ASIN("asin", Math::asin),
		ACOS("acos", Math::acos), ATAN("atan", Math::atan), TORADIANS("toRadians", Math::toRadians),
		TODEGREES("toDegrees", Math::toDegrees), EXP("exp", Math::exp), LN("ln", Math::log), LOG("log", Math::log10),
		SQRT("sqrt", Math::sqrt), CBRT("cbrt", Math::cbrt), CEIL("ceil", Math::ceil), FLOOR("floor", Math::floor),
		RINT("rint", Math::rint), ROUND("round", (Fun1P) Math::round), ABS("abs", (Fun1P) Math::abs),
		ULP("ulp", (Fun1P) Math::ulp), SIGNUM("signum", (Fun1P) Math::signum), SINH("sinh", Math::sinh),
		COSH("cosh", Math::cosh), TANH("tanh", Math::tanh), MAX("max", Math::max), MIN("min", Math::min),
		HYPOT("hypot", Math::hypot), ATAN2("atan2", Math::atan2), POW("pow", Math::pow),
		IEEEREMAINDER("mod", Math::IEEEremainder), RAND("rand", Function::rand);

		private static Random random = new Random();

		private static double rand(double max) {
			return random.nextDouble() * max;
		}

		private String name;
		private Fun1P fun1p;
		private Fun2P fun2p;
		private int paramCount;

		private Function(String name, Fun1P fun) {
			this.name = name;
			this.fun1p = fun;
			this.paramCount = 1;
		}

		private Function(String name, Fun2P fun) {
			this.name = name;
			this.fun2p = fun;
			this.paramCount = 2;
		}

		public String getName() {
			return name;
		}

		public int getParamCount() {
			return paramCount;
		}

		public double invoke(double... in) {
			double result = 0D;
			switch (paramCount) {
			case 1:
				result = fun1p.invok(in[0]);
				break;
			case 2:
				result = fun2p.invok(in[0], in[1]);
				break;
			}
			return result;
		}

		interface Fun {
		}

		@FunctionalInterface
		interface Fun1P extends Fun {

			double invok(double p1);

		}

		@FunctionalInterface
		interface Fun2P extends Fun {

			double invok(double p1, double p2);

		}

	}

}
