package com.knu.subway.controller;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.service.StationInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@RequestMapping("/api/v1/subway-info")
@RestController
public class StationInfoApiController {
    private final StationInfoService stationInfoService;
    @GetMapping
    public List<StationInfo> getAllSubwayInfo() {
        return stationInfoService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<StationInfo> getSubwayInfoById(@PathVariable String id) {
        return stationInfoService.findById(id);
    }

    @PostMapping
    public StationInfo createSubwayInfo(@RequestBody StationInfo stationInfo) {
        return stationInfoService.save(stationInfo);
    }

    @PutMapping("/{id}")
    public StationInfo updateSubwayInfo(@PathVariable String id, @RequestBody StationInfo stationInfo) {
        // Ensure the ID in the path and in the request body match
        stationInfo.setStationId(id);
        return stationInfoService.save(stationInfo);
    }

    @DeleteMapping("/{id}")
    public void deleteSubwayInfo(@PathVariable String id) {
        stationInfoService.deleteById(id);
    }
}
