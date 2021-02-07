package com.example.fileinput;

import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.fileinput.databean.Course;
import com.example.fileinput.process.InputCourseData;
import com.example.fileinput.process.SimpleFreeTable;

import java.io.FileInputStream;
import java.util.List;

public class freeTable extends AppCompatActivity {
    /** 第一个无内容的格子 */
    protected TextView empty;
    /** 星期一的格子 */
    protected TextView monColum;
    /** 星期二的格子 */
    protected TextView tueColum;
    /** 星期三的格子 */
    protected TextView wedColum;
    /** 星期四的格子 */
    protected TextView thrusColum;
    /** 星期五的格子 */
    protected TextView friColum;
    /** 星期六的格子 */
    protected TextView satColum;
    /** 星期日的格子 */
    protected TextView sunColum;
    /** 课程表body部分布局 */
    protected RelativeLayout course_table_layout;
    /** 屏幕宽度 **/
    protected int screenWidth;
    /** 课程格子平均宽度 **/
    protected int aveWidth;

    protected int height;
    protected int gridHeight;

    private List<Course> Freetable;
    private String week;

    //用于保存布局文件；
    List<RelativeLayout> layouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freetable);
        //Fileinput fin=new Fileinput(this);
        //fin.Read();
        InputCourseData icd=new InputCourseData(this);
        List<Course> courseDatas=icd.ReadFile();

        SimpleFreeTable sift=new SimpleFreeTable();
        sift.FormatTheCourseData(courseDatas);
        Freetable=sift.OutputFreeTable(courseDatas);
        week="13";
        //默认为13，后续根据实际情况进行调整
        //该构造函数，初步调用一些函数，便于后续改进与调整
        //数据展示控件
        DisplayMetrics dm = new DisplayMetrics();
        //设置基本信息
        setBaseData(dm);

        //设置课表界面
        //动态生成10 * maxCourseNum个textview
        SetEmptyCourseTable(dm);
        //后续的函数调用问题

        InitialTheCourseTable(Freetable,week);
    }



    //用于布局基本数据的设置
    public void setBaseData(DisplayMetrics dm){
        //获得列头的控件
        empty = (TextView) this.findViewById(R.id.test_empty);
        monColum = (TextView) this.findViewById(R.id.test_monday_course);
        tueColum = (TextView) this.findViewById(R.id.test_tuesday_course);
        wedColum = (TextView) this.findViewById(R.id.test_wednesday_course);
        thrusColum = (TextView) this.findViewById(R.id.test_thursday_course);
        friColum = (TextView) this.findViewById(R.id.test_friday_course);
        satColum  = (TextView) this.findViewById(R.id.test_saturday_course);
        sunColum = (TextView) this.findViewById(R.id.test_sunday_course);
        course_table_layout = (RelativeLayout) this.findViewById(R.id.test_course_rl);
        //DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //屏幕宽度
        int width = dm.widthPixels;
        //平均宽度
        int aveWidth = width / 8;
        //第一个空白格子设置为25宽
        empty.setWidth(aveWidth * 3/4);
        monColum.setWidth(aveWidth * 33/32 + 1);
        tueColum.setWidth(aveWidth * 33/32 + 1);
        wedColum.setWidth(aveWidth * 33/32 + 1);
        thrusColum.setWidth(aveWidth * 33/32 + 1);
        friColum.setWidth(aveWidth * 33/32 + 1);
        satColum.setWidth(aveWidth * 33/32 + 1);
        sunColum.setWidth(aveWidth * 33/32 + 1);
        this.screenWidth = width;
        this.aveWidth = aveWidth;
        height = dm.heightPixels;
        gridHeight = height / 12;


    }

    //用于构建一个空的课表，便于后续展示
    public  void SetEmptyCourseTable(DisplayMetrics dm){
        int height = dm.heightPixels;
        int gridHeight = height / 12;
        for(int i = 1; i <= 10; i ++){

            for(int j = 1; j <= 8; j ++){

                TextView tx = new TextView(freeTable.this);
                tx.setId((i - 1) * 8  + j);
                //除了最后一列，都使用course_text_view_bg背景（最后一列没有右边框）
                if(j < 8)
                    tx.setBackgroundDrawable(freeTable.this.
                            getResources().getDrawable(R.drawable.course_text_view_bg));
                else
                    tx.setBackgroundDrawable(freeTable.this.
                            getResources().getDrawable(R.drawable.course_table_last_colum));
                //相对布局参数
                RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                        aveWidth * 33 / 32 + 1,
                        gridHeight);
                //文字对齐方式
                tx.setGravity(Gravity.CENTER);
                //字体样式
                tx.setTextAppearance(this, R.style.courseTableText);
                //如果是第一列，需要设置课的序号（1 到 10）
                if(j == 1)
                {
                    tx.setText(String.valueOf(i));
                    rp.width = aveWidth * 3/4;
                    //设置他们的相对位置
                    if(i == 1)
                        rp.addRule(RelativeLayout.BELOW, empty.getId());
                    else
                        rp.addRule(RelativeLayout.BELOW, (i - 1) * 8);
                }
                else
                {
                    rp.addRule(RelativeLayout.RIGHT_OF, (i - 1) * 8  + j - 1);
                    rp.addRule(RelativeLayout.ALIGN_TOP, (i - 1) * 8  + j - 1);
                    tx.setText("");
                }

                tx.setLayoutParams(rp);
                course_table_layout.addView(tx);
            }
        }

    }

    public  void InitialTheCourseTable(List<Course> Coursetable,String week){
        //尝试批量处理
        int day,time=0;
        String coursename,location;
        for(int i=0;i<Coursetable.size();i++){
            if(Coursetable.get(i).getWeeks().equals(week)){
                day=Integer.valueOf(Coursetable.get(i).getWeekday())+1;

                if(Coursetable.get(i).getTime().equals("[01-02节]"))
                    time=0;
                else if(Coursetable.get(i).getTime().equals("[03-04节]"))
                    time=2;
                else if(Coursetable.get(i).getTime().equals("[05-06节]"))
                    time=4;
                else if(Coursetable.get(i).getTime().equals("[07-08节]"))
                    time=6;
                else if(Coursetable.get(i).getTime().equals("[09-10节]"))
                    time=8;

                coursename="free";
                location="@图书馆";
                InputDataToTheCourseTableAndDisplay(day,time,coursename,location);
            }

        }
/*
        InputDataToTheCourseTableAndDisplay(1,0,"电工与电子学A","@第五教学楼203");
        InputDataToTheCourseTableAndDisplay(1,2,"形势与政策","@第五教学楼2-1");
        InputDataToTheCourseTableAndDisplay(1,4,"流体力学A","@四教四合班");
        InputDataToTheCourseTableAndDisplay(1,6,"概率论与数理统计B","@四教五合班");
        InputDataToTheCourseTableAndDisplay(1,8,"马克思主义基本原理概论","@第五教学楼1-1");
        InputDataToTheCourseTableAndDisplay(2,2,"大学英语(3)(B)","@第五教学楼106");
        InputDataToTheCourseTableAndDisplay(2,8,"工程力学","@四教四合班");
        InputDataToTheCourseTableAndDisplay(3,0,"普通物理学B(2)","@四教五合班");
        InputDataToTheCourseTableAndDisplay(3,2,"电工与电子学A","@四教四合班");
        InputDataToTheCourseTableAndDisplay(3,4,"普通物理学B实验","@四教学楼2楼");
        InputDataToTheCourseTableAndDisplay(3,6,"普通物理学B实验","@四教学楼2楼");
        InputDataToTheCourseTableAndDisplay(3,8,"普通物理学B实验","@四教学楼2楼");
        InputDataToTheCourseTableAndDisplay(4,4,"概率论与数理统计B","@第五教学楼2-1");
        InputDataToTheCourseTableAndDisplay(4,6,"大学英语(3)","@第五教学楼106");
        InputDataToTheCourseTableAndDisplay(4,8,"马克思主义基本原理概论","@第五教学楼1-1");
        InputDataToTheCourseTableAndDisplay(5,0,"工程力学","@四教四合班");
        InputDataToTheCourseTableAndDisplay(5,2,"流体力学A","@四教六合班");

 */
    }

    //实现基本数据的导入，便于后续开展
    public void InputDataToTheCourseTableAndDisplay(int day,int time,String coursename,String location ){
        int[] background = {R.drawable.course_info_blue, R.drawable.course_info_green,
                R.drawable.course_info_red, R.drawable.course_info_red,
                R.drawable.course_info_yellow};
        // 如下到结束，均为添加课程信息->获取相应定位
        //倒是可以设置一个导入数据的模块，实现一键导入
        StringBuilder text = new StringBuilder();
        text.append(coursename).append(System.getProperty("line.separator")).append(location);

        //String text=coursename+location;
        TextView courseInfo = new TextView(this);  //构建相关控件

        courseInfo.setText(text);

        //该textview的高度根据其节数的跨度来设置
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                aveWidth * 31 / 32,
                (gridHeight - 5) * 2 );
        //总览：textview的位置由课程开始节数和上课的时间（day of week）确定

        rlp.leftMargin = 2;
        //内边界的距离
        rlp.addRule(RelativeLayout.RIGHT_OF, day);
        // 偏移由这节课是星期几决定
        rlp.topMargin = 5 + time * gridHeight;
        //gridHeight即为一个方格的高度，5为基本高度

        //该textview的高度根据其节数的跨度来设置

        //字体居中
        courseInfo.setGravity(Gravity.CENTER);
        // 设置一种背景
        courseInfo.setBackgroundResource(background[1]);
        courseInfo.setTextSize(8);
        courseInfo.setLayoutParams(rlp);
        courseInfo.setTextColor(Color.WHITE);
        //设置不透明度
        courseInfo.getBackground().setAlpha(222);
        course_table_layout.addView(courseInfo);

    }
    //读取文件
    private void readSaveFile() {
        FileInputStream inputStream;

        try {
            String filename="app/sampledata/infos.txt";
            inputStream = openFileInput(filename);
            byte temp[] = new byte[1024];
            StringBuilder sb = new StringBuilder("");
            int len = 0;
            while ((len = inputStream.read(temp)) > 0){
                sb.append(new String(temp, 0, len));
            }
            System.out.println(sb);
            Log.d("msg", "readSaveFile: \n" + sb.toString());
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
