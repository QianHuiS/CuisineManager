package tw.idv.qianhuis.cuisinemanager;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/*
 * 隱藏StatusBar&ActivityBar:
 *   res\values\styles.xml 加設定
 *   AndroidManifest.xml 更改 <application android:theme>
 * 鎖定橫向螢幕: AndroidManifest.xml 加 <activity android:screenOrientation>
 */
public class MainActivity extends AppCompatActivity {

    //變數
    Button bt_next, bt_expired;
    LinearLayout ll_next1, ll_next2;

    Button bt_add, bt_search, bt_ssetting;   //expiration date 到期日.    //bt_側欄
    Button bt_freezing, bt_refrigerated, bt_fresh;
    final String ALL = "1";

    ImageView iv_nodata;
    GridView gv_food;
    ArrayList<HashMap<String, Object>> l_food, l_specie, l_simage;

    Button bt_select, bt_revise, bt_delete;

    ImageView iv_simage;
    TextView tv_sname, tv_slife;
    TextView tv_fname, tv_fquantity, tv_funit, tv_fposition, tv_fstoragetime;
    TextView tv_expirationdate, tv_foodlife;

    //DB
    private SQLiteDatabase mSQLiteDatabase= null;
    private static final String DATABASE_NAME = "app.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge);

        //開啟DB
        mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        String DROP_FOOD_TABLE=  "DROP TABLE IF EXISTS food";
        String DROP_SPECIE_TABLE=  "DROP TABLE IF EXISTS specie";
        String DROP_RECIPE_TABLE=  "DROP TABLE IF EXISTS recipe";
        String DROP_TYPE_TABLE=  "DROP TABLE IF EXISTS type";
        mSQLiteDatabase.execSQL(DROP_FOOD_TABLE);
        mSQLiteDatabase.execSQL(DROP_SPECIE_TABLE);
        mSQLiteDatabase.execSQL(DROP_RECIPE_TABLE);
        mSQLiteDatabase.execSQL(DROP_TYPE_TABLE);

        String CREATE_FOOD_TABLE = "CREATE TABLE IF NOT EXISTS " +  //建立表單(若表單不存在!!). 建錯移除app.
                "food (" +
                "food_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "food_specie INTEGER, " +     // REFERENCES specie(specie_id)外來鍵: 參照 某表 (某欄位).
                "food_name TEXT, food_quantity REAL, food_unit TEXT, " +
                "food_position TEXT, food_storagetime TEXT)";
        mSQLiteDatabase.execSQL(CREATE_FOOD_TABLE);     //執行SQL指令的字串.

        String CREATE_SPECIE_TABLE = "CREATE TABLE IF NOT EXISTS " +  //建立表單(若表單不存在!!). 建錯移除app.
                "specie (" +
                "specie_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "specie_name TEXT, specie_life INTEGER, specie_image TEXT)";
        mSQLiteDatabase.execSQL(CREATE_SPECIE_TABLE);     //執行SQL指令的字串.

        String CREATE_RECIPE_TABLE = "CREATE TABLE IF NOT EXISTS " +  //建立表單(若表單不存在!!). 建錯移除app.
                "recipe (" +
                "recipe_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_phc INTEGER, recipe_type TEXT, " +
                "recipe_name TEXT, recipe_image TEXT, " +
                "recipe_portion INTEGER, recipe_food TEXT, " +
                "recipe_remark TEXT, recipe_cookstep TEXT)";
        mSQLiteDatabase.execSQL(CREATE_RECIPE_TABLE);     //執行SQL指令的字串.

        String CREATE_TYPE_TABLE = "CREATE TABLE IF NOT EXISTS " +  //建立表單(若表單不存在!!). 建錯移除app.
                "type (" +
                "type_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type_main TEXT, type_content INTEGER)";
        mSQLiteDatabase.execSQL(CREATE_TYPE_TABLE);     //執行SQL指令的字串.

        //連結XML
        bt_next= findViewById(R.id.bt_next);
        bt_expired= findViewById(R.id.bt_expired);
        ll_next1= findViewById(R.id.ll_next1);
        ll_next2= findViewById(R.id.ll_next2);

        bt_freezing= findViewById(R.id.bt_freezing);
        bt_refrigerated= findViewById(R.id.bt_refrigerated);
        bt_fresh= findViewById(R.id.bt_fresh);
        bt_add= findViewById(R.id.bt_add);
        bt_search= findViewById(R.id.bt_search);
        bt_ssetting= findViewById(R.id.bt_ssetting);

        iv_nodata= findViewById(R.id.iv_nodata);
        gv_food= findViewById(R.id.gv_food);
        l_food= new ArrayList<>();
        l_specie= new ArrayList<>();
        l_simage= new ArrayList<>();

        //bt_側欄 bt_expired

        bt_delete= findViewById(R.id.bt_delete);
        bt_revise= findViewById(R.id.bt_revise);
        bt_select= findViewById(R.id.bt_select);

        iv_simage= findViewById(R.id.iv_simage);
        tv_sname= findViewById(R.id.tv_sname);
        tv_slife= findViewById(R.id.tv_slife);

        tv_fname= findViewById(R.id.tv_fname);
        tv_fquantity= findViewById(R.id.tv_fquantity);
        tv_funit= findViewById(R.id.tv_funit);
        tv_fposition= findViewById(R.id.tv_fposition);
        tv_fstoragetime= findViewById(R.id.tv_fstoragetime);
        tv_expirationdate= findViewById(R.id.tv_expirationdate);
        tv_foodlife= findViewById(R.id.tv_foodlife);

        simageList();

        Cursor c;
        //若種類table為空, 新增預設種類"未知類".
        c = mSQLiteDatabase.rawQuery("SELECT * FROM specie WHERE 1", null);
        if(c.getCount()==0) {
            String INSERT_S;
            INSERT_S = "INSERT INTO specie (specie_name, specie_life, specie_image) " +
                    "VALUES('未知類', '0', 'specie_unknown')";
            mSQLiteDatabase.execSQL(INSERT_S);

            INSERT_S = "INSERT INTO specie (specie_name, specie_life, specie_image) " +
                    "VALUES('五穀根莖', '7', 'specie_cereal')";
            mSQLiteDatabase.execSQL(INSERT_S);

            INSERT_S = "INSERT INTO specie (specie_name, specie_life, specie_image) " +
                    "VALUES('蛋&豆', '6', 'specie_egg')";
            mSQLiteDatabase.execSQL(INSERT_S);

            INSERT_S = "INSERT INTO specie (specie_name, specie_life, specie_image) " +
                    "VALUES('海產', '4', 'specie_fish')";
            mSQLiteDatabase.execSQL(INSERT_S);

            INSERT_S = "INSERT INTO specie (specie_name, specie_life, specie_image) " +
                    "VALUES('肉', '3', 'specie_meat')";
            mSQLiteDatabase.execSQL(INSERT_S);

            INSERT_S = "INSERT INTO specie (specie_name, specie_life, specie_image) " +
                    "VALUES('蔬菜', '5', 'specie_vagetable')";
            mSQLiteDatabase.execSQL(INSERT_S);

            INSERT_S = "INSERT INTO specie (specie_name, specie_life, specie_image) " +
                    "VALUES('水果', '0', 'specie_fruit')";
            mSQLiteDatabase.execSQL(INSERT_S);

            INSERT_S = "INSERT INTO specie (specie_name, specie_life, specie_image) " +
                    "VALUES('奶&油脂', '10', 'specie_milk')";
            mSQLiteDatabase.execSQL(INSERT_S);
        }
        c.close();

        //放入測試用data
        //判斷table內是否為空, 若為空則放入資料.
        int num=1;
        c = mSQLiteDatabase.rawQuery("SELECT * FROM food WHERE 1", null);
        num=c.getCount();
        c.close();

        if(num==0) {
            c=mSQLiteDatabase.rawQuery("SELECT * FROM specie WHERE 1 ", null);
            c.moveToFirst();
            c.moveToNext();

            for(int i=0; i<2; i++) {
                String INSERT_F = "INSERT INTO food (food_specie, food_name, food_quantity, " +
                        "food_unit, food_position, food_storagetime) " +
                        "VALUES('"+c.getString(0)+"', '地瓜"+i+"', '2', " +
                        "'顆', '冷凍室', '2018-11-21')";
                mSQLiteDatabase.execSQL(INSERT_F);
            }
            c.moveToNext();
            for(int i=0; i<2; i++) {
                String INSERT_F = "INSERT INTO food (food_specie, food_name, food_quantity, " +
                        "food_unit, food_position, food_storagetime) " +
                        "VALUES('"+c.getString(0)+"', '雞蛋"+i+"', '1.5', " +
                        "'顆', '保鮮室', '2018-11-20')";
                mSQLiteDatabase.execSQL(INSERT_F);
            }
            c.moveToNext();
            for(int i=0; i<2; i++) {
                String INSERT_F = "INSERT INTO food (food_specie, food_name, food_quantity, " +
                        "food_unit, food_position, food_storagetime) " +
                        "VALUES('"+c.getString(0)+"', '海帶"+i+"', '1', " +
                        "'包', '冷藏室', '2018-11-22')";
                mSQLiteDatabase.execSQL(INSERT_F);
            }
            c.close();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        specieList();   //查詢種類DB放入種類list.
        showList(ALL);

        //上下列切換顯示/隱藏
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ll_next1.getVisibility()==View.VISIBLE) {
                    ll_next1.setVisibility(View.GONE);
                    ll_next2.setVisibility(View.VISIBLE);
                    bt_next.setText("↑");
                } else {
                    ll_next2.setVisibility(View.GONE);
                    ll_next1.setVisibility(View.VISIBLE);
                    bt_next.setText("↓");
                }
            }
        });

        //冷凍food資料
        bt_freezing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否有點擊三bt
                if(!bt_freezing.isSelected()) {
                    String FREEZING = "food_position = '冷凍室' ";
                    bt_freezing.setSelected(true);
                    bt_refrigerated.setSelected(false);
                    bt_fresh.setSelected(false);
                    bt_search.setSelected(false);
                    showList(FREEZING);
                } else {
                    bt_freezing.setSelected(false);
                    showList(ALL);
                }
                /*
                if(!frzIsClick) {
                    String FREEZING = "food_position = '冷凍室' ";
                    showList(FREEZING);
                    frzIsClick= true;
                    refIsClick= false;
                    frsIsClick= false;
                    searchIsClick= false;
                } else {
                    showList(ALL);
                    frzIsClick = false;
                }
                */
            }
        });

        //冷藏food資料
        bt_refrigerated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bt_refrigerated.isSelected()) {
                    String REFRIGERATED = "food_position = '冷藏室' ";
                    bt_freezing.setSelected(false);
                    bt_refrigerated.setSelected(true);
                    bt_fresh.setSelected(false);
                    bt_search.setSelected(false);
                    showList(REFRIGERATED);
                } else {
                    bt_refrigerated.setSelected(false);
                    showList(ALL);
                }
                /*
                if(!refIsClick) {
                    String REFRIGERATED = "food_position = '冷藏室' ";
                    showList(REFRIGERATED);
                    frzIsClick= false;
                    refIsClick= true;
                    frsIsClick= false;
                    searchIsClick= false;
                } else {
                    showList(ALL);
                    refIsClick = false;
                }
                */
            }
        });

        //保鮮food資料
        bt_fresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bt_fresh.isSelected()) {
                    String FRESH = "food_position = '保鮮室' ";
                    bt_freezing.setSelected(false);
                    bt_refrigerated.setSelected(false);
                    bt_fresh.setSelected(true);
                    bt_search.setSelected(false);
                    showList(FRESH);
                } else {
                    bt_fresh.setSelected(false);
                    showList(ALL);
                }
                /*
                if(!frsIsClick) {
                    String FRESH = "food_position = '保鮮室' ";
                    showList(FRESH);
                    frzIsClick= false;
                    refIsClick= false;
                    frsIsClick= true;
                    searchIsClick= false;
                } else {
                    showList(ALL);
                    frsIsClick = false;
                }
                */
            }
        });

    }

    // TODO: 2018/8/13 建立bar側欄, ????.

    //其他函式
    //顯示食物gridview
    public void showList(final String WHERE){ //把DB內容放入HashMap, 把HashMap放入ArrayList, 把ArrayList放入SimpleAdapter顯示.
        //取得foodTable資料
        l_food.clear();
        Cursor c1;
        c1= mSQLiteDatabase.rawQuery("SELECT * FROM food WHERE "+WHERE, null);
        c1.moveToFirst();

        //檢查是否有資料
        if(c1.getCount()==0) {   //若無資料, 顯示找不到資料及圖片.
            c1.close();

            //禁用bt
            bt_delete.setEnabled(false);
            bt_revise.setEnabled(false);
            bt_select.setEnabled(false);

            //種類!!
            tv_fname.setText("");
            tv_fquantity.setText("");
            tv_funit.setText("");
            tv_fposition.setText("");
            tv_fstoragetime.setText("");

            iv_nodata.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "沒有資料!!", Toast.LENGTH_SHORT).show();
        } else {    //若有資料, 將每筆資料取出.
            iv_nodata.setVisibility(View.GONE); //有資料則不顯示圖片.

            while(!c1.isAfterLast()){
                //取出種類內容(同等外來鍵效果)
                Cursor c2;  //透過food的specieID欄位, 查詢specie資料並取出.
                String SELECT= "SELECT * FROM specie WHERE specie_id= " + (c1.getString(1));
                c2= mSQLiteDatabase.rawQuery(SELECT, null);
                c2.moveToFirst();

                SpecieItem si;
                si = new SpecieItem(c2.getString(0), c2.getString(1),
                        c2.getString(2), c2.getString(3));
                si.setImgId(String.valueOf(
                        getResources().getIdentifier(
                        si.getsImage(), "drawable", getPackageName())
                ));
                c2.close();

                FoodItem fi= new FoodItem(c1.getString(0),
                        c1.getString(1), c1.getString(2),
                        c1.getString(3), c1.getString(4),
                        c1.getString(5), c1.getString(6), si
                );

                l_food.add(fi.getfHashMap());
                c1.moveToNext();
            }
            c1.close();

            //即期排序
            if(bt_expired.isSelected()) {     //bt_即期.setOnClick{ isflag= !isflag; } → if(isflag) 排序;
                Collections.sort(l_food, new Comparator<HashMap<String, Object>>() {
                    public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                        Integer fLife1 = Integer.valueOf(o1.get("food_life").toString());  //從l_food裡面拿出來的第一個.
                        Integer fLife2 = Integer.valueOf(o2.get("food_life").toString());  //從l_food裡面拿出來的第二個.
                        return fLife1.compareTo(fLife2);
                    }
                });
            }

            SimpleAdapter adapter= new SimpleAdapter(MainActivity.this,
                    l_food, R.layout.gridview_fridge,
                    new String[]{"specie_imgid", "food_name", "food_life"},
                    new int[]{R.id.iv_showfspecie, R.id.tv_showfname, R.id.tv_showflife}
            );
            gv_food.setAdapter(adapter);

            //點擊gv顯示詳細資料
            gv_food.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //所選項目效果, 取出l_food資料, 顯示.
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //所選項目美工效果
                    gv_food.getAdapter().getView(position, view, parent).setSelected(true);
                    //關鍵為, 取得指定item的view, 設定狀態state_selected; Adapter介面下getView()即為item所屬view.

                    final FoodItem fi= new FoodItem(l_food.get(position));  //取得點擊的項目, 變成一物件.

                    //啟用bt
                    bt_delete.setEnabled(true);
                    bt_revise.setEnabled(true);
                    bt_select.setEnabled(true);

                    //顯示詳細資料
                    tv_sname.setText(fi.getsItem().getsName());
                    tv_slife.setText(fi.getsItem().getsLife());
                    iv_simage.setImageResource(Integer.valueOf(fi.getsItem().getImgId()));
                    tv_fname.setText(fi.getfName());
                    tv_fquantity.setText(fi.getfQuantity());
                    tv_funit.setText(fi.getfUnit());
                    tv_fposition.setText(fi.getfPosition());
                    tv_fstoragetime.setText(fi.getfStoragetime());
                    tv_expirationdate.setText(fi.getfExpirationdate());
                    tv_foodlife.setText(fi.getfLife());

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
                                    /*
                                    if(fDelete.getReturn().equals("")){
                                        Toast.makeText(MainActivity.this, "刪除失敗!?", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mSQLiteDatabase.execSQL(fDelete.getReturn());
                                        showList(WHERE); //刷新lv.
                                        Toast.makeText(MainActivity.this, "刪除成功!!", Toast.LENGTH_SHORT).show();
                                    }
                                    */
                                    showList(WHERE); //刷新lv.
                                }
                            });
                        }
                    });

                    //修改food資料
                    bt_revise.setOnClickListener(new View.OnClickListener() {   //修改bt, DB修改後showList().
                        @Override
                        public void onClick(View v) {
                            final CustomDialog fRevise= new CustomDialog(MainActivity.this);
                            fRevise.buildFInput(fi, l_specie);
                            fRevise.show();
                            //監聽alert是否關閉(關閉後執行code)
                            fRevise.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    /*
                                    if(fRevise.getReturn().equals("")){
                                        Toast.makeText(MainActivity.this, "修改失敗!?", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mSQLiteDatabase.execSQL(fRevise.getReturn());
                                        showList(WHERE); //刷新lv.
                                        Toast.makeText(MainActivity.this, "修改成功!!", Toast.LENGTH_SHORT).show();
                                    }
                                    */
                                    showList(WHERE); //刷新lv.
                                }
                            });

                        }
                    });

                }
            });

            // TODO: 2018/10/13 待修正, 預設點擊沒有setSelected()效果.
            //預設點擊項目
            gv_food.getOnItemClickListener().onItemClick(
                    null,null,0,0);
            /*
            gv_food.performItemClick(
                    gv_food.getAdapter().getView(0,null,null),
                    0, 0);

            gv_food.performItemClick(
                    gv_food.getChildAt(0),
                    0,
                    0);*/
            //View: The view within the AdapterView that was clicked.
        }

        // TODO: 2018/8/21 待優化, 數量單位輸入改用下拉選單?
        // TODO: 2018/9/6 待優化, 自訂鍵盤顯示.
        //新增food資料
        bt_add.setOnClickListener(new View.OnClickListener() {  //
            @Override
            public void onClick(View v) {
                final CustomDialog fAdd= new CustomDialog(MainActivity.this);
                fAdd.buildFInput(l_specie);
                fAdd.show();
                //監聽alert是否關閉(關閉後執行code)
                fAdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        /*
                        if(fAdd.getReturn().equals("")){
                            Toast.makeText(MainActivity.this, "新增失敗!?", Toast.LENGTH_SHORT).show();
                        } else {
                            mSQLiteDatabase.execSQL(fAdd.getReturn());
                            showList(WHERE); //刷新lv.
                            Toast.makeText(MainActivity.this, "新增成功!!", Toast.LENGTH_SHORT).show();
                        }
                        */
                        showList(WHERE); //刷新lv.
                    }
                });
            }
        });


        //搜尋food資料
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bt_search.isSelected()) {

                    final CustomDialog fsearch= new CustomDialog(MainActivity.this);
                    fsearch.buildSearch(l_specie);
                    fsearch.show();
                    fsearch.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(fsearch.getReturn().equals("")) {
                                Toast.makeText(MainActivity.this, "查詢失敗!?", Toast.LENGTH_SHORT).show();
                            } else {
                                showList(fsearch.getReturn());
                                bt_freezing.setSelected(false);
                                bt_refrigerated.setSelected(false);
                                bt_fresh.setSelected(false);
                                bt_search.setSelected(true);
                                Toast.makeText(MainActivity.this, "查詢成功!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    /*
                    frzIsClick= false;
                    refIsClick= false;
                    frsIsClick= false;
                    searchIsClick= true;
                    */
                } else {
                    bt_search.setSelected(false);
                    showList(ALL);
                }
            }
        });

        //種類設定
        bt_ssetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomDialog specie= new CustomDialog(MainActivity.this);
                specie.buildSset(l_specie);
                specie.show();
                specie.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //判斷操作為新增或修改
                        if(specie.getReturn().equals("ADD")) {
                            final CustomDialog sAdd= new CustomDialog(MainActivity.this);
                            sAdd.buildSInput(l_simage);
                            sAdd.show();
                            sAdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    /*
                                    if (sAdd.getReturn().equals("")) {
                                        Toast.makeText(MainActivity.this, "新增失敗!?", Toast.LENGTH_SHORT).show();
                                        // TODO: 2018/9/5 待優化, 只能在設定中新增修改種類, 改進為即時新增及刷新種類.
                                    } else {
                                        mSQLiteDatabase.execSQL(sAdd.getReturn());
                                        Toast.makeText(MainActivity.this, "新增成功!!", Toast.LENGTH_SHORT).show();
                                        specieList();
                                        showList(WHERE);
                                    }
                                    */
                                    specieList();
                                    showList(WHERE);
                                }
                            });
                        } else if(!specie.getReturn().equals("")) {
                            SpecieItem si= new SpecieItem(l_specie.get(
                                    Integer.valueOf(specie.getReturn())
                            ));

                            final CustomDialog sRevise= new CustomDialog(MainActivity.this);
                            sRevise.buildSInput(si, l_simage);
                            sRevise.show();
                            sRevise.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    /*
                                    if (sRevise.getReturn().equals("")) {
                                        Toast.makeText(MainActivity.this, "修改失敗!?", Toast.LENGTH_SHORT).show();
                                        // TODO: 2018/9/5 待優化, 只能在設定中新增修改種類, 改進為即時新增及刷新種類.
                                    } else {
                                        mSQLiteDatabase.execSQL(sRevise.getReturn());
                                        Toast.makeText(MainActivity.this, "修改成功!!", Toast.LENGTH_SHORT).show();
                                        specieList();
                                        showList(WHERE);
                                    }
                                    */
                                    specieList();
                                    showList(WHERE);
                                }
                            });
                        }
                    }
                });
            }
        });

        //即期food資料排序
        bt_expired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_expired.setSelected(!bt_expired.isSelected());
                //isExpired= !isExpired;  //若點擊, 則開啟即期排序.
                showList(WHERE);
                // TODO: 2018/9/6 待優化, 廣播提示將過期食品.
            }
        });

    }

    public void specieList() {
        l_specie.clear();

        String SELECT= "SELECT * FROM specie WHERE 1 ";
        Cursor c= mSQLiteDatabase.rawQuery(SELECT, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            SpecieItem si= new SpecieItem(c.getString(0), c.getString(1),
                    c.getString(2), c.getString(3)
            );
            si.setImgId(String.valueOf(
                    getResources().getIdentifier(si.getsImage(), "drawable", getPackageName())
            ));

            l_specie.add(si.getsHashMap());
            c.moveToNext();
        }
        c.close();
    }

    public void simageList() {
        l_simage.clear();

        String[] imgName= new String[] {
                "specie_unknown", "specie_cereal", "specie_egg",
                "specie_fish", "specie_meat", "specie_vagetable",
                "specie_fruit", "specie_milk", "specie_dessert",
                "specie_drink", "specie_delicatessen"
        };

        String[] imgId= new String[imgName.length];
        for(int i=0; i<imgName.length; i++)
            imgId[i]= String.valueOf(
                    getResources().getIdentifier(imgName[i], "drawable", getPackageName())
            );

        for(int i=0; i<imgName.length; i++) {
            HashMap<String, Object> imgHashMap= new HashMap<>();
            imgHashMap.put("simage_name", imgName[i]);
            imgHashMap.put("simage_id", imgId[i]);
            l_simage.add(imgHashMap);
        }
    }

}
