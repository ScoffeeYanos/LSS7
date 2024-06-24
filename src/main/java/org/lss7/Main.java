package org.lss7;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class Main {
    static WebDriver driver;
    static StationManager stationManager;
    static MissionManager missionManager;
    public static void main(String[] args) {
        {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            driver = new LSSDriver(options);
            driver.get("https://www.leitstellenspiel.de/users/sign_in");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        }//create Webdriver with Startops: maximized && impWait:3s
        {
            String[] login = ConfigReader.readlogin();
            driver.findElement(By.cssSelector("#user_email")).sendKeys(login[0]);
            driver.findElement(By.cssSelector("#user_password")).sendKeys(login[1]);
            driver.findElement(By.cssSelector("#new_user > input")).click();
        }//login
        {
            driver.findElement(By.className("js-cookies-eu-ok")).click();
            if(!LocalDate.now().equals(LocalDate.parse(ConfigReader.getConfig("LastStart")))){
                driver.get("https://www.leitstellenspiel.de/daily_bonuses");
                try {
                    driver.findElement(By.className("collect-button")).click();//TODO UNTESTED for multiple days
                }catch (NoSuchElementException e){}
                driver.get("https://www.leitstellenspiel.de/");
                ConfigWriter.setConfig("LastStart",LocalDate.now().toString());
            }
        }//Accept Cookies && Daily abholen
        {
            //stationManager = new StationManager(driver);
            //stationManager.recruitment();
            //stationManager.start();
            missionManager = new MissionManager(driver);
            while (true)
            {
                missionManager.manage();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}