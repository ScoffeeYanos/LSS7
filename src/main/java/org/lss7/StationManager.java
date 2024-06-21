package org.lss7;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class StationManager extends Thread{
    ArrayList<Station> stationArrayList;
    WebDriver driver;
    public StationManager(WebDriver driver){
        stationArrayList = new ArrayList<Station>();
        this.driver = driver;
        {
            String stationList = ConfigReader.readStation("Stationlist");
            if(stationList==null){
                driver.get("https://www.leitstellenspiel.de/");
                driver.get(driver.findElement(By.partialLinkText("Details")).getAttribute("href"));
                analyzePage();
            }else {
                String[] stationListSplit = stationList.split(" ");
                for (int i = 0; i < stationListSplit.length; i++) {
                    stationArrayList.add(new Station(stationListSplit[i],parseInt(ConfigReader.readStation(stationListSplit[i] + ".id")),
                            parseInt(ConfigReader.readStation(stationListSplit[i] + ".level")),
                            parseInt(ConfigReader.readStation(stationListSplit[i] + ".pops")),
                            LocalDate.parse(ConfigReader.readStation(stationListSplit[i] + ".recruitment"))));
                }
                assertStations();
            }

        }//Fileload
    }
    public void run(){
        stationArrayList.get(0).save();
        System.out.println("new run");
    }
    public void assertStations(){
        driver.get("https://www.leitstellenspiel.de/buildings/"+stationArrayList.get(0).getID());
        while(true){
            boolean var = false;
            int i = 0;
            for (i = 0;i< stationArrayList.size();i++){
                if(stationArrayList.get(i).getName().equals(driver.findElement(By.cssSelector("#iframe-inside-container > div.building-title > h1")).getText())){
                    var = true;
                    break;
                }
            }
            if(var==true){
                stationArrayList.get(i).setLevel(parseInt(driver.findElement(By.cssSelector("#iframe-inside-container > dl > dd:nth-child(2)")).getText().split(" ")[0]));
                stationArrayList.get(i).setPops(parseInt(driver.findElement(By.cssSelector("#iframe-inside-container > dl > dd:nth-child(10)")).getText().split(" ")[0]));
                driver.findElement(By.cssSelector("#building-navigation-container > a:nth-child(2)")).click();
                if (driver.findElement(By.cssSelector("#iframe-inside-container > div.building-title > h1")).getText().equals(stationArrayList.get(0).getName())){
                    System.out.println("Cycled");
                    break;
                }
            }
            analyzePage();
        }
    }
    public void analyzePage(){
        stationArrayList.add(new Station(driver.findElement(By.cssSelector("#iframe-inside-container > div.building-title > h1")).getText(),
                parseInt(driver.getCurrentUrl().split("/")[driver.getCurrentUrl().split("/").length-1]),
                parseInt(driver.findElement(By.cssSelector("#iframe-inside-container > dl > dd:nth-child(2)")).getText().split(" ")[0]),
                parseInt(driver.findElement(By.cssSelector("#iframe-inside-container > dl > dd:nth-child(10)")).getText().split(" ")[0]),
                LocalDate.parse("2000-01-01")));
    }
}
class Station{
    String name;
    int id;
    int level;
    int pops;
    LocalDate recruitment;
    public Station(String name,int id, int level, int pops, LocalDate recruitment){
        this.name = name;
        this.id = id;
        this.level = level;
        this.pops = pops;
        this.recruitment = recruitment;
    }
    public LocalDate getRecruitment(){
        return recruitment;
    }
    public LocalDate resetRecruitment(){
        recruitment = LocalDate.now().plusDays(3);
        return recruitment;
    }
    public void save(){
        ConfigWriter.addStation(name);
        ConfigWriter.saveStation(name+".id",""+id);
        ConfigWriter.saveStation(name+".level",""+level);
        ConfigWriter.saveStation(name+".pops",""+pops);
        ConfigWriter.saveStation(name+".recruitment",recruitment.toString());
    }
    public String getName(){
        return name;
    }
    public void setLevel(int level){
        this.level=level;
    }
    public void setPops(int pops){
        this.pops=pops;
    }
    public int getID(){
        return id;
    }
}