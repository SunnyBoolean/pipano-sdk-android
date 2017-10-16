package com.pi.demo.common;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Administrator on 2017/10/11.
 */

public class ActionBarDrawerToggleDiy extends ActionBarDrawerToggle
{
    public interface ActionBarDrawerToggleDiyListener
    {
        void refreshUI();
    }

    private ActionBarDrawerToggleDiyListener mActionBarDrawerToggleDiyListener = null;

    public ActionBarDrawerToggleDiy(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes, ActionBarDrawerToggleDiyListener listener)
    {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);

        mActionBarDrawerToggleDiyListener = listener;
    }

    @Override
    public void onDrawerOpened(View drawerView)
    {
        super.onDrawerOpened(drawerView);

        if (null != mActionBarDrawerToggleDiyListener)
        {
            mActionBarDrawerToggleDiyListener.refreshUI();
        }
    }
}
