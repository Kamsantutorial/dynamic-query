package com.example.crudrestapi.vo.request;

import lombok.Data;

/**
 * @author KAMSAN TUTORIAL
 */
@Data
public class UserCreateRequestVO {
    private String username;
    private String password;
    private String email;
}
