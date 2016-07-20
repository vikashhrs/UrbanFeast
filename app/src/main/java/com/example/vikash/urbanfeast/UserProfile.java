package com.example.vikash.urbanfeast;

import java.io.Serializable;

/**
 * Created by Vikash on 20-Jul-16.
 */
public class UserProfile implements Serializable {
    String NAME;
    String DOB;
    String EMAIL;
    String PASSWORD;
    String GENDER;
    String LOGINFEATURE;
    public UserProfile(String NAME,String DOB,String EMAIL,String GENDER,String PASSWORD,String LOGINFEATURE){
        this.NAME=NAME;
        this.EMAIL=EMAIL;
        this.DOB=DOB;
        this.LOGINFEATURE=LOGINFEATURE;
        this.GENDER= GENDER;
        this.PASSWORD=PASSWORD;
    }
    String getNAME(){
        return  NAME;
    }
    String getDOB(){
        return DOB;
    }
    String getEMAIL(){
        return EMAIL;
    }
    String getPASSWORD(){
        return  PASSWORD;
    }
    String getGENDER(){
        return GENDER;
    }
    String getLOGINFEATURE(){
        return LOGINFEATURE;
    }
}
