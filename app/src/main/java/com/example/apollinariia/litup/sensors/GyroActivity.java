package com.example.apollinariia.litup.sensors;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.AngularVelocity;
import com.mbientlab.metawear.module.GyroBmi160;
import com.mbientlab.metawear.module.GyroBmi160.*;


import com.mbientlab.metawear.MetaWearBoard;

import bolts.Continuation;
import bolts.Task;

public class GyroActivity extends AppCompatActivity {

    private static final String GYRO_STREAM_KEY = "gyro_stream";
    private GyroBmi160 gyroBmi160;

    private MetaWearBoard board;
    private static Data d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gyroBmi160 = board.getModule(GyroBmi160.class);

        // set the data rat to 50Hz and the
        // data range to +/- 2000 degrees/s
        gyroBmi160.configure()
                .odr(OutputDataRate.ODR_50_HZ)
                .range(Range.FSR_2000)
                .commit();

        gyroBmi160.angularVelocity().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.stream(new Subscriber() {
                    @Override
                    public void apply(Data data, Object... env) {
                        Log.i("GyroActivity", data.value(AngularVelocity.class).toString());
                        d = data;
                    }
                });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                gyroBmi160.angularVelocity();
                gyroBmi160.start();
                Log.i("GyroActivity", "Normally the gyro has started...");
                return null;
            }
        });

    }

    public static String getGyroData() {
        if(null != d)
            return d.value(AngularVelocity.class).toString();
        Log.i("GyroActivity", "No data from gyro");
        return "N/A";
    }
}
