package com.example.demo.repository;
import com.example.demo.bean.Profes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfesDAO extends JpaRepository<Profes, Integer> {
	public List<Profes> findByProf(String prof);
	//public List<Presencia> findAllByStartDateLessThanEqualAndEndDateGreaterThanEqual(Date endDate, Date startDate);

}









