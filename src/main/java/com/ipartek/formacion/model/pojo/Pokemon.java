package com.ipartek.formacion.model.pojo;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class Pokemon {

	@NotNull
	private int id;

	@NotNull
	@NotBlank
	@Size(min = 1, max = 50)
	private String nombre;

	@NotNull
	private String imagen;

	private ArrayList<Habilidad> habilidades;

	public Pokemon() {
		super();
		this.id = 0;
		this.nombre = "";
		this.imagen = "https://f0.pngfuel.com/png/295/911/pokemon-pokeball-png-clip-art.png";
		this.habilidades = new ArrayList<Habilidad>();
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

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public ArrayList<Habilidad> getHabilidades() {
		return habilidades;
	}

	public void setHabilidades(ArrayList<Habilidad> habilidades) {
		this.habilidades = habilidades;
	}

	@Override
	public String toString() {
		return "Pokemon [id=" + id + ", nombre=" + nombre + ", imagen=" + imagen + ", habilidades=" + habilidades + "]";
	}

}
