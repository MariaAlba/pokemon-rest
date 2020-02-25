package com.ipartek.formacion.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ipartek.formacion.model.pojo.Pokemon;

public class PokemonTest {

	@Test
	public void test() {
		Pokemon p = new Pokemon();

		assertEquals(0, p.getHabilidades().size());
	}

}
