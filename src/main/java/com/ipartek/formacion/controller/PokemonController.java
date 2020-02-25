package com.ipartek.formacion.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.ipartek.formacion.model.PokemonDAO;
import com.ipartek.formacion.model.pojo.MensajeResponse;
import com.ipartek.formacion.model.pojo.Pokemon;
import com.ipartek.formacion.utils.Utilidades;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

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

	private static String pathInfo;
	private static int statusCode;
	private static Object responseBody;

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

		Pokemon pOut = null;
		try {
			// convertir json del request body a Objeto
			BufferedReader reader = request.getReader();
			Gson gson = new Gson();
			Pokemon p = gson.fromJson(reader, Pokemon.class);
			LOG.debug("pokemon p: " + p);

			// validar objeto
			Set<ConstraintViolation<Pokemon>> validacionesErrores = validator.validate(p);

			if (validacionesErrores.isEmpty()) {
				if (p.getId() == 0) {
					pOut = pokemonDAO.create(p);
					statusCode = HttpServletResponse.SC_CREATED;
					responseBody = pOut;

				} else {
					pOut = pokemonDAO.update(p.getId(), p);
					statusCode = HttpServletResponse.SC_OK;
					responseBody = pOut;
				}

			} else {
				MensajeResponse responseMensaje = new MensajeResponse("Valores no correctos");
				ArrayList<String> errores = new ArrayList<String>();

				statusCode = HttpServletResponse.SC_BAD_REQUEST;

				for (ConstraintViolation<Pokemon> error : validacionesErrores) {
					errores.add(error.getPropertyPath() + " " + error.getMessage());
				}
				responseMensaje.setErrores(errores);

				responseBody = responseMensaje;
			}
		} catch (MySQLIntegrityConstraintViolationException e) {
			statusCode = HttpServletResponse.SC_CONFLICT;
			responseBody = new MensajeResponse(e.getMessage());

		} catch (SQLException e) {
			statusCode = HttpServletResponse.SC_CONFLICT;
			// entra con nombre repetido
			String texto = "Nombre del pokemon ";
			responseBody = new MensajeResponse(texto + " - " + e.getMessage());
		} catch (Exception e) {
			statusCode = HttpServletResponse.SC_CONFLICT;
			String texto = e.getMessage();
			if (texto.contains("nombre_UNIQUE")) {
				texto = "El nombre del pokemon ya existe";
			}
			if (texto.contains("habilidad")) {
				texto = "No puede añadir una habilidad inexistente";
			}
			responseBody = new MensajeResponse(texto);
		}
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Pokemon p = null;

		// TODO como mostrar el pokemon cuando no tiene habilidades
		// mirar inner join del get by id

		try {

			p = pokemonDAO.delete(idPokemon);
			statusCode = HttpServletResponse.SC_OK;
			responseBody = p;

		} catch (Exception e) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
			responseBody = null;
			LOG.error(e);
		}

	}

}
