package com.rayosoft.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rayosoft.model.Solicitud;
import com.rayosoft.model.Usuario;
import com.rayosoft.model.Vacante;
import com.rayosoft.service.ISolicitudesService;
import com.rayosoft.service.IUsuariosService;
import com.rayosoft.service.IVacantesService;
import com.rayosoft.util.Utileria;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/solicitudes")
public class SolicitudesController {

	@Value("${empleosapp.ruta.cv}")
	private String ruta;
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private IVacantesService serviceVacantes;
	
	@Autowired
	private ISolicitudesService serviceSolicitudes;
	
	@GetMapping("/index")
	public String mostrarIndex(Model model) {
		List<Solicitud> lista = serviceSolicitudes.buscarTodas();
    	model.addAttribute("solicitudes", lista);
		return "solicitudes/listSolicitudes";
	}
	
	@GetMapping(value = "/indexPaginate")
	public String mostrarIndexPaginado(Model model, Pageable page) {
		Page<Solicitud>lista = serviceSolicitudes.buscarTodas(page);
		model.addAttribute("solicitudes", lista);
		return "solicitudes/listSolicitudes";
	}
	
	@GetMapping(value = "/create/{idVacante}")
	public String crear(Solicitud solicitud,@PathVariable("idVacante") Integer IdVacante,Model model) {
		Vacante vacante=serviceVacantes.buscarPorId(IdVacante);
		model.addAttribute("vacante",vacante);
		return "solicitudes/formSolicitud";
	}
	
	@GetMapping(value = "/delete/{id}")
	public String eliminar(@PathVariable("id") int IdSolicitud,RedirectAttributes attributes) {
		serviceSolicitudes.eliminar(IdSolicitud);
		attributes.addFlashAttribute("msg","La solicitud fue eliminada!.");
		return "redirect:/solicitudes/indexPaginate";
	}
	
	@PostMapping("/save")
	public String guardar(Solicitud solicitud, BindingResult result,Model model, HttpSession session,
			RedirectAttributes attributes, @RequestParam("archivoCV") MultipartFile multiPart,
			Authentication autentication) {
		String username=autentication.getName();
		if (result.hasErrors()) {
			for (ObjectError error: result.getAllErrors()){
				System.out.println("Ocurrio un error: "+ error.getDefaultMessage());
			}			
			return "solicitudes/formSolicitud";
		}
		
		if (!multiPart.isEmpty()) {
			//String ruta = "/empleos/img-vacantes/"; // Linux/MAC
			//String ruta = "c:/empleos/img-vacantes/"; // Windows
			String nombreArchivo = Utileria.guardarArchivo(multiPart, ruta);
			if (nombreArchivo != null){ // La imagen si se subio
				// Procesamos la variable nombreImagen
				solicitud.setArchivo(nombreArchivo);
			}
		}
		Usuario usuario=serviceUsuarios.buscarPorUsername(username);
		solicitud.setUsuario(usuario);
		
		try {
			serviceSolicitudes.guardar(solicitud);
			attributes.addFlashAttribute("msg","Gracias por enviar tu CV");
		} catch(Exception e) {
			attributes.addFlashAttribute("msg","Solicitud duplicada");
		}
		
		return "redirect:/";
	}
	
}
