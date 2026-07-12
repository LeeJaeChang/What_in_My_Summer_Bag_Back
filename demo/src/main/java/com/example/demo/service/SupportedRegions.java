package com.example.demo.service;

import java.util.Map;
import java.util.Optional;

// OpenWeatherMap 지오코딩이 한글 지역명은 인식 못 하고 로마자 지명만 인식해서,
// 서비스가 지원하는 지역만 화이트리스트로 관리하며 로마자 질의어로 변환한다.
// 여기 없는 지역명은 INVALID_REGION으로 처리한다.
//
// 모든 항목은 OpenWeatherMap Geocoding API(/geo/1.0/direct)에 실제로 질의해 좌표를 검증했다.
// 일부 군(郡) 지역은 시/군 이름 그대로 질의하면 동명의 다른 리(里)·동(洞)으로 잘못 매칭돼서
// (예: "Pyeongchang" → 강원 평창군이 아니라 서울 종로구 평창동으로 매칭),
// 실제 군청 소재지 읍 이름으로 질의어를 바꿔 정확한 좌표가 나오도록 했다.
// "고성"(강원/경남), "광주"(광역시/경기도 광주시)처럼 동명 지역은 괄호로 구분해 별도 키로 등록한다.
final class SupportedRegions {

    private static final Map<String, String> KOREAN_TO_QUERY = Map.ofEntries(
            // 특별시·광역시·특별자치시
            Map.entry("서울", "Seoul,KR"),
            Map.entry("부산", "Busan,KR"),
            Map.entry("대구", "Daegu,KR"),
            Map.entry("인천", "Incheon,KR"),
            Map.entry("광주", "Gwangju,KR"),
            Map.entry("대전", "Daejeon,KR"),
            Map.entry("울산", "Ulsan,KR"),
            Map.entry("세종", "Sejong,KR"),

            // 강원특별자치도
            Map.entry("춘천", "Chuncheon,KR"),
            Map.entry("원주", "Wonju,KR"),
            Map.entry("강릉", "Gangneung,KR"),
            Map.entry("속초", "Sokcho,KR"),
            Map.entry("동해", "Donghae,KR"),
            Map.entry("삼척", "Samcheok,KR"),
            Map.entry("태백", "Taebaek,KR"),
            Map.entry("정선", "Jeongseon,KR"),
            Map.entry("평창", "Pyeongchang-eup,KR"),
            Map.entry("홍천", "Hongcheon,KR"),
            Map.entry("횡성", "Hoengseong,KR"),
            Map.entry("영월", "Yeongwol,KR"),
            Map.entry("인제", "Inje,KR"),
            Map.entry("고성(강원)", "Ganseong-eup,KR"),
            Map.entry("양양", "Yangyang,KR"),
            Map.entry("철원", "Cheorwon,KR"),

            // 충청북도
            Map.entry("청주", "Cheongju,KR"),
            Map.entry("충주", "Chungju,KR"),
            Map.entry("제천", "Jecheon,KR"),
            Map.entry("단양", "Danyang,KR"),
            Map.entry("보은", "Boeun,KR"),
            Map.entry("옥천", "Okcheon-eup,KR"),
            Map.entry("영동", "Yeongdong-eup,KR"),
            Map.entry("진천", "Jincheon,KR"),
            Map.entry("괴산", "Goesan-eup,KR"),
            Map.entry("음성", "Eumseong,KR"),

            // 충청남도
            Map.entry("천안", "Cheonan,KR"),
            Map.entry("공주", "Gongju,KR"),
            Map.entry("보령", "Boryeong,KR"),
            Map.entry("아산", "Asan,KR"),
            Map.entry("서산", "Seosan,KR"),
            Map.entry("논산", "Nonsan,KR"),
            Map.entry("계룡", "Gyeryong,KR"),
            Map.entry("당진", "Dangjin,KR"),
            Map.entry("태안", "Taean,KR"),
            Map.entry("홍성", "Hongseong,KR"),
            Map.entry("예산", "Yesan,KR"),
            Map.entry("부여", "Buyeo,KR"),
            Map.entry("서천", "Seocheon-eup,KR"),
            Map.entry("청양", "Cheongyang-eup,KR"),

            // 전북특별자치도
            Map.entry("전주", "Jeonju,KR"),
            Map.entry("군산", "Gunsan,KR"),
            Map.entry("익산", "Iksan,KR"),
            Map.entry("정읍", "Jeongeup,KR"),
            Map.entry("남원", "Namwon,KR"),
            Map.entry("김제", "Gimje,KR"),
            Map.entry("완주", "Samnye-eup,KR"),
            Map.entry("진안", "Jinan-eup,KR"),
            Map.entry("무주", "Muju,KR"),
            Map.entry("장수", "Jangsu-eup,KR"),
            Map.entry("임실", "Imsil,KR"),
            Map.entry("순창", "Sunchang,KR"),
            Map.entry("고창", "Gochang,KR"),
            Map.entry("부안", "Buan-eup,KR"),

            // 전라남도
            Map.entry("목포", "Mokpo,KR"),
            Map.entry("여수", "Yeosu,KR"),
            Map.entry("순천", "Suncheon,KR"),
            Map.entry("나주", "Naju,KR"),
            Map.entry("광양", "Gwangyang,KR"),
            Map.entry("담양", "Damyang,KR"),
            Map.entry("곡성", "Gokseong,KR"),
            Map.entry("구례", "Gurye-eup,KR"),
            Map.entry("고흥", "Goheung,KR"),
            Map.entry("보성", "Boseong,KR"),
            Map.entry("화순", "Hwasun,KR"),
            Map.entry("장흥", "Jangheung-eup,KR"),
            Map.entry("강진", "Gangjin,KR"),
            Map.entry("해남", "Haenam,KR"),
            Map.entry("영암", "Yeongam,KR"),
            Map.entry("무안", "Muan-eup,KR"),
            Map.entry("함평", "Hampyeong,KR"),
            Map.entry("영광", "Yeonggwang,KR"),
            Map.entry("장성", "Jangseong,KR"),
            Map.entry("완도", "Wando,KR"),
            Map.entry("진도", "Jindo,KR"),
            Map.entry("신안", "Jido-eup,KR"),

            // 경상북도
            Map.entry("포항", "Pohang,KR"),
            Map.entry("경주", "Gyeongju,KR"),
            Map.entry("김천", "Gimcheon,KR"),
            Map.entry("안동", "Andong,KR"),
            Map.entry("구미", "Gumi,KR"),
            Map.entry("영주", "Yeongju,KR"),
            Map.entry("영천", "Yeongcheon,KR"),
            Map.entry("상주", "Sangju,KR"),
            Map.entry("문경", "Mungyeong,KR"),
            Map.entry("경산", "Gyeongsan,KR"),
            Map.entry("군위", "Gunwi,KR"),
            Map.entry("의성", "Uiseong,KR"),
            Map.entry("청송", "Cheongsong-eup,KR"),
            Map.entry("영양", "Yeongyang-eup,KR"),
            Map.entry("영덕", "Yeongdeok,KR"),
            Map.entry("청도", "Cheongdo-eup,KR"),
            Map.entry("고령", "Daegaya-eup,KR"),
            Map.entry("성주", "Seongju-eup,KR"),
            Map.entry("칠곡", "Waegwan-eup,KR"),
            Map.entry("예천", "Yecheon,KR"),
            Map.entry("봉화", "Bonghwa-eup,KR"),
            Map.entry("울진", "Uljin,KR"),
            Map.entry("울릉", "Ulleung,KR"),

            // 경상남도
            Map.entry("창원", "Changwon,KR"),
            Map.entry("진주", "Jinju,KR"),
            Map.entry("통영", "Tongyeong,KR"),
            Map.entry("사천", "Sacheon,KR"),
            Map.entry("김해", "Gimhae,KR"),
            Map.entry("밀양", "Miryang,KR"),
            Map.entry("거제", "Geoje,KR"),
            Map.entry("양산", "Yangsan,KR"),
            Map.entry("의령", "Uiryeong,KR"),
            Map.entry("함안", "Haman,KR"),
            Map.entry("창녕", "Changnyeong,KR"),
            Map.entry("고성(경남)", "Goseong-eup,KR"),
            Map.entry("남해", "Namhae,KR"),
            Map.entry("하동", "Hadong-eup,KR"),
            Map.entry("산청", "Sancheong,KR"),
            Map.entry("함양", "Hamyang,KR"),
            Map.entry("거창", "Geochang,KR"),
            Map.entry("합천", "Hapcheon,KR"),

            // 제주특별자치도
            Map.entry("제주", "Jeju,KR"),
            Map.entry("서귀포", "Seogwipo,KR"),

            // 경기도
            Map.entry("수원", "Suwon,KR"),
            Map.entry("성남", "Seongnam,KR"),
            Map.entry("고양", "Goyang,KR"),
            Map.entry("용인", "Yongin,KR"),
            Map.entry("부천", "Bucheon,KR"),
            Map.entry("안산", "Ansan,KR"),
            Map.entry("안양", "Anyang,KR"),
            Map.entry("남양주", "Namyangju,KR"),
            Map.entry("화성", "Hwaseong,KR"),
            Map.entry("평택", "Pyeongtaek,KR"),
            Map.entry("파주", "Paju,KR"),
            Map.entry("김포", "Gimpo,KR"),
            Map.entry("이천", "Icheon,KR"),
            Map.entry("가평", "Gapyeong,KR"),
            Map.entry("양평", "Yangpyeong,KR"),
            Map.entry("포천", "Pocheon,KR"),
            Map.entry("연천", "Yeoncheon,KR"),
            Map.entry("동두천", "Dongducheon,KR"),
            Map.entry("의정부", "Uijeongbu,KR"),
            Map.entry("광명", "Gwangmyeong,KR"),
            Map.entry("시흥", "Siheung,KR"),
            Map.entry("군포", "Gunpo,KR"),
            Map.entry("오산", "Osan,KR"),
            Map.entry("하남", "Hanam,KR"),
            Map.entry("여주", "Yeoju,KR"),
            Map.entry("광주(경기)", "Gwangju-si,Gyeonggi-do,KR")
    );

    private SupportedRegions() {
    }

    static Optional<String> toGeocodingQuery(String regionName) {
        return Optional.ofNullable(KOREAN_TO_QUERY.get(regionName));
    }
}
