package com.team13.jpmorganchase.feedthechildren;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//Do not modify this code - it is part of the side panel.

public class ContentActivity extends AppCompatActivity {

    private static final String STATE_ACTIVE_POSITION = "net.simonvt.menudrawer.samples.ContentActivity.activePosition";
    private static final String STATE_CONTENT_TEXT = "net.simonvt.menudrawer.samples.ContentActivity.contentText";
    private static final int POSITION_HOME = 0;
    //private static final int POSITION_FEATURES = 1;
    private static final int POSITION_FRIENDS = 2;
    private static final int POSITION_ADD_FRIEND = 3;
    //private static final int POSITION_SETTING = 4;
    private static final int POSITION_ACCOUNT_SETTING = 5;
    private static final int POSITION_NOTIFICATION_SETTING = 6;
    //private static final int POSITION_OTHERS = 7;
    private static final int POSITION_RATE_THIS_APP = 8;
    private static final int POSITION_ABOUT_US = 9;
    private static final int POSITION_LOGOUT = 10;

    private MenuDrawer mMenuDrawer;
    private MenuAdapter mAdapter;
    private ListView mList;
    private int mActivePosition = -1;
    private String mContentText;
    private TextView mContentTextView;

    private ParseUser user;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.nav_icon);
        }

        user = ParseUser.getCurrentUser();

        if (inState != null) {
            mActivePosition = inState.getInt(STATE_ACTIVE_POSITION);
            mContentText = inState.getString(STATE_CONTENT_TEXT);
        }

        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_CONTENT);
        mMenuDrawer.setContentView(R.layout.activity_content);
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);

        List<Object> items = new ArrayList<>();
        items.add(new Item("Home", R.drawable.ic_action_select_all_dark));
        items.add(new Category("Consumer"));
        items.add(new Item("Store Location", R.drawable.ic_action_select_all_dark));
        items.add(new Item("Categories", R.drawable.ic_action_select_all_dark));
        items.add(new Category("Supplier"));
        items.add(new Item("Add item", R.drawable.ic_action_select_all_dark));
        items.add(new Item("Remove item", R.drawable.ic_action_select_all_dark));
        items.add(new Category(getString(R.string.others)));
        items.add(new Item(getString(R.string.rate_this_app), R.drawable.ic_action_select_all_dark));
        items.add(new Item(getString(R.string.about_us), R.drawable.ic_action_select_all_dark));
        items.add(new Item(getString(R.string.logout), R.drawable.ic_action_select_all_dark));

        mList = new ListView(this);
        mAdapter = new MenuAdapter(items);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(mItemClickListener);

        mMenuDrawer.setMenuView(mList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mContentTextView = (TextView) findViewById(R.id.contentText);
        //mContentTextView.setText(mContentText);

        mMenuDrawer.setOnInterceptMoveEventListener(new MenuDrawer.OnInterceptMoveEventListener() {
            @Override
            public boolean isViewDraggable(View v, int dx, int x, int y) {
                return v instanceof SeekBar;
            }
        });

        //Display Full Name
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(Utility.getName(user));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ACTIVE_POSITION, mActivePosition);
        outState.putString(STATE_CONTENT_TEXT, mContentText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mMenuDrawer.toggleMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(Utility.checkNewEntry()){
            Utility.setChangedRecord();
            Utility.generateFriendList(user);
        }

        final int drawerState = mMenuDrawer.getDrawerState();

        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
            mMenuDrawer.closeMenu();
            return;
        }

        if (doubleBackToExitPressedOnce) {
            // this is to close the app entirely, but it will still be in the stack

            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mActivePosition = position;
            mMenuDrawer.setActiveView(view, position);
            //mContentTextView.setText(((TextView) view).getText());             // Delete later
            mMenuDrawer.closeMenu();
            if(Utility.checkNewEntry()){
                Utility.setChangedRecord();
                Utility.generateFriendList(user);
            }

            switch(position) {
                case POSITION_HOME:
                    // Nothing really need to be done...
                    break;

                case POSITION_FRIENDS:
                    break;

                case POSITION_ADD_FRIEND:

                    break;

                case POSITION_ACCOUNT_SETTING:
                    Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
                    break;

                case POSITION_NOTIFICATION_SETTING:
                    Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
                    //do something
                    break;

                case POSITION_RATE_THIS_APP:
                    Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
                    //do something
                    break;

                case POSITION_ABOUT_US:

                    //do something
                    break;

                case POSITION_LOGOUT:
                    ParseUser.logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent intent = new Intent(ContentActivity.this, MainActivity.class);
                                ContentActivity.this.finish();
                                Utility.resetExistingFriendList();
                                Utility.setChangedRecord();
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.logout_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;
            }
        }
    };




    //Related to Side-Menu
    private static class Item {
        String mTitle;
        int mIconRes;

        Item(String title, int iconRes) {
            mTitle = title;
            mIconRes = iconRes;
        }
    }

    private static class Category {
        String mTitle;

        Category(String title) {
            mTitle = title;
        }
    }

    private class MenuAdapter extends BaseAdapter {
        private List<Object> mItems;

        MenuAdapter(List<Object> items) {
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position) instanceof Item ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItem(position) instanceof Item;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Object item = getItem(position);

            if (item instanceof Category) {
                if (v == null) {
                    v = getLayoutInflater().inflate(R.layout.menu_row_category, parent, false);
                }
                ((TextView) v).setText(((Category) item).mTitle);
            } else {
                if (v == null) {
                    v = getLayoutInflater().inflate(R.layout.menu_row_item, parent, false);
                }
                TextView tv = (TextView) v;
                tv.setText(((Item) item).mTitle);
                tv.setCompoundDrawablesWithIntrinsicBounds(((Item) item).mIconRes, 0, 0, 0);
            }

            v.setTag(R.id.mdActiveViewPosition, position);

            if (position == mActivePosition) {
                mMenuDrawer.setActiveView(v, position);
            }

            return v;
        }
    }
}
