package com.ipartek.formacion.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
	private boolean hasParams;

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

		String termino = request.getParameter("nombre");
		hasParams = (termino != null && !termino.isEmpty()) ? true : false;

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

		if (hasParams) {
			String termino = request.getParameter("nombre");
			try {
				lista = (ArrayList<Pokemon>) pokemonDAO.searchByNameLike(termino);
				statusCode = lista.isEmpty() ? HttpServletResponse.SC_NO_CONTENT : HttpServletResponse.SC_OK;
				responseBody = lista;
			} catch (Exception e) {
				statusCode = HttpServletResponse.SC_CONFLICT;
				LOG.error(e);
			}

		} else {

			try {

				if (idPokemon != -1) {
					Pokemon p = null;
					p = pokemonDAO.getById(idPokemon);
					statusCode = (p != null) ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND;
					responseBody = p;
				} else {

					lista = (ArrayList<Pokemon>) pokemonDAO.getAll();
					statusCode = (lista.isEmpty()) ? HttpServletResponse.SC_NO_CONTENT : HttpServletResponse.SC_OK;
					responseBody = lista;

				}
			} catch (Exception e) {

				LOG.error(e);
			}
		}

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
