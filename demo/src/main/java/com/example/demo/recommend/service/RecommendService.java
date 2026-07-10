package com.example.demo.recommend.service;

import com.example.demo.recommend.dto.RecommendRequest;
import com.example.demo.recommend.dto.RecommendResponse;

public interface RecommendService {
    RecommendResponse recommend(
            RecommendRequest request
    );

}
