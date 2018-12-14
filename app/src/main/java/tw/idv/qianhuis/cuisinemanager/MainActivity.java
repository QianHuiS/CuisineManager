package tw.idv.qianhuis.cuisinemanager;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

    //fridge變數
    View layout_fridge, layout_recipe;
    boolean fridgeLoad=false, recipeLoad=false, cookLoad=false;  //layout是否載入過.
    final int FRIDGE=0, RECIPE=1, COOK=2;
    int layout_this= FRIDGE;
    int x1=0, y1=0, x2=0, y2=0;

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

    //recipe變數
    Button bt_suggest, bt_tsetting;
    Button bt_typemain, bt_typecontent;

    ListView lv_recipe;
    ArrayList<HashMap<String, Object>> l_recipe, l_type;

    ImageView iv_rimage;
    TextView tv_rportion, tv_rfood;
    TextView tv_rtype;

    Button bt_cookstept1, bt_cookstept2;
    TextView tv_rcookstep;

    //DB
    private SQLiteDatabase mSQLiteDatabase= null;
    private static final String DATABASE_NAME = "app.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_fridge);     //預設設定view.

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
                "recipe_phc TEXT, recipe_type TEXT, " +
                "recipe_name TEXT, recipe_image TEXT, " +
                "recipe_portion INTEGER, recipe_food TEXT, " +
                "recipe_cookstep TEXT, recipe_stepimage, " +
                "recipe_remark TEXT)";
        mSQLiteDatabase.execSQL(CREATE_RECIPE_TABLE);     //執行SQL指令的字串.

        String CREATE_TYPE_TABLE = "CREATE TABLE IF NOT EXISTS " +  //建立表單(若表單不存在!!). 建錯移除app.
                "type (" +
                "type_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type_main TEXT, type_tag INTEGER)";
        mSQLiteDatabase.execSQL(CREATE_TYPE_TABLE);     //執行SQL指令的字串.

        //切換各功能View
        LayoutInflater inflater = getLayoutInflater();
        layout_fridge= inflater.inflate(R.layout.activity_fridge, null);
        layout_recipe= inflater.inflate(R.layout.activity_recipe, null);
        setFridgeView();

        layout_fridge.setOnTouchListener(new nextView());
        layout_recipe.setOnTouchListener(new nextView());


        simageList();   //種類圖片載入.

        Cursor c;
        //若種類table為空, 新增預設種類"未知類".
        c = mSQLiteDatabase.rawQuery("SELECT * FROM specie WHERE 1", null);
        if(c.getCount()==0) {
            String INSERT_S;
            INSERT_S = "INSERT INTO specie (specie_name, specie_life, specie_image) " +
                    "VALUES('未知類', '0', 'specie_unknown')";
            mSQLiteDatabase.execSQL(INSERT_S);
        }
        c.close();

        //若種類table為空, 新增預設種類"未知類".
        c = mSQLiteDatabase.rawQuery("SELECT * FROM type WHERE 1", null);
        if(c.getCount()==0) {
            String INSERT_T;
            INSERT_T = "INSERT INTO type (type_main, type_tag) " +
                    "VALUES('未知類', '未知')";
            mSQLiteDatabase.execSQL(INSERT_T);
        }
        c.close();


        //測試用資料
        setSpecieData();
        setTypeData();


    }

    @Override
    protected void onResume() {
        super.onResume();

        //判斷當前功能畫面
        if(layout_this==FRIDGE) {
            specieList();   //查詢種類DB放入種類list.
            fridgeList(ALL);

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
                        fridgeList(FREEZING);
                    } else {
                        bt_freezing.setSelected(false);
                        fridgeList(ALL);
                    }
                /*
                if(!frzIsClick) {
                    String FREEZING = "food_position = '冷凍室' ";
                    fridgeList(FREEZING);
                    frzIsClick= true;
                    refIsClick= false;
                    frsIsClick= false;
                    searchIsClick= false;
                } else {
                    fridgeList(ALL);
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
                        fridgeList(REFRIGERATED);
                    } else {
                        bt_refrigerated.setSelected(false);
                        fridgeList(ALL);
                    }
                /*
                if(!refIsClick) {
                    String REFRIGERATED = "food_position = '冷藏室' ";
                    fridgeList(REFRIGERATED);
                    frzIsClick= false;
                    refIsClick= true;
                    frsIsClick= false;
                    searchIsClick= false;
                } else {
                    fridgeList(ALL);
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
                        fridgeList(FRESH);
                    } else {
                        bt_fresh.setSelected(false);
                        fridgeList(ALL);
                    }
                /*
                if(!frsIsClick) {
                    String FRESH = "food_position = '保鮮室' ";
                    fridgeList(FRESH);
                    frzIsClick= false;
                    refIsClick= false;
                    frsIsClick= true;
                    searchIsClick= false;
                } else {
                    fridgeList(ALL);
                    frsIsClick = false;
                }
                */
                }
            });
        } else if(layout_this==RECIPE) {
            recipeList(ALL);
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

        } else {
            //COOK
        }

    }

    // TODO: 2018/8/13 建立bar側欄, ????.

    //其他函式
    //顯示食物gridview
    public void fridgeList(final String WHERE){ //把DB內容放入HashMap, 把HashMap放入ArrayList, 把ArrayList放入SimpleAdapter顯示.
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
                                        fridgeList(WHERE); //刷新lv.
                                        Toast.makeText(MainActivity.this, "刪除成功!!", Toast.LENGTH_SHORT).show();
                                    }
                                    */
                                    fridgeList(WHERE); //刷新lv.
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
                                        fridgeList(WHERE); //刷新lv.
                                        Toast.makeText(MainActivity.this, "修改成功!!", Toast.LENGTH_SHORT).show();
                                    }
                                    */
                                    fridgeList(WHERE); //刷新lv.
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
                            fridgeList(WHERE); //刷新lv.
                            Toast.makeText(MainActivity.this, "新增成功!!", Toast.LENGTH_SHORT).show();
                        }
                        */
                        fridgeList(WHERE); //刷新lv.
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
                                fridgeList(fsearch.getReturn());
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
                    fridgeList(ALL);
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
                                        fridgeList(WHERE);
                                    }
                                    */
                                    specieList();
                                    fridgeList(WHERE);
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
                                        fridgeList(WHERE);
                                    }
                                    */
                                    specieList();
                                    fridgeList(WHERE);
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
                fridgeList(WHERE);
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


    //顯示食譜listview
    public void recipeList(final String WHERE){ //把DB內容放入HashMap, 把HashMap放入ArrayList, 把ArrayList放入SimpleAdapter顯示.
        //取得recipeTable資料
        l_recipe.clear();
        Cursor c1;
        c1= mSQLiteDatabase.rawQuery("SELECT * FROM recipe WHERE "+WHERE, null);
        c1.moveToFirst();

        //檢查是否有資料
        if(c1.getCount()==0) {   //若無資料, 顯示找不到資料及圖片.
            c1.close();

            //禁用bt
            bt_delete.setEnabled(false);
            bt_revise.setEnabled(false);
            bt_select.setEnabled(false);

            iv_rimage.setImageDrawable(getResources().getDrawable(
                    R.drawable.specie_delicatessen));

            tv_rportion.setText("");
            tv_rfood.setText("");
            tv_rtype.setText("");
            tv_rcookstep.setText("");

            iv_nodata.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "沒有資料!!", Toast.LENGTH_SHORT).show();
        } else {    //若有資料, 將每筆資料取出.
            iv_nodata.setVisibility(View.GONE); //有資料則不顯示圖片.

            while(!c1.isAfterLast()){
                //取出種類內容(同等外來鍵效果)
                Cursor c2;  //透過recipe的typeID欄位, 查詢type資料並取出.
                // TODO: 2018/12/13 待修正, typeIDs需被分割才能查詢及儲存成ti, 如何儲存多個ti?
                //String SELECT= "SELECT * FROM type WHERE type_id= " + (c1.getString(1));
                String SELECT= "SELECT * FROM type WHERE 1";    //未知類.
                c2= mSQLiteDatabase.rawQuery(SELECT, null);
                c2.moveToFirst();

                TypeItem ti;
                ti = new TypeItem(c2.getString(0), c2.getString(1),
                        c2.getString(2));
                c2.close();

                RecipeItem ri= new RecipeItem(c1.getString(0),
                        c1.getString(1), c1.getString(2),
                        c1.getString(3), c1.getString(4),
                        c1.getString(5), c1.getString(6),
                        c1.getString(7), c1.getString(8),
                        c1.getString(9), ti
                );

                l_recipe.add(ri.getrHashMap());
                c1.moveToNext();
            }
            c1.close();

            // TODO: 2018/12/13 待完成, 食譜推薦排序.
            //推薦排序
            /*
            if(bt_expired.isSelected()) {     //bt_即期.setOnClick{ isflag= !isflag; } → if(isflag) 排序;
                Collections.sort(l_recipe, new Comparator<HashMap<String, Object>>() {
                    public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                        Integer fLife1 = Integer.valueOf(o1.get("food_life").toString());  //從l_recipe裡面拿出來的第一個.
                        Integer fLife2 = Integer.valueOf(o2.get("food_life").toString());  //從l_recipe裡面拿出來的第二個.
                        return fLife1.compareTo(fLife2);
                    }
                });
            }
            */

            SimpleAdapter adapter= new SimpleAdapter(MainActivity.this,
                    l_recipe, R.layout.listview_recipe,
                    new String[]{"recipe_name", "recipe_phc"},
                    new int[]{R.id.tv_showrname, R.id.tv_showrphc}
            );
            lv_recipe.setAdapter(adapter);
            // TODO: 2018/12/13 lv顯示失敗!!!

            //點擊lv顯示詳細資料
            lv_recipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //所選項目效果, 取出l_recipe資料, 顯示.
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //所選項目美工效果
                    lv_recipe.getAdapter().getView(position, view, parent).setSelected(true);
                    //關鍵為, 取得指定item的view, 設定狀態state_selected; Adapter介面下getView()即為item所屬view.

                    final RecipeItem ri= new RecipeItem(l_recipe.get(position));  //取得點擊的項目, 變成一物件.

                    //啟用bt
                    bt_delete.setEnabled(true);
                    bt_revise.setEnabled(true);
                    bt_select.setEnabled(true);

                    //顯示詳細資料
                    tv_rportion.setText(ri.getrPortion());
                    tv_rfood.setText(ri.showFoods());
                    tv_rtype.setText(ri.getrType());    // TODO: 2018/12/13 待完成, getTypes().getSteps();
                    tv_rcookstep.setText(ri.getrCookstep());

                    // TODO: 2018/12/13 待完成, 刪除.修改食譜.
                    /*
                    //刪除recipe資料
                    bt_delete.setOnClickListener(new View.OnClickListener() {   //刪除bt, 再次確認alert, DB刪除後showList().
                        @Override
                        public void onClick(View v) {
                            final CustomDialog fDelete= new CustomDialog(MainActivity.this);
                            fDelete.buildDelete(ri);
                            fDelete.show();
                            fDelete.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {

                                    fridgeList(WHERE); //刷新lv.
                                }
                            });
                        }
                    });

                    //修改food資料
                    bt_revise.setOnClickListener(new View.OnClickListener() {   //修改bt, DB修改後showList().
                        @Override
                        public void onClick(View v) {
                            final CustomDialog fRevise= new CustomDialog(MainActivity.this);
                            fRevise.buildFInput(ri, l_specie);
                            fRevise.show();
                            //監聽alert是否關閉(關閉後執行code)
                            fRevise.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    fridgeList(WHERE); //刷新lv.
                                }
                            });

                        }
                    });
                    */

                }
            });

            // TODO: 2018/10/13 待修正, 預設點擊沒有setSelected()效果.
            //預設點擊項目
            gv_food.getOnItemClickListener().onItemClick(
                    null,null,0,0);
            // TODO: 2018/12/13 固定選擇第一項lv.
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
        /*
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
                        fridgeList(WHERE); //刷新lv.
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
                                fridgeList(fsearch.getReturn());
                                bt_freezing.setSelected(false);
                                bt_refrigerated.setSelected(false);
                                bt_fresh.setSelected(false);
                                bt_search.setSelected(true);
                                Toast.makeText(MainActivity.this, "查詢成功!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    bt_search.setSelected(false);
                    fridgeList(ALL);
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
                                    specieList();
                                    fridgeList(WHERE);
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
                                    specieList();
                                    fridgeList(WHERE);
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
                fridgeList(WHERE);
                // TODO: 2018/9/6 待優化, 廣播提示將過期食品.
            }
        });
        */
    }

    public void typeList() {
        l_type.clear();

        String SELECT= "SELECT * FROM type WHERE 1 ";
        Cursor c= mSQLiteDatabase.rawQuery(SELECT, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            TypeItem ti= new TypeItem(c.getString(0), c.getString(1), c.getString(2));

            l_type.add(ti.gettHashMap());
            c.moveToNext();
        }
        c.close();
    }

    private void setFridgeView() {
        setContentView(layout_fridge);  //切換至冰箱畫面.
        layout_this= FRIDGE;

        if(!fridgeLoad) {
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
            tv_fname.setMovementMethod(ScrollingMovementMethod.getInstance());
            tv_fquantity= findViewById(R.id.tv_fquantity);
            tv_funit= findViewById(R.id.tv_funit);
            tv_fposition= findViewById(R.id.tv_fposition);
            tv_fstoragetime= findViewById(R.id.tv_fstoragetime);
            tv_expirationdate= findViewById(R.id.tv_expirationdate);
            tv_foodlife= findViewById(R.id.tv_foodlife);

            //fridgeLoad= true;   //已連結過.
        }

    }

    private void setRecipeView() {
        setContentView(layout_recipe);  //切換至食譜畫面.
        layout_this= RECIPE;

        if(!recipeLoad) {
            //連結XML
            bt_next= findViewById(R.id.bt_next);
            bt_suggest= findViewById(R.id.bt_suggest);
            ll_next1= findViewById(R.id.ll_next1);
            ll_next2= findViewById(R.id.ll_next2);

            bt_add= findViewById(R.id.bt_add);
            bt_search= findViewById(R.id.bt_search);
            bt_tsetting= findViewById(R.id.bt_tsetting);

            bt_typemain= findViewById(R.id.bt_typemain);
            bt_typecontent= findViewById(R.id.bt_typecontent);

            iv_nodata= findViewById(R.id.iv_nodata);
            lv_recipe= findViewById(R.id.lv_recipe);
            l_recipe= new ArrayList<>();
            l_type= new ArrayList<>();

            //bt_側欄 bt_expired

            bt_delete= findViewById(R.id.bt_delete);
            bt_revise= findViewById(R.id.bt_revise);
            bt_select= findViewById(R.id.bt_select);

            iv_rimage= findViewById(R.id.iv_rimage);
            tv_rportion= findViewById(R.id.tv_rportion);
            tv_rfood= findViewById(R.id.tv_rfood);
            tv_rfood.setMovementMethod(ScrollingMovementMethod.getInstance());

            tv_rtype= findViewById(R.id.tv_rtype);

            bt_cookstept1= findViewById(R.id.bt_cookstept1);
            bt_cookstept2= findViewById(R.id.bt_cookstept2);
            tv_rcookstep= findViewById(R.id.tv_rcookstep);

            //recipeLoad= true;   //已連結過.
        }
    }

    private void setCookView() {
        /*
        setContentView(layout_cook);  //切換至食譜畫面.
        layout_this= COOK;
        if(!recipeLoad) {
            //連結XML
            recipeLoad= true;   //已連結過.
        }
        */
    }

    private class nextView implements View.OnTouchListener {   //滑動監聽.
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //取得使用者操作類型
            int action= event.getAction();
            //MotionEvent.ACTION_DOWN, 觸碰螢幕.
            //MotionEvent.ACTION_MOVE, 滑動.
            //MotionEvent.ACTION_UP, 離開螢幕.

            //Log.d("null", "("+String.valueOf(x1)+", "+String.valueOf(y1)+")");
            if(action == MotionEvent.ACTION_DOWN) {
                //取得起點
                x1= (int)event.getRawX();
                y1= (int)event.getRawY();
                Log.d("down", "("+String.valueOf(x1)+", "+String.valueOf(y1)+")");
            } else if(action == MotionEvent.ACTION_UP) {
                //取得終點
                x2= (int)event.getRawX();
                y2= (int)event.getRawY();
                Log.d("up", "("+String.valueOf(x2)+", "+String.valueOf(y2)+")");

                //計算滑動距離
                int distance= (int) Math.sqrt(Math.abs((x1 - x2)*(x1 - x2))+Math.abs((y1 - y2)*(y1 - y2)));
                Log.d("distance", String.valueOf(distance));
                //Toast.makeText(MainActivity.this, "距離= " + String.valueOf(distance), Toast.LENGTH_LONG).show();

                //判斷滑動方向
                int direciton= (x2-x1);

                if(distance>400) {  //滑超過一定距離.
                    if(direciton>0) {   //若往右滑.
                        switch(layout_this){    //視當前view決定切換頁面.
                            case FRIDGE:
                                setRecipeView();
                                break;
                            case RECIPE:
                                setCookView();
                                break;
                            case COOK:
                                setFridgeView();
                                break;
                        }
                    } else {    //若往左滑.
                        switch(layout_this){    //視當前view決定切換頁面.
                            case FRIDGE:
                                setCookView();
                                break;
                            case RECIPE:
                                setFridgeView();
                                break;
                            case COOK:
                                setRecipeView();
                                break;
                        }
                    }
                }
            }

            return true;    //表事件不會往後傳遞給容器與Activity元件???
        }
    }

    private void setSpecieData() {
        Cursor c;
        c = mSQLiteDatabase.rawQuery("SELECT * FROM specie WHERE 1", null);
        if(c.getCount()==1) {   //若只有未知類, 新增其他類
            String INSERT_S;

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

    private void setTypeData() {
        Cursor c;
        c = mSQLiteDatabase.rawQuery("SELECT * FROM type WHERE 1", null);
        if(c.getCount()==1) {   //若只有未知類.
            String[][] typearr= {
                    {"基本類", "主食"},
                    {"基本類", "小吃"},
                    {"基本類", "湯品"},
                    {"基本類", "糕餅"},
                    {"基本類", "烘焙"},
                    {"基本類", "飲品"},
                    {"菜式", "中式"},
                    {"菜式", "日式"},
                    {"菜式", "義式"},
                    {"菜式", "美式"},
                    {"烹調方式", "炒"},
                    {"烹調方式", "煎炸"},
                    {"烹調方式", "蒸煮"},
                    {"烹調方式", "烘烤"},
                    {"時段", "早餐"},
                    {"時段", "午晚餐"},
                    {"時段", "下午茶"},
                    {"族群", "素食"},
                    {"族群", "幼兒"}
            };
            //莫名無法用二維陣列輸入資料.

            String[] sql= new String[typearr.length];
            for(int i=0; i<typearr.length; i++) {
                sql[i]= "INSERT INTO type (type_main, type_tag) " +
                        "VALUES('" + typearr[i][0] + "', '" + typearr[i][1] + "')";
            }

            for(String INSERT_T : sql) {
                mSQLiteDatabase.execSQL(INSERT_T);
            }

        }
        c.close();

        //放入測試用data
        //判斷table內是否為空, 若為空則放入資料.
        int num=1;
        c = mSQLiteDatabase.rawQuery("SELECT * FROM recipe WHERE 1", null);
        num=c.getCount();
        c.close();

        if(num==0) {
            c=mSQLiteDatabase.rawQuery("SELECT * FROM type WHERE 1 ", null);
            c.moveToPosition(2);
            String types1= c.getString(0);
            c.moveToPosition(7);
            types1= types1.concat( " " +c.getString(0)); //以空格分開各tag.
            c.moveToPosition(12);
            types1= types1.concat( " " +c.getString(0)); //以空格分開各tag.
            c.moveToPosition(18);
            types1= types1.concat( " " +c.getString(0)); //以空格分開各tag.
            //Log.d("types1", "types1= "+types1);

            c.moveToPosition(1);
            String types2= c.getString(0);
            c.moveToPosition(7);
            types2= types2.concat( " " +c.getString(0)); //以空格分開各tag.
            c.moveToPosition(13);
            types2= types2.concat( " " +c.getString(0)); //以空格分開各tag.
            c.moveToPosition(18);
            types2= types2.concat( " " +c.getString(0)); //以空格分開各tag.
            //Log.d("types2", "types2= "+types2);
            c.close();

            String[][] recipearr= {
                    {"",types1,"香煎素豆包","圖",
                            "6","豆包 4 個 _老薑 3 片 _香菜 0 適量",
                            "豆包沖洗乾淨，小心的將水擠乾。__" +
                                    "鍋子小火燒熱，加入1大匙油，將薑片煸香後取出，豆包兩面煎恰恰。__" +
                                    "佐醬油糕+香油和香菜，趁熱切小塊食用。",
                            "圖","無備註"},
                    {"私房",types2,"金針菇炒絲瓜","圖",
                            "3","絲瓜 1 條 _金針菇 1 包 _老薑 3 片 _",
                            "絲瓜洗淨削皮切片，金針菇洗淨切3段。__" +
                                    "中火熱鍋，加1大匙油，薑片爆香後放入絲瓜和金針菇。__" +
                                    "放鹽適量，蓋上鍋蓋悶煮。__" +
                                    "絲瓜煮透後，加香油即可起鍋。",
                            "圖","先放鹽可以加速絲瓜出水；若額外加水會變成絲瓜湯..."}
            };

            String[] sql= new String[recipearr.length];
            for(int i=0; i<recipearr.length; i++) {
                sql[i]= "INSERT INTO recipe (" +
                        "recipe_phc, recipe_type, recipe_name, " +
                        "recipe_image, recipe_portion, recipe_food," +
                        "recipe_cookstep, recipe_stepimage, recipe_remark) " +
                        "VALUES('" + recipearr[i][0] + "', '" + recipearr[i][1] + "', " +
                        "'" + recipearr[i][2] + "', '" + recipearr[i][3] + "', " +
                        "'" + recipearr[i][4] + "', '" + recipearr[i][5] + "', " +
                        "'" + recipearr[i][6] + "', '" + recipearr[i][7] + "', " +
                        "'" + recipearr[i][8] + "')";
            }

            for(String INSERT_R : sql) {
                mSQLiteDatabase.execSQL(INSERT_R);
            }
        }

    }

}
