package com.example.demo.recommend.service;

import com.example.demo.recommend.client.AiRecommendClient;
import com.example.demo.recommend.client.WeatherClient;
import com.example.demo.recommend.dto.*;
import org.springframework.stereotype.Service;
import com.example.demo.icon.TdsPackingIcon;
import java.util.List;

@Service
public class RecommendServiceImpl implements RecommendService {
    private static final String DEFAULT_ICON_KEY = "u1F4E6";
    private final AiRecommendClient aiRecommendClient;
    private final WeatherClient weatherClient;

    public RecommendServiceImpl(
            AiRecommendClient aiRecommendClient,
            WeatherClient weatherClient
    ) {
        this.aiRecommendClient = aiRecommendClient;
        this.weatherClient = weatherClient;
    }

    @Override
    public RecommendResponse recommend(
            RecommendRequest request
    ) {
        WeatherSummaryResponse weather =
                weatherClient.getWeather(request);

        AiRecommendResult aiResult =
                aiRecommendClient.recommend(
                        request,
                        weather
                );
        List<RecommendItemResponse> packingItems =
                aiResult.packingItems()
                        .stream()
                        .map(item -> new RecommendItemResponse(
                                null,
                                item.name(),
                                item.category(),
                                validateIconKey(item.iconKey()),
                                item.reason(),
                                false,
                                item.sortOrder()
                        ))
                        .toList();
        return new RecommendResponse(
                1L,
                request.destination(),
                request.startDate(),
                request.endDate(),
                weather,
                request.activityTypes(),
                aiResult.travelTip(),
                packingItems
        );
    }
    private String validateIconKey(String iconKey) {
        return TdsPackingIcon.isValid(iconKey)
                ? iconKey
                : "u1F4E6";
    }
}