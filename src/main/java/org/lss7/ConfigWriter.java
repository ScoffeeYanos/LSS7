package org.lss7;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

public class ConfigWriter {
    private static void setFile(File configFile,String config, String value) {
        Properties props = new Properties();
        try (FileReader reader = new FileReader(configFile)) {
            props.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter(configFile)) {
            props.setProperty(config, value);
            props.store(writer, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void setConfig(String config, String value){
        File configFile = new File("src/main/resources/config.properties");
        setFile(configFile,config,value);
    }
    public static void saveStation(String config, String value){
        File configFile = new File("src/main/resources/stations.properties");
        setFile(configFile,config,value);
    }
    public static void addStation(String value){
        File configFile = new File("src/main/resources/stations.properties");
        String var = ConfigReader.readStation("Stationlist");
        if (var==null){
            setFile(configFile,"Stationlist",value);
            return;
        }
        String[] varlist = var.split(" ");
        for(int i = 0;i<varlist.length;i++){
            if(varlist[i].equals(value)){
                return;
            }
        }
        setFile(configFile,"Stationlist",var+" "+value);
    }
    public static void main(String args[]){
        addStation("007");
        addStation("001");
        addStation("002");
        addStation("003");
        System.out.println("");
    }
}
