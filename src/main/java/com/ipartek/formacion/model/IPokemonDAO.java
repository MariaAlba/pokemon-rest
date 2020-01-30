package com.ipartek.formacion.model;

import java.util.List;

import com.ipartek.formacion.model.pojo.Pokemon;

public interface IPokemonDAO extends IDAO<Pokemon> {

	List<Pokemon> searchByNameLike(String termino) throws Exception;

}
