package tw.idv.qianhuis.cuisinemanager;

import android.content.Context;

import java.util.HashMap;

public class SpecieItem {
    private String sId;
    private String sName;
    private String sLife;
    private String sImage;

    private String imgId= "";

    //public SpecieItem(){};

    public SpecieItem(HashMap<String, Object> selectItem){
        sId= (String)selectItem.get("specie_id");
        sName= (String)selectItem.get("specie_name");
        sLife= (String)selectItem.get("specie_life");
        sImage= (String)selectItem.get("specie_image");
        imgId= (String)selectItem.get("specie_imgid");
    }

    public SpecieItem(String sid, String sname, String slife, String simage){
        sId= sid;
        sName= sname;
        sLife= slife;
        sImage= simage;
    }

    //Getter&Setter
    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsLife() {
        return sLife;
    }

    public void setsLife(String sLife) {
        this.sLife = sLife;
    }

    public String getsImage() {
        return sImage;
    }

    public void setsImage(String sImage) {
        this.sImage = sImage;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public HashMap<String, Object> getsHashMap() {
        HashMap<String, Object> sHashMap= new HashMap<>();
        sHashMap.put("specie_id", sId);
        sHashMap.put("specie_name", sName);
        sHashMap.put("specie_life", sLife);
        sHashMap.put("specie_image", sImage);
        sHashMap.put("specie_imgid", imgId);

        return sHashMap;
    }
}
