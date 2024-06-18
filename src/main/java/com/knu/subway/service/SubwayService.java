package com.knu.subway.service;

import com.knu.subway.entity.Subway;
import com.knu.subway.repository.SubwayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SubwayService {
    private final SubwayRepository subwayRepository;

    public void save(Subway subway){
        subwayRepository.save(subway);
    }
}
