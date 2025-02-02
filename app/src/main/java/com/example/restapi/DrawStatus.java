package com.example.restapi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DrawStatus extends AppCompatActivity {
    public PieChart pieChart;
    public PieChart pieChart2;
    public TextView text_ram;

    //double rms = 0.0;
    //double peak = 0.0;


    double cpu = 0.0;
    String ram_total;
    double ram_usage = 0.0;
    double ram_usage_per = 0.0;
    String iip;
    String device_name;
    public static Thread thread2;
    int[] col = new int[]{
            Color.rgb(213,13,13), Color.rgb(162, 158, 158), Color.rgb(0,0,0),
            Color.rgb(0,0,0), Color.rgb(0,0,0)};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Intent intent = getIntent();
        iip = intent.getStringExtra("ip");


        pieChart = (PieChart)findViewById(R.id.chart2);
        pieChart2 = (PieChart)findViewById(R.id.chart3);
        text_ram = (TextView)findViewById(R.id.ram);

        thread2 = new Thread(new getStatus(iip));
        thread2.start();


    }

    @Override
    public void onBackPressed(){
        thread2.interrupt();
        //Log.v("threadstatus1",""+thread2.isInterrupted());
        super.onBackPressed();
    }

    private class getStatus implements Runnable {
        //private final AtomicBoolean condition = new AtomicBoolean(false);

        private String ip;
        public getStatus(String ip) {
            this.ip = ip;
        }


        String resultText1 = "[NULL]";

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {

            try {
                Log.v("ipipip","status : "+ip);
                resultText1 = new Task().execute("http://" + ip + ":50010/manage/Pc/info").get();
                JSONObject jsonObject = new JSONObject(resultText1);
                cpu = Double.parseDouble(jsonObject.getString("cpu").replace("%", ""));
                ram_total = jsonObject.getString("ram_total").replace("MB", "");
                ram_usage = Double.parseDouble(jsonObject.getString("ram_usage").replace("MB", ""));
                ram_usage_per = Double.parseDouble(jsonObject.getString("ram_usage_per").replace("%", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    text_ram.setText(ram_total);
                    pieChart.setUsePercentValues(true);
                    pieChart.getDescription().setEnabled(false);
                    pieChart.setExtraOffsets(5, 10, 5, 5);

                    pieChart.setDragDecelerationFrictionCoef(0.95f);

                    pieChart.setDrawHoleEnabled(false);
                    pieChart.setHoleColor(Color.BLACK);
                    pieChart.setTransparentCircleRadius(61f);


                    ArrayList yValues = new ArrayList();

                    yValues.add(new PieEntry((float) cpu, "USAGE"));
                    yValues.add(new PieEntry((float) (100 - cpu), "AVAILABLE"));


                    Description description = new Description();
                    description.setText("CPU (%)"); //라벨
                    description.setTextSize(30);
                    pieChart.setDescription(description);

                    pieChart.animateY(1000, Easing.EaseInOutCubic);


                    PieData data1 = new PieData();
                    PieDataSet dataSet = new PieDataSet(yValues, "%");


                    dataSet.setSliceSpace(3f);
                    dataSet.setSelectionShift(5f);
                    //Color.rgb(217, 80, 138), Color.rgb(254, 149, 7), Color.rgb(254, 247, 120),
                            //Color.rgb(106, 167, 134), Color.rgb(53, 194, 209)

                    dataSet.setColors(col);


                    data1.addDataSet(dataSet);
                    data1.setValueTextSize(20f);
                    data1.setValueTextColor(Color.rgb(0,102,51));

                    pieChart.setData(data1);


                    pieChart.invalidate();


                    //******원차트2******
                    pieChart2.setUsePercentValues(true);
                    pieChart2.getDescription().setEnabled(false);
                    pieChart2.setExtraOffsets(5, 10, 5, 5);

                    pieChart2.setDragDecelerationFrictionCoef(0.95f);

                    pieChart2.setDrawHoleEnabled(false);
                    pieChart2.setHoleColor(Color.WHITE);
                    pieChart2.setTransparentCircleRadius(61f);


                    ArrayList yValues2 = new ArrayList();

                    yValues2.add(new PieEntry((float) ram_usage_per, "USAGE"));
                    yValues2.add(new PieEntry((float) (100 - ram_usage_per), "AVAILABLE"));


                    Description description2 = new Description();
                    description2.setText("RAM (%)"); //라벨
                    description2.setTextSize(30);
                    pieChart2.setDescription(description2);

                    pieChart2.animateY(1000, Easing.EaseInOutCubic);


                    PieData data2 = new PieData();
                    PieDataSet dataSet2 = new PieDataSet(yValues2, "%");
                    data2.addDataSet(dataSet2);


                    dataSet2.setSliceSpace(3f);
                    dataSet2.setSelectionShift(5f);
                    dataSet2.setColors(col);


                    //PieData data = new PieData();


                    data2.setValueTextSize(20f);
                    data2.setValueTextColor(Color.rgb(0,102,51));

                    pieChart2.setData(data2);
                    pieChart2.invalidate();

                }
            });

        }
    }




}

