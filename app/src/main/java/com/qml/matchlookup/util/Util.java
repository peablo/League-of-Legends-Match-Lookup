package com.qml.matchlookup.util;

import com.qml.matchlookup.constants.Constants;

/**
 * Created by Pablo on 23/03/2015.
 */
public class Util
{


    public static boolean validName(String name)
    {
        String pattern= "^[a-zA-Z0-9]*$";
        if( !name.matches(pattern) ){
            return false;
        }

        if( name.length() < Constants.MIN_NAME_CHARACTERS ){
            return false;
        }

        if( name.length() > Constants.MAX_NAME_CHARACTERS ){
            return false;
        }

        return true;
    }



}
