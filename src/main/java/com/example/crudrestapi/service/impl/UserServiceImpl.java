package com.example.crudrestapi.service.impl;

import com.example.crudrestapi.dto.RequestPageableDTO;
import com.example.crudrestapi.dto.UserDTO;
import com.example.crudrestapi.entity.UserEntity;
import com.example.crudrestapi.mapper.UserMapper;
import com.example.crudrestapi.repository.UserRepository;
import com.example.crudrestapi.repository.base.BaseCriteria;
import com.example.crudrestapi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author KAMSAN TUTORIAL
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void create(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        UserMapper.INSTANCE.copyDtoToEntity(userDTO, userEntity);
        userRepository.save(userEntity);
        UserMapper.INSTANCE.copyEntityToDto(userEntity, userDTO);
    }

    @Override
    public void update(UserDTO userDTO, Long id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        if (Objects.nonNull(userEntity)) {
            UserMapper.INSTANCE.copyDtoToEntity(userDTO, userEntity);
            userRepository.save(userEntity);
        }
    }

    @Override
    public void delete(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        if (Objects.nonNull(userEntity)) {
            userEntity.setStatus("DELETED");
            userRepository.save(userEntity);
        }
    }

    @Override
    public UserDTO findOne(Long id) {
        UserDTO userDTO = new UserDTO();
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        UserMapper.INSTANCE.copyEntityToDto(userEntity, userDTO);
        return userDTO;
    }

    @Override
    public Page<UserDTO> findAll(UserDTO userDTO, RequestPageableDTO requestPageableDTO) {
        BaseCriteria<UserRepository> criteria = new BaseCriteria<>(userRepository);
        criteria.equal("status", "ACTIVE");
        criteria.or(
                criteria.like("username", userDTO.getUsername()),
                criteria.like("email", userDTO.getEmail())
        );
        Page<UserEntity> list = this.userRepository.findAll(criteria, Pageable.ofSize(requestPageableDTO.getLimit()));
        List<UserDTO> items = UserMapper.INSTANCE.copyEntityListToDtoList(list.getContent());
        return new PageImpl<>(items, list.getPageable(), list.getTotalElements());
    }

    @Override
    public List<UserDTO> findAll() {
        UserEntity userEntity = new UserEntity();
        userEntity.setStatus("ACTIVE");
        userEntity.setCreatedDate(null);
        List<UserEntity> entityList = userRepository.findAll(Example.of(userEntity));
        log.info("entityList {}", entityList);
        return UserMapper.INSTANCE.copyEntityListToDtoList(entityList);
    }
}
