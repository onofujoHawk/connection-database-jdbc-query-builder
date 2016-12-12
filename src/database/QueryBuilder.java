package database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueryBuilder {

	private static QueryBuilder instance = null;
	private StringBuilder queryBuilder = new StringBuilder();
	private String wildcard = "?";

	private QueryBuilder() {
	}

	public static synchronized QueryBuilder getInstance() {
		if (instance == null) {
			instance = new QueryBuilder();
		}
		return instance;
	}

	public String findAll(String entita) {
		return "SELECT * FROM " + entita.trim();
	}

	public String findById(String entita, Pair coppiaCampoValore) {
		cleanQueryBuilder();
		return queryBuilder.append("SELECT * FROM ").append(entita.trim()).append(" WHERE ")
				.append(coppiaCampoValore.getFirst()).append(" = ").append(coppiaCampoValore.getSecond()).toString();
	}

	public String insertSimple(String entita, List<Pair> valori) {
		cleanQueryBuilder();
		queryBuilder.append("INSERT INTO ").append(entita.trim()).append(" VALUES").append("(");
		for (Iterator<Pair> iterator = valori.iterator(); iterator.hasNext();) {
			queryBuilder.append("'").append(iterator.next().getSecond()).append("'");
			if (iterator.hasNext()) {
				queryBuilder.append(", ");
			}
		}
		return queryBuilder.append(")").toString();
	}

	public String updateSimple(String entita, List<Pair> coppiaCampoValore, Pair campoChiaveValore) {
		cleanQueryBuilder();
		queryBuilder.append("UPDATE ").append(entita.trim()).append(" SET ");
		for (Iterator<Pair> iterator = coppiaCampoValore.iterator(); iterator.hasNext();) {
			queryBuilder.append(iterator.next().getFirst()).append(" = ").append("'")
					.append(iterator.next().getSecond()).append("'");
			if (iterator.hasNext()) {
				queryBuilder.append(", ");
			}
		}
		return queryBuilder.append(" WHERE ").append(campoChiaveValore.getFirst()).append(" = ").append("'")
				.append(campoChiaveValore.getSecond()).append("'").toString();
	}

	public String deleteSimple(String entita, Pair coppiaCampoValore) {
		cleanQueryBuilder();
		return queryBuilder.append("DELETE FROM ").append(entita).append(" WHERE ").append(coppiaCampoValore.getFirst())
				.append(" = ").append("'").append(coppiaCampoValore.getSecond()).append("'").toString();
	}

	public String findByIdWildcard(String entita, String nomeCampo) {
		cleanQueryBuilder();
		return queryBuilder.append("SELECT * FROM ").append(entita.trim()).append(" WHERE ").append(nomeCampo)
				.append(" = ").append(wildcard).toString();
	}

	public String insertWildcard(String entita, List<String> nomeCampi) {
		cleanQueryBuilder();
		queryBuilder.append("INSERT INTO ").append(entita.trim()).append(" ");
		for (Iterator<String> iterator = nomeCampi.iterator(); iterator.hasNext();) {
			queryBuilder.append(iterator.next());
			if (iterator.hasNext()) {
				queryBuilder.append(", ");
			}
		}
		queryBuilder.append(" VALUES ").append("(");
		for (Iterator<String> iterator = nomeCampi.iterator(); iterator.hasNext();) {
			queryBuilder.append(wildcard);
			if (iterator.hasNext()) {
				queryBuilder.append(", ");
			}
		}
		return queryBuilder.append(")").toString();
	}

	public String updateWildcard(String entita, List<Pair> coppiaCampoValore, String nomeCampoId) {
		cleanQueryBuilder();
		queryBuilder.append("UPDATE ").append(entita).append(" SET ");
		for (Iterator<Pair> iterator = coppiaCampoValore.iterator(); iterator.hasNext();) {
			queryBuilder.append(iterator.next().getFirst()).append(" = ").append(wildcard);
			if (iterator.hasNext()) {
				queryBuilder.append(", ");
			}
		}
		return queryBuilder.append(" WHERE ").append(nomeCampoId).append(" = ").append(wildcard).toString();
	}

	public String deleteWildcard(String entita, String nomeCampo) {
		cleanQueryBuilder();
		return queryBuilder.append("DELETE FROM ").append(entita).append(" WHERE ")
				.append(nomeCampo).append(" = ").append(wildcard).toString();
	}

	public String findAllReflections(Class<?> className) {
		return queryBuilder.append("SELECT").append(" ").append("*").append(" ").append("FROM")
				.append(" ").append(className.getClass().getName().toLowerCase()).toString();
	}

	public String findByIdReflections(Class<?> className) {
		String campoId = className.getClass().getDeclaredFields()[0].getName();
		if (campoId.equalsIgnoreCase("id")) {
			return queryBuilder.append("SELECT").append(" ").append("*").append(" ").append("FROM").append(" ")
					.append(className.getClass().getName().toLowerCase()).append(" ").append("WHERE").append(" ")
					.append(campoId).append(" = ").append(wildcard).toString();
		}
		return null;
	}

	public String insertReflections(Class<?> className) {
		Field[] fields = className.getClass().getDeclaredFields();
		List<String> classfields = new ArrayList<String>();
		for (Field field : fields) {
			field.setAccessible(true);
			classfields.add(field.getName());
			System.out.println("Nome proprietà di classe " + className.getName() + " : " + field.getName());
		}
		cleanQueryBuilder();
		queryBuilder.append("INSERT INTO ").append(className.getClass().getName().toLowerCase()).append(" ");
		for (Iterator<String> iterator = classfields.iterator(); iterator.hasNext();) {
			queryBuilder.append(iterator.next());
			if (iterator.hasNext()) {
				queryBuilder.append(", ");
			}
		}
		queryBuilder.append(" VALUES ").append("(");
		for (Iterator<String> iterator = classfields.iterator(); iterator.hasNext();) {
			queryBuilder.append(wildcard);
			if (iterator.hasNext()) {
				queryBuilder.append(", ");
			}
		}
		return queryBuilder.append(")").toString();
	}

	public String updateReflections(Class<?> className) {
		Field[] fields = className.getClass().getDeclaredFields();
		List<String> fieldNames = new ArrayList<String>();
		for (Field field : fields) {
			field.setAccessible(true);
			fieldNames.add(field.getName());
		}
		cleanQueryBuilder();
		queryBuilder.append("UPDATE ").append(className.getClass().getName().toLowerCase()).append(" SET ");
		for (Iterator<String> iterator = fieldNames.iterator(); iterator.hasNext();) {
			queryBuilder.append(iterator.next()).append(" = ").append(wildcard);
			if (iterator.hasNext()) {
				queryBuilder.append(", ");
			}
		}
		return queryBuilder.append(" WHERE ").append(fieldNames.get(0)).append(" = ").append(wildcard).toString();
	}

	public String deleteReflections(Class<?> className) {
		String campoId = className.getClass().getDeclaredFields()[0].getName();
		if (campoId.equalsIgnoreCase("id")) {
			cleanQueryBuilder();
			return queryBuilder.append("DELETE FROM ").append(className.getClass().getName())
					.append(" WHERE ").append(campoId).append(" = ").append(wildcard).toString();
		}
		return null;
	}

	private void cleanQueryBuilder() {
		if (!isQueryBuilderEmpty()) {
			queryBuilder = new StringBuilder();
		}
	}

	private Boolean isQueryBuilderEmpty() {
		if (queryBuilder.length() > 0) {
			return false;
		}
		return true;
	}

}
