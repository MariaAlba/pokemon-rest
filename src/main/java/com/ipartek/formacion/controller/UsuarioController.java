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
import javax.servlet.http.HttpSession;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.ipartek.formacion.model.UsuarioDAO;
import com.ipartek.formacion.model.pojo.Usuario;
import com.ipartek.formacion.utils.Utilidades;

/**
 * Servlet implementation class UsuarioController
 */
@WebServlet("/api/usuario/*")
public class UsuarioController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final static Logger LOG = Logger.getLogger(UsuarioController.class);

	private UsuarioDAO usuarioDAO;

	private ValidatorFactory factory;
	private Validator validator;

	private String pathInfo;
	private int statusCode;
	Object responseBody;

	private int idUsuario;
	private boolean hasParams;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		usuarioDAO = UsuarioDAO.getInstance();
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		usuarioDAO = null;
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

			idUsuario = Utilidades.obtenerId(pathInfo);

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

		ArrayList<Usuario> listado = new ArrayList<Usuario>();
		try {

			if (idUsuario != -1) {
				Usuario u = null;
				u = usuarioDAO.getById(idUsuario);

				HttpSession session = request.getSession();
				session.setAttribute("usuarioLogeado", "Administrador");

				session.setMaxInactiveInterval(-1); // nunca caduca
				statusCode = (u != null) ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND;
				responseBody = u;
			} else {

				listado = (ArrayList<Usuario>) usuarioDAO.getAll();
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
