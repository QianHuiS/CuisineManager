package tw.idv.qianhuis.cuisinemanager;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*使用方法:
* CustomDialog ALERT= new CustomDialog(CONTEXT.this);
* ALERT.BUILD();
* ALERT.show();
* ALERT.setOnDismissListener(new ...{
*   if(!ALERT.getSqlcode().equals("")) {  mSQLiteDatabase.execSQL(ALERT.getSqlcode());   }  }
*/

public class CustomDialog extends Dialog {
    private Context context;
    private String sqlcode= "";

    public CustomDialog(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        //build();
    }

    public void buildAdd() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_finput, null);    //layout可換!

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                //連接layoutXML
                //種類!!
                EditText et_fname= alertView.findViewById(R.id.et_fname);
                EditText et_fquantity= alertView.findViewById(R.id.et_fquantity);
                EditText et_funit= alertView.findViewById(R.id.et_funit);
                EditText et_fposition= alertView.findViewById(R.id.et_fposition);
                EditText et_fstoragetime= alertView.findViewById(R.id.et_fstoragetime);

                //取得填寫的內容
                String[] fcontent=new String[7];
                fcontent[1]= "2"; //種類!!
                fcontent[2]= et_fname.getText().toString();
                fcontent[3]= et_fquantity.getText().toString();
                fcontent[4]= et_funit.getText().toString();
                fcontent[5]= et_fposition.getText().toString();
                fcontent[6]= et_fstoragetime.getText().toString();

                //檢查是否有欄位為空
                boolean isNull= false;
                for(int i=1; i<fcontent.length; i++){
                    if(fcontent[i].equals(""))
                        isNull= true;
                }

                if(isNull) {  //若有欄位未填寫.
                    Toast.makeText(context, "有欄位空著!!", Toast.LENGTH_SHORT).show();
                }else {
                    String INSERT_FOOD_TABLE = "INSERT INTO food (food_specie, food_name, food_quantity, " +
                            "food_unit, food_position, food_storagetime) " +
                            "VALUES('"+ fcontent[1] +"', '"+ fcontent[2] +"', '"+ fcontent[3] +"', " +
                            "'"+ fcontent[4] +"', '"+ fcontent[5] +"', '"+ fcontent[6] +"')";
                    //mSQLiteDatabase.execSQL(INSERT_FOOD_TABLE);
                    //Toast.makeText(context, "新增成功!!", Toast.LENGTH_SHORT).show();

                    sqlcode= INSERT_FOOD_TABLE;     //相當於return String.
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

    public void buildRevise(final FoodItem fi) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_finput, null);    //layout可換!

        //連接layoutXML
        //種類!!
        EditText et_fname= alertView.findViewById(R.id.et_fname);
        EditText et_fquantity= alertView.findViewById(R.id.et_fquantity);
        EditText et_funit= alertView.findViewById(R.id.et_funit);
        EditText et_fposition= alertView.findViewById(R.id.et_fposition);
        EditText et_fstoragetime= alertView.findViewById(R.id.et_fstoragetime);

        //傳入原資料
        //種類!!
        et_fname.setText(fi.getfName());
        et_fquantity.setText(fi.getfQuantity());
        et_funit.setText(fi.getfUnit());
        et_fposition.setText(fi.getfPosition());
        et_fstoragetime.setText(fi.getfStoragetime());

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                //連接layoutXML
                //種類!!
                EditText et_fname= alertView.findViewById(R.id.et_fname);
                EditText et_fquantity= alertView.findViewById(R.id.et_fquantity);
                EditText et_funit= alertView.findViewById(R.id.et_funit);
                EditText et_fposition= alertView.findViewById(R.id.et_fposition);
                EditText et_fstoragetime= alertView.findViewById(R.id.et_fstoragetime);

                //取得填寫的內容
                String[] fcontent=new String[7];
                fcontent[0]= fi.getfId();   //拿出物件中的某資訊.
                fcontent[1]= "2"; //種類!!
                fcontent[2]= et_fname.getText().toString();
                fcontent[3]= et_fquantity.getText().toString();
                fcontent[4]= et_funit.getText().toString();
                fcontent[5]= et_fposition.getText().toString();
                fcontent[6]= et_fstoragetime.getText().toString();

                //檢查是否有欄位為空
                boolean isNull= false;
                for(int i=1; i<fcontent.length; i++){
                    if(fcontent[i].equals(""))
                        isNull= true;
                }

                if(isNull) {  //若有欄位未填寫.
                    Toast.makeText(context, "有欄位空著!!", Toast.LENGTH_SHORT).show();
                }else {
                    String UPDATE_FOOD_TABLE= "Update food set " +
                            "food_specie="+ fcontent[1] +", food_name='"+ fcontent[2] +"', " +
                            "food_quantity="+ fcontent[3] +", food_unit='"+ fcontent[4] +"', " +
                            "food_position='"+ fcontent[5] +"', food_storagetime='"+ fcontent[6] +"' " +
                            "where index_id=" + fcontent[0];
                    //mSQLiteDatabase.execSQL(UPDATE_FOOD_TABLE);
                    //Toast.makeText(context, "修改成功!!", Toast.LENGTH_SHORT).show();

                    sqlcode= UPDATE_FOOD_TABLE;     //相當於return String.
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


    public void buildDelete(final FoodItem fi) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertView = inflater.inflate(R.layout.alertdialog_delete, null);    //layout可換!

        TextView tv_content= alertView.findViewById(R.id.tv_content);
        String content= "真的要刪除這"+fi.getfQuantity() + fi.getfUnit() + fi.getfName() +"？";
        tv_content.setText(content);

        alertView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {    //bt可換!
            @Override
            public void onClick(View v) {
                String DELETE_FOOD_TABLE= "Delete from food where index_id=" +fi.getfId();
                //mSQLiteDatabase.execSQL(DELETE_FOOD_TABLE);

                sqlcode= DELETE_FOOD_TABLE;
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

    public String getSqlcode() {
        return sqlcode;
    }
}
