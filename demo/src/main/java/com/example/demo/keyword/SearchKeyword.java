package com.example.demo.keyword;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 구매 링크 조회 키워드 카탈로그. product_links 테이블에 시딩된 41개 키워드와 1:1로 대응한다.
 *
 * 검증 계층 전용: 엔티티(PackingItem.searchKeyword)에는 String으로 저장하며 이 enum을
 * @Enumerated로 쓰지 않는다. LLM이 생성한 searchKeyword가 실제 구매 링크가 존재하는
 * 키워드인지 검증하거나(from/isValid), 프롬프트에 넣을 허용 키워드 목록을 만드는 용도
 * (promptList).
 *
 * 여기 값과 V3__product_links.sql의 search_keyword가 어긋나면 조인이 비어 구매 링크가
 * 노출되지 않는다. 키워드를 추가·변경할 때는 반드시 마이그레이션과 함께 수정한다.
 * 그룹 주석은 원본 키워드 목록 기준 분류이며 PackingCategory와 완전히 일치하지는 않는다.
 */
public enum SearchKeyword {

    // --- SUN_PROTECTION ---
    SUNSCREEN("sunscreen"),
    SUN_HAT("sun hat"),
    COOLING_ARM_SLEEVES("cooling arm sleeves"),
    ALOE_VERA_GEL("aloe vera gel"),

    // --- WATER ---
    SWIMSUIT("swimsuit"),
    BEACH_TOWEL("beach towel"),
    WATERPROOF_PHONE_POUCH("waterproof phone pouch"),
    AQUA_SHOES("aqua shoes"),
    DRY_BAG("dry bag"),
    SNORKEL_SET("snorkel set"),

    // --- CLOTHING ---
    SOCKS("socks"),
    PAJAMAS("pajamas"),
    SANDALS("sandals"),
    LIGHTWEIGHT_SUMMER_TOP("lightweight summer top"),
    LIGHTWEIGHT_SUMMER_PANTS("lightweight summer pants"),
    WINDBREAKER_JACKET("windbreaker jacket"),

    // --- TOILETRIES ---
    TRAVEL_TOOTHBRUSH_TOILETRY_SET("travel toothbrush toiletry set"),
    TRAVEL_SKINCARE_SET("travel skincare set"),
    SHOWER_HEAD_FILTER("shower head filter"),
    PORTABLE_BIDET_WIPES("portable bidet wipes"),
    DISPOSABLE_TOILET_SEAT_COVER("disposable toilet seat cover"),
    TRAVEL_LAUNDRY_DETERGENT_SHEETS("travel laundry detergent sheets"),

    // --- HEALTH ---
    BANDAGES("bandages"),
    WATERPROOF_ANTISEPTIC_OINTMENT("waterproof antiseptic ointment"),
    MOSQUITO_REPELLENT_SPRAY("mosquito repellent spray"),
    MOSQUITO_REPELLENT_PATCH("mosquito repellent patch"),
    BED_BUG_SPRAY("bed bug spray"),

    // --- ELECTRONICS ---
    PHONE_CHARGER_CABLE("phone charger cable"),
    WIRELESS_EARBUDS("wireless earbuds"),
    MINI_POWER_BANK("mini power bank"),
    UNIVERSAL_TRAVEL_ADAPTER("universal travel adapter"),
    PORTABLE_MINI_FAN("portable mini fan"),
    THREE_IN_ONE_CHARGING_CABLE("3 in 1 charging cable"),

    // --- ETC ---
    ZIPLOCK_BAGS("ziplock bags"),
    UMBRELLA_PARASOL("umbrella parasol"),
    PACKING_CUBES("packing cubes"),
    TRAVEL_NECK_PILLOW("travel neck pillow"),
    SLEEP_MASK("sleep mask"),
    LUGGAGE_SCALE("luggage scale"),
    PHONE_LANYARD_STRAP("phone lanyard strap"),
    FOOTREST_HAMMOCK("footrest hammock");

    private final String keyword;

    SearchKeyword(String keyword) {
        this.keyword = keyword;
    }

    /** DB(product_links.search_keyword)에 저장된 실제 문자열 값. */
    public String keyword() {
        return keyword;
    }

    /** 문자열이 유효한 구매 링크 키워드이면 해당 상수를, 아니면 빈 Optional을 반환한다. */
    public static Optional<SearchKeyword> from(String keyword) {
        for (SearchKeyword value : values()) {
            if (value.keyword.equals(keyword)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public static boolean isValid(String keyword) {
        return from(keyword).isPresent();
    }

    /** LLM 프롬프트에 넣을 허용 키워드 목록. 이 목록 밖의 값은 저장 시 거부된다. */
    public static String promptList() {
        return Arrays.stream(values())
                .map(SearchKeyword::keyword)
                .collect(Collectors.joining(", "));
    }
}
