package org.keshav.repository;

import java.time.LocalDate;
import java.util.List;

import org.keshav.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long>{
	Holiday save(Holiday holiday);
	List<Holiday> findByDateBetween(LocalDate start, LocalDate end);
	List<Holiday> findByType(String type);
}
