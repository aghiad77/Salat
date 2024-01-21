package com.example.salat.utils;

import android.os.Build;
import android.text.TextUtils;

import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.IslamicChronology;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Utils {

    static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
    public static Format formatd = new SimpleDateFormat("dd-MM-yyyy" , Locale.ENGLISH);
    public static List<String> GregorianMonths = Arrays.asList("January", "February", "March", "April","May",
            "June","July","August","September","October","November", "December");
    public static List<String> HijriMonths =  Arrays.asList("Muharram", "Safar", "Rabi' al-awwal", "Rabi' al-thani", "Jumada al-awwal",
            "Jumada al-thani", "Rajab", "Sha'aban", "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah");


    public static String getHijriDate(){
        return  HijrahDate.now().format(dateFormatter);
    }

    public static boolean isPasswordStrong(String password){
                // Checking lower alphabet in string
                int n = password.length();
                boolean hasLower = false, hasUpper = false,
                        hasDigit = false;
                for (char i : password.toCharArray())
                {
                    if (Character.isLowerCase(i))
                        hasLower = true;
                    if (Character.isUpperCase(i))
                        hasUpper = true;
                    if (Character.isDigit(i))
                        hasDigit = true;
                }

                // Strength of password
                System.out.print("Strength of password:- ");
                if (hasDigit && hasLower && hasUpper && (n >= 8))
                    return true;
                else if ((hasLower || hasUpper ) && (n >= 6))
                    return false;
                else
                   return false;
    }

    public static String getGregorianDate(){
        return  formatd.format(new Date());
    }

    public static String getHijriMonthName(String date){
        int month = Integer.valueOf(date.split("-")[1]);
        return HijriMonths.get(month-1);
    }

    public static String getHijriDay(String date){
        int day = Integer.valueOf(date.split("-")[0]);
        return String.valueOf(day);
    }

    public static String getHijriYear(String date){

        return date.split("-")[2];
    }

    public static String getGregorianMonthName(String date){
        int month = Integer.valueOf(date.split("-")[1]);
        return GregorianMonths.get(month-1);
    }

    public static String getGregorianDay(String date){
        int day = Integer.valueOf(date.split("-")[0]);
        return String.valueOf(day);
    }

    public static String getGregorianYear(String date){

        return date.split("-")[2];
    }

    public static String getDayOfWeek(){
        Date date=new Date();
        String dayWeekText = new SimpleDateFormat("EEEE").format(date);
        return dayWeekText;
    }

    public static String convertGregorianToHijri(int year,int month, int day){
        HijrahDate islamyDate = HijrahChronology.INSTANCE.date(LocalDate.of(year,month,day));
        return  islamyDate.format(dateFormatter);
    }

    public static String getDateFromDatePicker(Date date){
        return  formatd.format(date);
    }

    public static int getTimeZoneOffset(){

        String offset = new SimpleDateFormat("Z").format(new Date());
        offset = offset.substring(0, 3) + ":" + offset.substring(3);
        offset = offset.split(":")[0];
        if(offset.equals(""))
            return 0;
        int value = Integer.valueOf(offset);
        return value;
    }

    public static boolean isValidEmail(String target) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return (TextUtils.isEmpty(target) || !target.matches(emailPattern));
    }
}
