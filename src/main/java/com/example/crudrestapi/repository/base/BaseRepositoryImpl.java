package com.example.crudrestapi.repository.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.example.crudrestapi.repository.base.BaseCriteria;
import com.example.crudrestapi.repository.base.BaseRepository;

@NoRepositoryBean
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    private static final Logger logger = LogManager.getLogger(BaseRepositoryImpl.class);

    protected Class<T> entityClass;
    protected EntityManager entityManager;
    protected Root<T> root;
    protected CriteriaBuilder criteriaBuilder;
    protected CriteriaQuery<T> criteriaQuery;
    protected JpaEntityInformation<T, ?> entityInformation;

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityClass = domainClass;
        this.entityManager = em;
        this.criteriaBuilder = em.getCriteriaBuilder();
        this.criteriaQuery = this.criteriaBuilder.createQuery(domainClass);
        this.root = this.criteriaQuery.from(domainClass);
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(this.entityClass, this.entityManager);
    }

    @Override
    public EntityManager geEntityManager() {
        return this.entityManager;
    }

    @Override
    public Class<T> getEntity() {
        return this.entityClass;
    }

    @Override
    public Root<T> getRoot() {
        return this.root;
    }

    private <R extends BaseRepository<T, ID>> Predicate[] getPredicate(BaseCriteria<R> criteria) {
        return criteria.getPredicates().parallelStream().distinct().collect(Collectors.toList())
                .toArray(new Predicate[0]);
    }

    @Override
    public <R extends BaseRepository<T, ID>> Page<T> findAllWithPageable(BaseCriteria<R> criteria, Pageable pageable) {
        Predicate[] predicates = this.getPredicate(criteria);
        this.criteriaQuery.where(predicates).distinct(criteria.isDistinct());
        List<T> list = this.entityManager.createQuery(this.criteriaQuery).setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        CriteriaQuery<Long> cq = this.criteriaBuilder.createQuery(Long.class);
        Root<T> rootCount = cq.from(entityClass);
        this.countOrSumQueryJoins(rootCount);
        if (criteria.isDistinct())
            cq.select(this.criteriaBuilder.countDistinct(rootCount)).where(predicates)
                    .orderBy(this.criteriaQuery.getOrderList())
                    .groupBy(this.criteriaQuery.getGroupList());
        else
            cq.select(this.criteriaBuilder.count(rootCount)).where(predicates)
                    .orderBy(this.criteriaQuery.getOrderList())
                    .groupBy(this.criteriaQuery.getGroupList());
        long count = entityManager.createQuery(cq).getSingleResult();

        return new PageImpl<>(list, pageable, count);
    }

    @Override
    public <R extends BaseRepository<T, ID>> Page<T> findAllWithPage(BaseCriteria<R> criteria) {
        Predicate[] predicates = this.getPredicate(criteria);
        this.criteriaQuery.where(predicates).distinct(criteria.isDistinct());
        List<T> list = this.entityManager.createQuery(this.criteriaQuery).setFirstResult(criteria.getPage())
                .setMaxResults(criteria.getSize()).getResultList();

        CriteriaQuery<Long> cq = this.criteriaBuilder.createQuery(Long.class);
        Root<T> rootCount = cq.from(entityClass);
        this.countOrSumQueryJoins(rootCount);
        if (criteria.isDistinct()) {
            cq.select(this.criteriaBuilder.countDistinct(rootCount)).where(predicates)
                    .orderBy(this.criteriaQuery.getOrderList());
            // .groupBy(this.criteriaQuery.getGroupList());
        } else {
            cq.select(this.criteriaBuilder.count(rootCount)).where(predicates)
                    .orderBy(this.criteriaQuery.getOrderList());
            // .groupBy(this.criteriaQuery.getGroupList());
        }
        long count = entityManager.createQuery(cq).getSingleResult();

        return new PageImpl<>(list, PageRequest.ofSize(criteria.getSize()), count);
    }

    private BaseRepositoryImpl<T, ID> countOrSumQueryJoins(Root<T> rootCountOrSum) {
        this.root.getJoins().parallelStream().forEach(join -> {
            Join<T, ?> parent = rootCountOrSum.join(join.getAttribute().getName(), join.getJoinType());
            parent.alias(join.getAlias());
            getAllJoins(join, parent);
        });
        return this;
    }

    private void getAllJoins(Join<?, ?> join, Join<?, ?> parent) {
        if (!join.getJoins().isEmpty()) {
            join.getJoins().parallelStream().forEach(subJoin -> {
                Join<T, ?> subParent = parent.join(subJoin.getAttribute().getName(), subJoin.getJoinType());
                subParent.alias(subJoin.getAlias());
                getAllJoins(subJoin, subParent);
            });
        }
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return this.criteriaBuilder;
    }

    @Override
    public CriteriaQuery<T> getCriteriaQuery() {
        return this.criteriaQuery;
    }

    @Override
    public <R extends BaseRepository<T, ID>> Optional<T> findOneWithCriteria(BaseCriteria<R> criteria) {
        Predicate[] predicates = this.getPredicate(criteria);
        this.criteriaQuery.where(predicates).distinct(criteria.isDistinct());
        return this.entityManager.createQuery(this.criteriaQuery)
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public <R extends BaseRepository<T, ID>> List<T> findAllWithCriteria(BaseCriteria<R> criteria) {
        Predicate[] predicates = this.getPredicate(criteria);
        this.criteriaQuery.where(predicates).distinct(criteria.isDistinct());
        return this.entityManager.createQuery(this.criteriaQuery)
                .getResultList();
    }

    @Override
    public <R extends BaseRepository<T, ID>> void deleteByCriteria(BaseCriteria<R> criteria) {
        // create update
        CriteriaUpdate<T> update = this.criteriaBuilder.createCriteriaUpdate(this.entityClass);
        // perform update
        update.set("isDeleted", true);
        update.set("isActive", false);
        Predicate[] predicates = this.getPredicate(criteria);
        this.criteriaQuery.where(predicates);
        this.entityManager.createQuery(update).executeUpdate();
    }

    @Override
    public void deleteById(ID id) {
        // create update
        CriteriaUpdate<T> update = this.criteriaBuilder.createCriteriaUpdate(this.entityClass);
        // perform update
        update.set("isDeleted", true);
        update.set("isActive", false);
        this.criteriaQuery.where(this.criteriaBuilder.equal(root.get("id"), id));
        this.entityManager.createQuery(update).executeUpdate();
    }

    @Override
    public <R extends BaseRepository<T, ID>> Long countWithCriteria(BaseCriteria<R> criteria) {
        Predicate[] predicates = this.getPredicate(criteria);
        this.entityManager.createQuery(this.criteriaQuery);
        CriteriaQuery<Long> cq = this.criteriaBuilder.createQuery(Long.class);
        Root<T> rootCount = cq.from(entityClass);
        this.countOrSumQueryJoins(rootCount);
        cq.select(this.criteriaBuilder.count(rootCount)).where(predicates).distinct(criteria.isDistinct())
                .orderBy(this.criteriaQuery.getOrderList()).groupBy(this.criteriaQuery.getGroupList());
        return entityManager.createQuery(cq).getSingleResult();
    }

    @Transactional(readOnly = false)
    @Override
    public List<T> saveOrUpdateAll(Iterable<T> entities) {

        Assert.notNull(entities, "Entities must not be null!");

        List<T> result = new ArrayList<>();
        for (T entity : entities) {
            try {
                result.add(saveOrUpdate(entity));
            } catch (PersistenceException e) {
                logger.error("Error occurred while saving record: {}", e.getMessage());
                handleExceptionAndRollback(entity, e);
            }
        }
        return result;
    }

    @Transactional(readOnly = false)
    @Override
    public T saveOrUpdate(T entity) {

        Assert.notNull(entity, "Entity must not be null.");

        if (entityInformation.isNew(entity)) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }

    }

    private void handleExceptionAndRollback(T entity, PersistenceException e) {
        entityManager.detach(entity);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        logger.error("Error occurred while saving record: {}", e.getMessage());
    }

    @Override
    public <R extends BaseRepository<T, ID>> Long sumWithCriteria(BaseCriteria<R> criteria) {
        Predicate[] predicates = this.getPredicate(criteria);
        this.entityManager.createQuery(this.criteriaQuery);
        CriteriaQuery<Number> cq = this.criteriaBuilder.createQuery(Number.class);
        Root<T> rootSum = cq.from(entityClass);
        this.countOrSumQueryJoins(rootSum);

        Assert.notNull(criteria.getSumByColumn(), "Column sumByColumn must not be null.");

        cq.select(this.criteriaBuilder.sum(rootSum.get(criteria.getSumByColumn()))).where(predicates)
                .distinct(criteria.isDistinct()).orderBy(this.criteriaQuery.getOrderList())
                .groupBy(this.criteriaQuery.getGroupList());

        Number result = entityManager.createQuery(cq).getSingleResult();
        return (result != null) ? result.longValue() : 0L;
    }

}
