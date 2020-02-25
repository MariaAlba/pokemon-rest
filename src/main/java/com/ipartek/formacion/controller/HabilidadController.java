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
import com.ipartek.formacion.model.HabilidadDAO;
import com.ipartek.formacion.model.pojo.Habilidad;
import com.ipartek.formacion.utils.Utilidades;

/**
 * Servlet implementation class HabilidadController
 */
@WebServlet("/api/habilidad/*")
public class HabilidadController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(HabilidadController.class);

	private HabilidadDAO habilidadDAO;

	private ValidatorFactory factory;
	private Validator validator;

	private String pathInfo;
	private int statusCode;
	Object responseBody;

	private int idHabilidad;
	private boolean hasParams;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		habilidadDAO = HabilidadDAO.getInstance();
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	/**
	 * @see Servlet#destroy()
	 */
	@Override
	public void destroy() {
		habilidadDAO = null;
		factory = null;
		validator = null;
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
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

			idHabilidad = Utilidades.obtenerId(pathInfo);

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
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ArrayList<Habilidad> listado = new ArrayList<Habilidad>();
		try {

			if (idHabilidad != -1) {
				Habilidad h = null;
				h = habilidadDAO.getById(idHabilidad);
				statusCode = (h != null) ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND;
				responseBody = h;
			} else {

				listado = (ArrayList<Habilidad>) habilidadDAO.getAll();
				statusCode = (listado.isEmpty()) ? HttpServletResponse.SC_NO_CONTENT : HttpServletResponse.SC_OK;
				responseBody = listado;

			}
		} catch (Exception e) {

			LOG.error(e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
