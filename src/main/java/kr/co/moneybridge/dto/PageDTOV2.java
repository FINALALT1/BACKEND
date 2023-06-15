package kr.co.moneybridge.dto;

import kr.co.moneybridge.model.user.UserPropensity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter @Setter
public class PageDTOV2<T> {
    private List<T> list;
    private long totalElements;
    private int totalPages;
    private int curPage;
    private Boolean first;
    private Boolean last;
    private Boolean empty;
    private UserPropensity userPropensity;

    public PageDTOV2(List<T> list, Page<T> page) {
        this.list = list;
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.curPage = page.getNumber();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }
}

