package com.ipartek.formacion.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.ipartek.formacion.model.pojo.Pokemon;

public class PokemonTest {

	@Test
	public void test() {
		Pokemon p = new Pokemon();
		assertNotNull(p);
		assertEquals(0, p.getId());
		assertEquals("", p.getNombre());
		assertEquals(0, p.getHabilidades().size());
	}

}
