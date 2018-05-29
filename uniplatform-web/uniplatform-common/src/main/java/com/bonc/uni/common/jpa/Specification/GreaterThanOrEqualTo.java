package com.bonc.uni.common.jpa.Specification;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

/**
 * 大于等于
 * @author futao
 * 2017年8月28日
 * @param <T>
 */
public class GreaterThanOrEqualTo<T> implements Specification<T>, Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String property;
    private final Comparable<Object> compare;

    @SuppressWarnings("unchecked")
	public GreaterThanOrEqualTo(String property, Comparable<? extends Object> compare) {
        this.property = property;
        this.compare = (Comparable<Object>) compare;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.greaterThanOrEqualTo(root.get(property), compare);
    }
}
