package com.knu.subway.repository;

import com.knu.subway.entity.SubwayInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubwayInfoRepository extends JpaRepository<SubwayInfo, Long> {

}
