package com.example.demo.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="modulos")
public class Modulo {
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String modulo;
	private String prof;
	private String grupo;
	private int franjas_id;
	private int dias_id;
	public Modulo(int id, String modulo, String prof, String grupo, int franjas_id, int dias_id) {
		super();
		this.id = id;
		this.modulo = modulo;
		this.prof = prof;
		this.grupo = grupo;
		this.franjas_id = franjas_id;
		this.dias_id = dias_id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getModulo() {
		return modulo;
	}
	public void setModulo(String modulo) {
		this.modulo = modulo;
	}
	public String getProf() {
		return prof;
	}
	public void setProf(String prof) {
		this.prof = prof;
	}
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public int getFranjas_id() {
		return franjas_id;
	}
	public void setFranjas_id(int franjas_id) {
		this.franjas_id = franjas_id;
	}
	public int getDias_id() {
		return dias_id;
	}
	public void setDias_id(int dias_id) {
		this.dias_id = dias_id;
	}
}


