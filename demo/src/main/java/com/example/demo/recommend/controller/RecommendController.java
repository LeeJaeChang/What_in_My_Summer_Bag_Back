package com.example.demo.recommend.controller;

import com.example.demo.recommend.dto.RecommendRequest;
import com.example.demo.recommend.dto.RecommendResponse;
import com.example.demo.recommend.service.RecommendService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @PostMapping
    public RecommendResponse recommend(
            @Valid @RequestBody RecommendRequest request
    ) {
        return recommendService.recommend(request);
    }
}