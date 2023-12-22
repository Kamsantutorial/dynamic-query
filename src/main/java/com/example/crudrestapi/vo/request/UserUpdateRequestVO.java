package com.example.crudrestapi.vo.request;

import lombok.Data;
/**
 * @author KAMSAN TUTORIAL
 */
@Data
public class UserUpdateRequestVO {
    private String username;
    private String email;
    private String password;
}
