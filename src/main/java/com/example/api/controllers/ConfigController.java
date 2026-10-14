package com.example.api.controllers;

import com.example.index.utils.Config;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConfigController {

    public ConfigController() {
    }

    public static class FieldWeightRequest {
        public Integer fieldId;
        public Integer fieldWeight;

        public FieldWeightRequest() {}

    }


    @PostMapping("/updateWeights")
    public void setFieldWeight(@RequestBody List<FieldWeightRequest> fieldWeightRequests) {
        for (FieldWeightRequest fieldWeightRequest: fieldWeightRequests) {
            Config.setFieldWeight(fieldWeightRequest.fieldId, fieldWeightRequest.fieldWeight);
        }
    }
}
