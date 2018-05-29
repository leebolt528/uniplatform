package com.bonc.uni.common.jpa.Specification;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class LessThanOrEqualTo<T> implements Specification<T>, Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String property;
    private final Comparable<Object> compare;

    @SuppressWarnings("unchecked")
	public LessThanOrEqualTo(String property, Comparable<? extends Object> compare) {
        this.property = property;
        this.compare = (Comparable<Object>) compare;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.lessThanOrEqualTo(root.get(property), compare);
    }
}
