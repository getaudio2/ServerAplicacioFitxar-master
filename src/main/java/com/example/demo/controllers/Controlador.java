package com.example.demo.controllers;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.bean.Presencia;
import com.example.demo.bean.Profes;
import com.example.demo.bean.Usuario;
import com.example.demo.bean.Modulo;
import com.example.demo.repository.PresenciasDAO;
import com.example.demo.repository.ProfesDAO;
import com.example.demo.repository.ModuloDAO;

//metodo de mostrarProfes hecho por Paul, Fahad
//metodo verAusentesHoy hecho por Fahad

@Controller  //Lo convertimos en un servlet atiende peticiones http
@RequestMapping("/")   //localhost:8080 
public class Controlador {

	@Autowired
	PresenciasDAO presenciasDAO;
	
	@Autowired
	ModuloDAO modulosDAO;
	
	@Autowired
	ProfesDAO profesDAO;
	
	private int setmana = 0;
	

	@GetMapping("/") //Da salida al formulario de login
	public String iniciar(){
		return "profes";
	}
	
	@PostMapping("/")
	public String login(Usuario usuario, Model model) {
		//if (bd.compruebaUsuario(usuario.getNombre(),usuario.getPassword())) {
		if (usuario.getNom().equals("admin") && usuario.getPassword().equals("admin")) {
			List<Presencia> presencias = presenciasDAO.findAll(); 
			model.addAttribute("presencias",presencias);
			model.addAttribute("presencia",new Presencia());
			return "consulta";
		} else {
			return "login";
		}
	}
	@GetMapping(value="listar")//android web
	public ResponseEntity<List<Presencia>> getPresencias(){
		List<Presencia> presencias = presenciasDAO.findAll();
		return ResponseEntity.ok(presencias);
	}
	@GetMapping(value="listar/{name}")//android web
	public ResponseEntity<List<Presencia>> getPresenciasBy(@PathVariable("name") String name){
		List<Presencia> presencias = presenciasDAO.findByNom(name);
		return ResponseEntity.ok(presencias);
	}

	@PostMapping("insertar") //
	public ResponseEntity<Presencia> crearPresencia(String nom, double latitud, double longitud){
		//double latAustria=41.41694549369084;
		//double longAustria=2.1989492153444297;
		double latAustria = 41.4161732;
		double longAustria = 2.1991057;
		String comentario="";
		int distancia= (int)distancia2(latAustria, longAustria, latitud, longitud);
		boolean esta_dentro=distancia<25; 
		if (esta_dentro) comentario="Ha fichado correctamente";
		else comentario="No ha fichado correctamente";
		Presencia pre=new Presencia(nom, latitud, longitud, distancia,comentario);
		Presencia prenew= presenciasDAO.save(pre);
		return ResponseEntity.ok(prenew);
	}
	
	@PostMapping("/profes")
	public String mostrarProfes(String prof, Model model, String pastWeek, String nextWeek) {
		// Inicializar listas de grupos y presencias
		List<Profes> franjasProfe = profesDAO.findByProf(prof);
		List<Presencia> presencias = presenciasDAO.findAll();
		String[][] grupos = new String[12][6];
		
		// Inicializar calendarios y variables para la fecha actual y la fecha fichada
		Calendar calendarDiaFichado = GregorianCalendar.getInstance();
		Calendar calendarFechaActual = GregorianCalendar.getInstance();
		Calendar calendarDiaActual = GregorianCalendar.getInstance();
		
		int[] dias = new int[5];	// Guardar los números de los días de cada semana
		int[] mes = new int[5]; 	// Guardar el mes del día de la semana
		int[] anyo = new int[5];		// Guardar el año del día de la semana
		int distanciaPresencia = 1000;
		int diaPresencia = 0;
		int hora = 8;
		
		calendarFechaActual.setTime(new Date());
		calendarDiaActual.setTime(new Date());
		
		// Cambiar la semana a visualizar si se ha pulsado el botón
		if (pastWeek == null && nextWeek == null) setmana = 0;
		if (pastWeek != null) setmana -= 7;
	    if (nextWeek != null) setmana += 7;
		calendarFechaActual.add(Calendar.DATE, setmana);

		// Cogemos el número del día lunes y recorremos hasta guardar los 5 días de la semana
		calendarFechaActual.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		for (int i = 0; i < 5; i++) {
			dias[i] = calendarFechaActual.get(Calendar.DAY_OF_MONTH);
			mes[i] = calendarFechaActual.get(Calendar.MONTH);
			anyo[i] = calendarFechaActual.get(Calendar.YEAR);
			calendarFechaActual.add(Calendar.DATE, 1);
		}
		
		// Bucles 'for' anidados para crear una tabla del horario vacía por defecto
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 6; j++) {
				grupos[i][j] = new String("");
			}
			
			// Rellenar horas en la primera columna
			if (i < 3 || i > 8) {
				grupos[i][0] = "" + hora + ":00-" + (hora+1) + ":00";
			} else {
				grupos[i][0] = "" + hora + ":30-" + (hora+1) + ":30";
			}
			hora++;
			if (i == 8) hora++;
		}
		
		// Rellenar la tabla del horario con los módulos ordenados según la hora y el día
		for (int k = 0; k < franjasProfe.size(); k++) {
			int franjaModulo = franjasProfe.get(k).getFranjas_id();
			int diaModulo = franjasProfe.get(k).getDias_id();
			
			grupos[franjaModulo-1][diaModulo] = franjasProfe.get(k).getModulo() + " - " + franjasProfe.get(k).getProf();
		}
		
		// Recoger la presencia del profesor para comprobar cuando ha fichado
		for(Presencia presencia : presencias) { 	// Comprobar todas las presencias...
			if (presencia.getNom().equals(prof)) {  // ...y buscar las del profesor seleccionado
			
			calendarDiaFichado.setTime(presencia.getFecha()); // Agarrar la fecha de la presencia
			calendarFechaActual.set(Calendar.DAY_OF_WEEK, calendarDiaFichado.get(Calendar.DAY_OF_WEEK));
			
			// Comprobar que el dia fichado pertenezca a la semana y mes mostrados en el horario
			if (IntStream.of(dias).anyMatch(x -> x == calendarDiaFichado.get(Calendar.DAY_OF_MONTH))
					&& calendarDiaFichado.get(Calendar.MONTH)+1 == calendarFechaActual.get(Calendar.MONTH)+1
					&& calendarDiaFichado.get(Calendar.YEAR) == calendarFechaActual.get(Calendar.YEAR)) {
			
				distanciaPresencia = presencia.getDistancia();	  // Guardar la distancia de la presencia
				diaPresencia = calendarDiaFichado.get(Calendar.DAY_OF_WEEK) - 1; // Guardar el dia de la presencia
				
					// Marcar las franjas que ha fichado (o no)
					for (int i = 0; i < 12; i++) {
						if(!grupos[i][diaPresencia].isEmpty()) { // Si la franja no está vacía...
							// (Guardamos la hora y minutos de la franja)
							
							// Recoger los datos de cada franja
							// Según la fila(hora) y columna(dia) hay que saber:
							// El día del mes, la hora y el mes
							// Después, generar un objeto Calendar y compararlo con
							// el Calendar de la fecha actual
							String[] horaMinutoFranja = grupos[i][0].substring(grupos[i][0].indexOf("-")+1).split(":");
							
							Calendar calFranja = Calendar.getInstance();
							calFranja.set(Calendar.MONTH, calendarDiaFichado.get(Calendar.MONTH));
							calFranja.set(Calendar.DAY_OF_MONTH, calendarDiaFichado.get(Calendar.DAY_OF_MONTH));
							calFranja.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaMinutoFranja[0]));
							calFranja.set(Calendar.MINUTE, Integer.parseInt(horaMinutoFranja[1]));
							calFranja.set(Calendar.YEAR, calendarDiaFichado.get(Calendar.YEAR));
							
							// ... y está dentro del radio de 20m, el resto de franjas con módulos hasta terminar
							// el día estarán marcadas como "Fitxat OK"
							if (calFranja.compareTo(calendarDiaFichado) > 0
									&& distanciaPresencia <= 20) {
								grupos[i][diaPresencia] = grupos[i][diaPresencia] + " /Y";
							// En caso contrario, estarán marcadas como "No Fitxat"
							} else {
								grupos[i][diaPresencia] = grupos[i][diaPresencia] + " /N";
							}
						}
					}
				}
			}
		}
			// Marcar los módulos pasados sin fichar a tiempo
			for (int i = 0; i < 12; i++) {
				for (int j = 1; j <= 5; j++) {
					if(!grupos[i][j].isEmpty() && !grupos[i][j].contains("/")) { // Si el módulo no está fichado...
						// Recoger los datos de cada franja
						// Según la fila(hora) y columna(dia) hay que saber:
						// El día del mes, la hora y el mes
						// Después, generar un objeto Calendar y compararlo con
						// el Calendar de la fecha actual
						String[] horaMinutoFranja = grupos[i][0].substring(grupos[i][0].indexOf("-")+1).split(":");
						
						Calendar calFranja = Calendar.getInstance();
						calFranja.set(Calendar.MONTH, mes[j-1]);
						calFranja.set(Calendar.DAY_OF_MONTH, dias[j-1]);
						calFranja.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaMinutoFranja[0]));
						calFranja.set(Calendar.MINUTE, Integer.parseInt(horaMinutoFranja[1]));
						calFranja.set(Calendar.YEAR, anyo[j-1]);
						
						if (calFranja.compareTo(calendarDiaActual) < 0) {
							grupos[i][j] = grupos[i][j] + " /N";
						}
					}
				}
			}
		//}
			
		// Guardamos el mes y el año de la semana del horario
		String mesAnyo = "";
		if (mes[0] == mes[4]) {
			mesAnyo = new DateFormatSymbols().getMonths()[mes[0]].toUpperCase() + " " + anyo[0];
		} else {
			mesAnyo = new DateFormatSymbols().getMonths()[mes[0]].toUpperCase() + " "
					+ anyo[0] + " - " + new DateFormatSymbols().getMonths()[mes[4]].toUpperCase() + " " +
					+ anyo[4];
		}
		
		// Enviar arrays al html profes
		model.addAttribute("grupos", grupos); // Rellenar tabla html con lista modulos
		model.addAttribute("prof", prof);
		model.addAttribute("dias", dias);
		model.addAttribute("mesAnyo", mesAnyo);
		return "profes";
	}
	
	//metodo que se encarga de llenar la tabla del html profesoresAusentesHoy
	//para asi tener un historial de ausencias del dia 
	@GetMapping("/profesAusentesHoy")
	public String verAusentesHoy(Model model) {
		List<String[]> profesAusentes = generarContenidoTabla();
		
	    model.addAttribute("profesAusentes", profesAusentes);
	    return "profesAusentesHoy";
	}

	@ModelAttribute("contenidoTabla")
	public ArrayList<String[]> generarContenidoTabla() {
		List<String> profesoresAusentes = GeneradorAlertas.getProfesoresAusentesHoy();
		ArrayList<String[]> contenidoTabla = new ArrayList<>();

	    for (String profesor : profesoresAusentes) {
	        String[] datos = profesor.split(" - ");
	        contenidoTabla.add(datos);
	    }
	    return contenidoTabla;
	}

	
	// Método que muestra las classes (modulos) del grupo seleccionado
	@PostMapping("/classes")
	public String mostrarModulo(String grupos, Model model){
		// Recibe la lista de modulos del grupo seleccionado
		List<Modulo> lista = modulosDAO.findByGrupo(grupos);
		// El array para ordenar los modulos según las franjas de hora y dia
		String[][] modulos = new String[12][6];
		
		// Inicializar el objeto Calendar con un objeto Date
		Date date = new Date();
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		// Rellena el array modulos con un string vacío como valor inicial, y luego,
		// rellena la primera columna de la matriz con las horas de cada franja
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 6; j++) {
				modulos[i][j] = new String("");
			}
			
			if (i < 3 || i > 8) {
				modulos[i][0] = "" + calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)
						+calendar.get(Calendar.SECOND)+ "-" + (calendar.get(Calendar.HOUR_OF_DAY)+1)
						+":"+calendar.get(Calendar.MINUTE)+calendar.get(Calendar.SECOND);
			} else {
				modulos[i][0] = "" + calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)
						+ "-" + (calendar.get(Calendar.HOUR_OF_DAY)+1)+":"+calendar.get(Calendar.MINUTE);
			}
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			if (i == 2) calendar.add(Calendar.MINUTE, 30);
			if (i == 8) calendar.add(Calendar.MINUTE, 30);
		}
		// Un bucle que recorre la lista de modulos y los coloca dentro del array modulos[][]
		// según la franja y el dia
		for (int k = 0; k < lista.size(); k++) {
			int franjaModulo = lista.get(k).getFranjas_id();
			int diaModulo = lista.get(k).getDias_id();
			
			modulos[franjaModulo-1][diaModulo] = lista.get(k).getModulo() + " - " + lista.get(k).getProf();
		}
		
		model.addAttribute("modulos", modulos); // Rellenar tabla html con lista modulos
		return "classes";
	}

	@PostMapping("/filtrar")//Web
	public String filtrar(String nom, String fecha, Model model) throws ParseException {	
		List<Presencia> presencias;
		if (nom.equals("") && fecha.equals(""))
			presencias = presenciasDAO.findAll();
		else if (!nom.equals("") && fecha.equals(""))
			presencias = presenciasDAO.findByNom(nom);
		else if (nom.equals("") && !fecha.equals("")) {
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date1 = formatter1.parse(fecha+" 00:00:00");
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date2 = formatter2.parse(fecha+" 23:59:59");
			presencias = presenciasDAO.findByFechaBetween(date1,date2);
		}
		else {
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date1 = formatter1.parse(fecha+" 00:00:00");
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date2 = formatter2.parse(fecha+" 23:59:59");
			presencias = presenciasDAO.findByNomAndFechaBetween(nom,date1, date2);
		}
		model.addAttribute("presencias",presencias);
		return "consulta";		
	}
	
	@GetMapping("/borrado/{id}") 
	public String borrar(@PathVariable int id, Model model) {	
		presenciasDAO.deleteById(id);	
		List<Presencia> presencias = presenciasDAO.findAll(); 
		model.addAttribute("presencias",presencias);
		return "consulta";		
	}
	
    public static double distancia1(double lat1, double long1, double lat2, double long2) {
        return 6371*Math.acos(Math.sin(lat1) * Math.sin(lat2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.cos(long2 - (long1)));

    }
    public static double distancia2(double lat1, double lng1, double lat2, double lng2) {
        //double radioTierra = 3958.75;//en millas
        double radioTierra = 6371;//en kilómetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double distancia = radioTierra * va2;

        return distancia*1000;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double distancia3(double lat1,double lon1,double lat2, double lon2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * 6378137;
        //s = Math.round(s * 10000) / 10000;
        return s;
    }
}