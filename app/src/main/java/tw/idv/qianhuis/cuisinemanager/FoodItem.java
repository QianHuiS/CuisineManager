package tw.idv.qianhuis.cuisinemanager;

import android.util.Log;

import java.util.HashMap;

public class FoodItem {
    private String fId;
    private String fSpecie;
    private String fName;
    private String fQuantity;
    private String fUnit;
    private String fPosition;
    private String fStoragetime;

    private SpecieItem sItem;

    private String fExpirationdate;  //過期日.
    private String fLife;   //剩餘天.

    //public FoodItem(){};

    //建立HashMap
    public FoodItem(String fid, String fspecie, String fname, String fquantity,
                    String funit, String fposition, String fstoragetime, SpecieItem sitem){
        fId= fid;
        fSpecie= fspecie; //種類!!
        fName= fname;
        fQuantity= fquantity;
        fUnit= funit;
        fPosition= fposition;
        fStoragetime= fstoragetime;

        sItem=sitem;

        setfExpirationdate();
        setfLife();
    }

    //取得已有的HashMap內容物
    public FoodItem(HashMap<String, Object> selectItem){
        fId= (String)selectItem.get("food_id");   //拿出物件中的某資訊.
        fSpecie= (String)selectItem.get("food_specie"); //種類!!
        fName= (String)selectItem.get("food_name");
        fQuantity= (String)selectItem.get("food_quantity");
        fUnit= (String)selectItem.get("food_unit");
        fPosition= (String)selectItem.get("food_position");
        fStoragetime= (String)selectItem.get("food_storagetime");

        sItem= new SpecieItem(
                (String)selectItem.get("specie_id"),
                (String)selectItem.get("specie_name"),
                (String)selectItem.get("specie_life")
        );

        setfExpirationdate();
        setfLife();
    }

    //Getter&Setter
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

    public SpecieItem getsItem() {
        return sItem;
    }

    public void setsItem(SpecieItem sItem) {
        this.sItem = sItem;
    }

    public String getfExpirationdate() {
        return fExpirationdate;
    }

    public String getfLife() {
        return fLife;
    }

    //其他
    public HashMap<String, Object> getfHashMap() {      //僅允許內部設置.
        HashMap<String, Object> fHashMap= new HashMap<>();
        fHashMap.put("food_id", fId);
        fHashMap.put("food_specie", fSpecie);
        fHashMap.put("food_name", fName);
        fHashMap.put("food_quantity", fQuantity);
        fHashMap.put("food_unit", fUnit);
        fHashMap.put("food_position", fPosition);
        fHashMap.put("food_storagetime", fStoragetime);

        fHashMap.put("food_expirationdate", fExpirationdate);
        fHashMap.put("food_life", fLife);

        fHashMap.put("specie_id", sItem.getsId());
        fHashMap.put("specie_name", sItem.getsName());
        fHashMap.put("specie_life", sItem.getsLife());

        return fHashMap;
    }

    //計算過期日
    private void setfExpirationdate() {
        DateFunction df= new DateFunction();
        df.dateAdd(fStoragetime, sItem.getsLife());
        fExpirationdate= df.stringFormat();
    }

    //計算剩餘天數
    public void setfLife() {
        fLife= DateFunction.dateCalculation(
                fExpirationdate, DateFunction.getToday()
        );
    }

}
