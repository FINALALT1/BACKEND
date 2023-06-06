package kr.co.moneybridge.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageDTO<T> {
    private List<T> list;
    private long totalElements;
    private int totalPages;
    private int curPage;
    private Boolean first;
    private Boolean last;
    private Boolean empty;

    public PageDTO(List<T> content, Page<T> page) {
        this.list = content;
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.curPage = page.getNumber();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }
}
