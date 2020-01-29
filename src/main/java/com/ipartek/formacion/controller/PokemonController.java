package com.ipartek.formacion.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ipartek.formacion.model.PokemonDAO;
import com.ipartek.formacion.model.pojo.Pokemon;
import com.ipartek.formacion.utils.Utilidades;



/**
 * Servlet implementation class PokemonController
 */
@WebServlet("/api/pokemon/*")
public class PokemonController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final static Logger LOG = Logger.getLogger(PokemonController.class);

	private PokemonDAO pokemonDAO;
	
	private ValidatorFactory factory;
	private Validator validator;
	
	private String pathInfo;
	private int statusCode;
	Object responseBody;
	
	private int idPokemon;


	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		pokemonDAO = PokemonDAO.getInstance();
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		pokemonDAO = null;
		factory = null;
		validator = null;
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		pathInfo = request.getPathInfo();
		LOG.debug(request.getMethod() + " " + request.getRequestURL());
		
		
		

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		responseBody = null;
		

		
		try {

			idPokemon = Utilidades.obtenerId(pathInfo);

			// llama a doGEt, doPost, doPut, doDelete
			super.service(request, response);

		} catch (Exception e) {

			statusCode = HttpServletResponse.SC_BAD_REQUEST;
			

		} finally {

			response.setStatus(statusCode);

			if (responseBody != null) {
				// response body
				PrintWriter out = response.getWriter(); // out se encarga de poder escribir datos en el body
				Gson json = new Gson(); // conversion de Java a Json
				out.print(json.toJson(responseBody));
				out.flush();
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
		ArrayList<Pokemon> lista = new ArrayList<Pokemon>();
		String termino = request.getParameter("buscar");
		
		if(termino!=null && !termino.isEmpty()) {
			try {
				lista = (ArrayList<Pokemon>) pokemonDAO.searchLike(termino);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error(e);
			}
		}
		else {
			lista = (ArrayList<Pokemon>) pokemonDAO.getAll();
		}
		

		if (lista.isEmpty()) {
			statusCode = HttpServletResponse.SC_NO_CONTENT;
		} else {
			statusCode = HttpServletResponse.SC_OK;
			responseBody = lista;
		}

		// response body

//		PrintWriter out = response.getWriter(); // out se encarga de escribir datos en el body
//
//		String jsonResponseBody = new Gson().toJson(lista); // conversion de java a json
//
//		out.print(jsonResponseBody.toString());
//
//		out.flush(); // termina de escribir datos en body cierra el out

		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
