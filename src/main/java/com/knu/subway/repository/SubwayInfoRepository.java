package com.knu.subway.repository;

import com.knu.subway.entity.SubwayInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubwayInfoRepository extends JpaRepository<SubwayInfo, Long> {
}
