package com.example.demo.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="profes")
public class Profes {
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String grupo;
	private String prof;
	private String modulo;
	private int franjas_id;
	private int dias_id;
	private String aula;
	public Profes(int id, String grupo, String prof, String modulo, int franjas_id, int dias_id, String aula) {
		super();
		this.id = id;
		this.grupo = grupo;
		this.prof = prof;
		this.modulo = modulo;
		this.franjas_id = franjas_id;
		this.dias_id = dias_id;
		this.aula = aula;
	}
	public Profes() {}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public String getProf() {
		return prof;
	}
	public void setProf(String prof) {
		this.prof = prof;
	}
	public String getModulo() {
		return modulo;
	}
	public void setModulo(String modulo) {
		this.modulo = modulo;
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
	public String getAula() {
		return aula;
	}
	public void setAula(String aula) {
		this.aula = aula;
	}
}


