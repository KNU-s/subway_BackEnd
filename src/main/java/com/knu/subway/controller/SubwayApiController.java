package com.knu.subway.controller;

import com.knu.subway.entity.dto.SubwayDTO;
import com.knu.subway.entity.Subway;
import com.knu.subway.service.ApiService;
import com.knu.subway.service.SubwayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/subway")
@RequiredArgsConstructor
@RestController
public class SubwayApiController {
    private final SubwayService subwayService;
    private final ApiService apiService;

    @GetMapping("/station/{station}")
    public List<SubwayDTO> getSubwayByStation(@PathVariable("station") String station){
        return apiService.getSubwayArrivals(station);
    }
    @GetMapping("/stationLine/{stationLine}")
    public List<Subway> getSubwayByStationLine(@PathVariable("stationLine") String stationLine){
        return subwayService.findBySubwayLine(stationLine);
    }

    @GetMapping
    public List<Subway> getAllSubways() {
        return subwayService.findAll();
    }

    @GetMapping("/{id}")
    public Subway getSubwayById(@PathVariable String id) {
        return subwayService.findSubwayById_orElseThrow(id);
    }

    @PostMapping
    public String createSubway(@RequestBody Subway subway) {
        return subwayService.save(subway);
    }

    @PutMapping("/{id}")
    public String updateSubway(@PathVariable String id, @RequestBody Subway subway) {
        // Ensure the ID in the path and in the request body match
        return subwayService.save(subway);
    }

    @DeleteMapping("/{id}")
    public void deleteSubway(@PathVariable String id) {
        subwayService.deleteById(id);
    }
}
