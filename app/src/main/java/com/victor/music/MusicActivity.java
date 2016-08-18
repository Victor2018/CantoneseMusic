package com.victor.music;import android.annotation.TargetApi;import android.content.DialogInterface;import android.content.Intent;import android.graphics.Point;import android.net.Uri;import android.os.Build;import android.os.Bundle;import android.os.Environment;import android.os.Handler;import android.os.Message;import android.support.design.widget.TabLayout;import android.support.v4.app.Fragment;import android.support.v4.app.FragmentTransaction;import android.support.v4.view.ViewPager;import android.support.v7.app.AlertDialog;import android.support.v7.app.AppCompatActivity;import android.view.Menu;import android.view.MenuItem;import android.support.design.widget.NavigationView;import android.support.v4.view.GravityCompat;import android.support.v4.widget.DrawerLayout;import android.support.v7.app.ActionBarDrawerToggle;import android.support.v7.widget.Toolbar;import android.view.KeyEvent;import android.view.View;import android.view.ViewGroup;import android.view.Window;import android.view.animation.Animation;import android.view.animation.AnimationUtils;import android.view.animation.LinearInterpolator;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.TextView;import android.widget.Toast;import com.bumptech.glide.Glide;import com.github.clans.fab.FloatingActionButton;import com.github.clans.fab.FloatingActionMenu;import com.victor.adapter.ViewPagerAdapter;import com.victor.dao.DbDao;import com.victor.data.MusicData;import com.victor.fragments.MusicDownLoadFrag;import com.victor.fragments.MusicLocalFrag;import com.victor.fragments.MusicOnlineFrag;import com.victor.module.DataObservable;import com.victor.module.HttpRequestModule;import com.victor.module.PlayModule;import com.victor.util.ChineseUtil;import com.victor.util.Constant;import com.victor.util.FileUtil;import com.victor.util.MusicUtil;import com.victor.util.SharePreferencesUtil;import com.victor.view.CircleImageView;import com.victor.view.MovingTextView;import com.victor.view.MuiscHooldeView;import java.io.File;import java.util.ArrayList;import java.util.List;import java.util.Observable;import java.util.Observer;public class MusicActivity extends AppCompatActivity implements View.OnClickListener,        NavigationView.OnNavigationItemSelectedListener,Observer {    private String TAG = "MusicActivity";    private Toolbar toolbar;    private NavigationView navigationView;    private DrawerLayout drawer;    private ActionBarDrawerToggle toggle;    private FloatingActionMenu mFamMenu;    private FloatingActionButton mFabScan,mFabAdd;    private TabLayout mTabLayout;    private ViewPager mViewPager;    private CircleImageView mCivAlbum;    private MovingTextView mMtvTitle;    private TextView mTvArtist;    private ImageView mIvPlay,mIvNext;    private LinearLayout mLayoutPlayBottom;    private ViewPagerAdapter mViewPagerAdapter;    private String[] pagerTitles = new String[]{"我的粤乐","在线粤乐","下载"};    private List<Fragment> fragmentList = new ArrayList<>();    private MusicLocalFrag musicLocalFrag;    private MusicOnlineFrag musicListFrag;    private MusicDownLoadFrag musicDownLoadFrag;    private MusicData musicData;    private Animation albumRotateAnim;//音乐光盘旋转动画    private int playStatus = Constant.PlayStatus.PAUSE;    private long exitTime = 0;    Handler mHandler = new Handler(){        @Override        public void handleMessage(Message msg) {            switch (msg.what) {                case Constant.PlayStatus.PLAY:                    showBottomPlay();                    break;                case Constant.Action.DOWNLOAD_OVER:                    Toast.makeText(getApplicationContext(),"下载完毕！",Toast.LENGTH_SHORT).show();                    break;                case Constant.Msg.SHOW_PLAY_HOOLDE:                    showPlayHoolde((View) msg.obj);                    break;                case Constant.Msg.EXIT_APP:                    Toast.makeText(getApplicationContext(), "后台为你播放音乐", Toast.LENGTH_SHORT).show();                    finish();                    break;            }        }    };    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_music);        initialize();        initData();    }    private void initialize () {        DataObservable.getInstance().addObserver(this);        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());        toolbar = (Toolbar) findViewById(R.id.toolbar);        setSupportActionBar(toolbar);        setTitle("从你的全世界路过");        mFamMenu = (FloatingActionMenu) findViewById(R.id.fam_menu);        mFamMenu.setClosedOnTouchOutside(true);        mFabScan = (FloatingActionButton) findViewById(R.id.fab_scan);        mFabAdd = (FloatingActionButton) findViewById(R.id.fab_add);        mTabLayout = (TabLayout) findViewById(R.id.tabs);        mViewPager = (ViewPager) findViewById(R.id.view_pager);        mCivAlbum = (CircleImageView) findViewById(R.id.civ_album);        mMtvTitle = (MovingTextView) findViewById(R.id.mtv_title);        mTvArtist = (TextView) findViewById(R.id.tv_artist);        mIvPlay = (ImageView) findViewById(R.id.iv_play);        mIvNext = (ImageView) findViewById(R.id.iv_next);        mLayoutPlayBottom = (LinearLayout) findViewById(R.id.l_play_bottom);        musicLocalFrag = new MusicLocalFrag();        musicListFrag = new MusicOnlineFrag();        musicDownLoadFrag = new MusicDownLoadFrag();        fragmentList.add(musicLocalFrag);        fragmentList.add(musicListFrag);        fragmentList.add(musicDownLoadFrag);        mViewPagerAdapter.setFragTitles(pagerTitles);        mViewPagerAdapter.setFrags(fragmentList);        mViewPager.setAdapter(mViewPagerAdapter);        mTabLayout.setupWithViewPager(mViewPager);        mFabScan.setOnClickListener(this);        mFabAdd.setOnClickListener(this);        mIvPlay.setOnClickListener(this);        mIvNext.setOnClickListener(this);        mLayoutPlayBottom.setOnClickListener(this);        // 左上角 Menu 按钮        drawer = (DrawerLayout) findViewById(R.id.dl_menu);        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,                R.string.navigation_drawer_open, R.string.navigation_drawer_close);        drawer.setDrawerListener(toggle);        toggle.syncState();        // 菜单        navigationView = (NavigationView) findViewById(R.id.nav_view);        navigationView.setNavigationItemSelectedListener(this);        //初始化光盘旋转动画        albumRotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);        albumRotateAnim.setInterpolator(new LinearInterpolator());//重复播放不停顿        albumRotateAnim.setFillAfter(true);//停在最后    }    private void initData () {        if (PlayModule.getInstance(this).isPlaying()) {            playStatus = Constant.PlayStatus.PLAY;        } else {            playStatus = Constant.PlayStatus.PAUSE;        }    }    private void showBottomPlay () {        if (musicData == null) {            Toast.makeText(getApplicationContext(),"没有音乐",Toast.LENGTH_SHORT).show();            return;        }        mMtvTitle.setText(musicData.title);        mTvArtist.setText(musicData.artist);        if (musicData.artist.equals("<unknown>")) {            mTvArtist.setText("未知艺术家");        }        if (playStatus == Constant.PlayStatus.PLAY) {            mCivAlbum.startAnimation(albumRotateAnim);            mIvPlay.setImageResource(R.drawable.btn_pause_selector);        } else {            mCivAlbum.clearAnimation();            mIvPlay.setImageResource(R.drawable.btn_play_selector);        }        Glide.with(getApplicationContext()).load(musicData.pic_small).centerCrop().error(R.mipmap.default_cover).into(mCivAlbum);    }    @Override    public boolean onCreateOptionsMenu(Menu menu) {        // Inflate the menu; this adds items to the action bar if it is present.        getMenuInflater().inflate(R.menu.menu_music, menu);        return true;    }    @Override    public boolean onOptionsItemSelected(MenuItem item) {        // Handle action bar item clicks here. The action bar will        // automatically handle clicks on the Home/Up button, so long        // as you specify a parent activity in AndroidManifest.xml.        //noinspection SimplifiableIfStatement        switch (item.getItemId()) {            case R.id.action_search:                startActivity(new Intent(this,SearchMusicActivity.class));                return true;            case R.id.action_share:                Toast.makeText(getApplicationContext(),"you touch favorite!",Toast.LENGTH_SHORT).show();//                invalidateOptionsMenu();//如果修改图标则调用这个重新初始化                return true;            default:                return super.onOptionsItemSelected(item);        }    }    @Override    public boolean onPrepareOptionsMenu(Menu menu) {//        MenuItem menuItem = menu.findItem(R.id.action_favorite);//        menuItem.setIcon(iconFavorite[isFavourite ? 1 : 0]);//        isFavourite = !isFavourite;        return super.onPrepareOptionsMenu(menu);    }    @Override    public void onClick(View v) {        switch (v.getId()) {            case R.id.fab_scan:                mFamMenu.close(true);                Toast.makeText(getApplicationContext(),"you touch scanner!",Toast.LENGTH_SHORT).show();                break;            case R.id.fab_add:                mFamMenu.close(true);                Toast.makeText(getApplicationContext(),"you touch add!",Toast.LENGTH_SHORT).show();                break;            case R.id.iv_play:                if (playStatus == Constant.PlayStatus.PLAY) {                    PlayModule.getInstance(this).pause();                    playStatus = Constant.PlayStatus.PAUSE;                    showBottomPlay();                } else {                    playStatus = Constant.PlayStatus.PLAY;                    if ( PlayModule.getInstance(this).isPlayOnline()) {                        List<MusicData> onlineList = DbDao.getInstance(this).queryMusic(Constant.TB.MUSIC_ONLINE);                        if (onlineList != null && onlineList.size() > 0) {                            PlayModule.getInstance(this).playOnline(onlineList.get(0));                        }                    } else {                        int current = SharePreferencesUtil.getInt(this,Constant.CURRENT_POSITION_KEY);                        PlayModule.getInstance(this).play(current);                    }                    showBottomPlay();                }                break;            case R.id.iv_next:                PlayModule.getInstance(this).next();                break;            case R.id.l_play_bottom:                startActivity(new Intent(MusicActivity.this,MusicPlayActivity.class));                break;        }    }    @Override    public boolean onKeyDown(int keyCode, KeyEvent event) {        if (keyCode == KeyEvent.KEYCODE_BACK) {            exitApp();            return false;        }        return super.onKeyDown(keyCode, event);    }    @Override    public boolean onNavigationItemSelected(MenuItem item) {        switch (item.getItemId()) {            case R.id.nav_search:                startActivity(new Intent(this,SearchMusicActivity.class));                break;            case R.id.nav_download:                boolean downloadSidePlay = SharePreferencesUtil.getBoolean(getApplicationContext(),Constant.DOWNLOAD_SIDE_PLAY_KEY);                if (downloadSidePlay) {                    SharePreferencesUtil.putBoolean(getApplicationContext(),Constant.DOWNLOAD_SIDE_PLAY_KEY,false);                    Toast.makeText(getApplicationContext(),"已关闭边播边下功能！",Toast.LENGTH_SHORT).show();                } else {                    SharePreferencesUtil.putBoolean(getApplicationContext(),Constant.DOWNLOAD_SIDE_PLAY_KEY,true);                    Toast.makeText(getApplicationContext(),"已打开边播边下功能！",Toast.LENGTH_SHORT).show();                }                break;            case R.id.nav_update:                Toast.makeText(getApplicationContext(),"已打开自动更新功能！",Toast.LENGTH_SHORT).show();                break;            case R.id.nav_clear:                clearMusic();                break;            case R.id.nav_about:                Intent intent = new Intent(MusicActivity.this,HightAccuracyActivity.class);                startActivity(intent);                break;        }        drawer.closeDrawer(GravityCompat.START);        return true;    }    @Override    public void update(Observable observable, Object data) {        if (data instanceof MusicData) {            MusicData info = (MusicData) data;            int action = info.action;            if (action == Constant.Action.SHOW_CURRENT_PLAY) {                musicData = info;                playStatus = musicData.playStatus;                mHandler.sendEmptyMessage(Constant.PlayStatus.PLAY);            }        } else if (data instanceof Integer) {            int action = (int) data;            if (action == Constant.Action.DOWNLOAD_OVER) {                mHandler.sendEmptyMessage(Constant.Action.DOWNLOAD_OVER);            }        } else if (data instanceof View) {            Message msg = mHandler.obtainMessage(Constant.Msg.SHOW_PLAY_HOOLDE,data);            msg.sendToTarget();        }    }    private void clearMusic() {        AlertDialog.Builder dialog = new AlertDialog.Builder(this);        dialog.setTitle("清空缓存将会清除所有歌曲及歌词");        dialog.setMessage("确定要清空缓存吗？");        dialog.setPositiveButton("清空", new DialogInterface.OnClickListener() {            @Override            public void onClick(DialogInterface dialog, int which) {                FileUtil.delete(new File(FileUtil.getMusicDir()));                FileUtil.delete(new File(FileUtil.getLrcDir()));                MusicUtil.deleteMusic(getApplicationContext(),Constant.Action.CLEAR_ALL_MUSIC);                // 刷新媒体库                Uri uri =  Uri.parse("file://"+ Environment.getExternalStorageDirectory());                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));                DataObservable.getInstance().setData(Constant.Action.CLEAR_ALL_MUSIC);                Toast.makeText(getApplicationContext(),"缓存已清除",Toast.LENGTH_SHORT).show();            }        });        dialog.setNegativeButton("取消", null);        dialog.show();    }    private void showPlayHoolde (View view) {        MuiscHooldeView muiscHooldeView = new MuiscHooldeView(this);        int position[] = new int[2];        view.getLocationInWindow(position);        muiscHooldeView.setStartPosition(new Point(position[0], position[1]));        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();        rootView.addView(muiscHooldeView);        int endPosition[] = new int[2];        mCivAlbum.getLocationInWindow(endPosition);        muiscHooldeView.setEndPosition(new Point(endPosition[0] + mCivAlbum.getWidth() / 3, endPosition[1] + mCivAlbum.getHeight() / 3));        muiscHooldeView.startBeizerAnimation();        mCivAlbum.startAnimation(albumRotateAnim);    }    @TargetApi(Build.VERSION_CODES.LOLLIPOP)    @Override    protected void onResume() {        super.onResume();        Window window = getWindow();        window.setStatusBarColor(getResources().getColor(R.color.colorLimePrimaryDark));    }    @Override    protected void onDestroy() {        DataObservable.getInstance().deleteObserver(this);        super.onDestroy();    }    private void exitApp () {        if ((System.currentTimeMillis() - exitTime) > 2000) {            exitTime = System.currentTimeMillis();            mHandler.sendEmptyMessageDelayed(Constant.Msg.EXIT_APP,500);            return;        }        mHandler.removeMessages(Constant.Msg.EXIT_APP);        PlayModule.getInstance(this).pause();        Toast.makeText(this, "音乐已暂停播放！", Toast.LENGTH_SHORT).show();        finish();    }}