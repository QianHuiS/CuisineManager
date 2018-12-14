package tw.idv.qianhuis.cuisinemanager;

import java.util.HashMap;

public class TypeItem {
    private String tId;
    private String tMain;
    private String tTag;

    //public TypeItem(){};

    public TypeItem(HashMap<String, Object> selectItem){
        tId= (String)selectItem.get("type_id");
        tMain= (String)selectItem.get("type_main");
        tTag= (String)selectItem.get("type_tag");
    }

    public TypeItem(String tid, String tmain, String ttag){
        tId= tid;
        tMain= tmain;
        tTag= ttag;
    }

    //Getter&Setter
    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String gettMain() {
        return tMain;
    }

    public void settMain(String tMain) {
        this.tMain = tMain;
    }

    public String gettTag() {
        return tTag;
    }

    public void settTag(String tTag) {
        this.tTag = tTag;
    }

    public HashMap<String, Object> gettHashMap() {
        HashMap<String, Object> tHashMap= new HashMap<>();
        tHashMap.put("type_id", tId);
        tHashMap.put("type_main", tMain);
        tHashMap.put("type_tag", tTag);

        return tHashMap;
    }
}
