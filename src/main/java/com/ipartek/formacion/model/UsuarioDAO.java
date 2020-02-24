package com.ipartek.formacion.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ipartek.formacion.model.pojo.Usuario;

public class UsuarioDAO implements IUsuarioDAO {

	private final static Logger LOG = Logger.getLogger(UsuarioDAO.class);

	private static UsuarioDAO INSTANCE;

	private UsuarioDAO() {
		super();
	};

	public static synchronized UsuarioDAO getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UsuarioDAO();
		}
		return INSTANCE;
	}

	@Override
	public List<Usuario> getAll() {
		String sql = "SELECT \r\n" + "id, nombre, password \r\n" + "FROM usuario;";

		ArrayList<Usuario> registros = new ArrayList<Usuario>();

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				Usuario u = new Usuario();
				u.setId(rs.getInt("id"));
				u.setNombre(rs.getString("nombre"));
				u.setPassword(rs.getString("password"));
				registros.add(u);
			}

		} catch (Exception e) {
			LOG.error(e);
		}
		return registros;
	}

	@Override
	public Usuario getById(int id) {
		String sql = "SELECT \\r\\n\" + \"id, nombre, password \\r\\n\" + \"FROM usuario WHERE id = ? ";

		Usuario u = null;

		try (Connection con = ConnectionManager.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setInt(1, id);

			LOG.debug(pst);

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				u = new Usuario();
				u.setId(rs.getInt("id"));
				u.setNombre(rs.getString("nombre"));
				u.setPassword(rs.getString("password"));
			}

		} catch (Exception e) {
			LOG.error(e);

		}

		return u;
	}

	public Usuario isLogged(String nombre, String pass) {

		String sql = "SELECT id, nombre, password FROM pokemon WHERE nombre = ?  AND password = ?;";

		Usuario u = null;

		try (Connection con = ConnectionManager.getConnection(); PreparedStatement pst = con.prepareStatement(sql);) {

			pst.setString(1, nombre);
			pst.setString(2, pass);
			LOG.debug(pst);

			try (ResultSet rs = pst.executeQuery()) {

				if (rs.next()) {
					u = new Usuario();
					u.setId(rs.getInt("id"));
					u.setNombre(rs.getString("nombre"));
					u.setPassword(rs.getString("password"));
				}
			}

		} catch (Exception e) {
			LOG.error(e);
		}

		return u;
	}

	@Override
	public Usuario delete(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Usuario update(int id, Usuario pojo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Usuario create(Usuario pojo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
