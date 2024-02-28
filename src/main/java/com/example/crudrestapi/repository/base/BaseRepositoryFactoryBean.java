package com.example.crudrestapi.repository.base;

import java.io.Serializable;
import javax.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import com.example.crudrestapi.entity.base.BaseEntity;
import lombok.NonNull;

public class BaseRepositoryFactoryBean<R extends JpaRepository<T, I>, T extends BaseEntity<T>, I extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, I> {

    public BaseRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    @NonNull
    protected RepositoryFactorySupport createRepositoryFactory(@NonNull EntityManager em) {
        return new BaseRepositoryFactory<>(em);
    }

    private static class BaseRepositoryFactory<T extends BaseEntity<I>, I extends Serializable>
            extends JpaRepositoryFactory {

        private final EntityManager em;

        public BaseRepositoryFactory(EntityManager em) {
            super(em);
            this.em = em;
        }

        @Override
        @SuppressWarnings("unchecked")
        @NonNull
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
                @NonNull EntityManager entityManager) {
            return new BaseRepositoryImpl<>((Class<T>) information.getDomainType(), em);
        }

        @Override
        @NonNull
        protected Class<?> getRepositoryBaseClass(@NonNull RepositoryMetadata metadata) {
            return BaseRepositoryImpl.class;
        }
        
    }
}
