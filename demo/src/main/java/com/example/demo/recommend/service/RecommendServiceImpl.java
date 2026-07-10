package com.example.demo.recommend.service;

import com.example.demo.recommend.dto.RecommendItemResponse;
import com.example.demo.recommend.dto.RecommendRequest;
import com.example.demo.recommend.dto.RecommendResponse;
import com.example.demo.recommend.dto.WeatherSummaryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendServiceImpl implements RecommendService{
    @Override
    public RecommendResponse recommend(
            RecommendRequest request
    ) {

        return new RecommendResponse(
                "temp-id",
                new WeatherSummaryResponse(
                        33,
                        10,
                        "맑음"
                ),
                List.of(
                        new RecommendItemResponse(
                                "0001",
                                "선크림",
                                true,
                                "자외선이 강해요."
                        )
                )
        );

    }





}
