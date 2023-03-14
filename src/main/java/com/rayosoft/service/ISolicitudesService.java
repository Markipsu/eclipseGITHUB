package com.rayosoft.service;

import java.util.List;

import org.springframework.data.domain.*;

import com.rayosoft.model.Solicitud;

public interface ISolicitudesService {

	void guardar(Solicitud solicitud);
	
	void eliminar(Integer idSolicitud);
	
	List<Solicitud> buscarTodas();
	
	Solicitud buscarPorId(Integer idSolicitud);
	
	Page<Solicitud> buscarTodas(Pageable page);
}
