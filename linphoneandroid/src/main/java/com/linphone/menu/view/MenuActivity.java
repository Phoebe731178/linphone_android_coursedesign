package com.linphone.menu.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.linphone.R;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.linphone.about.view.AboutActivity;
import com.linphone.addressbook.view.AddressBookImpl;
import com.linphone.call.view.CallActivity;
import com.linphone.call.view.Dial;
import com.linphone.chat.single.view.ChatActivity;

import com.linphone.chat.view.ChatRecordActivity;
import com.linphone.login.view.LoginPhoneActivity;
import com.linphone.menu.view.animation.GuillotineAnimation;


public class MenuActivity extends Activity implements OnClickListener{

    private static final long RIPPLE_DURATION = 250;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.content_hamburger)
    View contentHamburger;
    // 底部菜单3个Linearlayout
    private LinearLayout ll_dial;
    private LinearLayout ll_address;
    private LinearLayout ll_message;

    private LinearLayout about_group;
    private LinearLayout assistant_group;

    private Intent dialIntent;
    private Intent addressIntent;
    private Intent messageIntent;



    // 底部菜单3个ImageView
    private ImageView iv_dial;
    private ImageView iv_address;
    private ImageView iv_message;

    // 底部菜单3个菜单标题
    private TextView tv_dial;
    private TextView tv_address;
    private TextView tv_message;



    // 中间内容区域
    //private ViewPager viewPager;

    // ViewPager适配器ContentAdapter
    private ContentAdapter adapter;

    //private List<View> views;

    //LocalActivityManager groupActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity_main);


        //groupActivity = new LocalActivityManager(this, true);
        //groupActivity.dispatchCreate(savedInstanceState);

        // 初始化控件
        initView();
        // 初始化底部按钮事件
        restartBotton();
        initEvent();

/*
        about_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this,
                        AboutActivity.class));
            }
        });
        assistant_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this,
                        LoginPhoneActivity.class));
            }
        });
        
 */

        //顶部导航栏
        if (toolbar != null) {
          // setSupportActionBar(toolbar);
        toolbar.setTitle(null);}
        ButterKnife.bind(this);


        View guillotineMenu = LayoutInflater.from(MenuActivity.this).inflate(R.layout.menu_top_guillotine, null);
        root.addView(guillotineMenu);
        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .build();

    }

    private void initEvent() {
        // 设置按钮监听
        ll_dial.setOnClickListener(this);
        ll_address.setOnClickListener(this);
        ll_message.setOnClickListener(this);
        about_group.setOnClickListener(this);
        assistant_group.setOnClickListener(this);


        //设置ViewPager滑动监听
        //viewPager.setOnPageChangeListener(this);
    }

    private void initView() {
        View guillotineMenu = LayoutInflater.from(MenuActivity.this).inflate(R.layout.menu_top_guillotine, null);
       // root.addView(guillotineMenu);
        this.about_group = guillotineMenu.findViewById(R.id.about_group);
        this.assistant_group = guillotineMenu.findViewById(R.id.assistant_group);

        // 底部菜单3个Linearlayout
        this.ll_dial = (LinearLayout) findViewById(R.id.ll_dial);
        this.ll_address = (LinearLayout) findViewById(R.id.ll_address);
        this.ll_message = (LinearLayout) findViewById(R.id.ll_message);

        // 底部菜单3个ImageView
        this.iv_dial = (ImageView) findViewById(R.id.iv_dial);
        this.iv_address = (ImageView) findViewById(R.id.iv_address);
        this.iv_message = (ImageView) findViewById(R.id.iv_message);

        // 底部菜单3个菜单标题
        this.tv_dial = (TextView) findViewById(R.id.tv_dial);
        this.tv_address = (TextView) findViewById(R.id.tv_address);
        this.tv_message = (TextView) findViewById(R.id.tv_message);

        this.dialIntent = new Intent(MenuActivity.this, Dial.class);
        this.addressIntent = new Intent(MenuActivity.this, AddressBookImpl.class);
        this.messageIntent = new Intent(MenuActivity.this, ChatRecordActivity.class);

        // 中间内容区域ViewPager
        //this.viewPager = (ViewPager) findViewById(R.id.vp_content);


        // 适配器
        //View page_01 = View.inflate(MenuActivity.this, R.layout.activity_dial_main, null);
        //View page_02 = View.inflate(MenuActivity.this, R.layout.activity_address_listview, null);
        //View page_03 = View.inflate(MenuActivity.this, R.layout.activity_message_listview, null);
        //View page_about = View.inflate(MenuActivity.this, R.layout.activity_about, null);

        //views = new ArrayList<View>();
        //views.add(getView("1", dialIntent));
        //views.add(getView("2",addressIntent));
        //views.add(getView("3", messageIntent));
        //views.add(page_about);


        //this.adapter = new ContentAdapter(views);
        //viewPager.setAdapter(adapter);

    }
    //private View getView(String id, Intent intent){
       // return groupActivity.startActivity(id, intent).getDecorView();
    //}

    @Override
    public void onClick(View v) {
        // 在每次点击后将所有的底部按钮(ImageView,TextView)颜色改为灰色，然后根据点击着色
        restartBotton();
        View guillotineMenu = LayoutInflater.from(MenuActivity.this).inflate(R.layout.menu_top_guillotine, null);
        //Animation hang_fall = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.hang_fall );
        //hang_fall.setAnimationListener(new Animation.AnimationListener() {
           // @Override
           // public void onAnimationEnd(Animation animation) {

                // ImageView和TetxView置为绿色，页面随之跳转
                switch (v.getId()) {
                    case R.id.ll_dial:
                        tv_dial.setTextColor(0xff1B940A);
                        //addFlagsToIntent(dialIntent);
                        startActivity(dialIntent);

                        break;
                    case R.id.ll_address:
                        //iv_address.setImageResource(R.drawable.tab_address_pressed);
                        tv_address.setTextColor(0xff1B940A);
                        //addFlagsToIntent(addressIntent);
                        startActivity(addressIntent);

                        break;
                    case R.id.ll_message:
                        //iv_message.setImageResource(R.drawable.tab_find_frd_pressed);
                        tv_message.setTextColor(0xff1B940A);
                        //addFlagsToIntent(messageIntent);
                        startActivity(messageIntent);

                        break;
                    case R.id.about_group:
                        //View guillotineMenu = LayoutInflater.from(MenuActivity.this).inflate(R.layout.menu_top_guillotine, null);
                        guillotineMenu.setVisibility(View.GONE);
                        startActivity(new Intent(MenuActivity.this,
                                AboutActivity.class));
                        break;
                    case R.id.assistant_group:

                        guillotineMenu.setVisibility(View.GONE);
                        startActivity(new Intent(MenuActivity.this,
                                LoginPhoneActivity.class));
                        break;
                    //case R.id.about_group:
                    //iv_message.setImageResource(R.drawable.tab_find_frd_pressed);
                    //viewPager.setCurrentItem(3);
                    //View guillotineMenu = LayoutInflater.from(MenuActivity.this).inflate(R.layout.menu_top_guillotine, null);
                    //root.addView(guillotineMenu);
                    //new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), about_group)
                    //  .setStartDelay(RIPPLE_DURATION)
                    //  .setActionBarViewForAnimation(toolbar)
                    //  .build();
                    //break;
                }
            }



    /*
    private void showView(final View v) {
        if (mView == null) {
            mView = new LinearLayout(this);
            mView.setBackgroundColor(Color.BLUE);
            addContentView(mView, new ViewGroup.LayoutParams(
                    FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
             }

            AnimationSet animSet = new AnimationSet(true);
               ScaleAnimation sa = new ScaleAnimation((float) v.getWidth()
                            / ((View) v.getParent()).getWidth(), 1.0f,
                             (float) v.getHeight() / ((View) v.getParent()).getHeight(),
                               1.0f, v.getX() + v.getWidth() / 2, v.getY() + v.getHeight() / 2);
                 sa.setDuration(2000);
                AlphaAnimation aa = new AlphaAnimation(0.2f, 1);
                 aa.setDuration(2000);
               animSet.addAnimation(sa);
                animSet.addAnimation(aa);
                 mView.startAnimation(animSet);
                mView.setVisibility(View.VISIBLE);
            }
    /*
    private void addFlagsToIntent(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    }

     */
    //public void onDestory(){
     //   System.gc();
      //  Field activityManager = aClass.getDeclaredField("mLocalActivityManager");
      //  activityManager.setAccessible(true);
     //   LocalActivityManager manager = ((LocalActivityManager) activityManager.get(activity));
     //   Field mResumed = manager.getClass().getDeclaredField("mResumed");
     //   mResumed.setAccessible(true);
     //   Object record = mResumed.get(manager);
     //   Field window = record.getClass().getDeclaredField("window");
     //   window.setAccessible(true);
     //   Field activity1 = record.getClass().getDeclaredField("activity");
      //  activity1.setAccessible(true);
     //   window.set(record, null);
     //   activity1.set(record, null);
      //  activityManager.set(activity, null);
      //  mResumed.set(manager, null);
      //  Runtime.getRuntime().gc();
      //  System.gc();
    //}

    private void restartBotton() {
        // ImageView置为灰色
        //iv_dial.setImageResource(R.drawable.tab_weixin_normal);
        //iv_address.setImageResource(R.drawable.tab_address_normal);
        //iv_message.setImageResource(R.drawable.tab_find_frd_normal);
        // TextView置为白色
        tv_dial.setTextColor(0xffffffff);
        tv_address.setTextColor(0xffffffff);
        tv_message.setTextColor(0xffffffff);
    }

    //@Override
    //public void onPageScrollStateChanged(int arg0) {

   // }

    //@Override
    //public void onPageScrolled(int arg0, float arg1, int arg2) {

    //}

    /*@Override
    public void onPageSelected(int arg0) {
        restartBotton();
        //当前view被选择的时候,改变底部菜单图片，文字颜色
        switch (arg0) {
            case 0:
               // iv_dial.setImageResource(R.drawable.tab_weixin_pressed);
                tv_dial.setTextColor(0xff1B940A);
                break;
            case 1:
               // iv_address.setImageResource(R.drawable.tab_address_pressed);
                tv_address.setTextColor(0xff1B940A);
                break;
            case 2:
               // iv_message.setImageResource(R.drawable.tab_find_frd_pressed);
                tv_message.setTextColor(0xff1B940A);
                break;

            default:
                break;
        }

    }

     */

}