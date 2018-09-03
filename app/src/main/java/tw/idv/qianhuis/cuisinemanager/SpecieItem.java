package tw.idv.qianhuis.cuisinemanager;

import java.util.HashMap;

public class SpecieItem {
    private String sId;
    private String sName;
    private String sLife;

    //public SpecieItem(){};

    public SpecieItem(HashMap<String, Object> selectItem){
        sId= (String)selectItem.get("specie_id");
        sName= (String)selectItem.get("specie_name");
        sLife= (String)selectItem.get("specie_life");
    }

    public SpecieItem(String sid, String sname, String sfoodlife){
        sId= sid;
        sName= sname;
        sLife= sfoodlife;
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

    public HashMap<String, Object> getsHashMap() {
        HashMap<String, Object> sHashMap= new HashMap<>();
        sHashMap.put("specie_id", sId);
        sHashMap.put("specie_name", sName);
        sHashMap.put("specie_life", sLife);

        return sHashMap;
    }
}
