package com.ipartek.formacion.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ipartek.formacion.model.pojo.Habilidad;

public class HabilidadDAO implements IHabilidadDAO {

	private final static Logger LOG = Logger.getLogger(HabilidadDAO.class);

	private static HabilidadDAO INSTANCE;

	private HabilidadDAO() {
		super();
	}

	public static synchronized HabilidadDAO getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new HabilidadDAO();
		}
		return INSTANCE;
	}

	@Override
	public List<Habilidad> getAll() {
		String sql = "SELECT id, nombre FROM habilidad ORDER BY id LIMIT 500";

		ArrayList<Habilidad> registros = new ArrayList<Habilidad>();

		LOG.debug(sql);
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				Habilidad h = new Habilidad();
				h.setId(rs.getInt("id"));
				h.setNombre(rs.getString("nombre"));
				registros.add(h);
			}

		} catch (Exception e) {
			LOG.error(e);
		}

		return registros;
	}

	@Override
	public Habilidad getById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Habilidad delete(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Habilidad update(int id, Habilidad pojo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Habilidad create(Habilidad pojo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
