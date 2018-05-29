package com.bonc.uni.common.jpa.Specification;

import org.springframework.data.domain.Range;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * between
 * @author futao
 * 2017年8月28日
 * @param <T>
 */
public class Between<T> implements Specification<T>, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String property;
    @SuppressWarnings("rawtypes")
	private final Range range;

    public Between(String property, @SuppressWarnings("rawtypes") Range range) {
        this.property = property;
        this.range = range;
    }

    @SuppressWarnings("unchecked")
	@Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.between(root.get(property), range.getLowerBound(), range.getUpperBound());
    }
}
