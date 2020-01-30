package com.ipartek.formacion.model.pojo;

import java.util.ArrayList;

public class MensajeResponse {

	private String texto;
	private ArrayList<String> errores;

	public MensajeResponse() {
		super();
		this.texto = "";
		this.errores = new ArrayList<String>();
	}

	public MensajeResponse(String texto) {
		this();
		this.texto = texto;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public ArrayList<String> getErrores() {
		return errores;
	}

	public void setErrores(ArrayList<String> errores) {
		this.errores = errores;
	}

	@Override
	public String toString() {
		return "MensajeResponse [texto=" + texto + ", errores=" + errores + "]";
	}

}
