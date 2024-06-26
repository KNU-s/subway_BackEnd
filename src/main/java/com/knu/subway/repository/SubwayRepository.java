package com.knu.subway.repository;

import com.knu.subway.entity.Subway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubwayRepository extends JpaRepository<Subway, Long> {
    List<Subway> findByStatnId(String statnId);
}
