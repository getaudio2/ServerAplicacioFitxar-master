package com.example.demo.repository;

import com.example.demo.bean.Modulo;
import com.example.demo.bean.Presencia;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuloDAO extends JpaRepository<Modulo, Integer> {
	public List<Modulo> findByGrupo(String grupo);
	
	//public List<Presencia> findAllByStartDateLessThanEqualAndEndDateGreaterThanEqual(Date endDate, Date startDate);

}









