package com.knu.subway.repository;

import com.knu.subway.entity.Subway;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubwayRepository extends JpaRepository<Subway, Long> {
}
