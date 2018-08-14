package tw.idv.qianhuis.cuisinemanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * 隱藏StatusBar&ActivityBar:
 *   res\values\styles.xml 加設定
 *   AndroidManifest.xml 更改 <application android:theme>
 * 鎖定橫向螢幕: AndroidManifest.xml 加 <activity android:screenOrientation>
 */
public class MainActivity extends AppCompatActivity {

    //變數
    Button bt_freezing, bt_refrigerated, bt_fresh;
    GridView gv_food;
    ArrayList<HashMap<String, Object>> l_food;

    //Button bt_側欄, bt_新增, bt_search, bt_expired;   //expiration date 到期日.

    Button bt_select, bt_revise, bt_delete;

    ImageView iv_fspecie;
    TextView tv_fname, tv_fquantity, tv_funit, tv_fposition, tv_fstoragetime;   //storagelife保存天數

    //DB
    private SQLiteDatabase mSQLiteDatabase= null;
    private static final String DATABASE_NAME = "app.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge);

        //開啟DB
        mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        String CREATE_FOOD_TABLE = "CREATE TABLE IF NOT EXISTS " +  //建立表單(若表單不存在!!). 建錯移除app.
                "food (" +
                "index_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "food_name TEXT, food_storagetime TEXT, food_specie INTEGER, " +
                "food_quantity REAL, food_unit TEXT, food_position TEXT)";
        mSQLiteDatabase.execSQL(CREATE_FOOD_TABLE);     //執行SQL指令的字串.

        //連結XML
        bt_freezing= findViewById(R.id.bt_freezing);
        bt_refrigerated= findViewById(R.id.bt_refrigerated);
        bt_fresh= findViewById(R.id.bt_fresh);
        gv_food= findViewById(R.id.gv_food);
        l_food= new ArrayList<>();

        //bt_側欄 bt_add bt_search bt_expired

        bt_delete= findViewById(R.id.bt_delete);
        bt_revise= findViewById(R.id.bt_revise);
        bt_select= findViewById(R.id.bt_select);
        iv_fspecie= findViewById(R.id.iv_fspecie);
        tv_fname= findViewById(R.id.tv_fname);
        tv_fquantity= findViewById(R.id.tv_fquantity);
        tv_funit= findViewById(R.id.tv_funit);
        tv_fposition= findViewById(R.id.tv_fposition);
        tv_fstoragetime= findViewById(R.id.tv_fstoragetime);

    }


    @Override
    protected void onResume() {
        super.onResume();

        // TODO: 2018/8/13 放入測試用data. 1
/*        //判斷table內是否為空, 若為空則放入資料.
        int num=1;
        c = mSQLiteDatabase.rawQuery("select * from music where 1", null);
        num=c.getCount();
        c.close();

        if(num==0) {
            for(int i=0; i<15; i++) {
                for(int j=0; j<2; j++) {
                    String str="tpoI_conversation0J";
                    str= str.replace("I", String.valueOf(i+1));
                    str= str.replace("J", String.valueOf(j+1));
                    String INSERT= "INSERT INTO music (music_name, listening_times) VALUES('" +
                            str+"','0')";
                    mSQLiteDatabase.execSQL(INSERT);
                }

                for(int j=0; j<4; j++) {
                    String str="tpoI_lecture0J";
                    str= str.replace("I", String.valueOf(i+1));
                    str= str.replace("J", String.valueOf(j+1));
                    String INSERT= "INSERT INTO music (music_name, listening_times) VALUES('" +
                            str+"','0')";
                    mSQLiteDatabase.execSQL(INSERT);
                }
            }
        }*/

    }

    // TODO: 2018/8/13 建立bar側欄, 寫新增查詢. 5

    //其他函式
    public void showList(){
        l_food.clear();
        Cursor c;
        c= mSQLiteDatabase.rawQuery("select * from food where 1", null);  //where 1 即成立, 沒有條件.
        c.moveToFirst();    //上列執行完c會在最後的下一筆位置(AfterLast), 故回到第一筆位置.

        // TODO: 2018/8/13 待優化; 改用for(int i=0; i<.總長; i++), put(欄位String[],i).
        while(!c.isAfterLast()){
            HashMap<String,Object> foodItem= new HashMap<>();
            foodItem.put("index_id", c.getString(0));
            foodItem.put("food_name", c.getString(1));
            foodItem.put("food_storagetime", c.getString(2));
            foodItem.put("food_specie", c.getString(3));
            foodItem.put("food_quantity", c.getString(4));
            foodItem.put("food_unit", c.getString(5));
            foodItem.put("food_position", c.getString(6));  //數字為欄位順序.
            l_food.add(foodItem);
            c.moveToNext();
        }
        c.close();
/*
        SimpleAdapter adapter= new SimpleAdapter(getApplicationContext(),
                l_food, R.layout.gridview_fridge,
                new String[]{"food_specie", "food_name", "food_quantity", "food_date"},
                new int[]{R.id.tv_date, R.id.tv_score, R.id.tv_goal, R.id.tv_needtime}
        );
        gv_food.setAdapter(adapter);

        //點擊gb顯示詳細資料
        gv_food.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, Object> selectItem= l_food.get(position); //取得點擊的項目, 變成一物件.
                //examneedtime= getNeedTime((String)selectItem.get("index_id"));   //拿出物件中的某資訊.

                //showNeedTime(examneedtime);
                // TODO: 2018/8/13 顯示詳細資料; 預設選擇項目1, 所選項目效果, 取出l_food資料. 2
            }
        });
*/
    }

    // TODO: 2018/8/13 刪除; 刪除bt, DB刪除後showList(). 3
    // TODO: 2018/8/13 修改; 修改bt, DB修改後showList(). 4
    /*
        //長按lv刪除項目
        gv_food.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, Object> selectItem= l_food.get(position); //取得點擊的項目, 變成一物件.
                String examId= (String)selectItem.get("index_id");   //拿出物件中的某資訊.
                mSQLiteDatabase.execSQL("Delete from exam where index_id=" +examId);

                showList(); //刷新lv.

                return true;   //是否將長短按分開.
            }
        });*/
}
