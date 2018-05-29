package com.bonc.uni.common.jpa.Specification;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

/**
 * 模糊
 * @author futao
 * 2017年8月28日
 * @param <T>
 */
public class Like<T> implements Specification<T>, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String property;
    private final String[] patterns;

    public Like(String property, String... patterns) {
        this.property = property;
        this.patterns = patterns;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (patterns.length == 1) {
            return cb.like(root.get(property), patterns[0]);
        }
        Predicate[] predicates = new Predicate[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            predicates[i] = cb.like(root.get(property), patterns[i]);
        }
        return cb.or(predicates);
    }
}
