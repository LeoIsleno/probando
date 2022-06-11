package com.seleniuim.test;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.selenium.driver.Drivers;

import pageObjects.despegarAlojamientosPage;
import pageObjects.despegarMainPage;
import pageObjects.despegarResoultsPage;

public class despegarTest extends Drivers {
	WebDriver driver;
	
	@BeforeMethod
	 public void setup(ITestContext context) throws InterruptedException{
		String url = "https://www.despegar.com.ar";
		String Browser = context.getCurrentXmlTest().getParameter("Navegador");
		String navegador = Browser != null ? Browser : "CHROME";
		
		//Inicializamos con el browser a utilizar
		driver = despegarTest.LevantarBrowser(navegador, url);
	}
	
	@Test
	public void ValidarDespegar() throws Exception {
		//Concatenacion de paginas
		despegarMainPage mainPage = new despegarMainPage(driver);
		despegarAlojamientosPage alojamientosPage = mainPage.goToAlojamientos();
		
		//Realizamos el test
		alojamientosPage.IngresarTexto("Bariloche");
		alojamientosPage.EliminarCookies();
		alojamientosPage.IngresarEntrada();
		alojamientosPage.IngresarSalida();
		alojamientosPage.BotonAplicar(1);
		alojamientosPage.IngresarHabitaciones();
		alojamientosPage.AumentarCantidadAdultos();
		alojamientosPage.AumentarCantidadNiños();
		alojamientosPage.EdadNiño();
		alojamientosPage.BotonAplicar(2);
		alojamientosPage.BotonAplicar(3);
		despegarResoultsPage resoultsPage = alojamientosPage.BuscarResultados();
		resoultsPage.VerDetalles();
		Thread.sleep(5000);
		}
	
	@AfterMethod
	public void endsetuo(){
		driver.close();
	}
}
  