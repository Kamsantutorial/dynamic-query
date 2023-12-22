package com.example.crudrestapi.service;

import com.example.crudrestapi.dto.RequestPageableDTO;
import com.example.crudrestapi.dto.UserDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    void create(UserDTO userDTO);
    void update(UserDTO userDTO, Long id);
    void delete(Long id);
    UserDTO findOne(Long id);
    Page<UserDTO> findAll(UserDTO userDTO, RequestPageableDTO requestPageableDTO);
    List<UserDTO> findAll();
}
