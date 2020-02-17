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
				+ "h.id AS 'id_habilidad',\r\n" + "h.nombre AS 'habilidad'\r\n"
				+ "FROM pokemon.pokemon_has_habilidades ph \r\n" + "RIGHT JOIN pokemon p ON ph.id_pokemon = p.id \r\n"
				+ "LEFT JOIN habilidad h ON ph.id_habilidad = h.id ORDER BY p.id DESC LIMIT 500;";

		HashMap<Integer, Pokemon> pokeHash = new HashMap<Integer, Pokemon>();

		LOG.debug(sql);
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

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

		String sql = "SELECT \n" + "p.id AS 'id_pokemon',\n" + "p.nombre AS 'pokemon',\n" + "h.id AS 'id_habilidad',\n"
				+ "h.nombre AS 'habilidad'\n" + "FROM pokemon.pokemon_has_habilidades ph, pokemon p, habilidad h\n"
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

		String sql = "SELECT \n" + "p.id AS 'id_pokemon',\n" + "p.nombre AS 'pokemon',\n" + "h.id AS 'id_habilidad',\n"
				+ "h.nombre AS 'habilidad'\n" + "FROM pokemon.pokemon_has_habilidades ph, pokemon p, habilidad h\n"
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

		String sql = "INSERT INTO pokemon (nombre) VALUES (?);";
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			pst.setString(1, pojo.getNombre());

			int affectedRows = pst.executeUpdate();
			if (affectedRows == 1) {
				// conseguimos el ID que acabamos de crear
				ResultSet rs = pst.getGeneratedKeys();
				if (rs.next()) {
					pojo.setId(rs.getInt(1));
				}

			}

		}

		return pojo;
	}

	@Override
	public Pokemon update(int id, Pokemon pojo) throws Exception {
		// TODO Auto-generated method stub
		String sql = "UPDATE pokemon SET nombre = ? WHERE id = ?;";

		try (Connection con = ConnectionManager.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setString(1, pojo.getNombre());
			pst.setInt(2, id);

			LOG.debug(pst);

			int affectedRows = pst.executeUpdate(); // lanza una excepcion si nombre

			if (affectedRows == 1) {
				pojo.setId(id);
			} else {
				throw new Exception("No se encontro registro para id=" + id);
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
