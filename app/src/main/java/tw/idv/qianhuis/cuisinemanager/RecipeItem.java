package tw.idv.qianhuis.cuisinemanager;

import android.util.Log;

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

    private ArrayList<TypeItem> tItems;

    //public FoodItem(){};

    //建立HashMap
    public RecipeItem(String rid, String rphc, String rtype, String rname,
                      String rimage, String rportion, String rfood, String rcookstep,
                      String rstepimage, String rremark, ArrayList<TypeItem> titem){
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
        rHashMap.put("recipe_titems", tItems);

        return rHashMap;
    }

    //分割rfood
    private String[][] getFoods() {
        //rFood範例= "豆包 4 個 _老薑 3 片 _香菜 0 適量 "
        int separate= 0;    //查詢字串index, 有幾次分割(分割完幾個字串).
        int num= 0;
        while(rFoods.indexOf("_", num)>0) {   //若字串中包含"_"(否為-1則不成立).
            num= rFoods.indexOf("_", num);
            //tmp1= tmp1.substring(tmp1.indexOf("_")+1);   //tmp=第一個出現"_"後, 到結尾的子字串.
            separate++;
        }

        String[][] foods= new String[separate+1][3];   //[分割次數+1個食材][名稱數量單位].
        String tmp1= rFoods;
        String tmp2= "";
        for(int i=0; i<foods.length; i++) {     //先分割各食材, 再分割名稱數量單位.
            if(tmp1.contains("_")) {    //若字串中包含"_"(不是最後一個).
                tmp2= tmp1.substring(0, tmp1.indexOf("_")-1);    //取得開始到第一個"_"前的子字串.
                tmp1= tmp1.substring(tmp1.indexOf("_")+1);   //tmp1= 第一個"_"後, 到結尾的字串.
            } else {
                tmp2= tmp1;
            }

            for(int j=0; j<foods[i].length; j--){
                foods[i][j]= tmp2.substring(0, tmp2.indexOf(" ")-1);    //取得開始到第一個" "前的字串.
                if(j==foods[i].length-1) {}     //若是最後一個, 啥都不做.
                else    tmp2= tmp2.substring(tmp2.indexOf(" ")+1); //tmp2= 第一個" "後, 到結尾的字串.
            }
        }

        return foods;
    }

    // TODO: 2018/12/12 待優化, 食材顯示改為可點擊的自定義view.
    public String showFoods() {
        String showf= "";
        showf= rFoods;
        showf= showf.replace(" _", "\n").trim().replace("0", "");

        /*
        //有編號
        String tmp= rFoods;
        int i= 1;
        while(tmp.contains("_")) {    //字串是否包含"_".
            if(tmp.contains("0"))   tmp.replace("0", "");   //若有數量0則省略.
            showf= showf.concat(i +". " +tmp.substring(0, tmp.indexOf("_")-1));     //取得開始到第一個"_"前的子字串.
            showf= showf.trim().concat("\n");   //去除首尾空白, 接換行.
            i++;
            tmp= tmp.substring(tmp.indexOf("_")+1);   //tmp1= 第一個"_"後, 到結尾的字串.
        }
        //剩最後一項食材(tmp沒有"_")
        showf= showf.concat(i +". " +tmp);
        showf= showf.trim();
        */

        return showf;
    }

    //分割rtype
    public String getTpyetag(int tag) {
        String typtag= "";

        //有編號
        String tmp= rTypes.trim();      //等同.substring(1,rTypes.length()-1);  //去除頭尾" ".
        //Log.d("rTypes", "rtypes="+rTypes+"\ntmp="+tmp);
        int i= 1;
        while(tmp.contains(" ")) {    //字串是否包含" ".
            if(i==tag) {
                typtag = typtag.concat(tmp.substring(0, tmp.indexOf(" "))).trim();     //取得開始到第一個" "前的子字串, 去除首尾空白.
                //Log.d("結果", "i= "+i+"   step= "+step+"   shows= "+shows);
            }
            i++;
            tmp= tmp.substring(tmp.indexOf(" ")+1);   //tmp= 第一個" "後, 到結尾的字串.
            //Log.d("i++", "i= "+i+"   tmp= "+tmp);
        }
        //剩最後一項(tmp沒有" ")
        if(i==tag) {
            typtag= typtag.concat(tmp).trim();
            //Log.d("結果", "i= "+i+"   step= "+step+"   shows= "+shows);
        }

        return typtag;
    }

    public String showTag(int tag) {
        String showt= "";
        if(tag<tItems.size())   showt= tItems.get(tag).gettTag();
        return showt;
    }

    //分割rcookstept
    private String[][] getSteps() {
        int separate= 0;
        String[][]steps= new String[separate][];
        return steps;
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
}
