-- 준비물 검색 키워드별 구매 링크. search_keyword를 PK로, packing_items.search_keyword와 매핑한다.
-- 1·2순위 브랜드명/구매 링크/이미지를 각각 NOT NULL로 저장한다.

CREATE TABLE product_links (
    search_keyword  VARCHAR(100) PRIMARY KEY,
    brand1_name     VARCHAR(100) NOT NULL,
    link1_url       VARCHAR(500) NOT NULL,
    link1_image     VARCHAR(500) NOT NULL,
    brand2_name     VARCHAR(100) NOT NULL,
    link2_url       VARCHAR(500) NOT NULL,
    link2_image     VARCHAR(500) NOT NULL
);
