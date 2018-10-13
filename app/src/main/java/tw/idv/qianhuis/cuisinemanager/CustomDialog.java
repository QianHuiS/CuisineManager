package tw.idv.qianhuis.cuisinemanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
* CustomDialog ALERT= new CustomDialog(CONTEXT.this);
* ALERT.BUILD();
* ALERT.show();
* ALERT.setOnDismissListener(new ...{
*   if(!ALERT.getReturn().equals("")) {  mSQLiteDatabase.execSQL(ALERT.getReturn());   }  }
*/

public class CustomDialog extends Dialog {
    private Context context;
    private String rcontent= "";

    //DB
    private SQLiteDatabase mSQLiteDatabase= null;
    private static final String DATABASE_NAME = "app.db";
    //mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

    public CustomDialog(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        //build();
    }

    //Food Add
    public void buildFInput(final ArrayList<HashMap<String, Object>> l_specie) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= inflater.inflate(R.layout.alertdialog_finput, null);    //layout可換!

        //種類輸入
        final ImageButton ib_specie= alertView.findViewById(R.id.ib_specie);
        ib_specie.setTag("");   //"初始化"tag, 避免無輸入時錯誤.
        ib_specie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomDialog fSpecie= new CustomDialog(context);
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
                                    Integer.valueOf(fSpecie.getReturn())    //回傳的item position.
                            ));

                            tv_showsname.setText(si.getsName());
                            tv_showslife.setText(si.getsLife());
                            ib_specie.setTag(si.getsId());  //用Tag紀錄ID.
                            // ib_specie.setImageResource(R.drawable.圖檔名稱不含副檔名)
                            //ib_specie.setImageResource();    //設置圖片.
                        }
                    }
                });
            }
        });

        //位置輸入
        final RadioGroup rg_fposition= alertView.findViewById(R.id.rg_fposition);
        rg_fposition.setTag("");
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

        //日期輸入
        final Button bt_fstoragetime= alertView.findViewById(R.id.bt_fstoragetime);
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
                EditText et_fname= alertView.findViewById(R.id.et_fname);
                EditText et_fquantity= alertView.findViewById(R.id.et_fquantity);
                EditText et_funit= alertView.findViewById(R.id.et_funit);
                //EditText et_fposition= alertView.findViewById(R.id.et_fposition);

                //取得填寫的內容
                String[] fcontent=new String[6];
                fcontent[0]= ib_specie.getTag().toString(); //取得ID
                fcontent[1]= et_fname.getText().toString();
                fcontent[2]= et_fquantity.getText().toString();
                fcontent[3]= et_funit.getText().toString();
                fcontent[4]= rg_fposition.getTag().toString();
                fcontent[5]= bt_fstoragetime.getText().toString();

                //檢查是否有欄位為空
                boolean isNull= false;
                for(int i=0; i<fcontent.length; i++){
                    if(fcontent[i].equals(""))
                        isNull= true;
                }   //若使用for each, 暫存each的對象為空時, 會造成錯誤.

                if(isNull) {  //若有欄位未填寫.
                    Toast.makeText(context, "有欄位空著!!", Toast.LENGTH_SHORT).show();
                }else {
                    String INSERT_FOOD_TABLE = "INSERT INTO food (food_specie, food_name, food_quantity, " +
                            "food_unit, food_position, food_storagetime) " +
                            "VALUES('"+ fcontent[0] +"', '"+ fcontent[1] +"', '"+ fcontent[2] +"', " +
                            "'"+ fcontent[3] +"', '"+ fcontent[4] +"', '"+ fcontent[5] +"')";
                    mSQLiteDatabase.execSQL(INSERT_FOOD_TABLE);
                    Toast.makeText(context, "新增成功!!", Toast.LENGTH_SHORT).show();

                    //rcontent= INSERT_FOOD_TABLE;     //相當於return String.
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

    //Food Revise
    public void buildFInput(final FoodItem fi, final ArrayList<HashMap<String, Object>> l_specie) {
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
                final CustomDialog fSpecie= new CustomDialog(context);
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
                            // ib_specie.setImageResource(R.drawable.圖檔名稱不含副檔名)
                            //ib_specie.setImageResource();    //設置圖片.
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
                String[] fcontent=new String[7];
                fcontent[0]= fi.getfId();   //拿出物件中的某資訊.
                fcontent[1]= ib_specie.getTag().toString(); //取得ID
                fcontent[2]= et_fname.getText().toString();
                fcontent[3]= et_fquantity.getText().toString();
                fcontent[4]= et_funit.getText().toString();
                fcontent[5]= rg_fposition.getTag().toString();
                fcontent[6]= bt_fstoragetime.getText().toString();

                //檢查是否有欄位為空
                boolean isNull= false;
                for(int i=1; i<fcontent.length; i++){
                    if(fcontent[i].equals(""))
                        isNull= true;
                }

                if(isNull) {  //若有欄位未填寫.
                    Toast.makeText(context, "有欄位空著!!", Toast.LENGTH_SHORT).show();
                }else {
                    String UPDATE_FOOD_TABLE= "UPDATE food SET " +
                            "food_specie="+ fcontent[1] +", food_name='"+ fcontent[2] +"', " +
                            "food_quantity="+ fcontent[3] +", food_unit='"+ fcontent[4] +"', " +
                            "food_position='"+ fcontent[5] +"', food_storagetime='"+ fcontent[6] +"' " +
                            "where food_id=" + fcontent[0];
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

    //Specie Select
    public void buildSselect(final ArrayList<HashMap<String, Object>> l_specie) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= inflater.inflate(R.layout.alertdialog_specie, null);    //layout可換!

        //gridview選擇種類項目
        //讀DBspecieTable(在main)放入list(傳給alert), 放入adapter顯示gridview.
        GridView gv_specie= alertView.findViewById(R.id.gv_specie);
        SimpleAdapter adapter= new SimpleAdapter(context,
                l_specie, R.layout.gridview_specie,
                new String[]{"specie_name", "specie_life"},
                new int[]{R.id.tv_showsname, R.id.tv_showslife}
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

    //Food Delete
    public void buildDelete(final FoodItem fi) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_delete, null);    //layout可換!

        TextView tv_content= alertView.findViewById(R.id.tv_content);
        tv_content.setText("真的要刪除這"+fi.getfQuantity() + fi.getfUnit() + fi.getfName() +"？");

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                String DELETE_FOOD_TABLE= "DELETE FROM food WHERE food_id=" +fi.getfId();
                mSQLiteDatabase.execSQL(DELETE_FOOD_TABLE);
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
    //Food Search
    public void buildSearch(final ArrayList<HashMap<String, Object>> l_specie) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_fsearch, null);    //layout可換!

        //種類按鈕
        final ImageButton ib_specie= alertView.findViewById(R.id.ib_specie);
        ib_specie.setTag("");   //"初始化"tag, 避免無輸入時錯誤.
        ib_specie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomDialog fSpecie= new CustomDialog(context);
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
                            // ib_specie.setImageResource(R.drawable.圖檔名稱不含副檔名)
                            //ib_specie.setImageResource();    //設置圖片.
                        }
                    }
                });
            }
        });

        //位置按鈕
        final CheckBox cb_freezing= alertView.findViewById(R.id.cb_freezing);
        final CheckBox cb_refrigerated= alertView.findViewById(R.id.cb_refrigerated);
        final CheckBox cb_fresh= alertView.findViewById(R.id.cb_fresh);
        cb_freezing.setTag("");
        cb_refrigerated.setTag("");
        cb_fresh.setTag("");
        cb_freezing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {    //buttonView為當前觸發的cb, isChecked為cb的選取狀態.
                if(isChecked) cb_freezing.setTag("冷凍室");
                else cb_freezing.setTag("");
            }
        });
        cb_refrigerated.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {    //buttonView為當前觸發的cb, isChecked為cb的選取狀態.
                if(isChecked) cb_refrigerated.setTag("冷藏室");
                else cb_refrigerated.setTag("");
            }
        });
        cb_fresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {    //buttonView為當前觸發的cb, isChecked為cb的選取狀態.
                if(isChecked) cb_fresh.setTag("保鮮室");
                else cb_fresh.setTag("");
            }
        });

        //日期按鈕
        final Button bt_fstoragetime1= alertView.findViewById(R.id.bt_fstoragetime1);
        bt_fstoragetime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar= Calendar.getInstance(); //取得一個日曆實體.

                DatePickerDialog datePickerDialog= new DatePickerDialog(
                        context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {  //month要+1.
                        String sdate= DateFunction.stringFormat(year, month+1, day);
                        bt_fstoragetime1.setText(sdate);
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
                EditText et_fname= alertView.findViewById(R.id.et_fname);
                //EditText et_fposition= alertView.findViewById(R.id.et_fposition);
                String fposition= "";
                //若cb被選重則加入字串(以空白間隔).
                if(!cb_freezing.getTag().toString().equals("")) fposition+=cb_freezing.getTag().toString()+" ";
                if(!cb_refrigerated.getTag().toString().equals("")) fposition+=cb_refrigerated.getTag().toString()+" ";
                if(!cb_fresh.getTag().toString().equals(""))    fposition+=cb_fresh.getTag().toString()+" ";
                fposition= fposition.trim();     //將字串末的空白去除.
                fposition= fposition.replaceAll(" ", "', '");     //則將所有空白替換成參數的字串.
                //fposition.replaceAll(" ", "' OR food_position= '");

                String[] fcontent=new String[4];
                fcontent[0]= ib_specie.getTag().toString();
                fcontent[1]= et_fname.getText().toString();
                fcontent[2]= fposition;
                fcontent[3]= bt_fstoragetime1.getText().toString();

                //取得填寫的內容
                String[] column=new String[4];
                column[0]= "food_specie= '"+ fcontent[0] +"'";  //種類!!
                column[1]= "food_name LIKE '%"+ fcontent[1] +"%'";
                column[2]= "food_position IN ('"+ fcontent[2] +"')";
                column[3]= "food_storagetime= '"+ fcontent[3] +"'";

                boolean first= true;
                String WHERE= "";
                for(int i=0; i<fcontent.length; i++) {
                    if (fcontent[i].equals("")) continue;
                    else {
                        if (first)  first= false;
                        else    WHERE += " and ";

                        WHERE += column[i];
                    }
                }

                rcontent= WHERE;     //相當於return String.
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
        setAlertWindow(0.9, 0.9, false);
    }


    //Specie Set
    public void buildSset(final ArrayList<HashMap<String, Object>> l_specie) {
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
                new String[]{"specie_name", "specie_life"},
                new int[]{R.id.tv_showsname, R.id.tv_showslife}
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

    //Specie Add
    public void buildSInput() {     //(修改傳入si)填寫完回傳sqlcode.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= inflater.inflate(R.layout.alertdialog_sinput, null);    //layout可換!

        //gridview

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                //連接layoutXML
                EditText et_sname= alertView.findViewById(R.id.et_sname);
                EditText et_slife= alertView.findViewById(R.id.et_slife);
                //圖片!!!

                //取得填寫的內容
                String[] scontent=new String[2];
                scontent[0]= et_sname.getText().toString();
                scontent[1]= et_slife.getText().toString();
                //scontent[2]= .getResources().toString();

                //檢查是否有欄位為空
                boolean isNull= false;
                for(int i=0; i<scontent.length; i++){
                    if(scontent[i].equals(""))
                        isNull= true;
                }   //若使用for each, 暫存each的對象為空時, 會造成錯誤.

                if(isNull) {  //若有欄位未填寫.
                    Toast.makeText(context, "有欄位空著!!", Toast.LENGTH_SHORT).show();
                }else {
                    String INSERT_SPECIE_TABLE = "INSERT INTO specie (specie_name, specie_life) " +   //, specie_image
                            "VALUES('"+ scontent[0] +"', '"+ scontent[1] +"')";     //, '"+ scontent[2] +"'
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

    //Specie Revise
    public void buildSInput(final SpecieItem si) {     //修改傳入si,填寫完回傳sqlcode.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= inflater.inflate(R.layout.alertdialog_sinput, null);    //layout可換!

        EditText et_sname= alertView.findViewById(R.id.et_sname);
        EditText et_slife= alertView.findViewById(R.id.et_slife);

        et_sname.setText(si.getsName());
        et_slife.setText(si.getsLife());
        //gridview

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                //連接layoutXML
                EditText et_sname= alertView.findViewById(R.id.et_sname);
                EditText et_slife= alertView.findViewById(R.id.et_slife);
                //圖片!!!

                //取得填寫的內容
                String[] scontent=new String[3];
                scontent[0]= si.getsId();
                scontent[1]= et_sname.getText().toString();
                scontent[2]= et_slife.getText().toString();
                //scontent[3]= .getResources().toString();

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
                            "specie_name='"+ scontent[1] +"', specie_life='"+ scontent[2] +"' " +   //, specie_image="+ scontent[3] +
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
                final CustomDialog sDelete= new CustomDialog(context);
                sDelete.buildDelete(si);
                sDelete.show();
                sDelete.setOnDismissListener(new DialogInterface.OnDismissListener() {
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

    //Specie Delete
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

                    Log.d("TAG", "UPDATE_FOOD_TABLE= "+UPDATE_FOOD_TABLE);
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

        if(!gravity.equals(null)) {
            switch (gravity) {
                case "top":
                    window.setGravity(Gravity.TOP);  //對話框居中靠底.
                    break;
                case "bottom":
                    window.setGravity(Gravity.BOTTOM);  //對話框居中靠底.
                    break;
                case "left":
                    window.setGravity(Gravity.LEFT);  //對話框居中靠底.
                    break;
                case "right":
                    window.setGravity(Gravity.RIGHT);  //對話框居中靠底.
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
