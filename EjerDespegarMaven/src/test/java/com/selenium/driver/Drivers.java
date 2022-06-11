package com.selenium.driver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Reporter;

public class Drivers {

	public static WebDriver LevantarBrowser(String navegador, String url) throws InterruptedException
	{
		WebDriver driver = null;
		switch(navegador) {
		
		case "CHROME":
			//Se define la ubicacion del driver a utilizar
			System.setProperty("webdriver.chrome.driver", "C:\\Users\\Usuario\\Documents\\Documentos Vates\\Testing Automatizado\\chromedriver.exe");
			Reporter.log("Abrir Navegador Chrome");
			//Se crea un nuevo webDriver
			driver = new ChromeDriver();
			//maximiza la pantalla
			driver.manage().window().maximize();
			break;
		
		case "FIREFOX":
			//Se define la ubicacion del driver a utilizar
			System.setProperty("webdriver.gecko.driver", "C:\\Users\\Usuario\\Documents\\Documentos Vates\\Testing Automatizado\\geckodriver.exe");
			Reporter.log("Abrir Navegador FOREFOX");
			//Se crea un nuevo webDriver
			driver = new FirefoxDriver();
			//maximiza la pantalla
			driver.manage().window().maximize();
			break;
			
		default:
			//Se define la ubicacion del driver a utilizar
			System.setProperty("webdriver.chome.driver", "C:\\Users\\Usuario\\Documents\\Documentos Vates\\Testing Automatizado\\chromedriver.exe");
			Reporter.log("Abrir Navegador Chrome");
			//Se crea un nuevo webDriver
			driver = new ChromeDriver();
			//maximiza la pantalla
			driver.manage().window().maximize();
			break;
		}
		//Ingresa al URL seleccionado
		driver.get(url);
		Thread.sleep(1000);
		
		//Le quitamos zoom a la pagina
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript("document.body.style.zoom = '1'");
		
		return driver;
	}
}