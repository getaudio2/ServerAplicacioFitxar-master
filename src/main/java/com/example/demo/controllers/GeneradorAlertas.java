package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.example.demo.bean.Presencia;
import com.example.demo.bean.Profes;
import com.example.demo.repository.PresenciasDAO;
import com.example.demo.repository.ProfesDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

//clase hecha por Jhoel y Alvaro

@Configuration
@EnableScheduling
public class GeneradorAlertas{
	
	//crear la instancia del bot de telegram
  	private static Bot bot = new Bot();
	
	@Autowired
	PresenciasDAO presenciasDAO; 
	@Autowired
	ProfesDAO profesDAO;
	
	private LocalDate hoy = LocalDate.now();
	
	//lista que contendra los profesores ausentes del dia
	private static List<String> profesoresAusentes = new ArrayList<>();
	
	@Scheduled(cron = "0 0 8 * * MON-FRI", zone = "Europe/Madrid")
	public void limpiarLista() {
		profesoresAusentes.clear();
	}
	
	@Scheduled(cron = "0 15 8-10 * * MON-FRI", zone = "Europe/Madrid")
	public void alertasFranjas1a3() throws TelegramApiException {
		System.out.println("Ejecutando la tarea de las franjas 1, 2 y 3");

		LocalDate hoy = LocalDate.now();
		int diaId = hoy.getDayOfWeek().getValue();

		LocalTime hora = LocalTime.now();
		int franjaId = obtenerFranjaId(hora);

		List<Profes> profesoresEnHora = obtenerProfesDeEstaHora(diaId, franjaId);
		System.out.println("Franja horaria " + franjaId + " - Profesores encontrados: " + profesoresEnHora.size());

		comprobarPresencias(profesoresEnHora, franjaId);
	}
	
	@Scheduled(cron = "0 45 11-16 * * MON-FRI", zone = "Europe/Madrid")
	public void alertasFranjas4a9() throws TelegramApiException {
		System.out.println("Ejecutando la tarea de las franjas 4, 5, 6, 7, 8 y 9");

		LocalDate hoy = LocalDate.now();
		int diaId = hoy.getDayOfWeek().getValue();

		LocalTime hora = LocalTime.now();
		int franjaId = obtenerFranjaId(hora);

		List<Profes> profesoresEnHora = obtenerProfesDeEstaHora(diaId, franjaId);
		System.out.println("Franja horaria " + franjaId + " - Profesores encontrados: " + profesoresEnHora.size());

		comprobarPresencias(profesoresEnHora, franjaId);
	}
	
	@Scheduled(cron = "0 15 18-20 * * MON-FRI", zone = "Europe/Madrid")
	public void alertasFranjas10a12() throws TelegramApiException {
		System.out.println("Ejecutando la tarea de las franjas 10, 11 y 12");

		LocalDate hoy = LocalDate.now();
		int diaId = hoy.getDayOfWeek().getValue();

		LocalTime hora = LocalTime.now();
		int franjaId = obtenerFranjaId(hora);

		List<Profes> profesoresEnHora = obtenerProfesDeEstaHora(diaId, franjaId);
		System.out.println("Franja horaria " + franjaId + " - Profesores encontrados: " + profesoresEnHora.size());

		comprobarPresencias(profesoresEnHora, franjaId);
	}
	/******************Modulo para enviar las alertas a Telegram**************************/
	
	public static void mensajeTelegram(String mensaje)  throws TelegramApiException{
	   	 TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
	   	 botsApi.registerBot(bot);            
	   	 bot.sendText(-921819428, mensaje);  //The L just turns the Integer into a Long
    }

	
	/************************Modulos Para Generar alertas*******************************/
	
	private List<Profes> obtenerProfesDeEstaHora(int diaId, int franjaId) {
		List<Profes> profesores = profesDAO.findAll();
		List<Profes> profesoresEnHora = new ArrayList<Profes>();

		for (Profes profesor : profesores) {
			if (profesor.getFranjas_id() == franjaId && profesor.getDias_id() == diaId && profesor.getModulo() != "Guardia CF") {
				profesoresEnHora.add(profesor);
			}
		}
		return profesoresEnHora;
	}

	 
	private void comprobarPresencias(List<Profes> profesoresEnHora, int franjaId) throws TelegramApiException{	
	    for (Profes profesor : profesoresEnHora) {
	        LocalTime horaInicioFranja = obtenerHoraInicio(franjaId);
	        LocalTime horaFinFranja = obtenerHoraFin(franjaId);

	        Instant instantInicio = horaInicioFranja.atDate(hoy).atZone(ZoneId.systemDefault()).toInstant();
	        Instant instantFin = horaFinFranja.atDate(hoy).atZone(ZoneId.systemDefault()).toInstant();

	        Date fechaHoraInicio = Date.from(instantInicio);
	        Date fechaHoraFin = Date.from(instantFin);

	        // Buscamos las presencias del día
	        List<Presencia> presenciaDelProfeEstaHora = presenciasDAO.encontrarPresenciasDelProfeEstaFranja(profesor.getProf(), fechaHoraInicio, fechaHoraFin);

	        if (!presenciaDelProfeEstaHora.isEmpty()) {
	            // Iteramos sobre todas las presencias del día correspondiente al profesor y comprobamos si alguna de ellas corresponde a la franja horaria actual
	            for (Presencia presencia : presenciaDelProfeEstaHora) {
	                LocalDateTime fechaHora = LocalDateTime.ofInstant(presencia.getFecha().toInstant(), ZoneId.systemDefault());
	                LocalTime horaPresencia = fechaHora.toLocalTime();

	                if (horaPresencia.isAfter(horaInicioFranja) && horaPresencia.isBefore(horaFinFranja)) {
	                    // La presencia se encuentra dentro de la franja horaria
	                    if (horaPresencia.isAfter(horaInicioFranja) && horaPresencia.isBefore(horaInicioFranja.plusMinutes(15)) && presencia.getDistancia() <=20) {
	                        // El fichaje se realizó dentro de los 10 minutos de la hora de inicio de la franja horaria
	                        System.out.println(profesor.getProf() + " - Presente en la franja horaria " + franjaId);
	                    } else {
	                        // El fichaje se realizó fuera de los 15 minutos de la hora de inicio de la franja horaria
	                        ausente(profesor);
	                        String infoProfesorAusente = profesor.getProf() + " - " + profesor.getModulo() + " - " + horaInicioFranja.toString();
	                        profesoresAusentes.add(infoProfesorAusente);
	                    }
	                }
	            }
	        } else {
	        	String infoProfesorAusente = profesor.getProf() + " - " + profesor.getModulo() + " - " + horaInicioFranja.toString();
                profesoresAusentes.add(infoProfesorAusente);
	            ausente(profesor);
	        }
	    }
	}

	//modulo que se encarga de crear el mensaje de alerta
	private void ausente(Profes profesor) throws TelegramApiException {
		String mensaje = "El profesor " + profesor.getProf() + " esta ausente en la clase " + profesor.getModulo() + ", por favor que el profesor de guardia acuda al aula " + profesor.getAula() + ".";
	    System.out.println(mensaje);
	    	mensajeTelegram(mensaje);
	}

	public static List<String> getProfesoresAusentesHoy(){
		return profesoresAusentes;
	}

	private int obtenerFranjaId(LocalTime hora) {
		int franjaId=0;
		if (hora.isAfter(LocalTime.of(8, 0)) && hora.isBefore(LocalTime.of(9, 0))) {
			franjaId= 1;
		} else if (hora.isAfter(LocalTime.of(9, 0)) && hora.isBefore(LocalTime.of(10, 0))) {
			franjaId = 2;
		} else if (hora.isAfter(LocalTime.of(10, 0)) && hora.isBefore(LocalTime.of(11, 00))) {
			franjaId = 3;
		} else if (hora.isAfter(LocalTime.of(11, 30)) && hora.isBefore(LocalTime.of(12, 30))) {
			franjaId = 4;
		} else if (hora.isAfter(LocalTime.of(12, 30)) && hora.isBefore(LocalTime.of(13, 30))) {
			franjaId = 5;
		} else if (hora.isAfter(LocalTime.of(13, 30)) && hora.isBefore(LocalTime.of(14, 30))) {
			franjaId = 6;
		} else if (hora.isAfter(LocalTime.of(14, 30)) && hora.isBefore(LocalTime.of(15, 30))) {
			franjaId = 7;
		} else if (hora.isAfter(LocalTime.of(15, 30)) && hora.isBefore(LocalTime.of(16, 30))) {
			franjaId = 8;
		} else if (hora.isAfter(LocalTime.of(16, 30)) && hora.isBefore(LocalTime.of(17, 30))) {
			franjaId = 9;
		} else if (hora.isAfter(LocalTime.of(18, 0)) && hora.isBefore(LocalTime.of(19, 0))) {
			franjaId = 10;
		} else if (hora.isAfter(LocalTime.of(19, 0)) && hora.isBefore(LocalTime.of(20, 0))) {
			franjaId = 11;
		} else if (hora.isAfter(LocalTime.of(20, 0)) && hora.isBefore(LocalTime.of(21, 00))) {
			franjaId = 12;
		}
		return franjaId;
	}

	private LocalTime obtenerHoraInicio(int franjaId) {
	    switch(franjaId) {
	        case 1:
	            return LocalTime.of(8, 0);
	        case 2:
	            return LocalTime.of(9, 0);
	        case 3:
	            return LocalTime.of(10, 0);
	        case 4:
	            return LocalTime.of(11, 30);
	        case 5:
	            return LocalTime.of(12, 30);
	        case 6:
	            return LocalTime.of(13, 30);
	        case 7:
	            return LocalTime.of(14, 30);
	        case 8:
	            return LocalTime.of(15, 30);
	        case 9:
	            return LocalTime.of(16, 30);
	        case 10:
	            return LocalTime.of(18, 0);
	        case 11:
	            return LocalTime.of(19, 0);
	        case 12:
	            return LocalTime.of(20, 0);
	        default:
	            return null;
	    }
	}

	private LocalTime obtenerHoraFin(int franjaId) {
	    switch(franjaId) {
	        case 1:
	            return LocalTime.of(9, 0);
	        case 2:
	            return LocalTime.of(10, 0);
	        case 3:
	            return LocalTime.of(11, 00);
	        case 4:
	            return LocalTime.of(12, 30);
	        case 5:
	            return LocalTime.of(13, 30);
	        case 6:
	            return LocalTime.of(14, 30);
	        case 7:
	            return LocalTime.of(15, 30);
	        case 8:
	            return LocalTime.of(16, 30);
	        case 9:
	            return LocalTime.of(17, 30);
	        case 10:
	            return LocalTime.of(19, 0);
	        case 11:
	            return LocalTime.of(20, 0);
	        case 12:
	            return LocalTime.of(20, 59);
	        default:
	            return null;
	    }
	}
}