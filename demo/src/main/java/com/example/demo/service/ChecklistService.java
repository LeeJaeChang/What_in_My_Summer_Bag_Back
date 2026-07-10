package com.example.demo.service;

import com.example.demo.dto.ChecklistItemResponse;
import com.example.demo.dto.ChecklistResponse;
import com.example.demo.dto.ToggleChecklistItemResponse;
import com.example.demo.entity.Checklist;
import com.example.demo.entity.ChecklistItem;
import com.example.demo.entity.Recommendation;
import com.example.demo.repository.ChecklistItemRepository;
import com.example.demo.repository.ChecklistRepository;
import com.example.demo.repository.RecommendationRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final RecommendationRepository recommendationRepository;
    private final ObjectMapper objectMapper;

    public ChecklistService(ChecklistRepository checklistRepository,
                            ChecklistItemRepository checklistItemRepository,
                            RecommendationRepository recommendationRepository,
                            ObjectMapper objectMapper) {
        this.checklistRepository = checklistRepository;
        this.checklistItemRepository = checklistItemRepository;
        this.recommendationRepository = recommendationRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ChecklistResponse createChecklist(String userId, String recommendationId) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ChecklistNotFoundException("존재하지 않는 recommendation_id: " + recommendationId));
        if (!recommendation.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("다른 사용자의 추천 결과입니다.");
        }

        Checklist checklist = new Checklist(
                "chk_" + UUID.randomUUID().toString().substring(0, 10),
                recommendation.getUser(),
                recommendation
        );

        List<RecommendedItem> recommendedItems = parseRecommendedItems(recommendation.getRecommendedItems());

        for (int index = 0; index < recommendedItems.size(); index++) {
            RecommendedItem item = recommendedItems.get(index);
            checklist.getItems().add(new ChecklistItem(
                    "ci_" + UUID.randomUUID().toString().substring(0, 10),
                    checklist,
                    item.name(),
                    item.isEssential(),
                    false,
                    item.reason(),
                    index
            ));
        }

        checklistRepository.save(checklist);
        return toResponse(checklist);
    }

    @Transactional(readOnly = true)
    public ChecklistResponse getChecklist(String userId, String checklistId) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new ChecklistNotFoundException("존재하지 않는 checklist_id: " + checklistId));
        if (!checklist.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("다른 사용자의 체크리스트입니다.");
        }
        return toResponse(checklist);
    }

    @Transactional
    public ToggleChecklistItemResponse toggleItem(String userId, String checklistItemId, boolean checked) {
        ChecklistItem item = checklistItemRepository.findById(checklistItemId)
                .orElseThrow(() -> new ItemNotFoundException("존재하지 않는 checklist_item_id: " + checklistItemId));
        if (!item.getChecklist().getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("다른 사용자의 항목입니다.");
        }

        item.setChecked(checked);
        checklistItemRepository.save(item);

        return new ToggleChecklistItemResponse(
                item.getChecklistItemId(),
                item.isChecked(),
                readinessPercent(item.getChecklist().getItems())
        );
    }

    private List<RecommendedItem> parseRecommendedItems(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<RecommendedItem>>() {
            });
        } catch (JacksonException e) {
            // Jackson 3에서는 파싱 예외가 unchecked(JacksonException)지만, 메시지를 명확히 하려고 감싼다.
            throw new IllegalStateException("recommended_items JSON 파싱 실패: " + e.getMessage(), e);
        }
    }

    // TODO: "전체 항목 기준" vs "필수(is_essential) 항목 기준" — API 명세서 TODO 확정되면 여기만 바꾸면 됨.
    // 지금은 전체 항목 기준(단순 checked/total)으로 구현.
    private int readinessPercent(List<ChecklistItem> items) {
        if (items.isEmpty()) {
            return 0;
        }
        long checkedCount = items.stream().filter(ChecklistItem::isChecked).count();
        return (int) Math.round(100.0 * checkedCount / items.size());
    }

    private ChecklistResponse toResponse(Checklist checklist) {
        List<ChecklistItemResponse> itemResponses = new ArrayList<>();
        for (ChecklistItem it : checklist.getItems()) {
            itemResponses.add(new ChecklistItemResponse(it.getChecklistItemId(), it.getName(), it.isChecked()));
        }
        return new ChecklistResponse(
                checklist.getUser().getUserId(),
                checklist.getChecklistId(),
                checklist.getRecommendation().getRegionName(),
                itemResponses,
                readinessPercent(checklist.getItems())
        );
    }
}
