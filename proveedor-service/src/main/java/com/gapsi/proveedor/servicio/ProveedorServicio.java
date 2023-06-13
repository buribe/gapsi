package com.gapsi.proveedor.servicio;

import java.util.List;

import com.gapsi.proveedor.entidades.Proveedor;
import com.gapsi.proveedor.repositorio.ProveedorRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProveedorServicio {

	@Autowired
	private ProveedorRepositorio proveedorRepositorio;

	public List<Proveedor> getAll() {
		return proveedorRepositorio.findAll();		
	}
	
	public Proveedor getProveedorById(Integer id) {
		return proveedorRepositorio.findById(id).orElse(null);	
	}
	
	public Proveedor getProveedorByNombre(String nombre) {
		return proveedorRepositorio.findByNombre(nombre); //.orElse(null);	
	}

	public Proveedor save(Proveedor proveedor) {
		Proveedor nuevoProveedor = proveedorRepositorio.save(proveedor);
		return nuevoProveedor;
	}
	
	public void eliminarProveedor(Integer id) {
		proveedorRepositorio.deleteById(id);
	}

}
