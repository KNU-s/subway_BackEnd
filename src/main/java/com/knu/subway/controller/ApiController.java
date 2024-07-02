package com.knu.subway;

import com.knu.subway.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ApiController {
    private final ApiService apiService;
    @GetMapping("/test")
    public String test(){
        List<Dto> api = apiService.getSubwayArrivals("?");
        return api.toString();
    }
    @GetMapping("/subway")
    public List<Dto> getData(@RequestParam String stationName) {
        return apiService.getSubwayArrivals(stationName);
    }
}
