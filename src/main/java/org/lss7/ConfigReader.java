package org.lss7;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

public class ConfigReader {
    private static String getFile(File configFile,String config){
        try{
            FileReader reader = new FileReader(configFile, StandardCharsets.ISO_8859_1);
            Properties props = new Properties();
            props.load(reader);
            String ret = props.getProperty(config);
            reader.close();
            return ret;
        }catch (IOException e){
            return "";
        }
    }
    private static Properties getFile(File configFile){
        try{
            FileReader reader = new FileReader(configFile, StandardCharsets.ISO_8859_1);
            Properties props = new Properties();
            props.load(reader);
            reader.close();
            return props;
        }catch (IOException e){
            return null;
        }
    }
    public static String getConfig(String config){
        File configFile = new File("src/main/resources/config.properties");
        return getFile(configFile,config);
    }
    public static Properties getBuildings(){
        File configFile = new File("src/main/resources/stations.properties");
        return getFile(configFile);
    }
    public static String readStation(String config){
        File configFile = new File("src/main/resources/stations.properties");
        return  getFile(configFile,config);
    }
    public static String[] readConfigArray(String config){
        File configFile = new File("src/main/resources/config.properties");
        String ret = getFile(configFile,config);
        return ret.split("_");
    }
    public static String[][] readlogin(){
        File configFile = new File("src/main/resources/login.properties");
        ArrayList<String[]> reta = new ArrayList<String[]>();
        int i = 1;
        while (true){
            String var = getFile(configFile,"Log"+i);
            i++;
            if (var==null||var.equals("")){
                break;
            }else {
                reta.add(var.split(","));
            }
        }
        String[][] ret = new String[reta.size()][];
        for (int j = 0; j<reta.size();j++){
            ret[j] = reta.get(j);
        }
        return ret;
    }
    public static void main(String args[]){
        Properties var = getBuildings();
    }
}
