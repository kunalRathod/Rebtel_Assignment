package com.rebtel.core.elementactions;

import org.apache.commons.logging.Log;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.rebtel.test.TestBed;
import com.rebtel.util.ExcelReader;
import com.rebtel.util.LogUtil;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.HashMap;

/** This class contains element action methods */
public class Element {

	/** The log. */
	static Log log = LogUtil.getLog(Element.class);

	protected AndroidDriver driver;

	/** Get mobile elements locators from excel sheet */
	protected static HashMap<String, String> commonElements = null;

	/**
	 * Constructor to initialize the driver
	 * 
	 * @param driver
	 */
	public Element(AndroidDriver driver) {
		this.driver = driver;
		commonElements = ExcelReader.getDataInHashMap("common_elements", "common_elements");
	}

	/**
	 * Method to identify element using mobile locator
	 * 
	 * @param locator
	 * @param identifier
	 * @return By reference object
	 */
	public By getMobileLocator(String locator, LocatorType identifier) {
		By by = null;
		try {
			switch (identifier) {
			case xpath:
				by = MobileBy.xpath(locator);
				break;
			case classname:
				by = MobileBy.className(locator);
				break;
			case id:
				by = MobileBy.id(locator);
				break;
			default:
				by = MobileBy.xpath(locator);
				break;
			}
		} catch (Exception e) {
			log.error("Failed to identify the locatorType:::" + locator + ". Error :::" + e.getMessage());
			e.printStackTrace();
		}
		if (by == null)
			log.error("Failed to identify the locatorType:::" + locator);
		return by;
	}

	/**
	 * This method returns the MobileElement using explicit wait
	 * 
	 * @param locator
	 * @return mobile element
	 */
	public MobileElement getElementByLocator(String locator) {
		MobileElement element = null;
		try {
			LocatorType identifier = LocatorType.valueOf(locator.substring(0, locator.indexOf("=")).toLowerCase());
			locator = locator.substring(locator.indexOf("=") + 1, locator.length());

			By by = getMobileLocator(locator, identifier);
			// Wait for the element to be visible
			WebDriverWait wait = new WebDriverWait(driver, TestBed.maxWaitSeconds);
			element = (MobileElement) wait.until(ExpectedConditions.presenceOfElementLocated(by));
			log.info("Element found ::presenceOfElementLocated::with locator : " + locator);
		} catch (TimeoutException e) {
			log.error(" Failed to find element even on Explicit wait using locator - " + locator + " Error Message:"
					+ e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Failed to find element even on Explicit wait using locator - " + locator + " Error Message :"
					+ e.getMessage());
			e.printStackTrace();
		}
		if (element == null)
			log.error("Failed to find element as element is NULL  with locator : " + locator);
		return element;
	}

	/**
	 * Method to verify element is seen on page
	 * 
	 * @param locator
	 * @return boolean true if element is present
	 */
	public boolean isElementPresent(String locator) {
		boolean flag = false;
		MobileElement element = null;
		try {
			element = getElementByLocator(locator);
			if (element != null && element.isDisplayed()) {
				flag = true;
				log.info("Element is found and is displayed using locator :::" + locator);
			}
		} catch (org.openqa.selenium.ElementNotVisibleException ex) {
			flag = false;
			log.error("Failed to find element using locator :::" + locator + ". Error message:::" + ex.getMessage());
			ex.printStackTrace();
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			flag = false;
			log.error("Failed to find element using locator :::" + locator + ". Error message:::" + ex.getMessage());
			ex.printStackTrace();
		} catch (org.openqa.selenium.TimeoutException ex) {
			flag = false;
			log.error("Failed to find element using locator :::" + locator + ". Error message:::" + ex.getMessage());
			ex.printStackTrace();
		} catch (java.lang.Exception ex) {
			flag = false;
			log.error("Failed to find element using locator :::" + locator + ". Error message:::" + ex.getMessage());
			ex.printStackTrace();
		}
		if (element == null) {
			flag = false;
			log.error("Failed to find element as Element is null using locator :::" + locator);
		} else
			flag = true;
		return flag;
	}

	/**
	 * Method to get element text
	 * 
	 * @param locator
	 * @return text of element
	 */
	public String getElementText(String locator) {
		String text = "";
		try {
			MobileElement element = getElementByLocator(locator);
			text = element.getText().trim();
			log.info("Actual Element text::: " + text);
		} catch (Exception ex) {
			log.error("Exception occured while getting element text for:::" + locator);
			log.error("Error :- " + ex.getMessage());
			ex.printStackTrace();
		}
		return getUTF8Text(text);
	}
	
	/**
	 * Method to convert Actual text to UTF8 
	 * 
	 * @param text of element
	 * @return UTF8 encoded text
	 */
	public String getUTF8Text(String text) {
		String retText = "";
		try {
			retText = new String(text.getBytes("UTF8"),"UTF8");
			log.info("UTF8 Encoded text::: " + retText);
		} catch (Exception ex) {
			log.error("Exception occured while encoding text");
			log.error("Error :- " + ex.getMessage());
			ex.printStackTrace();
		}
		return retText;
	}


	/**
	 * Method to send Keys on element with some test data as string
	 * 
	 * @param locator
	 * @param text
	 */
	public boolean sendKeys(String locator, String text) {
		try {
			MobileElement element = getElementByLocator(locator);
			element.clear();
			element.sendKeys(text);
			log.info("Sending text on element having locator:::" + locator + " with text :::" + text);
			return true;
		} catch (Exception e) {
			log.error("Failed to send keys to element having locator:::" + locator + " due to :::" + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}




	/**
	 * This is a click function, which is generic and can be used with all kind
	 * of locators In this function locator need to pass with locator type
	 * 
	 * @param locator
	 */
	public boolean click(String locator) {
		try {
			MobileElement mapElement = getElementByLocator(locator);
			if (mapElement != null) {
				mapElement.click();
				log.info("Clicked on Element having locator:::" + locator);
				return true;
			} else {
				log.info("Failed to click on element as element is NULL ::: Locator:::" + locator);
				return false;
			}
		} catch (Exception e) {
			log.error("Unable to click on element :::" + locator);
			e.printStackTrace();
		}
		return false;
	}
}