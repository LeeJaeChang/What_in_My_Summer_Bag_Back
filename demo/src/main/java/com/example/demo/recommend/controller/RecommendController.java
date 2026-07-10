package com.example.demo.recommend.controller;

import com.example.demo.recommend.dto.RecommendItemResponse;
import com.example.demo.recommend.dto.RecommendRequest;
import com.example.demo.recommend.dto.RecommendResponse;
import com.example.demo.recommend.dto.WeatherSummaryResponse;
import com.example.demo.recommend.service.RecommendService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
@RestController
@RequestMapping("/recommend")
public class RecommendController {
    @PostMapping
    public RecommendResponse recommend(@RequestBody RecommendRequest request) {
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
                                "자외선이 강해요"
                        )
                )
        );
    }
}*/
@RestController
@RequestMapping("/recommend")
public class RecommendController {
    private final RecommendService recommendService;
    public RecommendController(RecommendService recommendService){
        this.recommendService = recommendService;
    }
    @PostMapping
    public RecommendResponse recommend(@RequestBody RecommendRequest request) {
        return recommendService.recommend(request);
    }

}