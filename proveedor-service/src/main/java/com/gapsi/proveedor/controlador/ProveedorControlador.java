package com.gapsi.proveedor.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gapsi.proveedor.entidades.Proveedor;
import com.gapsi.proveedor.dto.LoginDto;
import com.gapsi.proveedor.servicio.ProveedorServicio;


@RestController
@RequestMapping("/proveedor")
@EnableJpaRepositories(basePackages = "com.gapsi.proveedor.*")
public class ProveedorControlador {

	@Autowired
	private ProveedorServicio proveedorServicio;	
	
	@GetMapping("/login") 
	public ResponseEntity<LoginDto> login() {
		LoginDto loginDto = new LoginDto();
		loginDto.setBienvenida("Bienvenido Candidato 01");
		loginDto.setVersion("version 0.0.1");

		return ResponseEntity.ok(loginDto);
	}	 
	
	@GetMapping
	public ResponseEntity<List<Proveedor>> listarProveedores() {
		List<Proveedor> proveedores = proveedorServicio.getAll();
		if( proveedores.isEmpty() ) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(proveedores);
	} 
	
	@GetMapping("/{id}")
	public ResponseEntity<Proveedor> obtenerProveedor(@PathVariable("id") Integer id) {
		Proveedor proveedor = proveedorServicio.getProveedorById(id);
		if( proveedor == null ) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(proveedor);
	}
	
	@PostMapping
	public ResponseEntity<?> guardarProveedor(@RequestBody Proveedor proveedor) {
		
		try {
			Proveedor proveedorExistente = proveedorServicio.getProveedorByNombre(proveedor.getNombre());
			
			if( proveedorExistente == null) {
				Proveedor nuevoProveedor = proveedorServicio.save(proveedor);
				return ResponseEntity.ok(nuevoProveedor);
			} else {
				return new ResponseEntity<Proveedor>(HttpStatus.FOUND);
			}
		} catch(Exception exc) {
			return new ResponseEntity<Proveedor>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> actualizarProveedor(@RequestBody Proveedor proveedor, @PathVariable Integer id) {
		try {
			Proveedor proveedorExistente = proveedorServicio.getProveedorById(id);
			proveedorExistente.setNombre(proveedor.getNombre());
			proveedorExistente.setRazonSocial(proveedor.getRazonSocial());			
			proveedorExistente.setDireccion(proveedor.getDireccion());			
			
			proveedorServicio.save(proveedorExistente);
			
			return new ResponseEntity<Proveedor>(HttpStatus.OK);
		} catch(Exception exc) {
			return new ResponseEntity<Proveedor>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminarProveedor(@PathVariable Integer id) {		
		try {
			Proveedor proveedorExistente = proveedorServicio.getProveedorById(id);
			
			if( proveedorExistente == null) {
				return new ResponseEntity<Proveedor>(HttpStatus.NOT_FOUND);
			} else {
				proveedorServicio.eliminarProveedor(id);
				return new ResponseEntity<Proveedor>(HttpStatus.OK);
			}
		} catch(Exception exc) {
			return new ResponseEntity<Proveedor>(HttpStatus.NOT_FOUND);
		}		
	}

}
