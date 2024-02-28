package com.example.crudrestapi.repository.base;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.Assert;

import lombok.Data;

@Data
public class BaseCriteria<R extends BaseRepository<?, ?>> {

    private static final String COLUMN_NAME_MUST_NOT_BE_NULL = "Column name must not be null.";

    private static final Logger logger = LogManager.getLogger(BaseCriteria.class);

    protected final Class<?> entityClass;
    protected final EntityManager entityManager;
    protected final Root<?> root;
    protected final CriteriaBuilder criteriaBuilder;
    protected final CriteriaQuery<?> criteriaQuery;
    protected String sumByColumn;
    protected final R repository;
    protected List<Predicate> predicates = new ArrayList<>();
    protected Predicate predicate;
    protected int size = 10; // DEFAUL PAGE SIZE 10
    protected int page = 1; // DEFAULT PAGE 1
    protected boolean isDistinct = false;

    public BaseCriteria(R repository) {
        this.repository = repository;
        this.entityManager = repository.geEntityManager();
        this.root = repository.getRoot();
        this.entityClass = repository.getEntity();
        this.criteriaBuilder = repository.getCriteriaBuilder();
        this.criteriaQuery = repository.getCriteriaQuery();
        this.predicate = this.isNotDeleted("isDeleted");
        /*
         * FIND PAGE OFFSET
         */
        this.page = (int) PageRequest.of(this.page - 1, this.size).getOffset();
    }

    public void distinct(boolean isDistinct) {
        this.setDistinct(isDistinct);
    }

    public <V> Predicate like(String column, V value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        String ivalue = String.format("%s%s%s", "%", value, "%");
        int lastIndex = column.split("\\.").length - 1;
        Join<?, ?> parent = this.getAllParent(column);
        if (Objects.nonNull(parent)) {
            predicate = this.criteriaBuilder.like(parent.get(column.split("\\.")[lastIndex]), ivalue);
        } else {
            predicate = this.criteriaBuilder.like(root.get(column).as(String.class), ivalue);
        }
        this.predicates.add(predicate);
        return predicate;
    }

    public <V> Predicate ilike(String column, V value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        String ivalue = String.format("%s%s%s", "%", value, "%").toLowerCase();
        int lastIndex = column.split("\\.").length - 1;
        Join<?, ?> parent = this.getAllParent(column);
        if (Objects.nonNull(parent)) {
            predicate = this.criteriaBuilder.like(parent.get(column.split("\\.")[lastIndex]), ivalue);
        } else {
            predicate = this.criteriaBuilder.like(root.get(column).as(String.class), ivalue);
        }
        this.predicates.add(predicate);
        return predicate;
    }

    public void size(int size) {
        this.setSize(size);
    }

    /*
     * FIND PAGE OFFSET
     */
    public void page(int pageOffset) {
        this.setPage(pageOffset);
    }

    public Predicate and(Predicate... predicates) {
        List<Predicate> predicates2 = new ArrayList<>();
        for (Predicate predict : predicates) {
            if (!Objects.isNull(predict)) {
                this.predicates.removeIf(pre -> pre.equals(predict));
                predicates2.add(this.criteriaBuilder.and(predict));
            }
        }
        if (!predicates2.isEmpty()) {
            this.predicate = this.criteriaBuilder.and(predicates2.toArray(new Predicate[0]));
            this.predicates.add(predicate);
        }
        return predicate;
    }

    public Predicate or(Predicate... predicates) {
        List<Predicate> predicates2 = new ArrayList<>();
        for (Predicate predict : predicates) {
            if (!Objects.isNull(predict)) {
                this.predicates.removeIf(pre -> pre.equals(predict));
                predicates2.add(this.criteriaBuilder.or(predict));
            }
        }
        if (!predicates2.isEmpty()) {
            this.predicate = this.criteriaBuilder.or(predicates2.toArray(new Predicate[0]));
            this.predicates.add(predicate);
        }
        return predicate;
    }

    public <V> Predicate equal(String column, V value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getAllParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent)) {
            predicate = this.criteriaBuilder.equal(parent.get(column.split("\\.")[lastIndex]),
                    value);
        } else {
            predicate = this.criteriaBuilder.equal(root.get(column).as(String.class), value);
        }
        return this.addPredicate(predicate);
    }

    public Predicate equal(String column, Boolean value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getAllParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent)) {
            predicate = this.criteriaBuilder.equal(parent.get(column.split("\\.")[lastIndex]),
                    value);
        } else {
            predicate = this.criteriaBuilder.equal(root.get(column), value);
        }
        return this.addPredicate(predicate);
    }

    public void sumBy(String column) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        Join<?, ?> parent = this.getAllParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent)) {
            sumByColumn = column.split("\\.")[lastIndex];
        } else {
            sumByColumn = column;
        }
    }

    public Predicate equal(String column, Date value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getAllParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent)) {
            predicate = this.criteriaBuilder.equal(parent.get(column.split("\\.")[lastIndex]), value);
        } else {
            predicate = this.criteriaBuilder.equal(root.get(column).as(String.class), value);
        }
        return this.addPredicate(predicate);
    }

    public Predicate between(String column, LocalDate start, LocalDate end) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(start) && Objects.isNull(end))
            return null;
        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.between(parent.get(column.split("\\.")[lastIndex]).as(LocalDate.class),
                    start, end);
        else
            predicate = this.criteriaBuilder.between(root.get(column).as(LocalDate.class), start, end);
        return this.addPredicate(predicate);
    }

    public Predicate between(String column, Date start, Date end) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(start) && Objects.isNull(end))
            return null;
        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.between(parent.get(column.split("\\.")[lastIndex]),
                    start, end);
        else
            predicate = this.criteriaBuilder.between(root.get(column), start, end);
        return this.addPredicate(predicate);
    }

    public Predicate between(String column, Long start, Long end) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(start) && Objects.isNull(end))
            return null;
        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.between(parent.get(column.split("\\.")[lastIndex]), start, end);
        else
            predicate = this.criteriaBuilder.between(root.get(column), start, end);
        return this.addPredicate(predicate);
    }

    public Predicate between(String column, Double start, Double end) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(start) && Objects.isNull(end))
            return null;
        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.between(parent.get(column.split("\\.")[lastIndex]), start, end);
        else
            predicate = this.criteriaBuilder.between(root.get(column), start, end);
        return this.addPredicate(predicate);
    }

    public Predicate between(String column, Float start, Float end) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(start) && Objects.isNull(end))
            return null;
        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.between(parent.get(column.split("\\.")[lastIndex]), start, end);
        else
            predicate = this.criteriaBuilder.between(root.get(column), start, end);
        return this.addPredicate(predicate);
    }

    public Predicate greaterThan(String column, Long value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.greaterThan(parent.get(column.split("\\.")[lastIndex]), value);
        else
            predicate = this.criteriaBuilder.greaterThan(root.get(column), value);
        return this.addPredicate(predicate);
    }

    public Predicate greaterThan(String column, Integer value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.greaterThan(parent.get(column.split("\\.")[lastIndex]), value);
        else
            predicate = this.criteriaBuilder.greaterThan(root.get(column), value);
        return this.addPredicate(predicate);
    }

    public Predicate greaterThan(String column, Date value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.greaterThan(parent.get(column.split("\\.")[lastIndex]), value);
        else
            predicate = this.criteriaBuilder.greaterThan(root.get(column), value);
        return this.addPredicate(predicate);
    }

    public Predicate greaterThanOrEqual(String column, Date value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.greaterThanOrEqualTo(parent.get(column.split("\\.")[lastIndex]), value);
        else
            predicate = this.criteriaBuilder.greaterThanOrEqualTo(root.get(column), value);
        return this.addPredicate(predicate);
    }

    public Predicate lessThan(String column, Date value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.lessThan(parent.get(column.split("\\.")[lastIndex]), value);
        else
            predicate = this.criteriaBuilder.lessThan(root.get(column), value);
        return this.addPredicate(predicate);
    }

    public Predicate lessThanOrEqual(String column, Date value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.lessThanOrEqualTo(parent.get(column.split("\\.")[lastIndex]), value);
        else
            predicate = this.criteriaBuilder.lessThanOrEqualTo(root.get(column), value);
        return this.addPredicate(predicate);
    }

    private Predicate addPredicate(Predicate predicate) {
        this.predicates.removeIf(pre -> pre.equals(predicate));
        this.predicates.add(predicate);
        return predicate;
    }

    public <V> Predicate notEqual(String column, V value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.notEqual(parent.get(column.split("\\.")[lastIndex]), value);
        else
            predicate = this.criteriaBuilder.notEqual(root.get(column).as(String.class), value);
        return this.addPredicate(predicate);
    }

    public Predicate isActive(String column) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.isTrue(parent.get(column.split("\\.")[lastIndex]));
        else
            predicate = this.criteriaBuilder.isTrue(root.get(column));
        return this.addPredicate(predicate);
    }

    public Predicate isNotActive(String column) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.equal(parent.get(column.split("\\.")[lastIndex]), false);
        else
            predicate = this.criteriaBuilder.equal(root.get(column), false);
        return this.addPredicate(predicate);
    }

    public Predicate isDeleted(String column) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        Join<?, ?> parent = this.getParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.isTrue(parent.get(column.split("\\.")[lastIndex]));
        else
            predicate = this.criteriaBuilder.isTrue(root.get(column));
        return this.addPredicate(predicate);
    }

    public Predicate isNotDeleted(String column) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        Join<?, ?> parent = this.getAllParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.equal(parent.get(column.split("\\.")[lastIndex]),
                    false);
        else
            predicate = this.criteriaBuilder.equal(root.get(column), false);
        return this.addPredicate(predicate);
    }

    public Predicate isNull(String column) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        Join<?, ?> parent = this.getAllParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.isNull(parent.get(column.split("\\.")[lastIndex]));
        else
            predicate = this.criteriaBuilder.isNull(root.get(column));
        return this.addPredicate(predicate);
    }

    public Predicate isNotNull(String column) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        Join<?, ?> parent = this.getAllParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.isNotNull(parent.get(column.split("\\.")[lastIndex]));
        else
            predicate = this.criteriaBuilder.isNotNull(root.get(column));
        return this.addPredicate(predicate);
    }

    public Predicate in(String column, Object... value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getAllParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.and(parent.get(column.split("\\.")[lastIndex]).in(value));
        else
            predicate = this.criteriaBuilder.and(root.get(column).in(value));
        return this.addPredicate(predicate);
    }

    public Predicate in(String column, List<Long> value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getAllParent(column);
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.and(parent.get(column.split("\\.")[1]).in(value));
        else
            predicate = this.criteriaBuilder.and(root.get(column).in(value));
        return this.addPredicate(predicate);
    }

    public BaseCriteria<R> join(String column, JoinType joinType) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        Join<?, ?> parent = this.getAllParent(column);

        if (Objects.nonNull(parent)) {
            int lastIndex = column.split("\\.").length - 1;
            if (!Objects.equals(column.split("\\.")[lastIndex], column)) {
                Join<?, ?> join = findSubJoin(parent, column.split("\\.")[lastIndex]);
                if (Objects.isNull(join)) {
                    join = parent.join(column.split("\\.")[lastIndex], joinType);
                    join.alias(column.split("\\.")[lastIndex]);
                }
            }
        } else {
            Join<?, ?> join = root.join(column, joinType);
            join.alias(column);
        }

        return this;
    }

    public BaseCriteria<R> fetch(String column, JoinType joinType) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        Join<?, ?> parent = this.getParent(column);
        if (Objects.nonNull(parent)) {
            int lastIndex = column.split("\\.").length - 1;
            Join<?, ?> join = findJoin(column.split("\\.")[lastIndex]);
            if (Objects.isNull(join)) {
                join = (Join<?, ?>) root.fetch(column.split("\\.")[lastIndex], joinType);
                join.alias(column.split("\\.")[lastIndex]);
            }
        } else {
            Join<?, ?> join = findJoin(column);
            if (Objects.isNull(join)) {
                join = (Join<?, ?>) root.fetch(column, joinType);
                join.alias(column);
            }
        }

        return this;
    }

    private Join<?, ?> getParent(String column) {
        String[] cols = column.split("\\.");
        if (cols.length == 2) {
            return findJoin(cols[0]);
        }
        if (cols.length == 1) {
            return findJoin(column);
        }
        throw new IllegalArgumentException("INVALID_COLUMN");
    }

    private Join<?, ?> getAllParent(String column) {
        String[] cols = column.split("\\.");
        if (cols.length == 3) {
            return findAllJoin(root, cols[1]);
        }
        if (cols.length == 2) {
            return findAllJoin(root, cols[0]);
        }
        if (cols.length == 1) {
            return findAllJoin(root, column);
        }
        throw new IllegalArgumentException("INVALID_COLUMN");
    }

    private Join<?, ?> findSubJoin(Join<?, ?> join, String column) {
        Join<?, ?> result = join.getJoins()
                .parallelStream()
                .filter(subJoin -> subJoin.getAttribute().getName().equals(column) || subJoin.getAlias().equals(column))
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(result))
            return result;
        result = (Join<?, ?>) join.getFetches()
                .parallelStream()
                .filter(subJoin -> subJoin.getAttribute().getName().equals(column)
                        || ((Join<?, ?>) subJoin).getAlias().equals(column))
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(result))
            return result;

        return null;
    }

    private <T> Join<?, ?> findAllJoin(Root<T> join, String column) {
        Join<?, ?> result = join.getJoins().parallelStream().map(parent -> {
            if (parent.getAttribute().getName().equals(column) || parent.getAlias().equals(column)) {
                return parent;
            } else {
                return parent.getJoins()
                        .parallelStream()
                        .filter(subJoin -> subJoin.getAttribute().getName().equals(column)
                                || subJoin.getAlias().equals(column))
                        .findFirst()
                        .orElse(null);
            }

        }).filter(Objects::nonNull).findFirst()
                .orElse(null);

        if (Objects.nonNull(result))
            return result;

        result = (Join<?, ?>) join.getFetches()
                .parallelStream()
                .filter(subJoin -> subJoin.getAttribute().getName().equals(column)
                        || ((Join<?, ?>) subJoin).getAlias().equals(column))
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(result))
            return result;

        return null;
    }

    private <T> Join<?, ?> findSubJoin(Root<T> join, String column) {
        Join<?, ?> result = join.getJoins()
                .parallelStream()
                .filter(subJoin -> subJoin.getAttribute().getName().equals(column) || subJoin.getAlias().equals(column))
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(result))
            return result;

        result = (Join<?, ?>) join.getFetches()
                .parallelStream()
                .filter(subJoin -> subJoin.getAttribute().getName().equals(column)
                        || ((Join<?, ?>) subJoin).getAlias().equals(column))
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(result))
            return result;

        return null;
    }

    private Join<?, ?> findJoin(String column) {

        Join<?, ?> result = findSubJoin(root, column);

        if (Objects.nonNull(result))
            return result;

        return null;
    }

    public Predicate[] notIn(String column, List<Long> value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return new Predicate[0];
        Join<?, ?> parent = this.getAllParent(column);
        List<Predicate> pre = new ArrayList<>();
        for (Long v : value) {
            if (Objects.nonNull(parent))
                predicate = this.criteriaBuilder.notEqual(parent.get(column), v);
            else
                predicate = this.criteriaBuilder.notEqual(root.get(column), v);
            this.addPredicate(predicate);
            pre.add(predicate);
        }
        return pre.toArray(new Predicate[0]);
    }

    public Predicate not(String column, List<String> value) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        if (Objects.isNull(value))
            return null;
        Join<?, ?> parent = this.getAllParent(column);
        int lastIndex = column.split("\\.").length - 1;
        if (Objects.nonNull(parent))
            predicate = this.criteriaBuilder.not(parent.get(column.split("\\.")[lastIndex]).in(value));
        else
            predicate = this.criteriaBuilder.not(root.get(column).in(value));
        return this.addPredicate(predicate);
    }

    public BaseCriteria<R> orderBy(String column, Direction order) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        Join<?, ?> parent = this.getAllParent(column);
        if (Objects.nonNull(parent)) {
            if (order.equals(Direction.ASC)) {
                this.criteriaQuery.orderBy(this.criteriaBuilder.asc(parent.get(column)));
            } else if (order.equals(Direction.DESC)) {
                this.criteriaQuery.orderBy(this.criteriaBuilder.desc(parent.get(column)));
            } else {
                this.criteriaQuery.orderBy(this.criteriaBuilder.asc(parent.get(column)));
            }
        } else {
            if (order.equals(Direction.ASC)) {
                this.criteriaQuery.orderBy(this.criteriaBuilder.asc(root.get(column)));
            } else if (order.equals(Direction.DESC)) {
                this.criteriaQuery.orderBy(this.criteriaBuilder.desc(root.get(column)));
            } else {
                this.criteriaQuery.orderBy(this.criteriaBuilder.asc(root.get(column)));
            }
        }
        return this;
    }

    public BaseCriteria<R> orderBy(Direction order, String... columns) {
        Assert.notNull(columns, COLUMN_NAME_MUST_NOT_BE_NULL);

        List<Order> orders = new ArrayList<>();
        for (String column : columns) {
            Join<?, ?> parent = this.getAllParent(column);
            if (Objects.nonNull(parent)) {
                if (order.equals(Direction.ASC)) {
                    orders.add(this.criteriaBuilder.asc(parent.get(column)));
                } else if (order.equals(Direction.DESC)) {
                    orders.add(this.criteriaBuilder.desc(parent.get(column)));
                } else {
                    orders.add(this.criteriaBuilder.asc(parent.get(column)));
                }
            } else {
                if (order.equals(Direction.ASC)) {
                    orders.add(this.criteriaBuilder.asc(root.get(column)));
                } else if (order.equals(Direction.DESC)) {
                    orders.add(this.criteriaBuilder.desc(root.get(column)));
                } else {
                    orders.add(this.criteriaBuilder.asc(root.get(column)));
                }
            }
        }
        this.criteriaQuery.orderBy(orders);
        return this;
    }

    public BaseCriteria<R> groupBy(String column) {
        Assert.notNull(column, COLUMN_NAME_MUST_NOT_BE_NULL);

        Join<?, ?> parent = this.getAllParent(column);
        if (Objects.nonNull(parent)) {
            this.criteriaQuery.groupBy(parent.get(column));
        } else {
            this.criteriaQuery.groupBy(root.get(column));
        }
        return this;
    }

    public BaseCriteria<R> groupBy(String... columns) {
        Assert.notNull(columns, COLUMN_NAME_MUST_NOT_BE_NULL);

        List<Expression<?>> paths = new ArrayList<>();
        for (String column : columns) {
            Join<?, ?> parent = this.getAllParent(column);
            if (Objects.nonNull(parent)) {
                paths.add(parent.get(column));
            } else {
                paths.add(root.get(column));
            }
        }
        this.criteriaQuery.groupBy(paths);
        return this;
    }
}
