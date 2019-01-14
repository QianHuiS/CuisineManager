package tw.idv.qianhuis.cuisinemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class RecipeActivity extends AppCompatActivity {

    //變數
    RecipeDialog recipeDialog= null;    //用於儲存當前使用rd.

    View layout_this;
    int x1=0, y1=0, x2=0, y2=0;

    Button bt_next, bt_suggest;
    LinearLayout ll_next1, ll_next2;

    Button bt_add, bt_search, bt_tsetting;
    Button bt_typemain, bt_typetag;
    final String ALL = "1";

    ImageView iv_nodata;

    Button bt_select, bt_revise, bt_delete;

    ListView lv_recipe;
    ArrayList<HashMap<String, Object>> l_recipe, l_type;

    ImageView iv_rimage;
    TextView tv_rportion, tv_rfood;
    //TextView tv_rtype;
    LinearLayout ll_rtype;

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
        bt_typetag = findViewById(R.id.bt_typetag);

        iv_nodata= findViewById(R.id.iv_nodata);
        lv_recipe= findViewById(R.id.lv_recipe);
        l_recipe= new ArrayList<>();
        l_type= new ArrayList<>();

        //bt_側欄 bt_suggest

        bt_delete= findViewById(R.id.bt_delete);
        bt_revise= findViewById(R.id.bt_revise);
        bt_select= findViewById(R.id.bt_select);

        iv_rimage= findViewById(R.id.iv_rimage);
        tv_rportion= findViewById(R.id.tv_rportion);
        tv_rfood= findViewById(R.id.tv_rfood);
        tv_rfood.setMovementMethod(ScrollingMovementMethod.getInstance());
        //tv_rtype= findViewById(R.id.tv_rtype);
        ll_rtype= findViewById(R.id.ll_rtype);

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

        bt_typemain.setOnClickListener(new View.OnClickListener() {     //大分類查詢
            @Override
            public void onClick(View v) {
                if(!bt_typemain.isSelected()) {
                    final RecipeDialog rd= new RecipeDialog(RecipeActivity.this);
                    rd.buildTSelect();
                    rd.show();
                    rd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(!rd.getReturn().equals("")) {
                                bt_typemain.setSelected(true);
                                bt_typemain.setText(rd.getReturn());
                                bt_typetag.setEnabled(true);
                                recipeList(ALL);    //查詢ALL, if(bt.isSelected()) foreach查詢 if(tag.getMaintype()==bt.getText().toString()) l.add;
                                //Toast.makeText(RecipeActivity.this, "return= "+rd.getReturn(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    bt_typemain.setSelected(false);
                    bt_typemain.setText("");
                    bt_typetag.setSelected(false);
                    bt_typetag.setEnabled(false);
                    bt_typetag.setText("");
                    recipeList(ALL);
                }
            }
        });

        bt_typetag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bt_typetag.isSelected()) {
                    final RecipeDialog rd= new RecipeDialog(RecipeActivity.this);
                    rd.buildTSelect(bt_typemain.getText().toString());  //主類別下的子類別.
                    rd.show();
                    rd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(!rd.getReturn().equals("")) {
                                bt_typetag.setSelected(true);
                                bt_typetag.setText(rd.getReturn());
                                recipeList(ALL);    //查詢ALL, if(bt.isSelected()) foreach查詢 if(tag.getMaintype()==bt.getText().toString()) l.add;
                                //Toast.makeText(RecipeActivity.this, "return= "+rd.getReturn(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    bt_typetag.setSelected(false);
                    bt_typetag.setText("");
                    recipeList(ALL);
                }
            }
        });

    }

    // TODO: 2018/8/13 建立bar側欄, ????.

    //其他函式
    //顯示食譜listview
    public void recipeList(final String WHERE){ //把DB內容放入HashMap, 把HashMap放入ArrayList, 把ArrayList放入SimpleAdapter顯示.
        //取得recipeTable資料
        typeList();
        l_recipe.clear();
        Cursor c1;
        c1= mSQLiteDatabase.rawQuery("SELECT * FROM recipe WHERE "+WHERE, null);
        c1.moveToFirst();

        //檢查是否有資料
        if(c1.getCount()!=0) {    //若有資料, 將每筆資料取出.
            iv_nodata.setVisibility(View.GONE); //有資料則不顯示圖片.

            while (!c1.isAfterLast()) {
                boolean isAdd= true;
                RecipeItem ri = new RecipeItem(c1.getString(0),
                        c1.getString(1), c1.getString(2),
                        c1.getString(3), c1.getString(4),
                        c1.getString(5), c1.getString(6),
                        c1.getString(7), c1.getString(8),
                        c1.getString(9), "0", null
                );

                //推薦recipe資料排序
                if(bt_suggest.isSelected()) {
                    ArrayList<HashMap<String, Object>> expiredFoods= getExpiredFoods();  //取得foodTable>3天內過期&&做即期排序.

                    for(int i = 1; !(ri.showFoods(i).equals("")); i++){   //食譜中有用到越多的, 優先度++321. 顯示即期的食材?
                        String riFood= ri.getFoods(ri.showFoods(i), 1);     //ri所用食材_第i個.
                        for(HashMap<String, Object> ef :expiredFoods) {     //每一個即期fi.
                            FoodItem fi= new FoodItem(ef);
                            if(fi.getfName().contains(riFood)) {  //若即期fi名稱包含ri食材.
                                if(Integer.valueOf(fi.getfLife())<=0)   //即期fi天數<=0 ri優先度+3.
                                    ri.setrExpired(ri.getrExpired()-3);
                                else if(Integer.valueOf(fi.getfLife())==1)  //即期fi天數==1 ri優先度+2.
                                    ri.setrExpired(ri.getrExpired()-2);
                                else if(Integer.valueOf(fi.getfLife())==2)  //即期fi天數==2 ri優先度+1.
                                    ri.setrExpired(ri.getrExpired()-1);
                            }
                        }
                    }

                }

                //分類查詢處理
                if (bt_typemain.isSelected()) {
                    ri.settItems(findtItemsByTag(ri));   //設定ti.

                    //判斷主類別
                    int isMaintype= 0;
                    for (TypeItem ti : ri.gettItems())   //判斷所有tag是否包含主類別.
                        if (ti.gettMain().equals(bt_typemain.getText().toString()))
                            isMaintype++;   //若包含flag就+1.
                    if (isMaintype > 0) {
                        //判斷子類別
                        if(bt_typetag.isSelected()) {
                            int isTagtype= 0;
                            for (TypeItem ti : ri.gettItems())   //判斷所有tag是否包含子類別.
                                if (ti.gettTag().equals(bt_typetag.getText().toString()))
                                    isTagtype++;
                            if (isTagtype > 0)
                                l_recipe.add(ri.getrHashMap());     //若flag為包含(!=0)則加入list顯示.
                        } else  l_recipe.add(ri.getrHashMap());     //若flag為包含(!=0)則加入list顯示.
                    }
                } else l_recipe.add(ri.getrHashMap());     //若非分類查詢則直接加入list.
                c1.moveToNext();
            }
        }
        c1.close();

        //判斷list是否有資料
        if(l_recipe.size()==0)      dataIsNull();
        else {  //若有資料則顯示list.

            //即期排序
            if(bt_suggest.isSelected()) {
                Collections.sort(l_recipe, new Comparator<HashMap<String, Object>>() {
                    public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                        Integer rExpired1 = Integer.valueOf(o1.get("recipe_expired").toString());  //從l_recipe裡面拿出來的第一個.
                        Integer rExpired2 = Integer.valueOf(o2.get("recipe_expired").toString());  //從l_recipe裡面拿出來的第二個.
                        return rExpired1.compareTo(rExpired2);
                    }
                });
            }

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
                    //Log.d("被點擊", "recipeList onItemClick.");
                    //所選項目美工效果
                    //lv_recipe.getAdapter().getView(position, view, parent).setSelected(true);
                    //關鍵為, 取得指定item的view, 設定狀態state_selected; Adapter介面下getView()即為item所屬view.

                    final RecipeItem ri= new RecipeItem(l_recipe.get(position));  //取得點擊的項目, 變成一物件.
                    ri.settItems(findtItemsByTag(ri));   //設定ti
                    for(TypeItem ti:ri.gettItems())
                        Log.d("Types", "ti.gettTag= 「"+ti.gettTag()+"」");

                    //啟用bt
                    bt_delete.setEnabled(true);
                    bt_revise.setEnabled(true);
                    bt_select.setEnabled(true);

                    //顯示詳細資料
                    if((ri.getrImage().equals(""))) {   //若圖像為空, 設置預設圖.
                        iv_rimage.setImageResource(R.drawable.specie_delicatessen);
                    } else {    //否則設置圖像
                        iv_rimage.setImageBitmap(RecipeItem.stringToBitmap(ri.getrImage()));
                    }
                    tv_rportion.setText(ri.getrPortion());
                    tv_rfood.setText(ri.showFoods());
                    ll_rtype.removeAllViews();      //清空linearlayout中的所有view.
                    //for(int i = 1; !(ri.getTpyetag(i).equals("")); i++){
                    for(int i = 0; !(ri.showTag(i).equals("")); i++){   //動態新增btview.
                        final Button bt_rtypetag= new Button(RecipeActivity.this);
                        bt_rtypetag.setText(ri.showTag(i));
                        bt_rtypetag.setTextSize(10);
                        bt_rtypetag.setBackground(getResources().getDrawable(R.drawable.titem_bg));
                        bt_rtypetag.setLayoutParams(new LinearLayout.LayoutParams(
                                200, LinearLayout.LayoutParams.WRAP_CONTENT, 0));    //設定比重weight.
                        bt_rtypetag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(RecipeActivity.this, bt_rtypetag.getText().toString()+".OnClick!", Toast.LENGTH_SHORT).show();
                                // TODO: 2018/12/20 待優化, 點擊typeTag查詢類別.
                            }
                        });

                        ll_rtype.addView(bt_rtypetag);
                    }
                    Log.d("Types", "ri.getrTypes= 「"+ri.getrTypes()+"」");
                    tv_step.setText("1");
                    tv_rcookstep.setText(ri.showSteps(1));
                    bt_steptp.setEnabled(false);
                    bt_steptn.setEnabled(true);

                    final RecipeItem ritmp= ri;
                    //切換步驟
                    bt_steptp.setOnClickListener(new View.OnClickListener() {   //上一步.
                        @Override
                        public void onClick(View v) {
                            int step= Integer.valueOf(tv_step.getText().toString());
                            if(!bt_steptn.isEnabled()) {
                                bt_steptp.setEnabled(true);
                                tv_step.setText(String.valueOf(step));
                                tv_rcookstep.setText(ritmp.showSteps(step));
                                bt_steptn.setEnabled(true);
                            } else {
                                if(step==2) {
                                    bt_steptp.setEnabled(false);
                                    bt_steptn.setEnabled(true);
                                } else {
                                    bt_steptp.setEnabled(true);
                                }
                                step--;
                                tv_step.setText(String.valueOf(step));
                                tv_rcookstep.setText(ritmp.showSteps(step));
                            }
                        }
                    });

                    bt_steptn.setOnClickListener(new View.OnClickListener() {   //上一步.
                        @Override
                        public void onClick(View v) {
                            int step= Integer.valueOf(tv_step.getText().toString());
                            if(ritmp.showSteps(step+1).equals("")) {
                                bt_steptn.setEnabled(false);
                                tv_rcookstep.setText("備註：" +ritmp.getrRemark());
                            } else {    //若下一步的回傳值為空, 表無此步驟.
                                bt_steptn.setEnabled(true);
                                step++;
                                tv_step.setText(String.valueOf(step));
                                tv_rcookstep.setText(ritmp.showSteps(step));
                                bt_steptp.setEnabled(true);
                            }
                        }
                    });

                    final int PICK_FROM_CAMERA= 0;
                    final int PICK_FROM_GALLERY= 1;

                    /*
                    //測試
                    bt_select.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_rfood.setText("");
                            for(int i = 1; !(ri.showFoods(i).equals("")); i++){   //顯示原資料
                                String s_food= ri.showFoods(i);
                                //食材增加
                                tv_rfood.setText(tv_rfood.getText().toString().concat(i+". "+ri.getFoods(s_food, 1)+" "));
                                tv_rfood.setText(tv_rfood.getText().toString().concat(ri.getFoods(s_food, 2))+" ");
                                tv_rfood.setText(tv_rfood.getText().toString().concat(ri.getFoods(s_food, 3))+"\n");
                            }

                        }
                    });
                    */

                    //刪除recipe資料
                    bt_delete.setOnClickListener(new View.OnClickListener() {   //刪除bt, 再次確認alert, DB刪除後showList().
                        @Override
                        public void onClick(View v) {
                            final RecipeDialog rDelete= new RecipeDialog(RecipeActivity.this);
                            rDelete.buildDelete(ri);
                            rDelete.show();
                            rDelete.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    recipeList(WHERE); //刷新lv.
                                }
                            });
                        }
                    });

                    //修改recipe資料
                    bt_revise.setOnClickListener(new View.OnClickListener() {   //修改bt, DB修改後showList().
                        @Override
                        public void onClick(View v) {
                            final RecipeDialog rRevise= new RecipeDialog(RecipeActivity.this);
                            rRevise.buildRInput(ri);
                            rRevise.show();
                            recipeDialog= rRevise;
                            //監聽alert是否關閉(關閉後執行code)
                            rRevise.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    recipeList(WHERE); //刷新lv.
                                }
                            });

                        }
                    });

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

        //新增recipe資料
        bt_add.setOnClickListener(new View.OnClickListener() {  //
            @Override
            public void onClick(View v) {
                final RecipeDialog rAdd= new RecipeDialog(RecipeActivity.this);
                rAdd.buildRInput();
                rAdd.show();
                recipeDialog= rAdd;
                //監聽alert是否關閉(關閉後執行code)
                rAdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        recipeList(WHERE); //刷新lv.
                        recipeDialog= null;
                    }
                });
            }
        });

        //搜尋recipe資料
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bt_search.isSelected()) {

                    final RecipeDialog rSearch= new RecipeDialog(RecipeActivity.this);
                    rSearch.buildSearch();
                    rSearch.show();
                    rSearch.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Log.d("WHERE", "where="+rSearch.getReturn());
                            if(!rSearch.getReturn().equals("")) {
                                recipeList(rSearch.getReturn());
                                bt_typemain.setSelected(false);
                                bt_typemain.setText("");
                                bt_typetag.setSelected(false);
                                bt_typetag.setEnabled(false);
                                bt_typetag.setText("");
                                bt_search.setSelected(true);
                                Toast.makeText(RecipeActivity.this, "查詢成功!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    bt_search.setSelected(false);
                    recipeList(ALL);
                }
            }
        });
/*
        //種類設定
        bt_ssetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FridgeDialog specie= new FridgeDialog(FridgeActivity.this);
                specie.buildSSet(l_specie);
                specie.show();
                specie.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //判斷操作為新增或修改
                        if(specie.getReturn().equals("ADD")) {
                            final FridgeDialog sAdd= new FridgeDialog(FridgeActivity.this);
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

                            final FridgeDialog sRevise= new FridgeDialog(FridgeActivity.this);
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
*/
        //推薦recipe資料排序
        bt_suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_suggest.setSelected(!bt_suggest.isSelected());
                recipeList(WHERE);
            }
        });

    }

    private void typeList() {
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
/*
    private ArrayList<TypeItem> findtItemsById(RecipeItem ri) {     //從所有type中找到對應的type放入list.
        ArrayList<TypeItem> tis= new ArrayList<>();
        int i=1;
        for (HashMap<String, Object> map : l_type) {
            if(map.get("type_id").equals(ri.getTpyetag(i))) {
                tis.add(new TypeItem(map));
                i++;
            }
        }
        return tis;
    }
*/
    private ArrayList<TypeItem> findtItemsByTag(RecipeItem ri) {     //從所有type中找到對應的type放入list.
        ArrayList<TypeItem> tis= new ArrayList<>();

        for(int i=1; !ri.getTpyetag(i).equals(""); i++) //查詢每個ri_tag
            for(HashMap<String, Object> map : l_type) {     //對比所有tag找出ri_tag
                /*
                Log.d("foreach"+i, "map.get(\"type_tag\").equals(ri.getTpyetag(i))= "
                        +map.get("type_tag").equals(ri.getTpyetag(i))
                        +"\nmap.get(\"type_tag\")= "+map.get("type_tag")
                        +"\nri.getTpyetag(i)= "+ri.getTpyetag(i));
                */
                if(map.get("type_tag").equals(ri.getTpyetag(i))) {
                    tis.add(new TypeItem(map));
                }
            }
        return tis;
    }

    private void dataIsNull() {
        //禁用bt
        bt_delete.setEnabled(false);
        bt_revise.setEnabled(false);
        bt_select.setEnabled(false);

        iv_rimage.setImageDrawable(getResources().getDrawable(
                R.drawable.specie_delicatessen));

        tv_rportion.setText("");
        tv_rfood.setText("");
        ll_rtype.removeAllViews();
        tv_rcookstep.setText("");

        iv_nodata.setVisibility(View.VISIBLE);
        Toast.makeText(RecipeActivity.this, "沒有資料!!", Toast.LENGTH_SHORT).show();
    }

    private ArrayList<HashMap<String, Object>> getExpiredFoods() {      //取得foodTable>3天內過期&&做即期排序.
        ArrayList<HashMap<String, Object>> l_food= new ArrayList<>();
        //取得foodTable資料
        l_food.clear();
        Cursor c1;
        c1= mSQLiteDatabase.rawQuery("SELECT * FROM food WHERE "+ ALL, null);
        c1.moveToFirst();

        //檢查是否有資料
        if(c1.getCount()==0) {   //若無資料, 顯示找不到資料及圖片.
            c1.close();
            Toast.makeText(RecipeActivity.this, "冰箱沒有食物!!", Toast.LENGTH_SHORT).show();
        } else {    //若有資料, 將每筆資料取出.
            while (!c1.isAfterLast()) {
                //取出種類內容(同等外來鍵效果)
                Cursor c2;  //透過food的specieID欄位, 查詢specie資料並取出.
                String SELECT = "SELECT * FROM specie WHERE specie_id= " + (c1.getString(1));
                c2 = mSQLiteDatabase.rawQuery(SELECT, null);
                c2.moveToFirst();

                SpecieItem si;
                si = new SpecieItem(c2.getString(0), c2.getString(1),
                        c2.getString(2), c2.getString(3));
                si.setImgId(String.valueOf(
                        getResources().getIdentifier(
                                si.getsImage(), "drawable", getPackageName())
                ));
                c2.close();

                FoodItem fi = new FoodItem(c1.getString(0),
                        c1.getString(1), c1.getString(2),
                        c1.getString(3), c1.getString(4),
                        c1.getString(5), c1.getString(6), si
                );

                //即期日在3天以上的排除
                if(Integer.valueOf(fi.getfLife())<3)
                    l_food.add(fi.getfHashMap());
                c1.moveToNext();
            }
            c1.close();

            //即期排序
            Collections.sort(l_food, new Comparator<HashMap<String, Object>>() {
                public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                    Integer fLife1 = Integer.valueOf(o1.get("food_life").toString());  //從l_food裡面拿出來的第一個.
                    Integer fLife2 = Integer.valueOf(o2.get("food_life").toString());  //從l_food裡面拿出來的第二個.
                    return fLife1.compareTo(fLife2);
                }
            });

        }
        return l_food;
    }

    //調用相簿圖檔相關
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int PICK_FROM_CAMERA= 0;
        final int PICK_FROM_GALLERY= 1;
        final int PICK_FROM_GET= 2;
        if(data!=null) {    //避免沒有開啟相簿直接取消.
            Uri outputFileUri= data.getData();

            if (requestCode == PICK_FROM_CAMERA || requestCode == PICK_FROM_GALLERY) {
                if (resultCode == RESULT_OK) {

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(outputFileUri, "image/*");
                    intent.putExtra("crop", "true");  //crop = true時就打開裁切畫面
                    intent.putExtra("aspectX", 1);    //aspectX與aspectY是設定裁切框的比例
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 150);  //這則是裁切的照片大小
                    intent.putExtra("outputY", 150);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, PICK_FROM_GET);

                }
            }  else if (requestCode == PICK_FROM_GET) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");

                    int oldwidth = photo.getWidth();
                    int oldheight = photo.getHeight();
                    float scaleWidth = 100 / (float) oldwidth;
                    float scaleHeight = 100 / (float) oldheight;
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    // create the new Bitmap object
                    Bitmap resizedBitmap = Bitmap.createBitmap(photo, 0, 0, oldwidth,
                            oldheight, matrix, true);

                    //取得當前使用的rd.ib, 設置圖片及tag, 並重設ib為空.
                    if(recipeDialog.getImageButton()!= null) {
                        recipeDialog.getImageButton().setImageBitmap(resizedBitmap);
                        recipeDialog.getImageButton().setTag(
                                RecipeItem.bitmapToString(resizedBitmap));
                        //Log.d("onActivityResult", "bitmap= 「"+resizedBitmap+"」");
                        //Log.d("onActivityResult", "recipeDialog.getTag= 「"+recipeDialog.getImageButton().getTag()+"」");
                        recipeDialog.setImageButtonNull();
                    }

                }
            }
        }

    }

}