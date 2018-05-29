package com.bonc.uni.common.jpa.Specification;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

/**
 * in
 * @author futao
 * 2017年8月28日
 * @param <T>
 */
public class InSpecification<T> implements Specification<T>, Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String property;
    private Object[] values;

    public InSpecification(String property, Object[] values) {
        this.property = property;
        this.values = values;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return root.get(property).in(values);
    }
}
