package com.example.crudrestapi.mapper;

import com.example.crudrestapi.dto.RequestPageableDTO;
import com.example.crudrestapi.dto.UserDTO;
import com.example.crudrestapi.entity.UserEntity;
import com.example.crudrestapi.vo.request.UserCreateRequestVO;
import com.example.crudrestapi.vo.request.UserRequestPageVO;
import com.example.crudrestapi.vo.request.UserUpdateRequestVO;
import com.example.crudrestapi.vo.response.UserResponseVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import java.util.List;

/**
 * @author KAMSAN TUTORIAL
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    void copyEntityToDto(UserEntity userEntity, @MappingTarget UserDTO userDTO);

    void copyDtoToEntity(UserDTO userDTO, @MappingTarget UserEntity userEntity);

    void copyRequestCreateVoToDto(UserCreateRequestVO userCreateRequestVO, @MappingTarget UserDTO userDTO);

    void copyRequestUpdateVoToDto(UserUpdateRequestVO userUpdateRequestVO, @MappingTarget UserDTO userDTO);

    List<UserDTO> copyEntityListToDtoList(List<UserEntity> list);

    List<UserResponseVO> copyDtoListToVoList(List<UserDTO> list);

    UserResponseVO copyDtoToResponseVo(UserDTO userDTO);

    void copyRequestPageVoToDto(UserRequestPageVO requestPageVO, @MappingTarget UserDTO userDTO);

    void copyRequestPageVoToRequestPageDto(UserRequestPageVO requestPageVO, @MappingTarget RequestPageableDTO requestPageableDTO);

    UserDTO map(UserEntity value);
}
