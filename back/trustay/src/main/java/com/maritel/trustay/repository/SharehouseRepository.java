package com.maritel.trustay.repository;

import com.maritel.trustay.entity.Sharehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SharehouseRepository extends JpaRepository<Sharehouse, Long>, JpaSpecificationExecutor<Sharehouse> {
    // 필요 시 검색 메소드 추가 (예: 지역별 검색 등)
}