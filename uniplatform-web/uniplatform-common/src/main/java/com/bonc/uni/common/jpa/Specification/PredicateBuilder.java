package com.bonc.uni.common.jpa.Specification;


import javax.persistence.criteria.*;

import static javax.persistence.criteria.Predicate.BooleanOperator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Predicate构造  
 * @author futao
 * 2017年8月28日
 * @param <T>
 */
public class PredicateBuilder<T> {

	/**
	 * 条件  and  or
	 */
	private final Predicate.BooleanOperator operator;
	
	//倒序查询条件
    private String orderByDESC;

    //升序查询条件
    private String orderByASC;

    private List<Specification<T>> specifications;

    public PredicateBuilder(Predicate.BooleanOperator operator) {
        this.operator = operator;
        this.specifications = new ArrayList<>();
    }
    
    public PredicateBuilder<T> orderByDESC(String orderByDESC){
    	this.orderByDESC = orderByDESC;
    	return this;
    }
    
    public PredicateBuilder<T> orderByASC(String orderByASC){
    	this.orderByASC = orderByASC;
    	return this;
    } 
    
    public PredicateBuilder<T> like(String property, String... patterns) {
        return this.predicate(true,new Like<T>(property, patterns));
    }
    
    public PredicateBuilder<T> like(boolean bool,String property, String... patterns) {
        return this.predicate(bool,new Like<T>(property, patterns));
    }
    
    public PredicateBuilder<T> ep(boolean bool,String property, Object... patterns) {
        return this.predicate(bool,new Equal<T>(property, patterns));
    }
    
    public PredicateBuilder<T> ep(String property, Object... patterns) {
        return this.predicate(true,new Equal<T>(property, patterns));
    }
    
    public PredicateBuilder<T> notEp(String property, Object... values) {
        return this.predicate(true,new NotEqual<T>(property, values));
    }
    
    public PredicateBuilder<T> gt(boolean condition, String property, Comparable<?> compare) {
        return this.predicate(true,new GreaterThan<T>(property, compare));
    }
    
    public PredicateBuilder<T> gtOrEp(boolean condition, String property, Comparable<? extends Object> compare) {
        return this.predicate(true,new GreaterThanOrEqualTo<T>(property, compare));
    }
    
    public PredicateBuilder<T> lt(String property, Comparable<?> compare) {
        return this.predicate(true,new LessThanOrEqualTo<T>(property, compare));
    }

    public PredicateBuilder<T> le(String property, Comparable<?> compare) {
        return this.predicate(true,new LessThan<T>(property, compare));
    }

    public PredicateBuilder<T> between(String property, Range<? extends Comparable<?>> range) {
        return this.predicate(true,new Between<T>(property, range));
    }

    public PredicateBuilder<T> notLike(String property, String... patterns) {
        return this.predicate(true,new NotLike<T>(property, patterns));
    }
    public PredicateBuilder<T> in(String property, Object... values) {
        return this.predicate(true,new InSpecification<T>(property, values));
    }

    public PredicateBuilder<T> notIn(String property, Object... values) {
        return this.predicate(true,new NotIn<T>(property, values));
    }

    //TODO: 联表查询join功能的封装
    public PredicateBuilder<T> join(String right, JoinType joinType) {
        return this.predicate(true,new PredicateJoin<T>(right, joinType));
    }

    public PredicateBuilder<T> predicate(boolean bool, Specification<T> specification) {
    	if (bool) {
    		this.specifications.add(specification);
    	}
        return this;
    }

    public Specification<T> build() {
    	Specification<T> toPredicate = new Specification<T>() {
    		public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb){
    			if(!StringUtils.isEmpty(orderByASC))
    	            query.orderBy(cb.asc(root.get(orderByASC)));
    	        if(!StringUtils.isEmpty(orderByDESC))
    	            query.orderBy(cb.desc(root.get(orderByDESC)));
	            Predicate[] predicates = new Predicate[specifications.size()];
	            for (int i = 0; i < specifications.size(); i++) {
	                predicates[i] = specifications.get(i).toPredicate(root, query, cb);
	            }
	            return BooleanOperator.OR.equals(operator) ? cb.or(predicates) : cb.and(predicates);
	        }
    	};
        return toPredicate;
    }
}
