package com.example.crudrestapi.dto;

import lombok.Data;
import org.springframework.data.domain.Sort;
/**
 * @author KAMSAN TUTORIAL
 */
@Data
public class RequestPageableDTO {
    private int page;
    private int limit;
    private String[] orderBy;
    private Sort.Direction direction;
}
