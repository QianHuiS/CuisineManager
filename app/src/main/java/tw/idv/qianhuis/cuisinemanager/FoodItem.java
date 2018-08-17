package tw.idv.qianhuis.cuisinemanager;

import java.util.Date;
import java.util.HashMap;

public class FoodItem {

    private HashMap<String, Object> foodItem;
    private String fId;
    private String fSpecie;
    private String fName;
    private String fQuantity;
    private String fUnit;
    private String fPosition;
    private String fStoragetime;

    private String fExpirationdate;     //過期日.
    private String fStoragelife;         //過期日.

    public FoodItem(){};

    public FoodItem(String fid, String fspecie, String fname, String fquantity,
                    String funit, String fposition, String fstoragetime){
        fId= fid;
        fSpecie= fspecie; //種類!!
        fName= fname;
        fQuantity= fquantity;
        fUnit= funit;
        fPosition= fposition;
        fStoragetime= fstoragetime;

        //fexpirationdate= getExpirationdate();
        //storagelife= getStoragelife();

        setFoodItem();
    };

    public FoodItem(HashMap<String, Object> selectItem){
        fId= (String)selectItem.get("index_id");   //拿出物件中的某資訊.
        fSpecie= (String)selectItem.get("food_specie"); //種類!!
        fName= (String)selectItem.get("food_name");
        fQuantity= (String)selectItem.get("food_quantity");
        fUnit= (String)selectItem.get("food_unit");
        fPosition= (String)selectItem.get("food_position");
        fStoragetime= (String)selectItem.get("food_storagetime");
    }

    //取得過期日
    public static int getExpirationdate(int storagetime,int foodlife) {
        int expirationdate= Integer.valueOf(storagetime) +foodlife;
        return expirationdate;
    }

    public void setfExpirationdate(String fExpirationdate) {
        this.fExpirationdate = fExpirationdate;
    }

    //取得剩餘天數
    public static String getStoragelife(int expirationdate) {

        int storagelife= expirationdate-getToday();

        return String.valueOf(storagelife);
    }

    public void setfStoragelife(String fStoragelife) {
        this.fStoragelife = fStoragelife;
    }

    //取得今天日期
    public static int getToday(){
        Date myDate= new Date();
        int thisYear= myDate.getYear()+1900;
        int thisMonth= myDate.getMonth()+1; //變數們
        int thisDay= myDate.getDate();

        String date= dateFormat(thisYear, thisMonth, thisDay);
        return Integer.valueOf(date);
    }

    //日期格式轉換
    public static String dateFormat(int year, int month, int day){
        String date= String.valueOf(year) +
                String.valueOf(month < 10 ? "0"+month: month) +
                String.valueOf(day < 10 ? "0"+day: day);
        return date;
    }

    //Getter&Setter
    public HashMap<String, Object> getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(HashMap<String, Object> foodItem) {
        this.foodItem = foodItem;
    }

    public void setFoodItem() {
        foodItem= new HashMap<>();
        foodItem.put("index_id", fId);
        foodItem.put("food_specie", fSpecie);
        foodItem.put("food_name", fName);
        foodItem.put("food_quantity", fQuantity);
        foodItem.put("food_unit", fUnit);
        foodItem.put("food_position", fPosition);  //數字為欄位順序.
        foodItem.put("food_storagetime", fStoragetime);
    }

    public String getfId() {
        return fId;
    }

    public void setfId(String fId) {
        this.fId = fId;
    }

    public String getfSpecie() {
        return fSpecie;
    }

    public void setfSpecie(String fSpecie) {
        this.fSpecie = fSpecie;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getfQuantity() {
        return fQuantity;
    }

    public void setfQuantity(String fQuantity) {
        this.fQuantity = fQuantity;
    }

    public String getfUnit() {
        return fUnit;
    }

    public void setfUnit(String fUnit) {
        this.fUnit = fUnit;
    }

    public String getfPosition() {
        return fPosition;
    }

    public void setfPosition(String fPosition) {
        this.fPosition = fPosition;
    }

    public String getfStoragetime() {
        return fStoragetime;
    }

    public void setfStoragetime(String fStoragetime) {
        this.fStoragetime = fStoragetime;
    }

}
