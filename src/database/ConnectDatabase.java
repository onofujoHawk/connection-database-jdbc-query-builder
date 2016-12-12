package database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConnectDatabase {

	private Connection connection;
	private PreparedStatement statement;
	private ResultSet resultSet;
	private QueryBuilder instanceQuery = QueryBuilder.getInstance();
	private SchemaProperties instanceDatabase = SchemaProperties.getInstance();

	public void caricaDriver() throws ClassNotFoundException, IOException {
		instanceDatabase.caricaProprietà();
		Class.forName(instanceDatabase.parsingDriverManager());
		Locale.setDefault(Locale.ENGLISH);
	}

	public void apriConnessione() throws SQLException {
		String URL = instanceDatabase.parsingPassword();
		String username = instanceDatabase.parsingUsername();
		String password = instanceDatabase.parsingPassword();
		connection = DriverManager.getConnection(URL, username, password);
		connection.setAutoCommit(true);
	}

	public void chiudiConnessione() throws SQLException {
		// rilascio le risorse utilizzate
		if (!isClosed()) {
			connection.close();
			statement.close();
			resultSet.close();
		}
	}

	private Boolean isClosed() throws SQLException {
		if (connection.isClosed() && statement.isClosed() && resultSet.isClosed()) {
			return true;
		}
		return false;
	}

	public void eseguiQuery() throws ClassNotFoundException, SQLException, IOException {
		caricaDriver();
		apriConnessione();
		// creazione del PreparedStatement, l'oggetto che utilizzo per spedire i
		// comandi SQL al database
		statement = connection.prepareStatement(instanceQuery.findAll("customers"));
		// eseguo la query attraverso lo statement e recupero i risultati
		// attraverso l'interfaccia ResultSet
		resultSet = statement.executeQuery();

		// scandisco il ResultSet per la visualizzazione delle informazioni
		// recuperate
		while (resultSet.next()) {
			System.out.println("\n*********************************");
			System.out.println("  Nome : " + resultSet.getString(2));
			System.out.println("  Cognome : " + resultSet.getString(3));
			System.out.println("  Telefono : " + resultSet.getString(4));
			System.out.println("  Email : " + resultSet.getString(5));
			System.out.println("  Categoria : " + resultSet.getString(6));
			System.out.println("*********************************");
		}

		// ricerca fatta con wildcard query
		statement = connection.prepareStatement(instanceQuery.findByIdWildcard("customers", "Id"));
		statement.setString(1, "3");
		resultSet = statement.executeQuery();

		// scandisco il ResultSet per la visualizzazione delle informazioni
		// recuperate
		while (resultSet.next()) {
			System.out.println("\n*********************************");
			System.out.println("  Nome : " + resultSet.getString(2));
			System.out.println("  Cognome : " + resultSet.getString(3));
			System.out.println("  Telefono : " + resultSet.getString(4));
			System.out.println("  Email : " + resultSet.getString(5));
			System.out.println("  Categoria : " + resultSet.getString(6));
			System.out.println("*********************************");
		}

		// inserimento attraverso simple query builder e utility class Pair
		List<Pair> coppiaValori = new ArrayList<Pair>();
		coppiaValori.add(new Pair("id", "206"));
		coppiaValori.add(new Pair("nome", "test_nome"));
		coppiaValori.add(new Pair("cognome", "test_cognome"));
		coppiaValori.add(new Pair("telefono", "555-0392"));
		coppiaValori.add(new Pair("email", "info@customers.com"));
		coppiaValori.add(new Pair("categoria", "test_categoria"));
		statement.executeQuery(instanceQuery.insertSimple("customers", coppiaValori));

		chiudiConnessione();
	}

}
