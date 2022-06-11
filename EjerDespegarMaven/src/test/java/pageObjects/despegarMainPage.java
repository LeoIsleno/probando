package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

public class despegarMainPage {

	  @FindBy(xpath="//ul[@class='header-list-products']//li[@class='header-product-item ']//div[@class='button-circle-icon']//i[@class='shifu-icon-product shifu-3-icon-hotels']")  
	  WebElement buttonAlojamientos;
	  
	  private WebDriver driver = null;
	  
	  public despegarMainPage (WebDriver driver) {
		  this.driver = driver;
		  PageFactory.initElements(driver, this);
		  //initElements(driver, this);
	  }
	  
	  public boolean buttonAlojamientos() {
		  return this.buttonAlojamientos.isDisplayed();
	  }
	  
	  public despegarAlojamientosPage goToAlojamientos() {
		  buttonAlojamientos.click();
		  return new despegarAlojamientosPage(this.driver);
	  }
}
