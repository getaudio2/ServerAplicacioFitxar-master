package com.example.demo.repository;

import com.example.demo.bean.Presencia;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PresenciasDAO extends JpaRepository<Presencia, Integer> {
	public List<Presencia> findByNom(String nom);
	public List<Presencia> findByFechaBetween(Date to, Date from);
	public List<Presencia> findByNomAndFechaBetween(String nom, Date to, Date from);
	public List<Presencia> findByFecha(Date fecha);
	public Presencia findByNomAndFecha(String nom, LocalDateTime fecha);
	
	@Query("SELECT p FROM Presencia p WHERE p.nom = :nom AND p.fecha BETWEEN :fechaHoraInicio AND :fechaHoraFin")
	List<Presencia> encontrarPresenciasDelProfeEstaFranja(@Param("nom") String nom, 
											  @Param("fechaHoraInicio") Date fechaHoraInicio, 
											  @Param("fechaHoraFin") Date fechaHoraFin);
}
