package tw.idv.qianhuis.cuisinemanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class RecipeItem {
    private String rId;
    private String rPhc;
    private String rTypes;   //未分割處理的type.
    private String rName;
    private String rImage;
    private String rPortion;
    private String rFoods;   //未分割處理的food.
    private String rCooksteps;   //未分割處理的cookstep
    private String rStepimages;
    private String rRemark;

    private String rExpired;

    private ArrayList<TypeItem> tItems;

    //public FoodItem(){};

    //建立HashMap
    public RecipeItem(String rid, String rphc, String rtype, String rname,
                      String rimage, String rportion, String rfood, String rcookstep,
                      String rstepimage, String rremark, String rexpired, ArrayList<TypeItem> titem){
        rId = rid;
        rPhc = rphc; //種類!!
        rTypes = rtype;
        rName = rname;
        rImage = rimage;
        rPortion= rportion;
        rFoods = rfood;
        rCooksteps = rcookstep;
        rStepimages = rstepimage;
        rRemark = rremark;

        rExpired = rexpired;

        tItems = titem;

    }

    //取得已有的HashMap內容物
    public RecipeItem(HashMap<String, Object> selectItem){
        rId = (String)selectItem.get("recipe_id");   //拿出物件中的某資訊.
        rPhc = (String)selectItem.get("recipe_phc");
        rTypes = (String)selectItem.get("recipe_types");
        rName = (String)selectItem.get("recipe_name");
        rImage = (String)selectItem.get("recipe_image");
        rPortion= (String)selectItem.get("recipe_portion");
        rFoods = (String)selectItem.get("recipe_foods");
        rCooksteps = (String)selectItem.get("recipe_cooksteps");
        rStepimages = (String)selectItem.get("recipe_stepimages");
        rRemark = (String)selectItem.get("recipe_remark");

        rExpired = (String)selectItem.get("recipe_expired");
    }

    //Getter&Setter


    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getrPhc() {
        return rPhc;
    }

    public void setrPhc(String rPhc) {
        this.rPhc = rPhc;
    }

    public String getrTypes() {
        return rTypes;
    }

    public void setrTypes(String rTypes) {
        this.rTypes = rTypes;
    }

    public String getrName() {
        return rName;
    }

    public void setrName(String rName) {
        this.rName = rName;
    }

    public String getrImage() {
        return rImage;
    }

    public void setrImage(String rImage) {
        this.rImage = rImage;
    }

    public String getrPortion() {
        return rPortion;
    }

    public void setrPortion(String rPortion) {
        this.rPortion = rPortion;
    }

    public String getrFoods() {
        return rFoods;
    }

    public void setrFoods(String rFoods) {
        this.rFoods = rFoods;
    }

    public String getrCooksteps() {
        return rCooksteps;
    }

    public void setrCooksteps(String rCooksteps) {
        this.rCooksteps = rCooksteps;
    }

    public String getrStepimages() {
        return rStepimages;
    }

    public void setrStepimages(String rStepimages) {
        this.rStepimages = rStepimages;
    }

    public String getrRemark() {
        return rRemark;
    }

    public void setrRemark(String rRemark) {
        this.rRemark = rRemark;
    }

    public ArrayList<TypeItem> gettItems() {
        return tItems;
    }

    public void settItems(ArrayList<TypeItem> tItems) {
        this.tItems = tItems;
    }

    public int getrExpired() {
        return Integer.valueOf(rExpired);
    }

    public void setrExpired(int rExpired) {
        this.rExpired = String.valueOf(rExpired);
    }

    //其他
    public HashMap<String, Object> getrHashMap() {      //僅允許內部設置.
        HashMap<String, Object> rHashMap= new HashMap<>();
        rHashMap.put("recipe_id", rId);
        rHashMap.put("recipe_phc", rPhc);
        rHashMap.put("recipe_types", rTypes);
        rHashMap.put("recipe_name", rName);
        rHashMap.put("recipe_image", rImage);
        rHashMap.put("recipe_portion", rPortion);
        rHashMap.put("recipe_foods", rFoods);
        rHashMap.put("recipe_cooksteps", rCooksteps);
        rHashMap.put("recipe_stepimages", rStepimages);
        rHashMap.put("recipe_remark", rRemark);

        rHashMap.put("recipe_expired", rExpired);

        return rHashMap;
    }

    // TODO: 2018/12/12 待優化, 食材顯示改為可點擊的自定義view.
    public String showFoods() {
        String showf= "";
        /*
        //無編號
        showf= rFoods;
        showf= showf.replace(" _", "\n").trim()
                .replace("0", "");   //若為0則以空取代顯示.
        */
        for(int i = 1; !(showFoods(i).equals("")); i++){   //顯示原資料
            String s_food= showFoods(i);
            //食材增加
            if(i!=1)    showf= showf.concat("\n");
            showf= showf.concat(i+". "+getFoods(s_food, 1)+" ");
            if(getFoods(s_food, 2).equals("0"))
                showf= showf+" ";
            else
                showf= showf.concat(getFoods(s_food, 2))+" ";
            showf= showf.concat(getFoods(s_food, 3));
        }

        return showf;
    }

    public String showFoods(int food) {
        String showf= "";

        String tmp= rFoods;
        int i= 1;
        while(tmp.contains("_")) {    //字串是否包含"_".
            if(i== food) {
                showf= showf.concat(tmp.substring(0, tmp.indexOf("_")));     //取得開始到第一個"_"前的子字串.
                showf= showf.trim();
            }
            i++;
            tmp= tmp.substring(tmp.indexOf("_")+1);   //tmp1= 第一個"_"後, 到結尾的字串.
        }
        //剩最後一項食材(tmp沒有"_")
        if(i==food) {
            showf = showf.concat(tmp).trim();
        }

        return showf;
    }

    public String getFoods(String s_food, int part) {
        String food= "";

        String tmp= s_food;
        int i= 1;
        while(tmp.contains(" ")) {    //字串是否包含" ".
            if(i== part) {
                food= food.concat(tmp.substring(0, tmp.indexOf(" ")));     //取得開始到第一個"_"前的子字串.
                food= food.trim();
            }
            i++;
            tmp= tmp.substring(tmp.indexOf(" ")+1);   //tmp1= 第一個" "後, 到結尾的字串.
        }
        //剩最後一項食材(tmp沒有" ")
        if(i==part) {
            food = food.concat(tmp).trim();
        }

        return food;
    }

    //分割rtype
    public String getTpyetag(int tag) {
        String typtag= "";

        //有編號
        String tmp= rTypes.trim();      //等同.substring(1,rTypes.length()-1);  //去除頭尾" ".
        //Log.d("rTypes", "rtypes="+rTypes+" tmp="+tmp);
        int i= 1;
        while(tmp.contains(" ")) {    //字串是否包含" ".
            if(i==tag) {
                typtag = typtag.concat(tmp.substring(0, tmp.indexOf(" "))).trim();     //取得開始到第一個" "前的子字串, 去除首尾空白.
                //Log.d("結果", "i= "+i+" rtypes="+rTypes+" tmp="+tmp);
            }
            i++;
            tmp= tmp.substring(tmp.indexOf(" ")+1);   //tmp= 第一個" "後, 到結尾的字串.
            //Log.d("i++", "i= "+i+"   tmp= "+tmp);
        }
        //剩最後一項(tmp沒有" ")
        if(i==tag) {
            typtag= typtag.concat(tmp).trim();
            //Log.d("結果", "i= "+i+" rtypes="+rTypes+" tmp="+tmp);
        }

        return typtag;
    }

    public String showTag(int tag) {
        String showt= "";
        if(tag<tItems.size())   showt= tItems.get(tag).gettTag();
        return showt;
    }

    public String showSteps(int step) {
        String shows= "";

        //有編號
        String tmp= rCooksteps;
        int i= 1;
        while(tmp.contains("_")) {    //字串是否包含"_".
            if(i==step) {
                shows = shows.concat(tmp.substring(0, tmp.indexOf("_"))).trim();     //取得開始到第一個"_"前的子字串, 去除首尾空白.
                //Log.d("結果", "i= "+i+"   step= "+step+"   shows= "+shows);
            }
            i++;
            tmp= tmp.substring(tmp.indexOf("_")+2);   //tmp1= 第一個"_"後, 到結尾的字串.
            //Log.d("i++", "i= "+i+"   tmp= "+tmp);
        }
        //剩最後一項食材(tmp沒有"_")
        if(i==step) {
            shows= shows.concat(tmp).trim();
            //Log.d("結果", "i= "+i+"   step= "+step+"   shows= "+shows);
        }

        return shows;
    }

    //bitmap轉string
    public static String bitmapToString(Bitmap bitmap){
        //将Bitmap转换成字符串
        String string=null;
        ByteArrayOutputStream bStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[]bytes=bStream.toByteArray();
        string= Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }

    //string轉bitmap
    public static Bitmap stringToBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
