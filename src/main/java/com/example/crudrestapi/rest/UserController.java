package com.example.crudrestapi.rest;

import com.example.crudrestapi.dto.RequestPageableDTO;
import com.example.crudrestapi.dto.UserDTO;
import com.example.crudrestapi.mapper.UserMapper;
import com.example.crudrestapi.service.UserService;
import com.example.crudrestapi.vo.PageResponseVO;
import com.example.crudrestapi.vo.ResponseMessage;
import com.example.crudrestapi.vo.request.UserCreateRequestVO;
import com.example.crudrestapi.vo.request.UserRequestPageVO;
import com.example.crudrestapi.vo.request.UserUpdateRequestVO;
import com.example.crudrestapi.vo.response.UserResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * @author KAMSAN TUTORIAL
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/create")
    public ResponseMessage<Object> create(@RequestBody UserCreateRequestVO userCreateRequestVO) {
        log.info("userCreateRequestVO {}", userCreateRequestVO);
        UserDTO userDTO = new UserDTO();
        UserMapper.INSTANCE.copyRequestCreateVoToDto(userCreateRequestVO, userDTO);
        this.userService.create(userDTO);
        return new ResponseMessage<>()
                .success()
                .response();
    }

    @PostMapping("/update/{id}")
    public ResponseMessage<Object> update(@RequestBody UserUpdateRequestVO userUpdateRequestVO, @PathVariable Long id) {
        UserDTO userDTO = new UserDTO();
        UserMapper.INSTANCE.copyRequestUpdateVoToDto(userUpdateRequestVO, userDTO);
        this.userService.update(userDTO, id);
        return new ResponseMessage<>()
                .success()
                .response();
    }

    @GetMapping("/find-all-with-page")
    public ResponseMessage<PageResponseVO<UserResponseVO>> findAllWithPage(UserRequestPageVO requestPageVO) {
        UserDTO userDTO = new UserDTO();
        RequestPageableDTO requestPageableDTO = new RequestPageableDTO();
        UserMapper.INSTANCE.copyRequestPageVoToDto(requestPageVO, userDTO);
        UserMapper.INSTANCE.copyRequestPageVoToRequestPageDto(requestPageVO, requestPageableDTO);
        Page<UserDTO> list = this.userService.findAll(userDTO, requestPageableDTO);
        return new ResponseMessage<PageResponseVO<UserResponseVO>>()
                .body(new PageResponseVO<>(list.getTotalElements(), list.getTotalPages(), UserMapper.INSTANCE.copyDtoListToVoList(list.getContent()), requestPageVO))
                .success()
                .response();
    }

    @GetMapping("/find-all")
    public ResponseMessage<Object> findAll() {
        return new ResponseMessage<>()
                .body(UserMapper.INSTANCE.copyDtoListToVoList(this.userService.findAll()))
                .success()
                .response();
    }

    @GetMapping("/find-one/{id}")
    public ResponseMessage<Object> findOne(@PathVariable Long id) {
        return new ResponseMessage<>()
                .body(UserMapper.INSTANCE.copyDtoToResponseVo(this.userService.findOne(id)))
                .success()
                .response();
    }

    @PostMapping("/delete/{id}")
    public ResponseMessage<Object> delete(@PathVariable Long id) {
        this.userService.delete(id);
        return new ResponseMessage<>()
                .body(null)
                .success()
                .response();
    }

}
