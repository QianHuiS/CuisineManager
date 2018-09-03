package tw.idv.qianhuis.cuisinemanager;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFunction {
    private int year;
    private int month;
    private int day;

    //建構子
    public DateFunction() {

    }

    public DateFunction(int year, int month, int day) {
        this.year=year;
        this.month=month;
        this.day=day;
    }

    //日期格式轉換(yyyy-mm-dd轉為yyyy,mm,dd)
    public DateFunction(String showDate) {
        year=Integer.valueOf(showDate.substring(0,4));  //substring(此下個開始, 到這個為止)
        month=Integer.valueOf(showDate.substring(5,7));
        day=Integer.valueOf(showDate.substring(8,10));
    }

    //日期格式轉換(yyyy,mm,dd轉為yyyy-mm-dd)
    public static String stringFormat(int year, int month, int day){
        String date= String.valueOf(year) + "-" +
                String.valueOf(month < 10 ? "0"+month: month) + "-" +
                String.valueOf(day < 10 ? "0"+day: day);
        return date;
    }

    public String stringFormat(){
        String date= String.valueOf(year) + "-" +
                String.valueOf(month < 10 ? "0"+month: month) + "-" +
                String.valueOf(day < 10 ? "0"+day: day);
        return date;
    }

    //取得今天日期(yyyy-mm-dd)
    public static String getToday(){
        Date myDate= new Date();
        int thisYear= myDate.getYear()+1900;
        int thisMonth= myDate.getMonth()+1;
        int thisDay= myDate.getDate();

        return stringFormat(thisYear, thisMonth, thisDay);
    }

    //日期+天
    public void dateAdd(String sdate, String addDay) {
        /*
        //定義好時間字串的格式
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");

        //將字串轉成Date型
        Date date = null;
        try {
            date = dateFormat.parse(sdate); //sdate為yyyy-mm-dd.
        } catch (ParseException e) {
            e.printStackTrace();
        }
        */

        //新增一個Calendar,並且指定時間
        Calendar calendar = Calendar.getInstance();
        //calendar.setTime(date);     //設定日曆選擇日期.
        DateFunction df= new DateFunction(sdate);
        calendar.set(df.getYear(),df.getMonth(),df.getDay());
        calendar.add(Calendar.DAY_OF_MONTH, Integer.valueOf(addDay));    //DAY_OF_MONTH為選擇日期的日, 再加上int天. (int可為負做減法)
        //Date sumDate=calendar.getTime();    //取得加減過後的Date.

        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);
    }

    //日期-日期(取得日差)
    public static String dateCalculation(String sdate1, String sdate2) {
        //新增一個Calendar, 並且指定日期.
        DateFunction df;
        Calendar calendar = Calendar.getInstance();
        df= new DateFunction(sdate1);
        calendar.set(df.getYear(),df.getMonth(),df.getDay());

        Long ut1= calendar.getTimeInMillis();   //取得Unix時間

        calendar = Calendar.getInstance();
        df= new DateFunction(sdate2);
        calendar.set(df.getYear(),df.getMonth(),df.getDay());

        Long ut2= calendar.getTimeInMillis();

        //相減獲得兩個時間差距
        String distance=String.valueOf((ut1-ut2)/(1000*60*60*24));   //毫秒差換日差.
        return distance;
    }

    //Getter&Setter
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
