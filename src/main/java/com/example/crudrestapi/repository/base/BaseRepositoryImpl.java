package com.example.crudrestapi.repository.base;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    protected Class<T> entityClass;
    protected EntityManager entityManager;
    protected Root<T> root;
    protected CriteriaBuilder criteriaBuilder;
    protected CriteriaQuery<T> criteriaQuery;

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityClass = domainClass;
        this.entityManager = em;
        this.criteriaBuilder = em.getCriteriaBuilder();
        this.criteriaQuery = this.criteriaBuilder.createQuery(domainClass);
        this.root = this.criteriaQuery.from(domainClass);
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
        return criteria.getPredicates().toArray(new Predicate[0]);
    }

    @Override
    public <R extends BaseRepository<T, ID>> Page<T> findAll(BaseCriteria<R> criteria, Pageable pageable) {
        Predicate[] predicates = this.getPredicate(criteria);
        this.criteriaQuery.where(predicates);
        List<T> list = this.entityManager.createQuery(this.criteriaQuery).setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize()).getResultList();

        CriteriaQuery<Long> cq = this.criteriaBuilder.createQuery(Long.class);
        cq.select(this.criteriaBuilder.count(cq.from(entityClass))).where(predicates);
        long count = entityManager.createQuery(cq).getSingleResult();

        return new PageImpl<>(list, pageable, count);
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
    public <R extends BaseRepository<T, ID>> Optional<T> findWithCriteria(BaseCriteria<R> criteria) {
        Predicate[] predicates = this.getPredicate(criteria);
        this.criteriaQuery.where(predicates);
        return this.entityManager.createQuery(this.criteriaQuery)
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public <R extends BaseRepository<T, ID>> List<T> findAllWithCriteria(BaseCriteria<R> criteria) {
        Predicate[] predicates = this.getPredicate(criteria);
        this.criteriaQuery.where(predicates);
        return this.entityManager.createQuery(this.criteriaQuery)
                .getResultList();
    }

    @Override
    public <R extends BaseRepository<T, ID>> void deleteByCriteria(BaseCriteria<R> criteria) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByCriteria'");
    }

    @Override
    public void deleteById() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

}
