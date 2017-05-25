package com.example.panqian.myapplication;

import android.os.Bundle;
import android.os.Process;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button start;
    TextView displayView;
    RadioGroup queueGroup,policyGroup;
    ThreadPoolExecutor executor;
    BlockingQueue<Runnable> queue;
    RejectedExecutionHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
    }
    private void findViews(){
        start=(Button)findViewById(R.id.start);
        displayView=(TextView)findViewById(R.id.display);
        queueGroup=(RadioGroup)findViewById(R.id.queue);
        policyGroup=(RadioGroup)findViewById(R.id.policy);
        queueGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton=(RadioButton)group.findViewById(checkedId);
                Toast.makeText(MainActivity.this,radioButton.getText(),Toast.LENGTH_SHORT).show();
                switch (checkedId){
                    case R.id.queue_first:
                        queue=new LinkedBlockingQueue<Runnable>();
                        break;
                    case R.id.queue_second:
                        queue=new ArrayBlockingQueue<Runnable>(5);
                        break;
                    case R.id.queue_third:
                        queue=new PriorityBlockingQueue<Runnable>();
                        break;
                    case R.id.queue_fourth:
                        queue=new SynchronousQueue<Runnable>();
                        break;
                }
            }
        });
        policyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton=(RadioButton)group.findViewById(checkedId);
                Toast.makeText(MainActivity.this,radioButton.getText(),Toast.LENGTH_SHORT).show();
                switch (checkedId){
                    case R.id.policy_first:
                        handler=new ThreadPoolExecutor.AbortPolicy();
                        break;
                    case R.id.policy_second:
                        handler=new ThreadPoolExecutor.CallerRunsPolicy();
                        break;
                    case R.id.policy_third:
                        handler=new ThreadPoolExecutor.DiscardOldestPolicy();
                        break;
                    case R.id.policy_fourth:
                        handler=new ThreadPoolExecutor.DiscardPolicy();
                        break;
                }
            }
        });
    }

    // click start button
    public void startToExecute(View view){
        if (null!=executor){
            executor.shutdown();
        }
        if (-1==queueGroup.getCheckedRadioButtonId()||-1==policyGroup.getCheckedRadioButtonId()){
            Toast.makeText(this,"请先选择",Toast.LENGTH_LONG).show();
            return;
        }
        displayView.setText("");
        executor=new ThreadPoolExecutor(2,4,5, TimeUnit.SECONDS,queue,handler);
        for (int i=0;i<15;++i){
            displayView.append("当前线程池大小[+" + executor.getPoolSize() + "],当前队列大小[" + queue.size() + "] \n");
            try{
                executor.execute(new MyThread("Thread "+(i+1)));
            }catch (RejectedExecutionException e){
                e.printStackTrace();
            }

        }
        executor.shutdown();
    }

     class MyThread implements Runnable {
        private String name;

        public MyThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            // 做点事情
            try {
                Log.d("pan",name+": ["+ Process.myTid()+"]  begin to run  ");
                Thread.sleep(3000);
                Log.d("pan",name+": ["+ Process.myTid()+"]  end to run  ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
