package com.ddl.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * ClassName: MathUtil  
 * 计算工具类
 * @author DOUBLE
 * @version
 */
public class MathUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MathUtil.class);


    private MathUtil() {
    }

    /**
     * (v1-v2)
     */
    public static BigDecimal sub(BigDecimal v1, BigDecimal v2) {
        if (v1 == null) {
            v1 = BigDecimal.ZERO;
        }
        if (v2 == null) {
            v2 = BigDecimal.ZERO;
        }
        return v1.subtract(v2);
    }

    /**
     * 类型转float，
     *
     * @param d
     * @return if null return 0
     */
    public static Float floatValue(BigDecimal d) {
        if (d == null) {
//            LOGGER.debug("计算器计算类型转换float时，传入参数为null, 默认设置为0");
            return null;
        } else {
            return d.floatValue();
        }
    }


    /**
     * 类型转float，
     *
     * @param d
     * @return if null return 0
     */
    public static Double doubleValue(BigDecimal d) {
        if (d == null) {
//            LOGGER.debug("计算器计算类型转换Double时，传入参数为null, 默认设置为0");
            return null;
        } else {
            return d.doubleValue();
        }
    }

    /**
     * 加法运算 v1+v2
     */
    public static BigDecimal add(Float v1, Float v2) {
        return add(BigDecimal.valueOf(v1), BigDecimal.valueOf(v2));
    }

    /**
     * 加法运算 v1+v2
     */
    public static BigDecimal add(BigDecimal v1, Float v2) {
        return add(v1, BigDecimal.valueOf(v2));
    }

    /**
     * 加法运算 v1+v2
     */
    public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
        if (v1 == null) {
//            LOGGER.debug("计算器 加法运算 时，传入v1为null, 默认设置为0");
            v1 = BigDecimal.ZERO;
        }
        if (v2 == null) {
//            LOGGER.debug("计算器 加法运算 时，传入v2为null, 默认设置为0");
            v2 = BigDecimal.ZERO;
        }
        return v1.add(v2);
    }


    /**
     * 计算 ： v1/v2
     */
    public static BigDecimal div(BigDecimal v1, int v2, int scale) {
        if (v2 == 0) {
            LOGGER.warn("计算器 除法运算 时，被除数v2传入0, 返回结果 设置为 null");
            return null;
        }
        BigDecimal b2 = new BigDecimal(String.valueOf(v2));
        return div(v1, b2, scale);
    }

    /**
     * 计算 ： v1/v2
     */
    public static Integer div(int v1, int v2) {
        if (v2 == 0) {
            LOGGER.warn("计算器 除法运算 时，被除数v2传入0, 返回结果 设置为 null");
            return null;
        }
        return Math.floorDiv(v1, v2);
    }

    /**
     * 计算 ： v1/v2 当发生除不尽的情况时， 由scale参数指定精度
     */
    public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        if (v1 == null) {
            return BigDecimal.ZERO;
        }
        if (v2 == null || v2.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        return v1.divide(v2, scale, BigDecimal.ROUND_DOWN);
    }

    /**
     * 乘法运算 v1 * v2
     */
    public static BigDecimal mul(BigDecimal v1, BigDecimal v2) {
        if (v1 == null) {
//            LOGGER.debug("计算器 乘法运算 时，传入v1为null, 默认设置为0");
            v1 = BigDecimal.ZERO;
        }
        if (v2 == null) {
//            LOGGER.debug("计算器 乘法运算 时，传入v2为null, 默认设置为0");
            v2 = BigDecimal.ZERO;
        }
        return v1.multiply(v2);
    }


    /**
     * 乘法运算 v1 * v2
     */
    public static BigDecimal mul(Float v1, Integer v2) {
        if (v1 == null) {
//            LOGGER.debug("计算器 乘法运算 时，传入v1为null, 默认设置为0");
            v1 = 0f;
        }
        if (v2 == null) {
//            LOGGER.debug("计算器 乘法运算 时，传入v2为null, 默认设置为0");
            v2 = 0;
        }
        return mul(new BigDecimal(String.valueOf(v1)), new BigDecimal(String.valueOf(v2)));
    }

}
