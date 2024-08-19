package com.knu.subway.controller;

import com.knu.subway.entity.StationInfo;
import com.knu.subway.service.StationInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/v1/station-info")
@RestController
public class StationInfoApiController {
    private final StationInfoService stationInfoService;

    @GetMapping
    public List<StationInfo> getAllSubwayInfo() {
        return stationInfoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationInfo> getSubwayInfoById(@PathVariable String id) {
        Optional<StationInfo> stationInfo = stationInfoService.findById(id);
        return stationInfo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StationInfo> createSubwayInfo(@RequestBody StationInfo stationInfo) {
        StationInfo createdStationInfo = stationInfoService.save(stationInfo);
        return ResponseEntity.ok(createdStationInfo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StationInfo> updateSubwayInfo(@PathVariable String id, @RequestBody StationInfo stationInfo) {
        // Ensure the ID in the path and in the request body match
        stationInfo.setStationId(id);
        StationInfo updatedStationInfo = stationInfoService.save(stationInfo);
        return ResponseEntity.ok(updatedStationInfo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubwayInfo(@PathVariable String id) {
        stationInfoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{station}")
    public List<StationInfo> getSubwayOrder(@PathVariable("station") String station) {
        List<StationInfo> stationLine = stationInfoService.findByStationLine(station);
        return stationLine.stream()
                .sorted(Comparator.comparingInt(StationInfo::getOrder))
                .collect(Collectors.toList());
    }
}
