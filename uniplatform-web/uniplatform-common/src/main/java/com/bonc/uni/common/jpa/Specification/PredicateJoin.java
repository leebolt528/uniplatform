package com.bonc.uni.common.jpa.Specification;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

import java.io.Serializable;

/**
 * Created by yedunyao on 2017/9/21.
 */
//TODO: 联表查询join功能的封装
public class PredicateJoin<T> implements Specification<T>, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //right table
    String right;
    JoinType joinType;

    public PredicateJoin(String right, JoinType joinType) {
        this.right = right;
        this.joinType = joinType;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Join<T, Object> join = root.join(right, joinType);
        return null;
    }

}
