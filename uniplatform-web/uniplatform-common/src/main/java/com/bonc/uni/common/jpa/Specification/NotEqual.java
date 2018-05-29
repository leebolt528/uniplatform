package com.bonc.uni.common.jpa.Specification;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class NotEqual<T> implements Specification<T>, Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String property;
    private final Object[] values;

    public NotEqual(String property, Object... values) {
        this.property = property;
        this.values = values;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (values == null) {
            return cb.isNotNull(root.get(property));
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
        return value == null ? cb.isNotNull(root.get(field)) : cb.notEqual(root.get(field), value);
    }
}

