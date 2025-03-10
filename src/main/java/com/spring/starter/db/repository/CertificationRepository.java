package com.spring.starter.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.starter.db.entity.Certification;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
	Certification findByEmail(String email);
}
