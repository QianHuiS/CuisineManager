package tw.idv.qianhuis.cuisinemanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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

    public CustomDialog(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
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
                fSpecie.buildSpecie(l_specie);  // TODO: 2018/9/3 疑問, buildSpecie()使用算高耦合度? 是否應該在alert中進行DB查詢?
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
                EditText et_fposition= alertView.findViewById(R.id.et_fposition);

                //取得填寫的內容
                String[] fcontent=new String[6];
                fcontent[0]= ib_specie.getTag().toString(); //取得ID
                fcontent[1]= et_fname.getText().toString();
                fcontent[2]= et_fquantity.getText().toString();
                fcontent[3]= et_funit.getText().toString();
                fcontent[4]= et_fposition.getText().toString();
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
                    //mSQLiteDatabase.execSQL(INSERT_FOOD_TABLE);
                    //Toast.makeText(context, "新增成功!!", Toast.LENGTH_SHORT).show();

                    rcontent= INSERT_FOOD_TABLE;     //相當於return String.
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
        EditText et_fposition= alertView.findViewById(R.id.et_fposition);
        final Button bt_fstoragetime= alertView.findViewById(R.id.bt_fstoragetime);

        //傳入原資料
        tv_showsname.setText(fi.getsItem().getsName());    //取得點選的種類ID, 取出詳細資料顯示在button.
        tv_showslife.setText(fi.getsItem().getsLife());
        ib_specie.setTag(fi.getfSpecie());
        et_fname.setText(fi.getfName());
        et_fquantity.setText(fi.getfQuantity());
        et_funit.setText(fi.getfUnit());
        et_fposition.setText(fi.getfPosition());
        bt_fstoragetime.setText(fi.getfStoragetime());

        //種類輸入
        ib_specie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomDialog fSpecie= new CustomDialog(context);
                fSpecie.buildSpecie(l_specie);  // TODO: 2018/9/3 疑問, buildSpecie()使用算高耦合度? 是否應該在alert中進行DB查詢?
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
                EditText et_fposition= alertView.findViewById(R.id.et_fposition);

                //取得填寫的內容
                String[] fcontent=new String[7];
                fcontent[0]= fi.getfId();   //拿出物件中的某資訊.
                fcontent[1]= ib_specie.getTag().toString(); //取得ID
                fcontent[2]= et_fname.getText().toString();
                fcontent[3]= et_fquantity.getText().toString();
                fcontent[4]= et_funit.getText().toString();
                fcontent[5]= et_fposition.getText().toString();
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
                    //mSQLiteDatabase.execSQL(UPDATE_FOOD_TABLE);
                    //Toast.makeText(context, "修改成功!!", Toast.LENGTH_SHORT).show();

                    rcontent= UPDATE_FOOD_TABLE;     //相當於return String.
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

    //Specie
    public void buildSpecie(final ArrayList<HashMap<String, Object>> l_specie) {
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
                rcontent= String.valueOf(position);
                dismiss();
            }
        });

        setContentView(alertView);
        this.setAlertWindow(0.7, 0.9, true, "right", 0.6f);
    }

    //Specie Add
    public void buildSInput() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView= inflater.inflate(R.layout.alertdialog_sinput, null);    //layout可換!

        ImageButton ib_specie= alertView.findViewById(R.id.ib_specie);
        ib_specie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2018/8/28  gridview選擇種類項目, 讀檔image放入list放入adapter顯示gridview.
                //選擇圖片alert
                // ib_specie.setImageResource(R.drawable.圖檔名稱不含副檔名)
                //下方290行 取得圖檔
            }
        });

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                //連接layoutXML
                //種類!!
                EditText et_sname= alertView.findViewById(R.id.et_sname);
                EditText et_slife= alertView.findViewById(R.id.et_slife);
                ImageButton ib_specie= alertView.findViewById(R.id.ib_specie);  //是否不需要??

                //取得填寫的內容
                String[] scontent=new String[3];
                scontent[0]= et_sname.getText().toString();
                scontent[1]= et_slife.getText().toString();
                scontent[2]= ib_specie.getResources().toString();   //是否有問題!!!

                //檢查是否有欄位為空
                boolean isNull= false;
                for(int i=0; i<scontent.length; i++){
                    if(scontent[i].equals(""))
                        isNull= true;
                }   //若使用for each, 暫存each的對象為空時, 會造成錯誤.

                if(isNull) {  //若有欄位未填寫.
                    Toast.makeText(context, "有欄位空著!!", Toast.LENGTH_SHORT).show();
                }else {
                    String INSERT_FOOD_TABLE = "INSERT INTO specie (specie_name, specie_life, specie_image) " +
                            "VALUES('"+ scontent[0] +"', '"+ scontent[1] +"', '"+ scontent[2] +"')";
                    //mSQLiteDatabase.execSQL(INSERT_FOOD_TABLE);
                    //Toast.makeText(context, "新增成功!!", Toast.LENGTH_SHORT).show();

                    rcontent= INSERT_FOOD_TABLE;     //相當於return String.
                    dismiss();
                }
            }
        });

        alertView.findViewById(R.id.bt_delete).setActivated(false);     //新增specie無刪除功能.

        setContentView(alertView);
        setAlertWindow(0.9, 0.7, false);
    }

    // TODO: 2018/8/29 Specie Revise

    //Food Delete
    public void buildDelete(final FoodItem fi) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_delete, null);    //layout可換!

        TextView tv_content= alertView.findViewById(R.id.tv_content);
        String content= "真的要刪除這"+fi.getfQuantity() + fi.getfUnit() + fi.getfName() +"？";
        tv_content.setText(content);

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                String DELETE_FOOD_TABLE= "DELETE FROM food WHERE food_id=" +fi.getfId();
                //mSQLiteDatabase.execSQL(DELETE_FOOD_TABLE);

                rcontent= DELETE_FOOD_TABLE;
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

    //Food Search
    public void buildSearch() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_fsearch, null);    //layout可換!

        // TODO: 2018/9/3 種類按鈕和XML元件修改 1

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
                //種類!!
                EditText et_fname= alertView.findViewById(R.id.et_fname);
                EditText et_fposition= alertView.findViewById(R.id.et_fposition);

                String[] fcontent=new String[4];
                fcontent[0]= ""; //種類!!
                fcontent[1]= et_fname.getText().toString();
                fcontent[2]= et_fposition.getText().toString();
                fcontent[3]= bt_fstoragetime.getText().toString();

                //取得填寫的內容
                String[] column=new String[4];
                //column[0]= MessageFormat.format("food_specie= '{0}'", fcontent[0]);  //種類!!
                column[0]= "food_specie= '"+ fcontent[0] +"'";  //種類!!
                column[1]= "food_name like '%"+ fcontent[1] +"%'";
                column[2]= "food_position= '"+ fcontent[2] +"'";
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
                //Toast.makeText(context, WHERE, Toast.LENGTH_LONG).show();

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
