package com.example.demo.icon;

import java.util.Optional;

/**
 * 준비물 → TDS 아이콘 키 매핑 카탈로그.
 *
 * 검증 계층 전용: 엔티티(PackingItem.iconKey)에는 String으로 저장하며 이 enum을
 * @Enumerated로 쓰지 않는다. LLM이 생성한 iconKey가 유효한 TDS 키인지 검증하거나
 * (from/isValid), 프롬프트에 넣을 허용 키 목록을 이 enum에서 생성하는 용도.
 *
 * 주석의 category 라벨은 아이콘 표 원문 기준 분류이며, 현재 PackingCategory enum과
 * 값이 완전히 일치하지 않는다(SUN_PROTECTION/WATER/HEALTH 없음, MEDICINE/GEAR 남음).
 * 여기서는 그룹핑 참고용 주석으로만 둔다.
 */
public enum TdsPackingIcon {

    // --- SUN_PROTECTION ---
    SUNSCREEN("u1F9F4"),        // 🧴 선크림, 로션, 선블록
    SUNGLASSES("u1F576"),       // 🕶️ 선글라스
    SUN_HAT("u1F452"),          // 👒 챙모자, 밀짚모자
    CAP("u1F9E2"),              // 🧢 볼캡, 캡모자
    PARASOL("u2602"),           // ☂️ 양산
    HAND_FAN("u1FAAD"),         // 🪭 부채, 휴대용 선풍기

    // --- WATER ---
    SWIMSUIT("u1FA71"),         // 🩱 수영복, 래시가드
    BIKINI("u1F459"),           // 👙 비키니
    FLIP_FLOPS("u1FA74"),       // 🩴 조리, 아쿠아슈즈, 슬리퍼
    GOGGLES("u1F97D"),          // 🥽 물안경, 수경
    LIFE_RING("u1F6DF"),        // 🛟 튜브, 구명조끼
    BEACH("u1F3D6"),            // 🏖️ 돗자리, 비치매트, 비치용품
    DRINK("u1F964"),            // 🥤 텀블러, 물병, 음료

    // --- CLOTHING ---
    TSHIRT("u1F455"),           // 👕 티셔츠, 상의
    SHORTS("u1FA73"),           // 🩳 반바지
    DRESS("u1F457"),            // 👗 원피스, 드레스
    SNEAKERS("u1F45F"),         // 👟 운동화
    SANDALS("u1F461"),          // 👡 샌들
    SOCKS("u1F9E6"),            // 🧦 양말
    OUTERWEAR("u1F9E5"),        // 🧥 겉옷, 바람막이, 가디건

    // --- TOILETRIES ---
    TOOTHBRUSH("u1FAA5"),       // 🪥 칫솔, 치약
    SOAP("u1F9FC"),             // 🧼 비누, 세면도구
    COSMETICS("u1F484"),        // 💄 화장품, 파우치, 메이크업
    TISSUE("u1F9FB"),           // 🧻 물티슈, 티슈

    // --- HEALTH ---
    MEDICINE("u1F48A"),         // 💊 상비약, 비상약
    BANDAGE("u1FA79"),          // 🩹 밴드, 반창고
    THERMOMETER("u1F321"),      // 🌡️ 체온계
    MASK("u1F637"),             // 😷 마스크
    INSECT_REPELLENT("u1F99F"), // 🦟 모기기피제, 벌레약

    // --- ELECTRONICS ---
    PHONE("u1F4F1"),            // 📱 휴대폰, 스마트폰
    POWER_BANK("u1F50B"),       // 🔋 보조배터리
    CHARGER("u1F50C"),          // 🔌 충전기, 어댑터, 케이블
    EARPHONES("u1F3A7"),        // 🎧 이어폰, 헤드폰
    CAMERA("u1F4F7"),           // 📷 카메라
    FLASHLIGHT("u1F526"),       // 🔦 손전등, 랜턴

    // --- DOCUMENTS ---
    ID_CARD("u1FAAA"),          // 🪪 신분증, 여권
    WALLET("u1F45B"),           // 👛 지갑
    CARD("u1F4B3"),             // 💳 카드, 신용카드
    CASH("u1F4B5"),             // 💵 현금
    KEY("u1F511"),              // 🔑 열쇠, 키
    TICKET("u1F3AB"),           // 🎫 티켓, 항공권, 입장권
    AIRPLANE("u2708"),          // ✈️ 비행기, 항공

    // --- ETC ---
    LUGGAGE("u1F9F3"),          // 🧳 캐리어, 여행가방
    BACKPACK("u1F392"),         // 🎒 백팩, 배낭
    UMBRELLA("u2614"),          // ☔ 우산
    TENT("u26FA"),              // ⛺ 텐트
    CAMPING("u1F3D5"),          // 🏕️ 캠핑장, 야외
    SNACK("u1F36A"),            // 🍪 간식, 군것질
    BOOK("u1F4D6"),             // 📖 가이드북, 책
    MAP("u1F5FA");              // 🗺️ 지도

    private final String assetKey;

    TdsPackingIcon(String assetKey) {
        this.assetKey = assetKey;
    }

    public String assetKey() {
        return assetKey;
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
}
