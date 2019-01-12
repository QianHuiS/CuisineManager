package tw.idv.qianhuis.cuisinemanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/*使用方法:
* FridgeDialog ALERT= new FridgeDialog(CONTEXT.this);
* ALERT.BUILD();
* ALERT.show();
* ALERT.setOnDismissListener(new ...{
*   if(!ALERT.getReturn().equals("")) {  mSQLiteDatabase.execSQL(ALERT.getReturn());   }  }
*/

public class RecipeDialog extends Dialog {
    private Context context;
    private String rcontent= "";

    //DB
    private SQLiteDatabase mSQLiteDatabase= null;
    private static final String DATABASE_NAME = "app.db";
    //mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

    public RecipeDialog(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        //build();
    }

    //Recipe Type Search
    public void buildTSelect() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_type, null);    //layout可換!

        final ArrayList<HashMap<String, Object>> l_type= new ArrayList<>();
        l_type.clear();

        String SELECT= "SELECT DISTINCT type_main FROM type WHERE 1 ";  //distinct 欄位, 表刪除重複資料.
        Cursor c= mSQLiteDatabase.rawQuery(SELECT, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            HashMap<String, Object> hm= new HashMap<>();
            hm.put("type_main", c.getString(0));
            l_type.add(hm);
            c.moveToNext();
        }
        c.close();

        //gridview選擇項目
        //讀DBtypeTable(在main)放入list(傳給alert), 放入adapter顯示gridview.
        GridView gv_typemain= alertView.findViewById(R.id.gv_typemain);
        SimpleAdapter adapter= new SimpleAdapter(context,
                l_type, R.layout.gridview_type,
                new String[]{"type_main"},
                new int[]{R.id.tv_showtname}
        );
        gv_typemain.setAdapter(adapter);

        //gridviewitem選擇事件
        gv_typemain.setOnItemClickListener(new AdapterView.OnItemClickListener() {    //選擇後設定rcontent=s_id, 關閉alert.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> selectItem= l_type.get(position);
                rcontent= (String) selectItem.get("type_main");
                dismiss();
            }
        });

        setContentView(alertView);
        this.setAlertWindow(0.4, 0.8, true, "left", 0f);
    }

    //Recipe Type Search
    public void buildTSelect(final String mainType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_type, null);    //layout可換!

        final ArrayList<HashMap<String, Object>> l_type= new ArrayList<>();
        l_type.clear();

        String SELECT= "SELECT DISTINCT type_tag FROM type WHERE type_main='"+mainType+"' ";  //distinct 欄位, 表刪除重複資料.
        Cursor c= mSQLiteDatabase.rawQuery(SELECT, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            HashMap<String, Object> hm= new HashMap<>();
            hm.put("type_tag", c.getString(0));
            l_type.add(hm);
            c.moveToNext();
        }
        c.close();

        //gridview選擇項目
        //讀DBtypeTable(在main)放入list(傳給alert), 放入adapter顯示gridview.
        GridView gv_typemain= alertView.findViewById(R.id.gv_typemain);
        SimpleAdapter adapter= new SimpleAdapter(context,
                l_type, R.layout.gridview_type,
                new String[]{"type_tag"},
                new int[]{R.id.tv_showtname}
        );
        gv_typemain.setAdapter(adapter);

        //gridviewitem選擇事件
        gv_typemain.setOnItemClickListener(new AdapterView.OnItemClickListener() {    //選擇後設定rcontent=s_id, 關閉alert.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> selectItem= l_type.get(position);
                rcontent= (String) selectItem.get("type_tag");
                dismiss();
            }
        });

        setContentView(alertView);
        this.setAlertWindow(0.4, 0.8, true, "left", 0f);
    }

    //Recipe Type Search
    public void buildTAll() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_type, null);    //layout可換!

        final ArrayList<HashMap<String, Object>> l_type= new ArrayList<>();
        l_type.clear();

        String SELECT= "SELECT DISTINCT type_tag FROM type WHERE 1 ";  //distinct 欄位, 表刪除重複資料.
        Cursor c= mSQLiteDatabase.rawQuery(SELECT, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            HashMap<String, Object> hm= new HashMap<>();
            hm.put("type_tag", c.getString(0));
            l_type.add(hm);
            c.moveToNext();
        }
        c.close();

        //gridview選擇項目
        //讀DBtypeTable(在main)放入list(傳給alert), 放入adapter顯示gridview.
        GridView gv_typemain= alertView.findViewById(R.id.gv_typemain);
        SimpleAdapter adapter= new SimpleAdapter(context,
                l_type, R.layout.gridview_type,
                new String[]{"type_tag"},
                new int[]{R.id.tv_showtname}
        );
        gv_typemain.setAdapter(adapter);

        //gridviewitem選擇事件
        gv_typemain.setOnItemClickListener(new AdapterView.OnItemClickListener() {    //選擇後設定rcontent=s_id, 關閉alert.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> selectItem= l_type.get(position);
                rcontent= (String) selectItem.get("type_tag");
                dismiss();
            }
        });

        setContentView(alertView);
        this.setAlertWindow(0.4, 0.8, true, "left", 0f);
    }

    //Recipe Add
    public void buildRInput() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= inflater.inflate(R.layout.alertdialog_rinput, null);    //layout可換!

        //切換步驟
        TextView tv_part1= alertView.findViewById(R.id.tv_part1);
        tv_part1.setSelected(true);

        Button bt_tp= alertView.findViewById(R.id.bt_tp);
        bt_tp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_part1= alertView.findViewById(R.id.tv_part1);
                TextView tv_part2= alertView.findViewById(R.id.tv_part2);
                TextView tv_part3= alertView.findViewById(R.id.tv_part3);
                LinearLayout ll_part1= alertView.findViewById(R.id.ll_part1);
                LinearLayout ll_part2= alertView.findViewById(R.id.ll_part2);
                LinearLayout ll_part3= alertView.findViewById(R.id.ll_part3);
                Button bt_ok= alertView.findViewById(R.id.bt_ok);

                if(ll_part3.getVisibility()==View.VISIBLE) {
                    tv_part3.setSelected(false);
                    tv_part2.setSelected(true);
                    ll_part3.setVisibility(View.GONE);
                    ll_part2.setVisibility(View.VISIBLE);
                    bt_ok.setEnabled(false);
                } else if(ll_part2.getVisibility()==View.VISIBLE) {
                    tv_part2.setSelected(false);
                    tv_part1.setSelected(true);
                    ll_part2.setVisibility(View.GONE);
                    ll_part1.setVisibility(View.VISIBLE);
                }
            }
        });
        Button bt_tn= alertView.findViewById(R.id.bt_tn);
        bt_tn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_part1= alertView.findViewById(R.id.tv_part1);
                TextView tv_part2= alertView.findViewById(R.id.tv_part2);
                TextView tv_part3= alertView.findViewById(R.id.tv_part3);
                LinearLayout ll_part1= alertView.findViewById(R.id.ll_part1);
                LinearLayout ll_part2= alertView.findViewById(R.id.ll_part2);
                LinearLayout ll_part3= alertView.findViewById(R.id.ll_part3);
                Button bt_ok= alertView.findViewById(R.id.bt_ok);

                if(ll_part1.getVisibility()==View.VISIBLE) {
                    tv_part1.setSelected(false);
                    tv_part2.setSelected(true);
                    ll_part1.setVisibility(View.GONE);
                    ll_part2.setVisibility(View.VISIBLE);
                } else if(ll_part2.getVisibility()==View.VISIBLE) {
                    tv_part2.setSelected(false);
                    tv_part3.setSelected(true);
                    ll_part2.setVisibility(View.GONE);
                    ll_part3.setVisibility(View.VISIBLE);
                    bt_ok.setEnabled(true);
                }
            }
        });

        //分類輸入
        final LinearLayout ll_rtype= alertView.findViewById(R.id.ll_rtype);
        ll_rtype.setTag("");
        final Button bt_rtypeall= alertView.findViewById(R.id.bt_rtypeall);
        bt_rtypeall.setOnClickListener(new View.OnClickListener() {     //選擇分類
            @Override
            public void onClick(View v) {
                final RecipeDialog rd= new RecipeDialog(context);
                rd.buildTAll();
                rd.show();
                rd.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(!rd.getReturn().equals("")) {
                            //分類增加
                            final Button bt_rtype= new Button(context);
                            bt_rtype.setText(rd.getReturn());
                            bt_rtype.setTextSize(10);
                            bt_rtype.setBackground(ContextCompat.getDrawable(context, R.drawable.titem_bg));
                            bt_rtype.setLayoutParams(new LinearLayout.LayoutParams(
                                    200, LinearLayout.LayoutParams.WRAP_CONTENT, 0));    //設定比重weight.
                            bt_rtype.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ll_rtype.removeView(bt_rtype);
                                    ll_rtype.setTag(ll_rtype.getTag().toString().replace(
                                            " "+bt_rtype.getText().toString(),""));    //將bt文字消除(替換為空字串).
                                }
                            });

                            ll_rtype.addView(bt_rtype);
                            ll_rtype.setTag(ll_rtype.getTag().toString().concat(" "+bt_rtype.getText().toString()));    //取得bt文字, 接到tag中.
                        }
                    }
                });
            }
        });

        //圖片輸入
        final ImageButton ib_rimg= alertView.findViewById(R.id.ib_rimg);
        ib_rimg.setTag("");
        ib_rimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2019/1/11 選擇圖片.
                ib_rimg.setTag("click");
            }
        });

        //食材輸入
        final LinearLayout ll_rfoods= alertView.findViewById(R.id.ll_rfoods);
        ll_rfoods.setTag("");

        if (ll_rfoods.getChildCount() == 0) {    //如果一個都沒有, 新增一個.
            final View v_rfoodadd = View.inflate(context, R.layout.view_rfoodadd, null);
            v_rfoodadd.setTag("");
            Button bt_remove = v_rfoodadd.findViewById(R.id.bt_remove);
            bt_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_rfoods.removeView(v_rfoodadd);
                }
            });
            ll_rfoods.addView(v_rfoodadd);
        }

        Button bt_foodadd= alertView.findViewById(R.id.bt_foodadd);
        bt_foodadd.setOnClickListener(new View.OnClickListener() {     //選擇分類
            @Override
            public void onClick(View v) {
                //食材增加
                final View v_rfoodadd = View.inflate(context, R.layout.view_rfoodadd, null);
                v_rfoodadd.setTag("");
                Button bt_remove = v_rfoodadd.findViewById(R.id.bt_remove);
                bt_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ll_rfoods.removeView(v_rfoodadd);
                    }
                });
                ll_rfoods.addView(v_rfoodadd);
            }
        });

        //步驟輸入
        final LinearLayout ll_rcookstep= alertView.findViewById(R.id.ll_rcookstep);
        ll_rcookstep.setTag("");

        if (ll_rcookstep.getChildCount() == 0) {    //如果一個都沒有, 新增一個.
            final View v_rstepadd = View.inflate(context, R.layout.view_rstepadd, null);
            v_rstepadd.setTag("");
            Button bt_remove = v_rstepadd.findViewById(R.id.bt_remove);
            bt_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_rcookstep.removeView(v_rstepadd);
                }
            });
            ll_rcookstep.addView(v_rstepadd);
        }

        Button bt_stepadd= alertView.findViewById(R.id.bt_stepadd);
        bt_stepadd.setOnClickListener(new View.OnClickListener() {     //選擇分類
            @Override
            public void onClick(View v) {
                //步驟增加
                final View v_rstepadd = View.inflate(context, R.layout.view_rstepadd, null);
                v_rstepadd.setTag("");

                //TextView tv_step= v_rstepadd.findViewById(R.id.tv_step);
                //tv_step.setText("作法 " +(ll_rcookstep.getChildCount()+1) +" ：");
                // TODO: 2019/1/12 待優化, 步驟含編號; 若移除,如何修改後續步驟編號.

                Button bt_remove = v_rstepadd.findViewById(R.id.bt_remove);
                bt_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ll_rcookstep.removeView(v_rstepadd);
                    }
                });
                ll_rcookstep.addView(v_rstepadd);
            }
        });


        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                //連接layoutXML
                EditText et_rname= alertView.findViewById(R.id.et_rname);
                EditText et_rportion= alertView.findViewById(R.id.et_rportion);
                EditText et_rremark= alertView.findViewById(R.id.et_rremark);

                boolean isNull= false;
                //取得食材內容
                for (int i = 0; i < ll_rfoods.getChildCount() && isNull==false; i++) {
                    View v_rfoodadd = ll_rfoods.getChildAt(i);
                    EditText et_rfname = v_rfoodadd.findViewById(R.id.et_rfname);
                    EditText et_rfquantity = v_rfoodadd.findViewById(R.id.et_rfquantity);
                    EditText et_rfunit = v_rfoodadd.findViewById(R.id.et_rfunit);
                    if(et_rfname.getText().toString().equals("")&&
                            et_rfquantity.getText().toString().equals("")&&
                            et_rfunit.getText().toString().equals("")) {    //若食材為空, 停止迴圈並清空ll_rfoodstag.
                        isNull= true;
                        ll_rfoods.setTag("");
                    }
                    else {    //若食材皆不為空.
                        //將食材內容並為字串
                        v_rfoodadd.setTag(et_rfname.getText().toString()+" " +
                                et_rfquantity.getText().toString()+" " +
                                et_rfunit.getText().toString()+" ");     //以空白間隔名稱數量單位.
                        if(i==0)
                            ll_rfoods.setTag(ll_rfoods.getTag().toString().concat(
                                    v_rfoodadd.getTag().toString()));   //取得vtag, 接到lltag中. 第一個食材不加底線.
                        else
                            ll_rfoods.setTag(ll_rfoods.getTag().toString().concat(
                                    "_"+v_rfoodadd.getTag().toString()));   //取得vtag, 接到lltag中. 以底線間隔食材.
                    }
                }

                //取得步驟內容
                for (int i = 0; i < ll_rcookstep.getChildCount() && isNull==false; i++) {
                    View v_rstepadd = ll_rcookstep.getChildAt(i);
                    ImageButton ib_rsimg = v_rstepadd.findViewById(R.id.ib_rsimg);
                    EditText et_rstep = v_rstepadd.findViewById(R.id.et_rstep);
                    if(et_rstep.getText().toString().equals("")) {      //若作法為空, 停止迴圈並清空ll_rcooksteptag.
                        isNull= true;
                        ll_rcookstep.setTag("");
                    }
                    else {    //若作法不為空.
                        // TODO: 2019/1/12 stepimage選取, 並判斷null處理方式.
                        //將步驟內容並為字串
                        v_rstepadd.setTag(et_rstep.getText().toString());
                        if(i==0)
                            ll_rcookstep.setTag(ll_rcookstep.getTag().toString().concat(
                                    v_rstepadd.getTag().toString()));   //取得vtag, 接到lltag中. 第一個步驟不加底線.
                        else
                            ll_rcookstep.setTag(ll_rcookstep.getTag().toString().concat(
                                    "__"+v_rstepadd.getTag().toString()));   //取得vtag, 接到lltag中. 以雙底線間隔步驟.
                    }
                }

                //取得填寫的內容
                String[] rcontent=new String[8];
                rcontent[0]= ll_rtype.getTag().toString().trim();
                rcontent[1]= et_rname.getText().toString();
                rcontent[2]= ib_rimg.getTag().toString();
                rcontent[3]= et_rportion.getText().toString();
                rcontent[4]= ll_rfoods.getTag().toString().trim();
                rcontent[5]= ll_rcookstep.getTag().toString().trim();
                //rcontent[6]= ib_rsimg.getText().toString();
                rcontent[6]= "圖";
                rcontent[7]= et_rremark.getText().toString();

                //檢查是否有欄位為空
                //boolean isNull= false;
                for(int i=0; i<rcontent.length; i++){
                    if(rcontent[i].equals(""))
                        isNull= true;
                }   //若使用for each, 暫存each的對象為空時, 會造成錯誤.

                if(isNull) {  //若有欄位未填寫.
                    Toast.makeText(context, "有欄位空著!!", Toast.LENGTH_SHORT).show();
                }else {
                    String INSERT_RECIPE_TABLE = "INSERT INTO recipe (" +
                            "recipe_phc, recipe_types, recipe_name, " +
                            "recipe_image, recipe_portion, recipe_foods," +
                            "recipe_cookstep, recipe_stepimage, recipe_remark) " +
                            "VALUES('私房', '" + rcontent[0] + "', " + "'" + rcontent[1] + "', " +
                            "'" + rcontent[2] + "', '" + rcontent[3] + "', '" + rcontent[4] + "', " +
                            "'" + rcontent[5] + "', '" + rcontent[6] + "', '" + rcontent[7] + "')";
                    mSQLiteDatabase.execSQL(INSERT_RECIPE_TABLE);
                    Toast.makeText(context, "新增成功!!", Toast.LENGTH_SHORT).show();

                    dismiss();
                }
            }
        });

        alertView.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        setContentView(alertView);
        setAlertWindow(1, 1, false);
    }

    //Recipe Revise
    public void buildRInput(final FoodItem fi, final ArrayList<HashMap<String, Object>> l_specie) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_finput, null);    //layout可換!

        //連接layoutXML
        final ImageButton ib_specie= alertView.findViewById(R.id.ib_specie);
        TextView tv_showsname= alertView.findViewById(R.id.tv_showsname);
        TextView tv_showslife= alertView.findViewById(R.id.tv_showslife);
        EditText et_fname= alertView.findViewById(R.id.et_fname);
        EditText et_fquantity= alertView.findViewById(R.id.et_fquantity);
        EditText et_funit= alertView.findViewById(R.id.et_funit);
        //EditText et_fposition= alertView.findViewById(R.id.et_fposition);
        final RadioGroup rg_fposition= alertView.findViewById(R.id.rg_fposition);
        final Button bt_fstoragetime= alertView.findViewById(R.id.bt_fstoragetime);

        //傳入原資料
        tv_showsname.setText(fi.getsItem().getsName());    //取得點選的種類ID, 取出詳細資料顯示在button.
        tv_showslife.setText(fi.getsItem().getsLife());
        ib_specie.setTag(fi.getfSpecie());
        ib_specie.setImageResource(Integer.valueOf(fi.getsItem().getImgId()));
        et_fname.setText(fi.getfName());
        et_fquantity.setText(fi.getfQuantity());
        et_funit.setText(fi.getfUnit());
        //et_fposition.setText(fi.getfPosition());
        rg_fposition.setTag(fi.getfPosition());
        switch(rg_fposition.getTag().toString()) {  //check rb.
            case "冷凍室":
                rg_fposition.check(R.id.rb_freezing);
                break;
            case "冷藏室":
                rg_fposition.check(R.id.rb_refrigerated);
                break;
            case "保鮮室":
                rg_fposition.check(R.id.rb_fresh);
                break;
        }
        bt_fstoragetime.setText(fi.getfStoragetime());

        //設定種類
        ib_specie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RecipeDialog fSpecie= new RecipeDialog(context);
                fSpecie.buildSselect(l_specie);
                fSpecie.show();
                fSpecie.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(!fSpecie.getReturn().equals("")) {
                            TextView tv_showsname= alertView.findViewById(R.id.tv_showsname);
                            TextView tv_showslife= alertView.findViewById(R.id.tv_showslife);

                            //取得點選的種類ID, 取出詳細資料顯示在button.
                            SpecieItem si= new SpecieItem(l_specie.get(
                                    Integer.valueOf(fSpecie.getReturn())
                            ));

                            tv_showsname.setText(si.getsName());
                            tv_showslife.setText(si.getsLife());
                            ib_specie.setTag(si.getsId());  //用Tag紀錄ID.
                            ib_specie.setImageResource(Integer.valueOf(si.getImgId()));
                        }
                    }
                });
            }
        });

        //設定位置
        rg_fposition.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.rb_freezing:
                        rg_fposition.setTag("冷凍室");
                        break;
                    case R.id.rb_refrigerated:
                        rg_fposition.setTag("冷藏室");
                        break;
                    case R.id.rb_fresh:
                        rg_fposition.setTag("保鮮室");
                        break;
                    default:
                }
            }
        });

        //設定日期
        bt_fstoragetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar= Calendar.getInstance(); //取得一個日曆實體.

                DatePickerDialog datePickerDialog= new DatePickerDialog(
                        context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {  //month要+1.
                        String sdate= DateFunction.stringFormat(year, month+1, day);
                        bt_fstoragetime.setText(sdate);
                        Toast.makeText(context, sdate, Toast.LENGTH_SHORT).show();
                    }
                },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();

            }
        });

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                //連接layoutXML
                //種類!!
                EditText et_fname= alertView.findViewById(R.id.et_fname);   //為避免EditText final無法編輯, 重新宣告連接xml.
                EditText et_fquantity= alertView.findViewById(R.id.et_fquantity);
                EditText et_funit= alertView.findViewById(R.id.et_funit);
                //EditText et_fposition= alertView.findViewById(R.id.et_fposition);

                //取得填寫的內容
                String[] rcontent=new String[7];
                rcontent[0]= fi.getfId();   //拿出物件中的某資訊.
                rcontent[1]= ib_specie.getTag().toString(); //取得ID
                rcontent[2]= et_fname.getText().toString();
                rcontent[3]= et_fquantity.getText().toString();
                rcontent[4]= et_funit.getText().toString();
                rcontent[5]= rg_fposition.getTag().toString();
                rcontent[6]= bt_fstoragetime.getText().toString();

                //檢查是否有欄位為空
                boolean isNull= false;
                for(int i=1; i<rcontent.length; i++){
                    if(rcontent[i].equals(""))
                        isNull= true;
                }

                if(isNull) {  //若有欄位未填寫.
                    Toast.makeText(context, "有欄位空著!!", Toast.LENGTH_SHORT).show();
                }else {
                    String UPDATE_FOOD_TABLE= "UPDATE food SET " +
                            "food_specie="+ rcontent[1] +", food_name='"+ rcontent[2] +"', " +
                            "food_quantity="+ rcontent[3] +", food_unit='"+ rcontent[4] +"', " +
                            "food_position='"+ rcontent[5] +"', food_storagetime='"+ rcontent[6] +"' " +
                            "where food_id=" + rcontent[0];
                    mSQLiteDatabase.execSQL(UPDATE_FOOD_TABLE);
                    Toast.makeText(context, "修改成功!!", Toast.LENGTH_SHORT).show();

                    //rcontent= UPDATE_FOOD_TABLE;     //相當於return String.
                    dismiss();
                }
            }
        });

        alertView.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        setContentView(alertView);
        setAlertWindow(0.9, 0.9, false);
    }

    //Type Select
    public void buildSselect(final ArrayList<HashMap<String, Object>> l_specie) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= inflater.inflate(R.layout.alertdialog_specie, null);    //layout可換!

        //gridview選擇種類項目
        //讀DBspecieTable(在main)放入list(傳給alert), 放入adapter顯示gridview.
        GridView gv_specie= alertView.findViewById(R.id.gv_specie);
        SimpleAdapter adapter= new SimpleAdapter(context,
                l_specie, R.layout.gridview_specie,
                new String[]{"specie_imgid", "specie_name", "specie_life"},
                new int[]{R.id.iv_simage, R.id.tv_showsname, R.id.tv_showslife}
                );
        gv_specie.setAdapter(adapter);

        //gridviewitem選擇事件
        gv_specie.setOnItemClickListener(new AdapterView.OnItemClickListener() {    //選擇後設定rcontent=s_id, 關閉alert.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rcontent= String.valueOf(position); //回傳此item的position.
                dismiss();
            }
        });

        setContentView(alertView);
        this.setAlertWindow(0.7, 0.9, true, "right", 0.6f);
    }

    //Recipe Delete
    public void buildDelete(final RecipeItem ri) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_delete, null);    //layout可換!

        TextView tv_content= alertView.findViewById(R.id.tv_content);
        tv_content.setText("真的要刪除食譜 "+ri.getrName()+" ？");

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                // TODO: 2018/12/27  待修正, 刪除只本食譜使用的TAG?? 或是typesetting提供一鍵刪除沒有用到的tag??
                String DELETE_RECIPE_TABLE= "DELETE FROM recipe WHERE recipe_id=" +ri.getrId();
                mSQLiteDatabase.execSQL(DELETE_RECIPE_TABLE);
                Toast.makeText(context, "刪除成功!!", Toast.LENGTH_SHORT).show();

                //rcontent= DELETE_FOOD_TABLE;
                dismiss();
            }
        });

        alertView.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        setContentView(alertView);
        setAlertWindow(0.8, 0.7, true);
    }

    // TODO: 2018/9/6 待優化, 更多搜尋範圍; between兩日期.多種類.多名稱...?
    //Recipe Search
    public void buildSearch() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_rsearch, null);    //layout可換!

        final LinearLayout ll_rfood= alertView.findViewById(R.id.ll_rfood);
        ll_rfood.setTag("");

        //食材增加按鈕
        final Button bt_rfoodadd= alertView.findViewById(R.id.bt_rfoodadd);
        bt_rfoodadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_rfood= alertView.findViewById(R.id.et_rfood);
                if(!et_rfood.getText().toString().equals("")) {

                    final Button bt_rfood= new Button(context);
                    bt_rfood.setText(et_rfood.getText().toString());
                    bt_rfood.setTextSize(10);
                    bt_rfood.setBackground(ContextCompat.getDrawable(context, R.drawable.titem_bg));
                    bt_rfood.setLayoutParams(new LinearLayout.LayoutParams(
                            200, LinearLayout.LayoutParams.WRAP_CONTENT, 0));    //設定比重weight.
                    bt_rfood.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //LinearLayout ll_rfood= alertView.findViewById(R.id.ll_rfood);
                            ll_rfood.removeView(bt_rfood);
                            ll_rfood.setTag(ll_rfood.getTag().toString().replace(
                                    " "+bt_rfood.getText().toString(),""));    //將bt文字消除(替換為空字串).
                        }
                    });

                    ll_rfood.addView(bt_rfood);
                    ll_rfood.setTag(ll_rfood.getTag().toString().concat(" "+bt_rfood.getText().toString()));    //取得bt文字, 接到tag中.
                    et_rfood.setText("");
                }
            }
        });

        //分類按鈕
        final Button bt_rtypeall= alertView.findViewById(R.id.bt_rtypeall);
        bt_rtypeall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RecipeDialog rd= new RecipeDialog(context);
                rd.buildTAll();
                rd.show();
                rd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(!rd.getReturn().equals("")) {
                            EditText et_rtype= alertView.findViewById(R.id.et_rtype);
                            et_rtype.setText(rd.getReturn());
                        }
                    }
                });
            }
        });

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                //連接layoutXML
                EditText et_rname= alertView.findViewById(R.id.et_rname);
                EditText et_rtype= alertView.findViewById(R.id.et_rtype);

                String rtype="";
                if(!et_rtype.getText().toString().equals("")) {
                    Cursor c;
                    c= mSQLiteDatabase.rawQuery("SELECT * FROM type WHERE type_tag= '"+et_rtype.getText().toString()+"' ", null);
                    c.moveToFirst();
                    if(c.getCount()!=0)     rtype= " "+c.getString(0)+" ";
                    else    Toast.makeText(context, "「"+et_rtype.getText().toString()+"」不存在!",Toast.LENGTH_SHORT).show();
                    c.close();
                }

                String[] rcontent=new String[3];
                rcontent[0]= et_rname.getText().toString();
                rcontent[1]= ll_rfood.getTag().toString().trim();
                rcontent[2]= rtype;

                //取得填寫的內容
                String[] column=new String[4];
                column[0]= "recipe_name LIKE '%"+ rcontent[0] +"%'";
                // TODO: 2018/12/27 BUG!! 若查詢都沒有的食材, 就會跑出ALL的結果(因為所有食譜都有"_"). 每個項目select群組再合併結果?
                column[1]= "recipe_foods LIKE '%_%' OR ";    // TODO: 2018/12/27 疑問, WHERE A OR B ...時, 是"必須有A" 或者有B...
                String tmp= "";
                while(rcontent[1].contains(" ")) {    //字串是否包含" ".
                    tmp= tmp.concat(rcontent[1].substring(0, rcontent[1].indexOf(" "))).trim();     //取得開始到第一個" "前的子字串, 去除首尾空白.
                    column[1]= column[1].concat("recipe_foods LIKE '%"+ tmp +"%' OR ");
                    rcontent[1]= rcontent[1].substring(rcontent[1].indexOf(" ")+1);   //tmp= 第一個" "後, 到結尾的字串.
                    //Log.d("i++", "i= "+i+"   tmp= "+tmp)
                }
                //剩最後一項(tmp沒有" ")
                tmp= tmp.concat(rcontent[1]).trim();
                column[1]= column[1].concat("recipe_foods LIKE '%"+ tmp +"%'");

                column[2]= "recipe_types LIKE '%"+ rcontent[2] +"%'";

                boolean first= true;
                String WHERE= "";
                for(int i=0; i<rcontent.length; i++) {
                    if (rcontent[i].equals("")) continue;
                    else {
                        if (first)  first= false;
                        else if(i==1)   WHERE += " OR ";
                        else    WHERE += " AND ";

                        WHERE += column[i];
                    }
                }

                RecipeDialog.this.rcontent = WHERE;     //相當於return String.
                dismiss();
            }
        });

        setContentView(alertView);
        this.setAlertWindow(0.5, 0.8, true, "left", 0f);
    }


    //Type Set
    public void buildTset(final ArrayList<HashMap<String, Object>> l_specie) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= inflater.inflate(R.layout.alertdialog_specie, null);    //layout可換!

        //新增種類
        Button bt_add= alertView.findViewById(R.id.bt_add);
        bt_add.setVisibility(View.VISIBLE);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcontent="ADD";
                dismiss();
            }
        });

        //gridview選擇種類項目
        //讀DBspecieTable(在main)放入list(傳給alert), 放入adapter顯示gridview.
        GridView gv_specie= alertView.findViewById(R.id.gv_specie);
        SimpleAdapter adapter= new SimpleAdapter(context,
                l_specie, R.layout.gridview_specie,
                new String[]{"specie_imgid", "specie_name", "specie_life"},
                new int[]{R.id.iv_simage, R.id.tv_showsname, R.id.tv_showslife}
        );
        gv_specie.setAdapter(adapter);

        //gridviewitem選擇事件
        gv_specie.setOnItemClickListener(new AdapterView.OnItemClickListener() {    //選擇後設定rcontent=s_id, 關閉alert.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rcontent= String.valueOf(position);
                dismiss();
            }
        });

        setContentView(alertView);
        this.setAlertWindow(0.7, 0.9, true);
    }

    //Type Add
    public void buildTInput(final ArrayList<HashMap<String, Object>> l_simage) {     //(修改傳入si)填寫完回傳sqlcode.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= inflater.inflate(R.layout.alertdialog_sinput, null);    //layout可換!

        //gridview選擇圖片
        //讀DBsimageTable(在main)放入list(傳給alert), 放入adapter顯示gridview.
        final GridView gv_simage= alertView.findViewById(R.id.gv_simage);
        gv_simage.setTag("");
        SimpleAdapter adapter= new SimpleAdapter(context,
                l_simage, R.layout.gridview_simage,
                new String[]{"simage_id"},
                new int[]{R.id.iv_simage}
        );
        gv_simage.setAdapter(adapter);

        //gridviewitem選擇事件
        gv_simage.setOnItemClickListener(new AdapterView.OnItemClickListener() {    //選擇後設定rcontent=s_id, 關閉alert.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gv_simage.getAdapter().getView(position, view, parent).setSelected(true);
                HashMap<String,Object> selectItem= new HashMap<>();
                gv_simage.setTag(l_simage.get(position).get("simage_name"));
            }
        });

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                //連接layoutXML
                EditText et_sname= alertView.findViewById(R.id.et_sname);
                EditText et_slife= alertView.findViewById(R.id.et_slife);

                //取得填寫的內容
                String[] scontent=new String[3];
                scontent[0]= et_sname.getText().toString();
                scontent[1]= et_slife.getText().toString();
                scontent[2]= gv_simage.getTag().toString();

                //檢查是否有欄位為空
                boolean isNull= false;
                for(int i=0; i<scontent.length; i++){
                    if(scontent[i].equals(""))
                        isNull= true;
                }   //若使用for each, 暫存each的對象為空時, 會造成錯誤.

                if(isNull) {  //若有欄位未填寫.
                    Toast.makeText(context, "有欄位空著!!", Toast.LENGTH_SHORT).show();
                }else {
                    String INSERT_SPECIE_TABLE = "INSERT INTO specie (specie_name, specie_life, specie_image) " +
                            "VALUES('"+ scontent[0] +"', '"+ scontent[1] +"', '"+ scontent[2] +"')";
                    mSQLiteDatabase.execSQL(INSERT_SPECIE_TABLE);
                    Toast.makeText(context, "新增成功!!", Toast.LENGTH_SHORT).show();

                    //rcontent= INSERT_SPECIE_TABLE;     //相當於return String.
                    dismiss();
                }
            }
        });

        setContentView(alertView);
        setAlertWindow(0.8, 0.8, true);
    }

    //Type Revise
    public void buildTInput(final SpecieItem si, final ArrayList<HashMap<String, Object>> l_simage) {     //修改傳入si,填寫完回傳sqlcode.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= inflater.inflate(R.layout.alertdialog_sinput, null);    //layout可換!

        EditText et_sname= alertView.findViewById(R.id.et_sname);
        EditText et_slife= alertView.findViewById(R.id.et_slife);

        et_sname.setText(si.getsName());
        et_slife.setText(si.getsLife());

        //gridview選擇圖片
        //讀DBsimageTable(在main)放入list(傳給alert), 放入adapter顯示gridview.
        final GridView gv_simage= alertView.findViewById(R.id.gv_simage);
        gv_simage.setTag(si.getsImage());
        Log.d("tag", "si.getsImage()= "+si.getsImage());
        SimpleAdapter adapter= new SimpleAdapter(context,
                l_simage, R.layout.gridview_simage,
                new String[]{"simage_id"},
                new int[]{R.id.iv_simage}
        );
        gv_simage.setAdapter(adapter);

        //gridviewitem選擇事件
        gv_simage.setOnItemClickListener(new AdapterView.OnItemClickListener() {    //選擇後設定rcontent=s_id, 關閉alert.
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gv_simage.getAdapter().getView(position, view, parent).setSelected(true);
                HashMap<String,Object> selectItem= new HashMap<>();
                gv_simage.setTag(l_simage.get(position).get("simage_name"));
            }
        });

        // 修改TODO: 2018/10/15 待修正, 預設點擊項目, 如何選擇指定圖片(如何得知指定圖片在gv的index)?
        //gv_simage.getOnItemClickListener().onItemClick(null, null, l_simage.indexOf(si.getsImage()), 0);

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                //連接layoutXML
                EditText et_sname= alertView.findViewById(R.id.et_sname);
                EditText et_slife= alertView.findViewById(R.id.et_slife);
                //圖片!!!

                //取得填寫的內容
                String[] scontent=new String[4];
                scontent[0]= si.getsId();
                scontent[1]= et_sname.getText().toString();
                scontent[2]= et_slife.getText().toString();
                scontent[3]= gv_simage.getTag().toString();

                //檢查是否有欄位為空
                boolean isNull= false;
                for(int i=0; i<scontent.length; i++){
                    if(scontent[i].equals(""))
                        isNull= true;
                }   //若使用for each, 暫存each的對象為空時, 會造成錯誤.

                if(si.getsName().equals("未知類")){
                    Toast.makeText(context, si.getsName() +" 為預設種類，無法刪除及修改。", Toast.LENGTH_SHORT).show();
                } else if(isNull) {  //若有欄位未填寫.
                    Toast.makeText(context, "有欄位空著!!", Toast.LENGTH_SHORT).show();
                } else {
                    String UPDATE_SPECIE_TABLE= "UPDATE specie SET " +
                            "specie_name='"+ scontent[1] +"', specie_life='"+ scontent[2] +"' , specie_image='"+ scontent[3] +"' " +
                            "where specie_id=" + scontent[0];
                    mSQLiteDatabase.execSQL(UPDATE_SPECIE_TABLE);
                    Toast.makeText(context, "修改成功!!", Toast.LENGTH_SHORT).show();
                    //rcontent= UPDATE_SPECIE_TABLE;     //相當於return String.
                    dismiss();
                }
            }
        });

        Button bt_delete= alertView.findViewById(R.id.bt_delete);
        bt_delete.setVisibility(View.VISIBLE);
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RecipeDialog sDelete= new RecipeDialog(context);
                sDelete.buildDelete(si);
                sDelete.show();
                sDelete.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (sDelete.getReturn().equals("")) {   //若取消則無動作, 成功就關閉alert.
                        } else {
                            dismiss();
                        }
                    }
                });
            }
        });

        setContentView(alertView);
        setAlertWindow(0.8, 0.8, true);
    }

    //Type Delete
    public void buildDelete(final SpecieItem si) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_delete, null);    //layout可換!

        TextView tv_content = alertView.findViewById(R.id.tv_content);
        //判斷是否為預設種類(未知類)
        if(si.getsName().equals("未知類")) {
            Button bt_cancel= alertView.findViewById(R.id.bt_cancel);
            bt_cancel.setVisibility(View.GONE); //沒有取消按鈕.

            tv_content.setText(si.getsName() + " 為預設種類，無法刪除及修改。");

            alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } else {
            String content = "真的要刪除 " + si.getsName() + " ？\n原使用此種類者，在種類被刪除後將被改為未知類。";
            tv_content.setText(content);

            alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
                @Override
                public void onClick(View v) {
                    //查詢未知類ID
                    Cursor c;
                    String SELECT= "SELECT * FROM specie WHERE specie_name= '未知類' ";
                    c= mSQLiteDatabase.rawQuery(SELECT, null);
                    c.moveToFirst();

                    String UPDATE_FOOD_TABLE= "UPDATE food SET " +
                            "food_specie='"+ c.getString(0) + //設定種類id=未知類id.
                            "' WHERE food_specie= " + si.getsId();   //當種類id=當前種類.

                    mSQLiteDatabase.execSQL(UPDATE_FOOD_TABLE);
                    c.close();

                    String DELETE_SPECIE_TABLE = "DELETE FROM specie WHERE specie_id=" + si.getsId();
                    mSQLiteDatabase.execSQL(DELETE_SPECIE_TABLE);
                    rcontent= "success!";

                    //rcontent = DELETE_SPECIE_TABLE;
                    dismiss();
                }
            });

            alertView.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
        }

        setContentView(alertView);
        setAlertWindow(0.7, 0.8, true);
    }


    //視窗大小設定
    private void setAlertWindow(double w, double h, boolean touchOut){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();   //取得螢幕寬高.
        Point point = new Point();
        display.getSize(point);

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();   //取得當前alert參數.

        //window.setGravity(Gravity.BOTTOM);  //對話框居中靠底.
        //lp.x = 250; //alert右移.
        //lp.y = 250; //alert上移.

        //alert尺寸
        lp.width = (int) (point.x * w);     //螢幕寬度的幾倍.
        lp.height = (int) (point.y * h);    //螢幕高度的幾倍.
//        layoutParams.width = (int) (display.getWidth() * 0.5);
//        layoutParams.height = (int) (display.getHeight() * 0.5);
        window.setAttributes(lp);
        setCanceledOnTouchOutside(touchOut);    //是否點擊alert外部區域即關閉alert.

    }

    private void setAlertWindow(double w, double h, boolean touchOut, String gravity, float dimamount){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();   //取得螢幕寬高.
        Point point = new Point();
        display.getSize(point);

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();   //取得當前alert參數.

        if(!gravity.equals("")) {
            switch (gravity) {
                case "top":
                    window.setGravity(Gravity.TOP);  //對話框居中靠上.
                    break;
                case "bottom":
                    window.setGravity(Gravity.BOTTOM);  //對話框居中靠下.
                    break;
                case "left":
                    window.setGravity(Gravity.START);  //對話框居中靠左.
                    break;
                case "right":
                    window.setGravity(Gravity.END);  //對話框居中靠右.
                    break;
                default:
            }
        }
        window.setDimAmount(dimamount);    //視窗背後遮罩明暗; 亮到暗的透明度變化(0f-1f最暗), 預設為0.6f.

        //alert尺寸
        lp.width = (int) (point.x * w);     //螢幕寬度的幾倍.
        lp.height = (int) (point.y * h);    //螢幕高度的幾倍.
//        layoutParams.width = (int) (display.getWidth() * 0.5);
//        layoutParams.height = (int) (display.getHeight() * 0.5);
        window.setAttributes(lp);
        setCanceledOnTouchOutside(touchOut);    //是否點擊alert外部區域即關閉alert.

    }

    public String getReturn() {
        return rcontent;
    }
}
