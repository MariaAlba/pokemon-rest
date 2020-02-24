package com.ipartek.formacion.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpCookie;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Validation;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.ipartek.formacion.model.UsuarioDAO;
import com.ipartek.formacion.model.pojo.Usuario;

/**
 * Servlet implementation class LoginController
 */
@WebServlet("/login")
public class LoginController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	


	private final static Logger LOG = Logger.getLogger(LoginController.class);

	private UsuarioDAO usuarioDAO;
       
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		usuarioDAO = UsuarioDAO.getInstance();		
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		usuarioDAO = null;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		Cookie[] cookie = request.getCookies();
		
		if(session.getAttribute("usuarioStorage") != null) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader reader = request.getReader();
		Gson gson = new Gson();
		Usuario usuario= null;
		usuario = gson.fromJson(reader, Usuario.class);
		Object responseBody = null; 

		// Probar que esta en la BD.

		Usuario usuarioLogeado = usuarioDAO.isLogged(usuario.getNombre(),usuario.getPassword());

		if(usuarioLogeado != null) {
			// Si el login es correcto creamos la sesion y respondemos con codigo 200

			HttpSession session = request.getSession();
			session.setAttribute("usuarioStorage", usuarioLogeado);
			session.setMaxInactiveInterval(600);
			
			responseBody = usuarioLogeado;

			response.setStatus(HttpServletResponse.SC_OK);
			PrintWriter out = response.getWriter(); // out se encarga de poder escribir datos en el body
			Gson json = new Gson(); // conversion de Java a Json
			out.print(json.toJson(responseBody));
			out.flush();


		} else {
			// Si el login no es correcto pues tendremos que responder con el codigo 401
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	
	
	}

}
