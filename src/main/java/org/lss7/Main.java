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
    static LSSDriver[] driver;
    static StationManager stationManager;
    static MissionManager missionManager;
    public static void main(String[] args) throws InterruptedException {
        {
            //stationManager = new StationManager(driver);
            //stationManager.recruitment();
            //stationManager.start();
            String[][] logins = ConfigReader.readlogin();
            driver = new LSSDriver[logins.length];
            for(int i = 0;i<logins.length;i++){
                driver[i]=createLSSdriver(logins[i]);
                driver[i].attachMissionManager();
                driver[i].runMissionManager();
            }
        }
    }
    public static LSSDriver createLSSdriver(String[] login){
        LSSDriver newdriver;
        {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            newdriver = new LSSDriver(options);
            newdriver.get("https://www.leitstellenspiel.de/users/sign_in");
            newdriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        }//create Webdriver with Startops: maximized && impWait:3s
        {
            newdriver.findElement(By.cssSelector("#user_email")).sendKeys(login[0]);
            newdriver.findElement(By.cssSelector("#user_password")).sendKeys(login[1]);
            newdriver.findElement(By.cssSelector("#new_user > input")).click();
        }//login
        {
            newdriver.findElement(By.className("js-cookies-eu-ok")).click();
            if(!LocalDate.now().equals(LocalDate.parse(ConfigReader.getConfig("LastStart")))){
                newdriver.get("https://www.leitstellenspiel.de/daily_bonuses");
                try {
                    newdriver.findElement(By.className("collect-button")).click();//TODO UNTESTED for multiple days
                }catch (NoSuchElementException e){}
                newdriver.get("https://www.leitstellenspiel.de/");
                ConfigWriter.setConfig("LastStart",LocalDate.now().toString());
            }
        }//Accept Cookies && Daily abholen
        return newdriver;
    }
}