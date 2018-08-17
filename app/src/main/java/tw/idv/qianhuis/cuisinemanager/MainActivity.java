package tw.idv.qianhuis.cuisinemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

    Button bt_側欄, bt_add, bt_search, bt_expired;   //expiration date 到期日.

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
                "food_specie INTEGER, food_name TEXT, food_quantity REAL, " +
                "food_unit TEXT, food_position TEXT, food_storagetime TEXT)";
        mSQLiteDatabase.execSQL(CREATE_FOOD_TABLE);     //執行SQL指令的字串.

        //連結XML
        bt_freezing= findViewById(R.id.bt_freezing);
        bt_refrigerated= findViewById(R.id.bt_refrigerated);
        bt_fresh= findViewById(R.id.bt_fresh);
        bt_add= findViewById(R.id.bt_add);
        bt_search= findViewById(R.id.bt_search);
        gv_food= findViewById(R.id.gv_food);
        l_food= new ArrayList<>();

        //bt_側欄 bt_expired

        bt_delete= findViewById(R.id.bt_delete);
        bt_revise= findViewById(R.id.bt_revise);
        bt_select= findViewById(R.id.bt_select);
        iv_fspecie= findViewById(R.id.iv_fspecie);
        tv_fname= findViewById(R.id.tv_fname);
        tv_fquantity= findViewById(R.id.tv_fquantity);
        tv_funit= findViewById(R.id.tv_funit);
        tv_fposition= findViewById(R.id.tv_fposition);
        tv_fstoragetime= findViewById(R.id.tv_fstoragetime);


        //放入測試用data
        //判斷table內是否為空, 若為空則放入資料.
        Cursor c;
        int num=1;
        c = mSQLiteDatabase.rawQuery("select * from food where 1", null);
        num=c.getCount();
        c.close();

        if(num==0) {
            for(int i=0; i<12; i++) {
                String INSERT = "INSERT INTO food (food_specie, food_name, food_quantity, " +
                        "food_unit, food_position, food_storagetime) " +
                        "VALUES('1', '高麗菜"+i+"', '1.5', " +
                        "'顆', '保鮮室', '20180814')";
                mSQLiteDatabase.execSQL(INSERT);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        showList();

        //新增food資料
        bt_add.setOnClickListener(new View.OnClickListener() {  //
            @Override
            public void onClick(View v) {
                final CustomDialog fAdd= new CustomDialog(MainActivity.this);
                fAdd.buildAdd();
                fAdd.show();
                //監聽alert是否關閉(關閉後執行code)
                fAdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(fAdd.getSqlcode().equals("")){
                            Toast.makeText(MainActivity.this, "新增失敗!?", Toast.LENGTH_SHORT).show();
                        } else {
                            mSQLiteDatabase.execSQL(fAdd.getSqlcode());
                            showList(); //刷新lv.
                            Toast.makeText(MainActivity.this, "新增成功!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



    }

    // TODO: 2018/8/13 建立bar側欄, 寫新增查詢. 5

    //其他函式
    //顯示食物gridview
    public void showList(){ //把DB內容放入HashMap, 把HashMap放入ArrayList, 把ArrayList放入SimpleAdapter顯示.
        l_food.clear();

        Cursor c;
        c= mSQLiteDatabase.rawQuery("select * from food where 1", null);  //where 1 即成立, 沒有條件.
        c.moveToFirst();    //上列執行完c會在最後的下一筆位置(AfterLast), 故回到第一筆位置.

        // TODO: 2018/8/13 待優化, 改用for(int i=0; i<.總長; i++), put(欄位String[],i).
        while(!c.isAfterLast()){
            FoodItem fi= new FoodItem(c.getString(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6));

            // TODO: 2018/8/14 待修正, 計算食物剩餘天數; 日期計算判斷式.
            int foodlife= 3;
            int storagetime= Integer.valueOf(fi.getfStoragetime());
            int expirationdate= FoodItem.getExpirationdate(storagetime,foodlife);
            String storagelife= FoodItem.getStoragelife(expirationdate);
/*
            foodItem.put("expirationdate", String.valueOf(expirationdate));
            foodItem.put("foodlife", String.valueOf(foodlife));
            foodItem.put("storagelife", storagelife);
*/
            l_food.add(fi.getFoodItem());
            c.moveToNext();
        }
        c.close();

        // TODO: 2018/8/14 待修正, 食物種類圖樣顯示, storagelife顯示.
        SimpleAdapter adapter= new SimpleAdapter(MainActivity.this,
                l_food, R.layout.gridview_fridge,
                new String[]{"food_specie", "food_name", "storagelife"},
                new int[]{R.id.tv_showfspecie, R.id.tv_showfname, R.id.tv_showfstoragelife}
        );
        gv_food.setAdapter(adapter);

        //點擊gv顯示詳細資料; 預設選擇項目1, 所選項目效果, 取出l_food資料.
        gv_food.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // TODO: 2018/8/15 Debug; 刪除或更新後預設選取項目詳細資料不會刷新. 優化解決?
                // TODO: 2018/8/14 待優化, 預設選取項目, 所選項目美工效果.
                final FoodItem fi= new FoodItem(l_food.get(position));  //取得點擊的項目, 變成一物件.

                //種類!!
                tv_fname.setText(fi.getfName());  //拿出物件中的某資訊.
                tv_fquantity.setText(fi.getfQuantity());
                tv_funit.setText(fi.getfUnit());
                tv_fposition.setText(fi.getfPosition());
                tv_fstoragetime.setText(fi.getfStoragetime());

                // TODO: 2018/8/14 待優化, 顯示過期日.種類有效期限.剩餘有效天數.
                //tv_過期日.setText((String)selectItem.get("expirationdate"));
                //tv_種類有效期限.setText((String)selectItem.get("storagelife"));
                //tv_剩餘有效天數.setText((String)selectItem.get("foodlife"));

                //刪除food資料
                bt_delete.setOnClickListener(new View.OnClickListener() {   //刪除bt, 再次確認alert, DB刪除後showList().
                    @Override
                    public void onClick(View v) {
                        final CustomDialog fDelete= new CustomDialog(MainActivity.this);
                        fDelete.buildDelete(fi);
                        fDelete.show();
                        fDelete.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if(fDelete.getSqlcode().equals("")){
                                    Toast.makeText(MainActivity.this, "刪除失敗!?", Toast.LENGTH_SHORT).show();
                                } else {
                                    mSQLiteDatabase.execSQL(fDelete.getSqlcode());
                                    showList(); //刷新lv.
                                    Toast.makeText(MainActivity.this, "刪除成功!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                //修改food資料
                bt_revise.setOnClickListener(new View.OnClickListener() {   //修改bt, DB修改後showList().
                    @Override
                    public void onClick(View v) {
                        final CustomDialog fRevise= new CustomDialog(MainActivity.this);
                        fRevise.buildRevise(fi);
                        fRevise.show();
                        //監聽alert是否關閉(關閉後執行code)
                        fRevise.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if(fRevise.getSqlcode().equals("")){
                                    Toast.makeText(MainActivity.this, "修改失敗!?", Toast.LENGTH_SHORT).show();
                                } else {
                                    mSQLiteDatabase.execSQL(fRevise.getSqlcode());
                                    showList(); //刷新lv.
                                    Toast.makeText(MainActivity.this, "修改成功!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });

            }
        });

    }

}
