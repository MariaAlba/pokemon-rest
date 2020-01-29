package com.ipartek.formacion.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ipartek.formacion.controller.PokemonController;
import com.ipartek.formacion.model.pojo.Habilidad;
import com.ipartek.formacion.model.pojo.Pokemon;

public class PokemonDAO implements IDAO<Pokemon> {
	
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
		
		String sql = "SELECT \n" + 
				"p.id AS 'id_pokemon',\n" + 
				"p.nombre AS 'pokemon',\n" + 
				"h.id AS 'id_habilidad',\n" + 
				"h.nombre AS 'habilidad'\n" + 
				"FROM pokemon.pokemon_has_habilidades ph, pokemon p, habilidad h\n" + 
				"WHERE ph.id_pokemon = p.id AND ph.id_habilidad = h.id\n" + 
				"ORDER BY p.id DESC\n" + 
				"LIMIT 500;";

		ArrayList<Pokemon> registros = new ArrayList<Pokemon>();
		
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			
			HashMap<Integer, Pokemon> pokeHash = new HashMap<Integer, Pokemon>();
		
			ArrayList<Habilidad> habilidades = new ArrayList<Habilidad>();
			
			while (rs.next()) {
				// TODO mapper

				Pokemon p = new Pokemon();
				Habilidad h = new Habilidad();
				
				p.setId(rs.getInt("id_pokemon"));
				p.setNombre(rs.getString("pokemon"));
				h.setId(rs.getInt("id_habilidad"));
				h.setNombre(rs.getString("habilidad"));

				if(pokeHash.containsKey(p.getId())) {
					
					habilidades.add(h);
					p.setHabilidades(habilidades);
				}else {
					habilidades = new ArrayList<Habilidad>();
					habilidades.add(h);
					p.setHabilidades(habilidades);
					pokeHash.put(p.getId(), p);
				
				}
				LOG.debug(pokeHash);
				
				
				
				
				
			}
		
			Set<Integer> keys = pokeHash.keySet();
			for(Integer i: keys) {
				registros.add(pokeHash.get(i));
			}
			
			

		} catch (Exception e) {
			// TODO: LOG
			e.printStackTrace();
		}

		return registros;
	}

	@Override
	public Pokemon getById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pokemon delete(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pokemon update(int id, Pokemon pojo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pokemon create(Pokemon pojo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public ArrayList<Pokemon> searchLike(String termino) throws Exception{
		String sql = "SELECT \n" + 
				"p.id AS 'id_pokemon',\n" + 
				"p.nombre AS 'pokemon',\n" + 
				"h.id AS 'id_habilidad',\n" + 
				"h.nombre AS 'habilidad'\n" + 
				"FROM pokemon.pokemon_has_habilidades ph, pokemon p, habilidad h\n" + 
				"WHERE ph.id_pokemon = p.id AND ph.id_habilidad = h.id\n" + 
				" AND p.nombre LIKE ? "+
				" ORDER BY p.id DESC\n" + 
				"LIMIT 500;";

		ArrayList<Pokemon> registros = new ArrayList<Pokemon>();
		
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(sql)){
					
			pst.setString(1, "%"+termino+"%");
			
			LOG.debug(pst);
					
			ResultSet rs = pst.executeQuery();
			
			
			HashMap<Integer, Pokemon> pokeHash = new HashMap<Integer, Pokemon>();
		
			ArrayList<Habilidad> habilidades = new ArrayList<Habilidad>();
			
			while (rs.next()) {
				// TODO mapper

				Pokemon p = new Pokemon();
				Habilidad h = new Habilidad();
				
				p.setId(rs.getInt("id_pokemon"));
				p.setNombre(rs.getString("pokemon"));
				h.setId(rs.getInt("id_habilidad"));
				h.setNombre(rs.getString("habilidad"));

				if(pokeHash.containsKey(p.getId())) {
					
					habilidades.add(h);
					p.setHabilidades(habilidades);
				}else {
					habilidades = new ArrayList<Habilidad>();
					habilidades.add(h);
					p.setHabilidades(habilidades);
					pokeHash.put(p.getId(), p);
				
				}
				LOG.debug(pokeHash);
				
				
				
				
				
			}
		
			Set<Integer> keys = pokeHash.keySet();
			for(Integer i: keys) {
				registros.add(pokeHash.get(i));
			}
			
			

		} catch (Exception e) {
			// TODO: LOG
			e.printStackTrace();
		}

		return registros;
	}

}
