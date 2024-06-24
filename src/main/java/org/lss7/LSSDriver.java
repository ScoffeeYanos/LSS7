package org.lss7;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class LSSDriver extends ChromeDriver {
    private boolean active;
    private final String mainWindowHandle;
    private String stationWindowHandle;
    private String missionWindowHandle;
    private MissionManager missionManager;
    public LSSDriver(ChromeOptions options){
        super(options);
        active = false;
        this.mainWindowHandle = getWindowHandle();
    }
    public void attachMissionManager(){
        missionManager = new MissionManager(this);
    }
    public void runMissionManager() {
        missionManager.start();
    }
    public void setMissionWindowHandle(String missionWindowHandle){
        this.missionWindowHandle = missionWindowHandle;
    }
    public void setStationWindowHandle(String stationWindowHandle){
        this.stationWindowHandle = stationWindowHandle;
    }
    public String getMainWindowHandle(){
        return mainWindowHandle;
    }
    public String getMissionWindowHandle(){
        return missionWindowHandle;
    }
    public String getStationWindowHandle(){
        return stationWindowHandle;
    }
    public boolean getActive(){
        return active;
    }
    public boolean activate(){
        if(active){
            return false;
        }else{
            active = true;
            return true;
        }
    }
    public boolean deactivate(){
        if(active){
            active = false;
            return true;
        }else{
            return false;
        }
    }
}