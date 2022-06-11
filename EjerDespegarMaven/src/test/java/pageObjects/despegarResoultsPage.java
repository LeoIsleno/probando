package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class despegarResoultsPage{
	private WebDriver driver = null;
	WebDriverWait wait = null;
	
	@FindBy(xpath="//button[@class='eva-3-btn -md -primary -eva-3-fwidth']//em[@class='btn-text']")  
	 private WebElement botonVerDetalle;
	 
	 public despegarResoultsPage (WebDriver driver) {
		  this.driver  = driver;
		  PageFactory.initElements(driver, this);
	  }
	 
	 public void VerDetalles() {
		 wait.until(ExpectedConditions.visibilityOf(botonVerDetalle));
		 Assert.assertTrue(botonVerDetalle.isDisplayed(),"Error, no accede a detalles");
		 botonVerDetalle.click();
	 }
}
