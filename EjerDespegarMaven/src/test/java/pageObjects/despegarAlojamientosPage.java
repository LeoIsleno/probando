package pageObjects;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;


public class despegarAlojamientosPage {
	WebDriverWait wait = null;

	  @FindBy(xpath="//div[@class='sbox-places-destination--1xd0k']//div[@class='input-container']//input[@class='input-tag']")  
	  private WebElement apartadoOrigen;
	  @FindBy(xpath="//input[@placeholder='Entrada']")  
	  private WebElement apartadoEntrada;
	  @FindBy(css="div.sbox5-floating-tooltip.sbox5-floating-tooltip-opened .sbox5-monthgrid.sbox5-compact-view .sbox5-monthgrid-dates.sbox5-monthgrid-dates-31 .sbox5-monthgrid-datenumber")  
	  private WebElement fechaSalida;
	  @FindBy(css="div.sbox5-floating-tooltip.sbox5-floating-tooltip-opened .sbox5-monthgrid.sbox5-compact-view .sbox5-monthgrid-datenumber.-today")  
	  private WebElement fechaEntradaHoy;
	  @FindBy(xpath="//button[@class='sbox5-3-btn -primary -md']//em[@class='btn-text']")  
	  private WebElement botonAplicar1;
	  @FindBy(css="a.lgpd-banner--button.eva-3-btn.-white.-md .btn-text")  
	  private WebElement botonCookies;
	  @FindBy(css="div.sbox5-3-distribution-passengers.sbox5-3-validation.-top-right .sbox5-3-second-input-wrapper")  
	  private WebElement apartadoHabitacion;
	  @FindBy(css="div.sbox5-floating-tooltip.sbox5-floating-tooltip-opened .steppers-icon-right.stepper__icon")  
	  private WebElement botonAumAdultos;
	  @FindBy(css="div.sbox5-floating-tooltip.sbox5-floating-tooltip-opened .stepper__distribution_container .stepper__room__row:nth-child(2) .steppers-icon-right.stepper__icon")  
	  private WebElement botonAumNiños;
	  @FindBy(css="div.sbox5-floating-tooltip.sbox5-floating-tooltip-opened .sbox5-3-btn.-md.-primary .btn-text")  
	  private WebElement botonAplicar2;
	  @FindBy(css="select.select")  
	  private WebElement abrirEdad;
	  @FindBy(css="button.sbox5-box-button-ovr.sbox5-3-btn.-secondary.-icon.-lg .btn-text")  
	  private WebElement botonBuscarResultados;
	  @FindBys( {
		   @FindBy(xpath = "//select[@class='select']//option[@class='select-option']")
		} )
		private List<WebElement> edades;
	  @FindBy(css="button.sbox5-box-button-ovr.sbox5-3-btn.-secondary.-icon.-lg .btn-text")  
	  private WebElement botonBuscar;
	  @FindBy(xpath="//select[@class='select']//option[@class='select-option'][6]")  
	  private WebElement edad;
	
	  
	  private WebDriver driver = null;
	  
	  public despegarAlojamientosPage (WebDriver driver){
		  this.driver = driver;
		  PageFactory.initElements(driver, this);
		  wait = new WebDriverWait(driver,Duration.ofSeconds(5));
	  }
	  
	  public void IngresarTexto (String texto) throws InterruptedException{
		  wait.until(ExpectedConditions.visibilityOf(apartadoOrigen));
		  apartadoOrigen.click();
		  Thread.sleep(500);
		  apartadoOrigen.sendKeys(texto);
		  Thread.sleep(1000);
		  apartadoOrigen.sendKeys(Keys.CONTROL);
		  Thread.sleep(1000);
		  apartadoOrigen.sendKeys(Keys.ENTER);
	  }
	  
	  public void IngresarEntrada(){
		  
		  wait.until(ExpectedConditions.visibilityOf(apartadoEntrada));
		  Assert.assertTrue(apartadoEntrada.isDisplayed(),"Error, no accede a Entrada");
		  apartadoEntrada.click();
		  
		  wait.until(ExpectedConditions.visibilityOf(fechaEntradaHoy));
		  Assert.assertTrue(fechaEntradaHoy.isDisplayed(),"Error, no selecciona fecha de Entrada");
		  fechaEntradaHoy.click();
	  }
	  
	  public void IngresarSalida() {
		  wait.until(ExpectedConditions.visibilityOf(fechaSalida));
		  Assert.assertTrue(fechaSalida.isDisplayed(),"Error, no selecciona fecha de Salida");
		  fechaSalida.click();
	  }

	  public void EliminarCookies() {
		  wait.until(ExpectedConditions.visibilityOf(botonCookies));
		  Assert.assertTrue(botonCookies.isDisplayed(),"Error, no elimina las cookies");
		  botonCookies.click();
	  }
	  
	  public void BotonAplicar(int num) {
		  switch(num) {
		  //primer boton aplicar
		  case 1:
			  wait.until(ExpectedConditions.visibilityOf(botonAplicar1));
			  Assert.assertTrue(botonAplicar1.isDisplayed(),"Error, no oprime el boton aplicar");
			  botonAplicar1.click();
			  break;
		  //Segundo boton aplicar
		  case 2:
			  wait.until(ExpectedConditions.visibilityOf(botonAplicar2));
			  Assert.assertTrue(botonAplicar2.isDisplayed(),"Error, no oprime el boton aplicar");
			  botonAplicar2.click();
			  break;
		  }
	  }
	  
	  public void IngresarHabitaciones() {
		  wait.until(ExpectedConditions.visibilityOf(apartadoHabitacion));
		  Assert.assertTrue(apartadoHabitacion.isDisplayed(),"Error, no ingresa al apartado habitaciones");
		  apartadoHabitacion.click();
	  }
	  
	  public void AumentarCantidadAdultos() {
		  wait.until(ExpectedConditions.visibilityOf(botonAumAdultos));
		  Assert.assertTrue(botonAumAdultos.isDisplayed(),"Error, no aumenta la cantidad de adultos");
		  botonAumAdultos.click();
	  }
	  
	  public void AumentarCantidadNiños() {
		  wait.until(ExpectedConditions.visibilityOf(botonAumNiños));
		  Assert.assertTrue(botonAumNiños.isDisplayed(),"Error, no aumenta la cantidad de adultos");
		  botonAumNiños.click();
	  }
	  
	  public void EdadNiño(int i) throws InterruptedException {
		  wait.until(ExpectedConditions.visibilityOf(abrirEdad));
		  Assert.assertTrue(abrirEdad.isDisplayed(),"Error, no aumenta la cantidad de adultos");
		  abrirEdad.click();
		  
		  Thread.sleep(1000);
		  
		 wait.until(ExpectedConditions.visibilityOfAllElements(edades));
		 edades.get(i).click();
  }
	  	  
	  public despegarResoultsPage BuscarResultados(){
		  wait.until(ExpectedConditions.visibilityOf(botonBuscarResultados));
		  Assert.assertTrue(botonBuscarResultados.isDisplayed(),"Error, no aumenta la cantidad de adultos");
		  botonBuscarResultados.click();
		return new despegarResoultsPage(this.driver);
	  }
}