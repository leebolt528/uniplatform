package com.bonc.uni.common.jpa.Specification;

import static javax.persistence.criteria.Predicate.BooleanOperator;

/**
 * jpa检索类
 * @author futao
 * 2017年8月29日
 */
public class SpecificationUtil {

	public static <T> PredicateBuilder<T> and() {
        return new PredicateBuilder<>(BooleanOperator.AND);
    }
    public static <T> PredicateBuilder<T> or() {
        return new PredicateBuilder<>(BooleanOperator.OR);
    }
}
