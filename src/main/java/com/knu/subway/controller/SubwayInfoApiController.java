package com.knu.subway.controller;

import com.knu.subway.entity.SubwayInfo;
import com.knu.subway.service.SubwayInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@RequestMapping("/api/v1/subway-info")
@CrossOrigin(origins = {"http://43.202.241.160:80", "http://localhost:3000"})
@RestController
public class SubwayInfoApiController {
    private final SubwayInfoService subwayInfoService;
    @GetMapping
    public List<SubwayInfo> getAllSubwayInfo() {
        return subwayInfoService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<SubwayInfo> getSubwayInfoById(@PathVariable String id) {
        return subwayInfoService.findById(id);
    }

    @PostMapping
    public SubwayInfo createSubwayInfo(@RequestBody SubwayInfo subwayInfo) {
        return subwayInfoService.save(subwayInfo);
    }

    @PutMapping("/{id}")
    public SubwayInfo updateSubwayInfo(@PathVariable String id, @RequestBody SubwayInfo subwayInfo) {
        // Ensure the ID in the path and in the request body match
        subwayInfo.setId(id);
        return subwayInfoService.save(subwayInfo);
    }

    @DeleteMapping("/{id}")
    public void deleteSubwayInfo(@PathVariable String id) {
        subwayInfoService.deleteById(id);
    }
}
