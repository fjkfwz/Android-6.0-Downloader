package com.makeblaze.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.makeblaze.myapplication.Services.DownloadService;
import com.makeblaze.myapplication.entities.FileInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int EXTERNAL_STORAGE_REQ_CODE = 10;
    private List<FileInfo> filelist;
    private FilelistAdapter mAdapter;
    public void requestPermission() {
        //判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "please give me the permission", Toast.LENGTH_SHORT).show();
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_REQ_CODE);
            }
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadService.Action_UPDATA.equals(intent.getAction())) {
                Log.i("Receiver Updata Message","Receiver Updata Message");
                int finish = intent.getIntExtra("finished", 0);
                Log.i("finish",finish+"");
                int id =intent.getIntExtra("id",0);
                Log.i("id",id+"");
//                progressBar.setProgress(finish);
                mAdapter.updateprogress(id, finish);
            }else if (DownloadService.Action_FINISH.equals(intent.getAction())) {
                Log.i("Receiver Finish Message","Receiver Finish Message");
                int id = intent.getIntExtra("id", 0);
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileinfo");
                mAdapter.updateprogress(id,100);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initFileInfo();
        initView();
        initBroadcastReceiver();
    }

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.Action_UPDATA);
        intentFilter.addAction(DownloadService.Action_FINISH);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQ_CODE: {
                // 如果请求被拒绝，那么通常grantResults数组为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请成功，进行相应操作
                    Toast.makeText(MainActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    //申请失败，可以继续向用户解释。
                    Toast.makeText(MainActivity.this, "申请失败", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void initFileInfo() {
        filelist = new ArrayList<FileInfo>();
        FileInfo fileInfo1 = new FileInfo(1, "http://www.imooc.com/mobile/mukewang.apk", "imooc.apk", 0, 0);
        FileInfo fileInfo2 = new FileInfo(2, "http://www.imooc.com/mobile/mukewang.apk", "imooc.apk", 0, 0);
        FileInfo fileInfo3 = new FileInfo(3, "http://www.imooc.com/mobile/mukewang.apk", "imooc.apk", 0, 0);
        FileInfo fileInfo4 = new FileInfo(4, "http://www.imooc.com/mobile/mukewang.apk", "imooc.apk", 0, 0);
        FileInfo fileInfo5 = new FileInfo(5, "http://www.imooc.com/mobile/mukewang.apk", "imooc.apk", 0, 0);
        filelist.add(fileInfo1);
        filelist.add(fileInfo2);
        filelist.add(fileInfo3);
        filelist.add(fileInfo4);
        filelist.add(fileInfo5);
    }

    private void initView() {
        ListView listView = (ListView) findViewById(R.id.listview);
        mAdapter = new FilelistAdapter(this, filelist);
        listView.setAdapter(mAdapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        View view = item.getActionView();
        if (id == R.id.nav_camera) {

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
