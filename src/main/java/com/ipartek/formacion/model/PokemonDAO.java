package com.ipartek.formacion.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.ipartek.formacion.model.pojo.Habilidad;
import com.ipartek.formacion.model.pojo.Pokemon;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class PokemonDAO implements IPokemonDAO {

	private final static Logger LOG = Logger.getLogger(PokemonDAO.class);

	private static PokemonDAO INSTANCE;

	private PokemonDAO() {
		super();
	};

	public static synchronized PokemonDAO getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PokemonDAO();
		}
		return INSTANCE;
	}

	@Override
	public List<Pokemon> getAll() {

		String sql = "SELECT \r\n" + "p.id AS 'id_pokemon',\r\n" + "p.nombre AS 'pokemon',\r\n"
				+ "p.imagen AS 'imagen',\r\n" + "h.id AS 'id_habilidad',\r\n" + "h.nombre AS 'habilidad'\r\n"
				+ "FROM pokemon.pokemon_has_habilidades ph \r\n" + "RIGHT JOIN pokemon p ON ph.id_pokemon = p.id \r\n"
				+ "LEFT JOIN habilidad h ON ph.id_habilidad = h.id ORDER BY p.id DESC LIMIT 500;";

		HashMap<Integer, Pokemon> pokeHash = new HashMap<Integer, Pokemon>();

		LOG.debug(sql);

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			LOG.debug(pst);

			while (rs.next()) {
				mapper(pokeHash, rs);
			}

		} catch (Exception e) {
			LOG.error(e);
		}

		return new ArrayList<Pokemon>(pokeHash.values());
	}

	@Override
	public Pokemon getById(int id) {

		String sql = "SELECT \n" + "p.id AS 'id_pokemon',\n" + "p.nombre AS 'pokemon',\n" + "p.imagen AS 'imagen',\n"
				+ "h.id AS 'id_habilidad',\n" + "h.nombre AS 'habilidad'\n"
				+ "FROM pokemon.pokemon_has_habilidades ph, pokemon p, habilidad h\n"
				+ "WHERE ph.id_pokemon = p.id AND ph.id_habilidad = h.id\n" + "AND p.id = ?;";

		Pokemon p = null;

		HashMap<Integer, Pokemon> hm = new HashMap<Integer, Pokemon>();

		try (Connection con = ConnectionManager.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setInt(1, id);

			LOG.debug(pst);

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				p = mapper(hm, rs);
			}

		} catch (Exception e) {
			LOG.error(e);

		}

		return p;
	}

	@Override
	public ArrayList<Pokemon> searchByNameLike(String termino) throws Exception {

		String sql = "SELECT \n" + "p.id AS 'id_pokemon',\n" + "p.nombre AS 'pokemon',\n" + "p.imagen AS 'imagen',\n"
				+ "h.id AS 'id_habilidad',\n" + "h.nombre AS 'habilidad'\n"
				+ "FROM pokemon.pokemon_has_habilidades ph, pokemon p, habilidad h\n"
				+ "WHERE ph.id_pokemon = p.id AND ph.id_habilidad = h.id\n" + " AND p.nombre LIKE ? "
				+ " ORDER BY p.id DESC\n" + "LIMIT 500;";

		HashMap<Integer, Pokemon> pokeHash = new HashMap<Integer, Pokemon>();

		try (Connection con = ConnectionManager.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setString(1, "%" + termino + "%");

			LOG.debug(pst);

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				mapper(pokeHash, rs);
			}

		} catch (Exception e) {
			LOG.error(e);
		}

		return new ArrayList<Pokemon>(pokeHash.values());
	}

	@Override
	public Pokemon delete(int id) throws Exception {
		String sql = "DELETE FROM pokemon where id = ?;";

		Pokemon p = null;

		try (Connection con = ConnectionManager.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setInt(1, id);

			p = this.getById(id); // recuperar

			int affectedRows = pst.executeUpdate(); // eliminar

			if (affectedRows != 1) {
				p = null;
				throw new Exception("No se puede eliminar " + p);
			}

		}

		return p;
	}

	@Override
	public Pokemon create(Pokemon pojo) throws Exception {

		String sql = "INSERT INTO pokemon (nombre, imagen) VALUES (?,?);";

		Connection con = null;

		try {

			con = ConnectionManager.getConnection();
			con.setAutoCommit(false);

			// Insert en tabla pokemon + obtener id generado
			PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			pst.setString(1, pojo.getNombre());
			pst.setString(2, pojo.getImagen());

			int affectedRows = pst.executeUpdate();
			if (affectedRows == 1) {
				// conseguimos el ID que acabamos de crear
				ResultSet rs = pst.getGeneratedKeys();
				if (rs.next()) {
					pojo.setId(rs.getInt(1));
				}
			}

			ArrayList<Habilidad> habilidades = pojo.getHabilidades();
			String sql2 = "INSERT INTO pokemon_has_habilidades (id_pokemon, id_habilidad) VALUES (?, ?)";
			PreparedStatement pst2 = con.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
			pst2.setInt(1, pojo.getId());

			if (habilidades.size() > 0) {

				// por cada habilidad (si las tiene)
				// insert tabla pokemon_has_habilidades
				for (Habilidad h : habilidades) {
					pst2.setInt(2, h.getId());
					int affectedRows2 = pst2.executeUpdate();
				}

			}

			// SI Todas las cosas OK
			con.commit();

		} catch (MySQLIntegrityConstraintViolationException e) {
			throw e;
		} catch (Exception e) {
			con.rollback();
			throw new Exception(e);
		} finally {
			// cierra conexion
			if (con != null) {
				con.close();
			}
		}

		return pojo;
	}

	private void borrarHabilidades(int idPokemon) throws SQLException {
		boolean correcto;
		String sql = "DELETE FROM  pokemon_has_habilidades  WHERE id_pokemon = ?";

		try (Connection con = ConnectionManager.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setInt(1, idPokemon);

			int affectedRows = pst.executeUpdate(); // eliminar

			if (affectedRows != 1) {

			}

		}

	}

	@Override
	public Pokemon update(int id, Pokemon pojo) throws Exception {

		borrarHabilidades(id);

		String sql = "UPDATE pokemon SET nombre = ?, imagen = ?  WHERE id = ?;";

		Connection con = null;

		try {

			con = ConnectionManager.getConnection();
			con.setAutoCommit(false);

			PreparedStatement pst = con.prepareStatement(sql);

			pst.setString(1, pojo.getNombre());
			pst.setString(2, pojo.getImagen());
			pst.setInt(3, id);

			LOG.debug(pst);

			int affectedRows = pst.executeUpdate();
			if (affectedRows == 1) {
				pojo.setId(id);
			} else {
				throw new Exception("No se encontro registro para id=" + id);
			}

			ArrayList<Habilidad> habilidades = pojo.getHabilidades();
			String sql3 = "INSERT INTO pokemon_has_habilidades (id_pokemon, id_habilidad) VALUES (?, ?)";
			PreparedStatement pst3 = con.prepareStatement(sql3);
			pst3.setInt(1, pojo.getId());

			if (habilidades.size() > 0) {

				// por cada habilidad (si las tiene)
				// insert tabla pokemon_has_habilidades
				for (Habilidad h : habilidades) {
					if (h != null) {

						pst3.setInt(2, h.getId());
						int affectedRows3 = pst3.executeUpdate();
					}
				}

			}

			// SI todas las cosas OK
			con.commit();

		} catch (MySQLIntegrityConstraintViolationException e) {
			throw e;
		} catch (Exception e) {
			con.rollback();
			throw new Exception(e);
		} finally {
			// cierra conexion
			if (con != null) {
				con.close();
			}
		}

		return pojo;

	}

	/**
	 * Utilidad para mapear un ResultSet a un Pokemon
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private Pokemon mapper(HashMap<Integer, Pokemon> hm, ResultSet rs) throws Exception {

		int idPokemon = rs.getInt("id_pokemon");
		Pokemon p = hm.get(idPokemon);
		if (p == null) {
			p = new Pokemon();
			p.setId(idPokemon);
			p.setNombre(rs.getString("pokemon"));
			p.setImagen(rs.getString("imagen"));
		}

		Habilidad h = new Habilidad();
		h.setId(rs.getInt("id_habilidad"));
		h.setNombre(rs.getString("habilidad"));

		if (h.getId() != 0) {
			p.getHabilidades().add(h);
		}

		hm.put(idPokemon, p);

		return p;
	}

}
