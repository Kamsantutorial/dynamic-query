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

    <R extends BaseRepository<T, ID>> Page<T> findAllWithPageable(BaseCriteria<R> criteria, Pageable pageable);

    <R extends BaseRepository<T, ID>> Page<T> findAllWithPage(BaseCriteria<R> criteria);

    <R extends BaseRepository<T, ID>> Optional<T> findOneWithCriteria(BaseCriteria<R> criteria);

    <R extends BaseRepository<T, ID>> List<T> findAllWithCriteria(BaseCriteria<R> criteria);

    <R extends BaseRepository<T, ID>> Long countWithCriteria(BaseCriteria<R> criteria);

    <R extends BaseRepository<T, ID>> Long sumWithCriteria(BaseCriteria<R> criteria);

    <R extends BaseRepository<T, ID>> void deleteByCriteria(BaseCriteria<R> criteria);

    List<T> saveOrUpdateAll(Iterable<T> entities);

    T saveOrUpdate(T entity);

    void deleteById(ID id);

    EntityManager geEntityManager();

    Class<T> getEntity();

    Root<T> getRoot();

    CriteriaBuilder getCriteriaBuilder();

    CriteriaQuery<T> getCriteriaQuery();

}
