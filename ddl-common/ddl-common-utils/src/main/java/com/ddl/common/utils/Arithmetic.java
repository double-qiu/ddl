/**
 * Arithmetic.java
 * 
 * 2018年3月19日
 */
package com.ddl.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Arithmetic
 */
public class Arithmetic {

	/** 错误提示. */
	private static final String ERROR_ZERO = "DividedZero";

	/** 检测一个字符串是不是数字. */
	private static Pattern pattern = Pattern.compile("\\d+\\.?\\d?");

	/** 运算符优先级. */
	private static Map<String, Integer> priority = new HashMap<>();

	/** 替换表达式中的中文括号. */
	private static Map<String, String> replace = new HashMap<>();

	/** 支持的运算符. */
	private static String[] operator = { "+", "-", "*", "/" };

	/** 堆栈. */
	private static Stack<String> stack = new Stack<>();

	static {
		priority.put("*", 2);
		priority.put("/", 2);
		priority.put("+", 1);
		priority.put("-", 1);
		priority.put("", -1); // 无运算符

		replace.put("（", "(");
		replace.put("）", ")");
	}

	/**
	 * -----------测试代码----------------
	 */
//	public static void main(String[] args) {
//		String expression = "80+(9-1)*8+7/3";
//		System.out.println(execute(expression));
//	}

	/**
	 * 整个计算过程
	 * 
	 * @param expression
	 *            传入的表达式
	 * @return 计算结果
	 */
	public static String execute(String expression) {
		return suffixToValue(inffixToSuffix(trim(expression)));
	}

	/**
	 * 整理算式，替换掉中文括号等
	 * 
	 * @param expression
	 *            算式
	 * @return 整理后的算式
	 */
	private static String trim(String expression) {
		String result = expression.trim();
		for (Map.Entry<String, String> entry : replace.entrySet()) {
			result = result.replace(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/**
	 * 将中缀表达式转换成后缀表达式 eg：中缀表达式8+(9-1）*8+7/2 后缀表达式8 9 1 - 8 * + 7 2 /
	 * +，元素之间之间用空格分隔。 从左到右遍历中缀表达式的每一个数字和运算符 如果数字就输出（即存入后缀表达式）
	 * 如果是右括号，则弹出左括号之前的运算符 如果优先级低于栈顶运算符，则弹出栈顶运算符，并将当前运算符进栈 遍历结束后，将栈则剩余运算符弹出。
	 * 
	 * @param expression
	 *            中缀表达式
	 * @return 后缀表达式
	 */
	private static String inffixToSuffix(String expression) {
		stack.clear();
		StringBuilder inffix = new StringBuilder(expression);
		StringBuilder suffix = new StringBuilder();
		String element = ""; // 中缀表达式的数字或者运算符
		String tmp = "";

		while (inffix.length() > 0) {
			element = popNextElement(inffix);

			if (isNum(element)) { // 是数字则输出
				suffix.append(element).append(" ");
			} else if (")".equals(element)) { // 右括号则将左括号之前的内容全弹出
				tmp = stack.pop();
				while (!"(".equals(tmp)) {
					suffix.append(tmp).append(" ");
					tmp = stack.pop();
				}
			} else if ("(".equals(element) || priority.get(element) >= priority.get(getTopOperator())) {
				stack.push(element);
			} else { // 优先级小于栈顶运算符，则弹出
				tmp = stack.pop();
				suffix.append(tmp).append(" ").append(element).append(" ");
			}
		}

		// 把栈中剩余运算符都弹出
		while (stack.size() > 0) {
			suffix.append(stack.pop()).append(" ");
		}

		return suffix.toString();
	}

	/**
	 * 根据后缀表达式算出结果 eg：中缀表达式8+(9-1）*8+7/2 后缀表达式8 9 1 - 8 * + 7 2 / +，元素之间之间用空格分隔。
	 * 从左到右遍历后缀表达式 遇到数字就进栈 遇到符号，就将栈顶的两个数字出栈运算，运算结果进栈，直到获得最终结果。
	 * -----------目前只支持整数，由于整数相除会出现浮点数，这里用String作为返回值--------
	 * 
	 * @param expression
	 *            后缀表达式
	 * @return 结果
	 */
	private static String suffixToValue(String expression) {
		// 已经用空格分隔，直接分割
		String[] suffix = expression.split(" ");
		stack.clear();
		double num1 = 0, num2 = 0; // 注意次序，num2在栈顶，整数运算结果也可能是double
		String tmp = "";

		for (int i = 0; i < suffix.length; i++) {
			if (isNum(suffix[i])) { // 数字
				stack.push(suffix[i]);
			} else { // 是操作符
				num2 = Double.parseDouble(stack.pop());
				num1 = Double.parseDouble(stack.pop());
				tmp = calculate(num1, num2, suffix[i]);
				if (ERROR_ZERO.equals(tmp)) {
					//throw new ArithmeticException("被除数不能为0");
					stack.push("0");
				} else {
					stack.push(tmp);
				}
			}
		}

		// 最终结果也压在栈中，取出即可
		return stack.pop();
	}

	/**
	 * 目前只支持整数，但是整数运算结果也可能是double，故这里传入的参数用double。
	 * 
	 * @param num1
	 *            第一个数，在前
	 * @param num2
	 *            第二个数，在后
	 * @param operator
	 *            运算符
	 * @return 运算结果，除数为0则返回Error
	 */
	private static String calculate(double num1, double num2, String operator) {
		double result = 0;
		switch (operator) {
		case "+":
			result = num1 + num2;
			break;
		case "-":
			result = num1 - num2;
			break;
		case "*":
			result = num1 * num2;
			break;
		case "/":
			if (num2 == 0) { // 除数为0
				return ERROR_ZERO;
			}
			result = 1.0 * num1 / num2;
			break;
		default:
			break;
		}
		return String.valueOf(result);
	}

	/**
	 * 检测字符是不是数字 浮点数其实可以写零宽断言，但是
	 * 
	 * @param c
	 *            待检测字符
	 * @return 检测结果
	 */
	private static boolean isNum(char c) {
		if ((c >= '0' && c <= '9') || c == '.') {
			return true;
		}
		return false;
	}

	/**
	 * 检测字符串是不是数字
	 * 
	 * @param str
	 *            待检测字符串
	 * @return 检测结果
	 */
	private static boolean isNum(String str) {
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 获取栈顶运算符
	 * 
	 * @return 如果栈中无运算符，则返回空字符串
	 */
	private static String getTopOperator() {
		String tmp = "";
		for (int i = stack.size() - 1; i >= 0; i--) {
			tmp = stack.get(i);
			if ("(".equals(tmp)) {
				break;
			} else if (isOperator(tmp)) {
				return tmp;
			}
		}
		return "";
	}

	/**
	 * 检测一个字符是不是运算符 栈中不是运算符就是括号
	 * 
	 * @param str
	 *            待检测字符，由于可能有多位数字字符串，这里用的是String
	 * @return 检测结果
	 */
	private static boolean isOperator(String str) {
		for (int i = 0; i < operator.length; i++) {
			if (operator[i].equals(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取表达式的下一个运算符或数字，同时从表达式中删除该元素
	 * 
	 * @param expression
	 *            表达式
	 * @return
	 */
	private static String popNextElement(StringBuilder expression) {
		StringBuilder result = new StringBuilder();
		char c = expression.charAt(0);
		expression.deleteCharAt(0);
		result.append(c);

		if (isNum(c)) {
			// 如果第一次取到的是数字，则继续检查
			while (expression.length() > 0 && isNum(c = expression.charAt(0))) {
				expression.deleteCharAt(0);
				result.append(c);
			}
		}

		return result.toString();
	}
}
