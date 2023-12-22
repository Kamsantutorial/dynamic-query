package com.example.crudrestapi.vo;

import lombok.Data;

import java.util.List;
/**
 * @author KAMSAN TUTORIAL
 */
@Data
public class PageResponseVO<T> {
    private Long totalRecords;
    private int page;
    private int totalPages;
    private List<T> content;

    public PageResponseVO(Long totalRecords, int totalPages, List<T> content, RequestPageableVO requestPageableVO) {
        this.totalRecords = totalRecords;
        this.content = content;
        this.page = requestPageableVO.getPage();
        this.totalPages = totalPages;
    }
}
