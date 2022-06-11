package com.seleniuim.test;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.selenium.driver.Drivers;

import pageObjects.despegarAlojamientosPage;
import pageObjects.despegarMainPage;
import pageObjects.despegarResoultsPage;

public class despegarTest extends Drivers {
	WebDriver driver;
	//Concatenacion de paginas
			
	
	@BeforeMethod(alwaysRun = true)
	 public void setup(ITestContext context) throws InterruptedException{
		String url = "https://www.despegar.com.ar";
		String Browser = context.getCurrentXmlTest().getParameter("Navegador");
		String navegador = Browser != null ? Browser : "CHROME";
		
		//Inicializamos con el browser a utilizar
		driver = despegarTest.LevantarBrowser(navegador, url);
	}
	
	@DataProvider (name = "DataProviderDespegar")
	public Object[][] dpDespegar(){
		return new Object[][] {{"Bariloche"},{"San Juan"},{"Córdoba"}};
	}
	
	
	@Test(groups =  "AlojamiendoDespegar" , dataProvider = "DataProviderDespegar")
	public void ValidarAlojamiendoDespegar(String searchText) throws Exception {
		despegarMainPage mainPage = new despegarMainPage(driver);
		despegarAlojamientosPage alojamientosPage = mainPage.goToAlojamientos();
		
		//Realizamos el test
		alojamientosPage.IngresarTexto(searchText);
		alojamientosPage.EliminarCookies();
		alojamientosPage.IngresarEntrada();
		alojamientosPage.IngresarSalida();
		alojamientosPage.BotonAplicar(1);
		alojamientosPage.IngresarHabitaciones();
		alojamientosPage.AumentarCantidadAdultos();
		alojamientosPage.AumentarCantidadNiños();
		alojamientosPage.EdadNiño(4);
		alojamientosPage.BotonAplicar(2);
		
		//Test a ResoultsPage
		despegarResoultsPage resoultsPage2 = alojamientosPage.BuscarResultados();
		resoultsPage2.VerDetalles();
	}
	
	@AfterMethod(alwaysRun = true)
	public void endsetuo(){
		driver.close();
	}
}