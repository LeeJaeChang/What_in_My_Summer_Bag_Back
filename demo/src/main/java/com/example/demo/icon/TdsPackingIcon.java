package com.example.demo.icon;

import com.example.demo.entity.PackingCategory;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 준비물 → TDS 아이콘 키 매핑 카탈로그.
 *
 * 검증 계층 전용: 엔티티(PackingItem.iconKey)에는 String으로 저장하며 이 enum을
 * @Enumerated로 쓰지 않는다. 세 가지 용도로 쓴다.
 *   1) LLM이 생성한 iconKey가 유효한 TDS 키인지 검증 (from/isValid)
 *   2) 프롬프트에 넣을 허용 키 카탈로그 생성 (promptList)
 *   3) LLM이 유효한 키를 못 주면 준비물 이름으로 재추론 (findByItemName)
 *
 * 각 상수의 category는 PackingCategory와 1:1로 대응한다. label은 그 아이콘이 가리키는
 * 준비물 이름들이며, 프롬프트 카탈로그와 이름 기반 재추론이 모두 이 값을 쓴다.
 * 아이콘을 추가·변경할 때 label을 비워두면 재추론에서 빠지므로 반드시 함께 채운다.
 */
public enum TdsPackingIcon {

    // --- SUN_PROTECTION ---
    SUNSCREEN("u1F9F4", PackingCategory.SUN_PROTECTION, "선크림, 로션, 선블록"),  // 🧴
    SUNGLASSES("u1F576", PackingCategory.SUN_PROTECTION, "선글라스"),  // 🕶️
    SUN_HAT("u1F452", PackingCategory.SUN_PROTECTION, "챙모자, 밀짚모자"),  // 👒
    CAP("u1F9E2", PackingCategory.SUN_PROTECTION, "볼캡, 캡모자"),  // 🧢
    PARASOL("u2602", PackingCategory.SUN_PROTECTION, "양산"),  // ☂️
    HAND_FAN("u1FAAD", PackingCategory.SUN_PROTECTION, "부채, 휴대용 선풍기"),  // 🪭

    // --- WATER ---
    SWIMSUIT("u1FA71", PackingCategory.WATER, "수영복, 래시가드"),  // 🩱
    BIKINI("u1F459", PackingCategory.WATER, "비키니"),  // 👙
    FLIP_FLOPS("u1FA74", PackingCategory.WATER, "조리, 아쿠아슈즈, 슬리퍼"),  // 🩴
    GOGGLES("u1F97D", PackingCategory.WATER, "물안경, 수경"),  // 🥽
    LIFE_RING("u1F6DF", PackingCategory.WATER, "튜브, 구명조끼"),  // 🛟
    BEACH("u1F3D6", PackingCategory.WATER, "돗자리, 비치매트, 비치용품"),  // 🏖️
    DRINK("u1F964", PackingCategory.WATER, "텀블러, 물병, 음료"),  // 🥤

    // --- CLOTHING ---
    TSHIRT("u1F455", PackingCategory.CLOTHING, "티셔츠, 상의"),  // 👕
    SHORTS("u1FA73", PackingCategory.CLOTHING, "반바지"),  // 🩳
    DRESS("u1F457", PackingCategory.CLOTHING, "원피스, 드레스"),  // 👗
    SNEAKERS("u1F45F", PackingCategory.CLOTHING, "운동화"),  // 👟
    SANDALS("u1F461", PackingCategory.CLOTHING, "샌들"),  // 👡
    SOCKS("u1F9E6", PackingCategory.CLOTHING, "양말"),  // 🧦
    OUTERWEAR("u1F9E5", PackingCategory.CLOTHING, "겉옷, 바람막이, 가디건"),  // 🧥

    // --- TOILETRIES ---
    TOOTHBRUSH("u1FAA5", PackingCategory.TOILETRIES, "칫솔, 치약"),  // 🪥
    SOAP("u1F9FC", PackingCategory.TOILETRIES, "비누, 세면도구"),  // 🧼
    COSMETICS("u1F484", PackingCategory.TOILETRIES, "화장품, 파우치, 메이크업"),  // 💄
    TISSUE("u1F9FB", PackingCategory.TOILETRIES, "물티슈, 티슈"),  // 🧻

    // --- HEALTH ---
    MEDICINE("u1F48A", PackingCategory.HEALTH, "상비약, 비상약"),  // 💊
    BANDAGE("u1FA79", PackingCategory.HEALTH, "밴드, 반창고"),  // 🩹
    THERMOMETER("u1F321", PackingCategory.HEALTH, "체온계"),  // 🌡️
    MASK("u1F637", PackingCategory.HEALTH, "마스크"),  // 😷
    INSECT_REPELLENT("u1F99F", PackingCategory.HEALTH, "모기기피제, 벌레약"),  // 🦟

    // --- ELECTRONICS ---
    PHONE("u1F4F1", PackingCategory.ELECTRONICS, "휴대폰, 스마트폰"),  // 📱
    POWER_BANK("u1F50B", PackingCategory.ELECTRONICS, "보조배터리"),  // 🔋
    CHARGER("u1F50C", PackingCategory.ELECTRONICS, "충전기, 어댑터, 케이블"),  // 🔌
    EARPHONES("u1F3A7", PackingCategory.ELECTRONICS, "이어폰, 헤드폰"),  // 🎧
    CAMERA("u1F4F7", PackingCategory.ELECTRONICS, "카메라"),  // 📷
    FLASHLIGHT("u1F526", PackingCategory.ELECTRONICS, "손전등, 랜턴"),  // 🔦

    // --- DOCUMENTS ---
    ID_CARD("u1FAAA", PackingCategory.DOCUMENTS, "신분증, 여권"),  // 🪪
    WALLET("u1F45B", PackingCategory.DOCUMENTS, "지갑"),  // 👛
    CARD("u1F4B3", PackingCategory.DOCUMENTS, "카드, 신용카드"),  // 💳
    CASH("u1F4B5", PackingCategory.DOCUMENTS, "현금"),  // 💵
    KEY("u1F511", PackingCategory.DOCUMENTS, "열쇠, 키"),  // 🔑
    TICKET("u1F3AB", PackingCategory.DOCUMENTS, "티켓, 항공권, 입장권"),  // 🎫
    AIRPLANE("u2708", PackingCategory.DOCUMENTS, "비행기, 항공"),  // ✈️

    // --- ETC ---
    LUGGAGE("u1F9F3", PackingCategory.ETC, "캐리어, 여행가방"),  // 🧳
    BACKPACK("u1F392", PackingCategory.ETC, "백팩, 배낭"),  // 🎒
    UMBRELLA("u2614", PackingCategory.ETC, "우산"),  // ☔
    TENT("u26FA", PackingCategory.ETC, "텐트"),  // ⛺
    CAMPING("u1F3D5", PackingCategory.ETC, "캠핑장, 야외"),  // 🏕️
    SNACK("u1F36A", PackingCategory.ETC, "간식, 군것질"),  // 🍪
    BOOK("u1F4D6", PackingCategory.ETC, "가이드북, 책"),  // 📖
    MAP("u1F5FA", PackingCategory.ETC, "지도");  // 🗺️

    private final String assetKey;
    private final PackingCategory category;
    private final String label;

    TdsPackingIcon(String assetKey, PackingCategory category, String label) {
        this.assetKey = assetKey;
        this.category = category;
        this.label = label;
    }

    public String assetKey() {
        return assetKey;
    }

    public PackingCategory category() {
        return category;
    }

    public String label() {
        return label;
    }

    /** 문자열 키가 유효한 TDS 준비물 아이콘 키이면 해당 상수를, 아니면 빈 Optional을 반환한다. */
    public static Optional<TdsPackingIcon> from(String assetKey) {
        for (TdsPackingIcon icon : values()) {
            if (icon.assetKey.equals(assetKey)) {
                return Optional.of(icon);
            }
        }
        return Optional.empty();
    }

    public static boolean isValid(String assetKey) {
        return from(assetKey).isPresent();
    }

    /**
     * LLM 프롬프트에 넣을 허용 아이콘 카탈로그. category별로 묶어 "키=준비물 이름들" 형태로 만든다.
     * 프롬프트와 enum이 어긋나면 LLM이 만든 iconKey가 전부 기본 아이콘으로 떨어지므로 여기서 생성한다.
     */
    public static String promptList() {
        Map<PackingCategory, String> grouped = Arrays.stream(values())
                .collect(Collectors.groupingBy(
                        TdsPackingIcon::category,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                icon -> "  %s = %s".formatted(icon.assetKey, icon.label),
                                Collectors.joining("\n"))));
        return grouped.entrySet().stream()
                .map(entry -> "[%s]\n%s".formatted(entry.getKey().name(), entry.getValue()))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 준비물 이름으로 아이콘을 재추론한다. LLM이 유효한 iconKey를 주지 못했을 때의 안전망이며,
     * label에 적힌 준비물 이름이 항목명에 포함되면 그 아이콘으로 본다. 공백은 무시한다.
     */
    public static Optional<TdsPackingIcon> findByItemName(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            return Optional.empty();
        }
        String normalized = itemName.replace(" ", "");
        for (TdsPackingIcon icon : values()) {
            for (String keyword : icon.label.split(",")) {
                String trimmed = keyword.replace(" ", "");
                if (!trimmed.isEmpty() && normalized.contains(trimmed)) {
                    return Optional.of(icon);
                }
            }
        }
        return Optional.empty();
    }
}
