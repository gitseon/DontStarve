package com.example.dontstarve.config;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PagingRes {
    private boolean hasNext;
    private int currentPage;
    private int startPage = 0;
    private int endPage;
    private int size; // 한 페이지에 출력된 데이터 개수
    private int numberOfElements; // 현재 페이지의 데이터 개수
    private long totalElements; // 총 데이터 개수

}
