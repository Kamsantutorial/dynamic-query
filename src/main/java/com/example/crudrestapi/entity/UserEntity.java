package com.example.crudrestapi.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;
/**
 * @author KAMSAN TUTORIAL
 */
@Entity
@Table(name = "users")
@Setter
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    @Column(name = "created_date")
    private Date createdDate;
    private String status = "ACTIVE";
}
