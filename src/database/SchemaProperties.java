package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SchemaProperties {

	private static SchemaProperties instance = null;
	private Properties properties = new Properties();

	private SchemaProperties() {
	}

	public static synchronized SchemaProperties getInstance() {
		if (instance == null) {
			instance = new SchemaProperties();
		}
		return instance;
	}

	public void caricaProprietà() throws IOException {
		InputStream inputStream = new FileInputStream("connector.properties");
		properties.load(inputStream);
	}

	public String parsingDriverManager() {
		return properties.getProperty("database.driverClassName");
	}

	public String parsingURL() {
		return properties.getProperty("database.url");
	}

	public String parsingUsername() {
		return properties.getProperty("database.username");
	}

	public String parsingPassword() {
		return properties.getProperty("database.password");
	}

}
