package com.example.crudrestapi.vo.response;

import lombok.Data;

import java.util.Set;

/**
 * @author KAMSAN TUTORIAL
 */
@Data
public class UserResponseVO {
    private Long id;
    private String username;
    private String email;
    private String createdDate;
}
