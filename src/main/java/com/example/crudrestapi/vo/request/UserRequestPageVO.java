package com.example.crudrestapi.vo.request;

import com.example.crudrestapi.vo.RequestPageableVO;
import lombok.Data;
/**
 * @author KAMSAN TUTORIAL
 */
@Data
public class UserRequestPageVO extends RequestPageableVO {
    private String username;
    private  String email;
}
