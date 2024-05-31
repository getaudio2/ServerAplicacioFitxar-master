package com.example.demo.repository;

import com.example.demo.bean.Modulo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ModulosDAO extends JpaRepository<Modulo, Integer> {
	public List<Modulo> findByGrupo(String grupos);
	public List<Modulo> findByProf(String prof);
}









