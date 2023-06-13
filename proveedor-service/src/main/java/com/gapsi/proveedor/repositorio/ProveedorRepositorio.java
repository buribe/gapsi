package com.gapsi.proveedor.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gapsi.proveedor.entidades.Proveedor;

//@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor, Integer> {

	public Proveedor findByNombre(String nombre);
}
