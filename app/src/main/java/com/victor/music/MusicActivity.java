package com.victor.music;import android.annotation.TargetApi;import android.content.Intent;import android.graphics.Color;import android.media.MediaScannerConnection;import android.net.Uri;import android.os.Build;import android.os.Bundle;import android.os.Environment;import android.os.Handler;import android.os.Message;import android.support.design.widget.TabLayout;import android.support.v4.app.Fragment;import android.support.v4.view.ViewPager;import android.support.v7.app.AppCompatActivity;import android.view.Menu;import android.view.MenuItem;import android.support.design.widget.NavigationView;import android.support.v4.view.GravityCompat;import android.support.v4.widget.DrawerLayout;import android.support.v7.app.ActionBarDrawerToggle;import android.support.v7.widget.Toolbar;import android.view.KeyEvent;import android.view.View;import android.view.Window;import android.widget.ImageView;import android.widget.TextView;import android.widget.Toast;import com.bumptech.glide.Glide;import com.github.clans.fab.FloatingActionButton;import com.github.clans.fab.FloatingActionMenu;import com.victor.adapter.ViewPagerAdapter;import com.victor.data.MusicData;import com.victor.fragments.MusicDownLoadFrag;import com.victor.fragments.MusicLocalFrag;import com.victor.fragments.MusicOnlineFrag;import com.victor.module.DataObservable;import com.victor.module.HttpRequestModule;import com.victor.module.PlayModule;import com.victor.util.Constant;import com.victor.util.FileUtil;import com.victor.util.SharePreferencesUtil;import com.victor.view.MovingTextView;import java.io.File;import java.util.ArrayList;import java.util.List;import java.util.Observable;import java.util.Observer;public class MusicActivity extends AppCompatActivity implements View.OnClickListener,        NavigationView.OnNavigationItemSelectedListener,Observer {    private String TAG = "MusicActivity";    private Toolbar toolbar;    private NavigationView navigationView;    private DrawerLayout drawer;    private ActionBarDrawerToggle toggle;    private FloatingActionMenu mFamMenu;    private FloatingActionButton mFabScan,mFabAdd;    private TabLayout mTabLayout;    private ViewPager mViewPager;    private MovingTextView mMtvTitle;    private TextView mTvArtist;    private ImageView mIvPlay,mIvNext;    private ViewPagerAdapter mViewPagerAdapter;    private String[] pagerTitles = new String[]{"我的粤乐","在线粤乐","下载"};    private List<Fragment> fragmentList = new ArrayList<>();    private MusicLocalFrag musicLocalFrag;    private MusicOnlineFrag musicListFrag;    private MusicDownLoadFrag musicDownLoadFrag;    private MusicData musicData;    private int playStatus = Constant.PlayStatus.PAUSE;    Handler mHandler = new Handler(){        @Override        public void handleMessage(Message msg) {            switch (msg.what) {                case Constant.Action.SHOW_CURRENT_PLAY:                    showBottomPlay();                    break;                case Constant.Action.DOWNLOAD_OVER:                    Toast.makeText(getApplicationContext(),"下载完毕！",Toast.LENGTH_SHORT).show();                    break;            }        }    };    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_music);        initialize();        initData();    }    private void initialize () {        DataObservable.getInstance().addObserver(this);        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());        toolbar = (Toolbar) findViewById(R.id.toolbar);        setSupportActionBar(toolbar);        setTitle("从你的全世界路过");        mFamMenu = (FloatingActionMenu) findViewById(R.id.fam_menu);        mFamMenu.setClosedOnTouchOutside(true);        mFabScan = (FloatingActionButton) findViewById(R.id.fab_scan);        mFabAdd = (FloatingActionButton) findViewById(R.id.fab_add);        mTabLayout = (TabLayout) findViewById(R.id.tabs);        mViewPager = (ViewPager) findViewById(R.id.view_pager);        mMtvTitle = (MovingTextView) findViewById(R.id.mtv_title);        mTvArtist = (TextView) findViewById(R.id.tv_artist);        mIvPlay = (ImageView) findViewById(R.id.iv_play);        mIvNext = (ImageView) findViewById(R.id.iv_next);        musicLocalFrag = new MusicLocalFrag();        musicListFrag = new MusicOnlineFrag();        musicDownLoadFrag = new MusicDownLoadFrag();        fragmentList.add(musicLocalFrag);        fragmentList.add(musicListFrag);        fragmentList.add(musicDownLoadFrag);        mViewPagerAdapter.setFragTitles(pagerTitles);        mViewPagerAdapter.setFrags(fragmentList);        mViewPager.setAdapter(mViewPagerAdapter);        mTabLayout.setupWithViewPager(mViewPager);        mFabScan.setOnClickListener(this);        mFabAdd.setOnClickListener(this);        mIvPlay.setOnClickListener(this);        mIvNext.setOnClickListener(this);        // 左上角 Menu 按钮        drawer = (DrawerLayout) findViewById(R.id.dl_menu);        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,                R.string.navigation_drawer_open, R.string.navigation_drawer_close);        drawer.setDrawerListener(toggle);        toggle.syncState();        // 菜单        navigationView = (NavigationView) findViewById(R.id.nav_view);        navigationView.setNavigationItemSelectedListener(this);    }    private void initData () {        String[] types = getResources().getStringArray(R.array.online_music_list_type);        String url = String.format(Constant.BASE_URL,Constant.METHOD_MUSIC_LIST,types[4],Constant.PAGE_SIZE);        HttpRequestModule.request(TAG, url, new HttpRequestModule.OnCompleteListener() {            @Override            public void onComplete(int status, Object result) {            }        });    }    private void showBottomPlay () {        if (musicData == null) {            Toast.makeText(getApplicationContext(),"没有音乐",Toast.LENGTH_SHORT).show();            return;        }        mMtvTitle.setText(musicData.title);        mTvArtist.setText(musicData.artist);        if (playStatus == Constant.PlayStatus.PLAY) {            playStatus = Constant.PlayStatus.PLAY;            mIvPlay.setImageResource(R.mipmap.ic_play_btn_pause);        } else {            playStatus = Constant.PlayStatus.PAUSE;            mIvPlay.setImageResource(R.mipmap.ic_play_btn_play);        }    }    @Override    public boolean onCreateOptionsMenu(Menu menu) {        // Inflate the menu; this adds items to the action bar if it is present.        getMenuInflater().inflate(R.menu.menu_music, menu);        return true;    }    @Override    public boolean onOptionsItemSelected(MenuItem item) {        // Handle action bar item clicks here. The action bar will        // automatically handle clicks on the Home/Up button, so long        // as you specify a parent activity in AndroidManifest.xml.        //noinspection SimplifiableIfStatement        switch (item.getItemId()) {            case R.id.action_search:                startActivity(new Intent(this,SearchMusicActivity.class));                return true;            case R.id.action_share:                Toast.makeText(getApplicationContext(),"you touch favorite!",Toast.LENGTH_SHORT).show();//                invalidateOptionsMenu();//如果修改图标则调用这个重新初始化                return true;            default:                return super.onOptionsItemSelected(item);        }    }    @Override    public boolean onPrepareOptionsMenu(Menu menu) {//        MenuItem menuItem = menu.findItem(R.id.action_favorite);//        menuItem.setIcon(iconFavorite[isFavourite ? 1 : 0]);//        isFavourite = !isFavourite;        return super.onPrepareOptionsMenu(menu);    }    @Override    public void onClick(View v) {        switch (v.getId()) {            case R.id.fab_scan:                mFamMenu.close(true);                Toast.makeText(getApplicationContext(),"you touch scanner!",Toast.LENGTH_SHORT).show();                break;            case R.id.fab_add:                mFamMenu.close(true);                Toast.makeText(getApplicationContext(),"you touch add!",Toast.LENGTH_SHORT).show();                break;            case R.id.iv_play:                if (playStatus == Constant.PlayStatus.PLAY) {                    PlayModule.getInstance(this).pause();                    playStatus = Constant.PlayStatus.PAUSE;                    showBottomPlay();                } else {                    playStatus = Constant.PlayStatus.PLAY;                    int current = SharePreferencesUtil.getInt(getApplicationContext(),Constant.CURRENT_POSITION_KEY);                    PlayModule.getInstance(this).play(current,Constant.DateType.ONLINE);                    showBottomPlay();                }                break;            case R.id.iv_next:                PlayModule.getInstance(this).next();                break;        }    }    @Override    public boolean onKeyDown(int keyCode, KeyEvent event) {        if (keyCode == KeyEvent.KEYCODE_BACK) {            finish();            return true;        } else {            return super.onKeyDown(keyCode, event);        }    }    @Override    public boolean onNavigationItemSelected(MenuItem item) {        switch (item.getItemId()) {            case R.id.nav_clear:                FileUtil.delete(new File(FileUtil.getMusicDir()));                Toast.makeText(getApplicationContext(),"缓存已清除",Toast.LENGTH_SHORT).show();                DataObservable.getInstance().setData(Constant.Action.CLEAR_ALL_MUSIC);                break;            case R.id.nav_search:                break;            case R.id.nav_add:                break;            case R.id.nav_about:                break;        }        drawer.closeDrawer(GravityCompat.START);        return true;    }    @Override    public void update(Observable observable, Object data) {        if (data instanceof MusicData) {            musicData = (MusicData) data;            int action = musicData.action;            switch (action) {                case Constant.Action.SHOW_CURRENT_PLAY:                    playStatus = Constant.PlayStatus.PLAY;                    mHandler.sendEmptyMessage(Constant.Action.SHOW_CURRENT_PLAY);                    break;            }        } else if (data instanceof Integer) {            int action = (int) data;            if (action == Constant.Action.DOWNLOAD_OVER) {                mHandler.sendEmptyMessage(Constant.Action.DOWNLOAD_OVER);            }        }    }    @TargetApi(Build.VERSION_CODES.LOLLIPOP)    @Override    protected void onResume() {        super.onResume();        Window window = getWindow();        window.setStatusBarColor(getResources().getColor(R.color.colorLimePrimaryDark));    }    @Override    protected void onDestroy() {        DataObservable.getInstance().deleteObserver(this);        super.onDestroy();    }}