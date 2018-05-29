package com.bonc.uni.common.jpa.Specification;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

/**
 * 等于
 * @author futao
 * 2017年8月28日
 * @param <T>
 */
public class Equal<T> implements Specification<T>, Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5789009572512754234L;
	private final String property;
    private final Object[] values;

    public Equal(String property, Object... values) {
        this.property = property;
        this.values = values;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (values == null) {
            return cb.isNull(root.get(property));
        }
        if (values.length == 1) {
            return getPredicate(root, cb, values[0], property);
        }

        Predicate[] predicates = new Predicate[values.length];
        for (int i = 0; i < values.length; i++) {
            predicates[i] = getPredicate(root, cb, values[i], property);
        }
        return cb.or(predicates);
    }

    private Predicate getPredicate(Root<T> root, CriteriaBuilder cb, Object value, String field) {
        return value == null ? cb.isNull(root.get(field)) : cb.equal(root.get(field), value);
    }
}

