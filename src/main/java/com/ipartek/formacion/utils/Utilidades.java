package com.ipartek.formacion.utils;

public class Utilidades {

	public static int obtenerId(String pathInfo) throws Exception {
		// throw new Exception("Sin implementar, primero Test!!!");
		int id = -1;

		if (pathInfo == null || "/".equals(pathInfo)) {
			id = -1;
		} else {

			String[] pathInfoParts = pathInfo.split("/");

			if (pathInfoParts.length > 2) {
				throw new Exception("url mal formada");
			}

			if (!pathInfoParts[1].matches("\\d+")) {
				throw new Exception("url mal formada");
			} else {
				id = Integer.parseInt(pathInfoParts[1]);
			}
		}

		return id;
	}
	
}
