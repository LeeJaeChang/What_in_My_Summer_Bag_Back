package com.example.demo.entity;

import com.example.demo.keyword.SearchKeyword;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * 여행지·날짜·활동·날씨에 따라 생성된 준비물과 체크 상태. Trip : PackingItem = 1 : N.
 *
 * TODO: 준비물 목록은 추후 AI가 생성한다. 지금은 Trip 생성 시 비워두고,
 * AI 연동이 붙으면 그 결과를 이 엔티티로 저장하도록 채운다.
 */
@Entity
@Table(name = "packing_items")
public class PackingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private PackingCategory category;

    @Column(name = "reason")
    private String reason;

    @Column(name = "icon_key")
    private String iconKey;

    // product_links.search_keyword와 매핑되는 구매 링크 조회 키. AI 생성 시 함께 채운다.
    @Column(name = "search_keyword")
    private String searchKeyword;

    @Column(name = "checked", nullable = false)
    private boolean checked = false;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    protected PackingItem() {
    }

    public PackingItem(Trip trip, String name, PackingCategory category, String reason, String iconKey, int sortOrder) {
        this.trip = trip;
        this.name = name;
        this.category = category;
        this.reason = reason;
        this.iconKey = iconKey;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public Trip getTrip() {
        return trip;
    }

    public String getName() {
        return name;
    }

    public PackingCategory getCategory() {
        return category;
    }

    public String getReason() {
        return reason;
    }

    public String getIconKey() {
        return iconKey;
    }

    public void setIconKey(String iconKey) {
        this.iconKey = iconKey;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    /**
     * 구매 링크 조회 키를 설정한다. SearchKeyword 카탈로그에 없는 값은 product_links와
     * 조인되지 않아 링크가 비므로 거부한다. 링크를 붙일 수 없는 준비물은 null로 둔다
     * (구매 목록 조회에서 자연스럽게 제외된다).
     *
     * LLM 응답을 저장할 때는 SearchKeyword.isValid로 먼저 걸러 유효하지 않은 값은
     * null로 넘겨야 한다. 여기까지 잘못된 값이 오면 프롬프트/파싱 버그이므로 예외로 드러낸다.
     */
    public void setSearchKeyword(String searchKeyword) {
        if (searchKeyword != null && !SearchKeyword.isValid(searchKeyword)) {
            throw new IllegalArgumentException("유효하지 않은 searchKeyword: " + searchKeyword);
        }
        this.searchKeyword = searchKeyword;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
