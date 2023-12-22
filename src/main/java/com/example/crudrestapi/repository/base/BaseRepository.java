package com.example.crudrestapi.repository.base;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    <R extends BaseRepository<T, ID>> Page<T> findAll(BaseCriteria<R> criteria, Pageable pageable);

    <R extends BaseRepository<T, ID>> Optional<T> findWithCriteria(BaseCriteria<R> criteria);

    <R extends BaseRepository<T, ID>> List<T> findAllWithCriteria(BaseCriteria<R> criteria);

    <R extends BaseRepository<T, ID>> void deleteByCriteria(BaseCriteria<R> criteria);

    void deleteById();

    EntityManager geEntityManager();

    Class<T> getEntity();

    Root<T> getRoot();

    CriteriaBuilder getCriteriaBuilder();

    CriteriaQuery<T> getCriteriaQuery();

}