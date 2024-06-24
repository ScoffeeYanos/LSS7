package org.lss7;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;

public class MissionManager {
    WebDriver driver;
    ArrayList<Mission> missionArrayList;
    byte LFZpop;
    byte SFZpop;
    boolean MTWenable;
    byte MTWpop;
    String[] vehiclelist;
    byte LFMTWswitchpop;
    public MissionManager(WebDriver driver){
        this.driver = driver;
        missionArrayList = new ArrayList<Mission>();
        driver.get("https://www.leitstellenspiel.de/");
        driver.findElement(By.cssSelector("#mission_select_attended")).click();
        driver.findElement(By.cssSelector("#mission_select_finishing")).click();
        LFZpop = Byte.parseByte(ConfigReader.getConfig("LFZpop"));
        SFZpop = Byte.parseByte(ConfigReader.getConfig("SFZpop"));
        MTWenable = Boolean.parseBoolean(ConfigReader.getConfig("MTWenable"));
        MTWpop = Byte.parseByte(ConfigReader.getConfig("MTWpop"));
        vehiclelist = ConfigReader.readConfigArray("vehicles");
        LFMTWswitchpop = Byte.parseByte(ConfigReader.getConfig("LFMTWswitchpop"));
    }
    public void manage(){
        ArrayList<String> links = new ArrayList<>();
        {
            if(!driver.getCurrentUrl().equals("https://www.leitstellenspiel.de/")){
                driver.get("https://www.leitstellenspiel.de/");
                System.out.println("Reload");
            }
            WebElement missionList = driver.findElement(By.id("mission_list"));
            List<WebElement> ml = missionList.findElements(By.className("mission_panel_red"));
            List<WebElement> alarms = new ArrayList<>();
            for (WebElement element : ml) {
                alarms.add(element.findElement(By.className("mission-alarm-button")));
            }
            System.out.println("Alarms List length: " + alarms.size());
            for (WebElement i : alarms) {
                links.add(i.getAttribute("href"));
            }
        }//Fill List of Alarm links
        if(links.size()==0){
            return;
        }
        missions:
        for (String link:links){
            try {
                driver.get(link);
                try {
                    driver.findElement(By.cssSelector("#missing_text > div"));
                }catch (NoSuchElementException f){
                    try {
                        driver.findElement(By.linkText("1LF")).click();
                        driver.findElement(By.id("alert_btn")).click();
                        driver.findElement(By.linkText("Anfahrten abbrechen")).click();
                        try {
                            Thread.sleep(500);
                        }catch (Exception e){}
                    }catch (UnhandledAlertException e){
                        continue;
                    }
                }//check for missing list and update in catch
                ArrayList<String> fullmissingAL;
                try {
                    fullmissingAL = new ArrayList<String>(Arrays.asList(driver.findElement(By.cssSelector("#missing_text > div:nth-child(1)")).getText().split(":")));
                }catch (NoSuchElementException e){
                    driver.findElement(By.linkText("Gefangene entlassen")).click();
                    continue missions;
                }


                int j = 2;
                while (true){
                    try {
                        String[] x =driver.findElement(By.cssSelector("#missing_text > div:nth-child("+j+")")).getText().split(":");
                        fullmissingAL.add(x[0]);
                        fullmissingAL.add(x[1]);
                        j++;
                    } catch (NoSuchElementException e) {
                        break;
                    }
                }
                String[] fullmissing = fullmissingAL.toArray(new String[fullmissingAL.size()]);
                int personal = 0;
                int LFZ = 0;
                int SFZ = 0;
                int SWZ = 0;
                for(int v = 0; v < fullmissing.length; v++) {
                    String fullmissingpart = fullmissing[v];
                    if(fullmissingpart.equals("Fehlende Fahrzeuge")){
                        System.out.println("Fehlende Fahrzeuge:"+fullmissing[v+1]);
                        String[] missingVehicles = fullmissing[1].split(",");
                        int i = 0;
                        String vehicle= "";
                        nextvehicle:
                        for(String vehicles:missingVehicles){
                            String[] missingVehicle = vehicles.split(" ");
                            for (String var:missingVehicle){
                                try {
                                    i = parseInt(var);
                                }catch (NumberFormatException e){
                                    if(var.equals("Ein")){
                                        i = 1;
                                    }else if (var.equals("Zwei")) {
                                        i = 2;
                                    }else if (i == 0){
                                        continue;
                                    }else if (var.length()!=0) {
                                        vehicle=var.replaceAll("[(),]","");
                                        if(vehicle.equals("LF")){
                                            LFZ+=i;
                                        } else if (vehicle.equals("SW")) {
                                            SWZ+=i;
                                        } else{
                                            SFZ+=i;
                                        }
                                        for(String check:vehiclelist){
                                            if(vehicle.equals(check)){
                                                if (!send(i,vehicle)){continue missions;}else{continue nextvehicle;}
                                            }
                                            if (var.length()!=0&&var.equals("beliebiges")) {
                                                driver.findElement(By.linkText(""+i+"LF")).click();//TODO REWORK POP RELATION
                                                LFZ+=i;
                                                SFZ-=i;
                                                continue nextvehicle;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                for(int v = 0; v < fullmissing.length; v++) {
                    String fullmissingpart = fullmissing[v];
                    if(fullmissingpart.equals("Uns fehlt")){
                        System.out.println("Uns fehlt:"+fullmissing[v+1]);
                        String[] missingRest = fullmissing[v+1].split(" ");
                        int i = 0;for (String var:missingRest){
                            try {
                                i = parseInt(var);
                            }catch (NumberFormatException e){
                                if(var.equals("Wasser")){
                                    if (i>10000){
                                        if (!send(i/10000,"SW")){continue missions;}else{SWZ+=i/10000;}
                                    }
                                    int LFwater=1600;
                                    i = (int) (i-(LFZ*LFwater*(1+(SWZ*0.25))));
                                    if (i>0){
                                        int lfsend = (int) (i/(LFwater*(1+(SWZ*0.25))));
                                        if((i%LFwater*(1+(SWZ*0.25)))!=0){
                                            lfsend++;
                                        }
                                        if (!send(lfsend,"LF")){continue missions;}
                                    }
                                }
                            }
                        }
                    }
                }
                for(int v = 0; v < fullmissing.length; v++) {
                    String fullmissingpart = fullmissing[v];
                    if(fullmissingpart.equals("Fehlendes Personal")){
                        System.out.println("Fehlendes Personal:"+fullmissing[v+1]);
                        String[] missingPops = fullmissing[v+1].split(" ");
                        int i = 0;
                        for (String var:missingPops){
                            try {
                                i = parseInt(var);
                            }catch (NumberFormatException e){
                                if(var.equals("Feuerwehrleute")||var.equals("Feuerwehrmann")){
                                    if(MTWenable){
                                        i = i-(LFZ*LFZpop)-(SFZ*SFZpop);
                                        if (i>0){
                                            int mtwsend = i/MTWpop;
                                            if(i%MTWpop>LFMTWswitchpop){
                                                mtwsend++;
                                            }else if(i%MTWpop!=0) {
                                                if (!send(i%MTWpop,"LF")){continue missions;}
                                            }
                                            if (mtwsend!=0) {
                                                if (!send(mtwsend,"MTW")){continue missions;}
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                driver.findElement(By.id("alert_btn")).click();
                System.out.println("ALARM CONFIRMED");
            }catch (UnhandledAlertException f){
                System.out.println("ALARM REFUSED");
            }
        }
        driver.get("https://www.leitstellenspiel.de/");
    }
    private boolean send(int num,String vehicle) throws UnhandledAlertException{
        try {
            driver.findElement(By.linkText(""+num+vehicle)).click();
            System.out.println("Sent:"+num+" "+vehicle);
            return true;
        }catch (NoSuchElementException g){
            System.out.println("Missing AAO: "+num+vehicle);
            return false;
        }
    }
}
class Mission{
    int id;
    String name;
    public Mission(int id,String name){
        this.id=id;
        this.name=name;
    }
}