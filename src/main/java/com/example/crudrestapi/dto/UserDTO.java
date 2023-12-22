package com.example.crudrestapi.dto;

import lombok.Data;

import java.util.Date;
/**
 * @author KAMSAN TUTORIAL
 */
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Date createdDate = new Date();
}
