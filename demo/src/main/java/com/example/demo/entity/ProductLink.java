package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// 준비물(검색 키워드)에 대응하는 구매 링크. search_keyword를 PK로,
// packing_items.search_keyword와 매핑해 1·2순위 브랜드/링크/이미지를 제공한다.
// 스키마와 시딩은 Flyway(V3__product_links.sql)가 관리한다.
@Entity
@Table(name = "product_links")
public class ProductLink {

    @Id
    @Column(name = "search_keyword")
    private String searchKeyword;

    @Column(name = "brand1_name", nullable = false)
    private String brand1Name;

    @Column(name = "link1_url", nullable = false)
    private String link1Url;

    @Column(name = "link1_image", nullable = false)
    private String link1Image;

    @Column(name = "brand2_name", nullable = false)
    private String brand2Name;

    @Column(name = "link2_url", nullable = false)
    private String link2Url;

    @Column(name = "link2_image", nullable = false)
    private String link2Image;

    protected ProductLink() {
    }

    public ProductLink(String searchKeyword,
                       String brand1Name, String link1Url, String link1Image,
                       String brand2Name, String link2Url, String link2Image) {
        this.searchKeyword = searchKeyword;
        this.brand1Name = brand1Name;
        this.link1Url = link1Url;
        this.link1Image = link1Image;
        this.brand2Name = brand2Name;
        this.link2Url = link2Url;
        this.link2Image = link2Image;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public String getBrand1Name() {
        return brand1Name;
    }

    public String getLink1Url() {
        return link1Url;
    }

    public String getLink1Image() {
        return link1Image;
    }

    public String getBrand2Name() {
        return brand2Name;
    }

    public String getLink2Url() {
        return link2Url;
    }

    public String getLink2Image() {
        return link2Image;
    }
}
