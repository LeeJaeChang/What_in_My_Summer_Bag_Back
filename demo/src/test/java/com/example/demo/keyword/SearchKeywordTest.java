package com.example.demo.keyword;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.entity.PackingCategory;
import com.example.demo.entity.PackingItem;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * SearchKeyword 카탈로그가 실제 product_links 시드와 어긋나면 구매 링크 조인이 비어
 * 링크가 노출되지 않는다. enum과 마이그레이션이 함께 수정됐는지 여기서 못 박는다.
 */
class SearchKeywordTest {

    // INSERT VALUES의 각 행에서 첫 번째 문자열 리터럴(= search_keyword)만 뽑는다
    private static final Pattern SEEDED_ROW = Pattern.compile("^\\s*\\('([^']+)'", Pattern.MULTILINE);

    @Test
    void enum_값이_마이그레이션에_시딩된_search_keyword와_정확히_일치한다() throws Exception {
        Set<String> seeded = new LinkedHashSet<>();
        Matcher matcher = SEEDED_ROW.matcher(readMigration());
        while (matcher.find()) {
            seeded.add(matcher.group(1));
        }

        Set<String> catalog = Arrays.stream(SearchKeyword.values())
                .map(SearchKeyword::keyword)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        assertThat(seeded).hasSize(41);
        assertThat(catalog).isEqualTo(seeded);
    }

    @Test
    void 카탈로그에_없는_search_keyword는_저장할_수_없다() {
        PackingItem item = new PackingItem(null, "선글라스", PackingCategory.ETC, "reason", "icon", 0);

        assertThatThrownBy(() -> item.setSearchKeyword("sunglasses"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sunglasses");
    }

    @Test
    void 링크를_붙일_수_없는_준비물은_null로_둘_수_있다() {
        PackingItem item = new PackingItem(null, "여권", PackingCategory.DOCUMENTS, "reason", "icon", 0);

        item.setSearchKeyword(null);

        assertThat(item.getSearchKeyword()).isNull();
    }

    @Test
    void 프롬프트용_허용_목록은_모든_키워드를_포함한다() {
        String promptList = SearchKeyword.promptList();

        assertThat(promptList).contains("sunscreen", "3 in 1 charging cable", "footrest hammock");
        assertThat(promptList.split(", ")).hasSize(SearchKeyword.values().length);
    }

    private String readMigration() throws Exception {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("db/migration/V3__product_links.sql")) {
            assertThat(in).as("V3 마이그레이션을 클래스패스에서 찾을 수 없다").isNotNull();
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
