package org.lss7;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static String getFile(File configFile,String config){
        try{
            FileReader reader = new FileReader(configFile);
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
            FileReader reader = new FileReader(configFile);
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
    public static String[] readlogin(){
        File configFile;
        try {
            configFile = new File("src/main/resources/login.properties");
        }catch (NullPointerException e){
            configFile = new File("src/main/resources/logintemplate.properties");
        }
        String[] ret = new String[2];
        ret[0] = getFile(configFile,"Login");
        ret[1] = getFile(configFile,"PW");
        if (ret[0]==""||ret[1]==""){
            System.err.println("No login!");
            System.exit(-1);
        }
        return ret;
    }
    public static void main(String args[]){
        Properties var = getBuildings();
    }
}
