package com.example.crudrestapi.repository.base;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.domain.Sort.Direction;
import lombok.Data;

@Data
public class BaseCriteria<R extends BaseRepository<?, ?>> {

    protected final Class<?> entityClass;
    protected final EntityManager entityManager;
    protected final Root<?> root;
    protected final CriteriaBuilder criteriaBuilder;
    protected final CriteriaQuery<?> criteriaQuery;
    protected final R repository;
    protected List<Predicate> predicates = new ArrayList<>();
    protected Predicate predicate;

    public BaseCriteria(R repository) {
        this.repository = repository;
        this.entityManager = repository.geEntityManager();
        this.root = repository.getRoot();
        this.entityClass = repository.getEntity();
        this.criteriaBuilder = repository.getCriteriaBuilder();
        this.criteriaQuery = repository.getCriteriaQuery();
        //this.predicate = this.isNotDeleted("isDeleted");
    }

    public <V> Predicate like(String column, V value) {
        String ivalue = String.format("%s%s%s", "%", value, "%");
        if (column.contains(".")) {
            String[] columns = column.split("\\.");
            if (columns.length == 2) {
                predicate = this.criteriaBuilder.like(root.get(columns[0]).get(columns[1]), ivalue);
            } else {
                predicate = this.criteriaBuilder.like(root.get(column).as(String.class), ivalue);
            }
        } else {
            predicate = this.criteriaBuilder.like(root.get(column).as(String.class), ivalue);
        }
        this.predicates.add(predicate);
        return predicate;
    }

    public Predicate and(Predicate... predicates) {
        List<Predicate> predicates2 = new ArrayList<>();
        for (Predicate predicate : predicates) {
            this.predicates.removeIf(pre -> pre.equals(predicate));
            predicates2.add(this.criteriaBuilder.and(predicate));
        }
        this.predicate = this.criteriaBuilder.and(predicates2.toArray(new Predicate[0]));
        this.predicates.add(predicate);
        return predicate;
    }

    public Predicate or(Predicate... predicates) {
        List<Predicate> predicates2 = new ArrayList<>();
        for (Predicate predicate : predicates) {
            this.predicates.removeIf(pre -> pre.equals(predicate));
            predicates2.add(this.criteriaBuilder.or(predicate));
        }
        this.predicate = this.criteriaBuilder.or(predicates2.toArray(new Predicate[0]));
        this.predicates.add(predicate);
        return predicate;
    }

    public <T, V> Predicate equal(String column, V value) {
        if (column.contains(".")) {
            String[] columns = column.split("\\.");
            if (columns.length == 2)
                predicate = this.criteriaBuilder.equal(root.get(columns[0]).get(columns[1]), value);
            else
                predicate = this.criteriaBuilder.equal(root.get(column).as(String.class), value);
        } else {
            predicate = this.criteriaBuilder.equal(root.get(column).as(String.class), value);
        }
        return this.addPredicate(predicate);
    }

    private Predicate addPredicate(Predicate predicate) {
        this.predicates.removeIf(pre -> pre.equals(predicate));
        this.predicates.add(predicate);
        return predicate;
    }

    public <V> Predicate notEqual(String column, V value) {
        if (column.contains(".")) {
            String[] columns = column.split("\\.");
            if (columns.length == 2)
                predicate = this.criteriaBuilder.notEqual(root.get(columns[0]).get(columns[1]), value);
            else
                predicate = this.criteriaBuilder.notEqual(root.get(column).as(String.class), value);
        } else {
            predicate = this.criteriaBuilder.notEqual(root.get(column).as(String.class), value);
        }
        return this.addPredicate(predicate);
    }

    public Predicate isActive(String column) {
        if (column.contains(".")) {
            String[] columns = column.split("\\.");
            if (columns.length == 2)
                predicate = this.criteriaBuilder.isTrue(root.get(columns[0]).get(columns[1]));
            else
                predicate = this.criteriaBuilder.isTrue(root.get(column));
        } else {
            predicate = this.criteriaBuilder.isTrue(root.get(column));
        }
        return this.addPredicate(predicate);
    }

    public Predicate isNotActive(String column) {
        if (column.contains(".")) {
            String[] columns = column.split("\\.");
            if (columns.length == 2)
                predicate = this.criteriaBuilder.equal(root.get(columns[0]).get(columns[1]), false);
            else
                predicate = this.criteriaBuilder.equal(root.get(column), false);
        } else {
            predicate = this.criteriaBuilder.equal(root.get(column), false);
        }
        return this.addPredicate(predicate);
    }

    public Predicate isDeleted(String column) {
        if (column.contains(".")) {
            String[] columns = column.split("\\.");
            if (columns.length == 2)
                predicate = this.criteriaBuilder.isTrue(root.get(columns[0]).get(columns[1]));
            else
                predicate = this.criteriaBuilder.isTrue(root.get(column));
        } else {
            predicate = this.criteriaBuilder.isTrue(root.get(column));
        }
        return this.addPredicate(predicate);
    }

    public Predicate isNotDeleted(String column) {
        if (column.contains(".")) {
            String[] columns = column.split("\\.");
            if (columns.length == 2)
                predicate = this.criteriaBuilder.equal(root.get(columns[0]).get(columns[1]), false);
            else
                predicate = this.criteriaBuilder.equal(root.get(column), false);
        } else {
            predicate = this.criteriaBuilder.equal(root.get(column), false);
        }
        return this.addPredicate(predicate);
    }

    public Predicate isNull(String column) {
        if (column.contains(".")) {
            String[] columns = column.split("\\.");
            if (columns.length == 2)
                predicate = this.criteriaBuilder.isNull(root.get(columns[0]).get(columns[1]));
            else
                predicate = this.criteriaBuilder.isNull(root.get(column));
        } else {
            predicate = this.criteriaBuilder.isNull(root.get(column));
        }
        return this.addPredicate(predicate);
    }

    public Predicate isNotNull(String column) {
        if (column.contains(".")) {
            String[] columns = column.split("\\.");
            if (columns.length == 2)
                predicate = this.criteriaBuilder.isNotNull(root.get(columns[0]).get(columns[1]));
            else
                predicate = this.criteriaBuilder.isNotNull(root.get(column));
        } else {
            predicate = this.criteriaBuilder.isNotNull(root.get(column));
        }
        return this.addPredicate(predicate);
    }

    public <V> Predicate in(String column, V... value) {
        if (column.contains(".")) {
            String[] columns = column.split("\\.");
            if (columns.length == 2)
                predicate = this.criteriaBuilder.and(root.get(columns[0]).get(columns[1]).in(value));
            else
                predicate = this.criteriaBuilder.and(root.get(column).in(value));
        } else {
            predicate = this.criteriaBuilder.and(root.get(column).in(value));
        }
        return this.addPredicate(predicate);
    }

    public BaseCriteria<R> join(String column, JoinType joinType) {
        root.join(column, joinType).alias(column);
        return this;
    }

    public <V> Predicate[] notIn(String column, V... value) {
        List<Predicate> pre = new ArrayList<>();
        for (V v : value) {
            predicate = this.criteriaBuilder.notEqual(root.get(column), v);
            this.addPredicate(predicate);
            pre.add(predicate);
        }
        return pre.toArray(new Predicate[0]);
    }

    public BaseCriteria<R> orderBy(String column, Direction order) {
        if (order.equals(Direction.ASC)) {
            this.criteriaQuery.orderBy(this.criteriaBuilder.asc(root.get(column)));
        } else if (order.equals(Direction.DESC)) {
            this.criteriaQuery.orderBy(this.criteriaBuilder.desc(root.get(column)));
        } else {
            this.criteriaQuery.orderBy(this.criteriaBuilder.asc(root.get(column)));
        }
        return this;
    }
}