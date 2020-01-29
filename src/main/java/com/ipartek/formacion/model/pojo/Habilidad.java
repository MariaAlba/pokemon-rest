package com.ipartek.formacion.model.pojo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Habilidad {
 
	@NotNull
	private int id;
	
	@NotNull
	@Size(min=1, max=50)
	private String nombre;

	public Habilidad() {
		super();
		this.id = 0;
		this.nombre = "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return "Habilidad [id=" + id + ", nombre=" + nombre + "]";
	}

}
