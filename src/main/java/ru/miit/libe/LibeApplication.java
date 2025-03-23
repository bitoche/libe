package ru.miit.libe;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Objects;

class EnvVar{
	String name;
	String className;
	public EnvVar(String n, String cn){
		name=n;
		className=cn;
	}
	@Override
	public String toString() {
		return "["+name+": "+className+"]";
	}
}

class EnvSettings{
	private static final Logger logger = LoggerFactory.getLogger("Main");
	public static void setEnvSettings(EnvVar[] requiredVars) throws Exception {
		logger.info("Loading .env ...");
		// Загружаем .env
		Dotenv dotenv = Dotenv.load();
		for (EnvVar variable : requiredVars){
			switch (variable.className){
				case "Str" -> {
					System.setProperty(variable.name, Objects.requireNonNull(dotenv.get(variable.name)));
				}
				case "Int" -> {
					// Нельзя загрузить значение в int в system.property?
					System.setProperty(variable.name, Objects.requireNonNull(dotenv.get(variable.name)));
				}
				case "BoolD" -> { // Парсит True/False в debug/false
					var notParsed = Objects.requireNonNull(dotenv.get(variable.name));

					if (notParsed.equalsIgnoreCase("true")){
						System.setProperty(variable.name, "DEBUG");
						logger.info("%s setted as DEBUG".formatted(variable));
					}
					else if (notParsed.equalsIgnoreCase("false")){
						System.setProperty(variable.name, "WARN");

					}
					else{
						throw new Exception("%s is not a boolean! (curr var is '%s')".formatted(notParsed, variable.name));
					}
				}
				default ->
						throw new Exception("ClassName %s is not supportred! (curr var is '%s')".formatted(variable.className, variable.name));
			}
			logger.info("%s setted as %s".formatted(variable, System.getProperty(variable.name)));
		}
	}
}

@SpringBootApplication
public class LibeApplication {
	static EnvVar[] needVars = {
			new EnvVar("DB_HOST", "Str"),
			new EnvVar("DB_PORT", "Int"),
			new EnvVar("DB_NAME", "Str"),
			new EnvVar("DB_USERNAME", "Str"),
			new EnvVar("DB_PASSWORD", "Str"),
			new EnvVar("DB_REPORTS_SCHEMA", "Str"),
			new EnvVar("DB_DEBUG", "Str"),
			new EnvVar("DEBUG", "Str"),
			new EnvVar("MAIL_USERNAME", "Str"),
			new EnvVar("MAIL_PASSWORD", "Str")
	};
	public static void main(String[] args) throws Exception {
		// подгружаем переменные из окружения
		EnvSettings.setEnvSettings(needVars);
		SpringApplication.run(LibeApplication.class, args);
	}

}
