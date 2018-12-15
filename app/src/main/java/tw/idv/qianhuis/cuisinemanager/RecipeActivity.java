package tw.idv.qianhuis.cuisinemanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class RecipeActivity extends AppCompatActivity {

    //變數
    View layout_this;
    int x1=0, y1=0, x2=0, y2=0;

    Button bt_next, bt_expired;
    LinearLayout ll_next1, ll_next2;

    Button bt_add, bt_search, bt_tsetting, bt_suggest;
    Button bt_typemain, bt_typecontent;
    final String ALL = "1";

    ImageView iv_nodata;

    Button bt_select, bt_revise, bt_delete;

    ListView lv_recipe;
    ArrayList<HashMap<String, Object>> l_recipe, l_type;

    ImageView iv_rimage;
    TextView tv_rportion, tv_rfood;
    TextView tv_rtype;

    Button bt_steptp, bt_steptn;
    TextView tv_step, tv_rcookstep;

    //DB
    private SQLiteDatabase mSQLiteDatabase= null;
    private static final String DATABASE_NAME = "app.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_recipe);     //預設設定view.

        //開啟DB
        mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        //滑動換頁
        LayoutInflater inflater = getLayoutInflater();
        layout_this = inflater.inflate(R.layout.activity_recipe, null);
        setContentView(layout_this);
        layout_this.setOnTouchListener(new View.OnTouchListener() {
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
                    //Toast.makeText(FridgeActivity.this, "距離= " + String.valueOf(distance), Toast.LENGTH_LONG).show();

                    //判斷滑動方向
                    int direciton= (x2-x1);

                    if(distance>300) {  //滑超過一定距離.
                        if(direciton>0) {   //若往右滑.
                            Intent intent= new Intent();
                            intent.setClass(RecipeActivity.this, FridgeActivity.class);
                            startActivity(intent);
                        } else {    //若往左滑.
                            Intent intent= new Intent();
                            intent.setClass(RecipeActivity.this, FridgeActivity.class);
                            startActivity(intent);
                        }
                    }
                }

                return true;    //表事件不會往後傳遞給容器與Activity元件???
            }
        });

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

        bt_steptp = findViewById(R.id.bt_steptp);
        bt_steptn = findViewById(R.id.bt_steptn);
        tv_step= findViewById(R.id.tv_step);
        tv_rcookstep= findViewById(R.id.tv_rcookstep);
        tv_rcookstep.setMovementMethod(
                ScrollingMovementMethod.getInstance());

        //simageList();   //種類圖片載入.

    }


    @Override
    protected void onResume() {
        super.onResume();

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

    }

    // TODO: 2018/8/13 建立bar側欄, ????.

    //其他函式
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
            Toast.makeText(RecipeActivity.this, "沒有資料!!", Toast.LENGTH_SHORT).show();
        } else {    //若有資料, 將每筆資料取出.
            iv_nodata.setVisibility(View.GONE); //有資料則不顯示圖片.

            while(!c1.isAfterLast()){
                /*
                //取出種類內容(同等外來鍵效果)
                Cursor c2;  //透過recipe的typeID欄位, 查詢type資料並取出.
                // TODO: 2018/12/13 typeIDs需被分割才能查詢及儲存成ti, 如何儲存多個ti???
                String SELECT= "SELECT * FROM type WHERE type_id= " + (c1.getString(1));
                //String SELECT= "SELECT * FROM type WHERE 1";    //未知類.
                c2= mSQLiteDatabase.rawQuery(SELECT, null);
                c2.moveToFirst();

                TypeItem ti;
                ti = new TypeItem(c2.getString(0), c2.getString(1),
                        c2.getString(2));
                c2.close();
                */
                TypeItem ti= new TypeItem("0000","測試用","測試");

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

            // TODO: 2018/12/13 食譜推薦排序!!!

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

            SimpleAdapter adapter= new SimpleAdapter(RecipeActivity.this,
                    l_recipe, R.layout.listview_recipe,
                    new String[]{"recipe_name", "recipe_phc"},
                    new int[]{R.id.tv_showrname, R.id.tv_showrphc}
            );
            lv_recipe.setAdapter(adapter);

            //點擊lv顯示詳細資料
            lv_recipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //所選項目效果, 取出l_recipe資料, 顯示.
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("被點擊", "recipeList onItemClick.");
                    //所選項目美工效果
                    //lv_recipe.getAdapter().getView(position, view, parent).setSelected(true);
                    //關鍵為, 取得指定item的view, 設定狀態state_selected; Adapter介面下getView()即為item所屬view.

                    final RecipeItem ri= new RecipeItem(l_recipe.get(position));  //取得點擊的項目, 變成一物件.

                    //啟用bt
                    bt_delete.setEnabled(true);
                    bt_revise.setEnabled(true);
                    bt_select.setEnabled(true);

                    //顯示詳細資料
                    tv_rportion.setText(ri.getrPortion());
                    tv_rfood.setText(ri.showFoods());
                    tv_rtype.setText(ri.getrType());    // TODO: 2018/12/13 getTypes().getSteps()!!!!不可使用arr[]!
                    tv_step.setText("1");
                    tv_rcookstep.setText(ri.showSteps(1));

                    //切換步驟
                    bt_steptp.setOnClickListener(new View.OnClickListener() {   //上一步.
                        @Override
                        public void onClick(View v) {
                            int step= Integer.valueOf(tv_step.getText().toString());
                            if(step!=1) {
                                step--;
                                tv_step.setText(String.valueOf(step));
                                tv_rcookstep.setText(ri.showSteps(step));
                            } else  Toast.makeText(RecipeActivity.this, "沒有上一步!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    bt_steptn.setOnClickListener(new View.OnClickListener() {   //上一步.
                        @Override
                        public void onClick(View v) {
                            int step= Integer.valueOf(tv_step.getText().toString());
                            if(!ri.showSteps(step+1).equals("")) {    //若下一步的回傳值為空, 表無此步驟.
                                step++;
                                tv_step.setText(String.valueOf(step));
                                tv_rcookstep.setText(ri.showSteps(step));
                            } else
                                Toast.makeText(RecipeActivity.this, "沒有下一步!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // TODO: 2018/12/13 刪除.修改食譜!!!
                    /*
                    //刪除recipe資料
                    bt_delete.setOnClickListener(new View.OnClickListener() {   //刪除bt, 再次確認alert, DB刪除後showList().
                        @Override
                        public void onClick(View v) {
                            final CustomDialog fDelete= new CustomDialog(FridgeActivity.this);
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
                            final CustomDialog fRevise= new CustomDialog(FridgeActivity.this);
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
            lv_recipe.getOnItemClickListener().onItemClick(
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
                final CustomDialog fAdd= new CustomDialog(FridgeActivity.this);
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

                    final CustomDialog fsearch= new CustomDialog(FridgeActivity.this);
                    fsearch.buildSearch(l_specie);
                    fsearch.show();
                    fsearch.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(fsearch.getReturn().equals("")) {
                                Toast.makeText(FridgeActivity.this, "查詢失敗!?", Toast.LENGTH_SHORT).show();
                            } else {
                                fridgeList(fsearch.getReturn());
                                bt_freezing.setSelected(false);
                                bt_refrigerated.setSelected(false);
                                bt_fresh.setSelected(false);
                                bt_search.setSelected(true);
                                Toast.makeText(FridgeActivity.this, "查詢成功!!", Toast.LENGTH_SHORT).show();
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
                final CustomDialog specie= new CustomDialog(FridgeActivity.this);
                specie.buildSset(l_specie);
                specie.show();
                specie.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //判斷操作為新增或修改
                        if(specie.getReturn().equals("ADD")) {
                            final CustomDialog sAdd= new CustomDialog(FridgeActivity.this);
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

                            final CustomDialog sRevise= new CustomDialog(FridgeActivity.this);
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

}