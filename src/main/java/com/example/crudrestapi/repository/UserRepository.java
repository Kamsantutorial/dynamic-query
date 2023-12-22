package com.example.crudrestapi.repository;

import com.example.crudrestapi.entity.UserEntity;
import com.example.crudrestapi.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;
/**
 * @author KAMSAN TUTORIAL
 */
@Repository
public interface UserRepository extends BaseRepository<UserEntity, Long> {
}
