package com.konka.renting.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * @date 创建时间：2017/3/13
 * @author 郭阳鹏
 * @description 管理栈内的Activity
 */
public class AppManager
{
	private static Stack<Activity> mActivityStack;
	private static AppManager		mAppManager;

	private AppManager() { }

	public static AppManager getInstance()
	{
		if (mAppManager == null)
		{
			mAppManager = new AppManager();
		}
		return mAppManager;
	}

	/** 添加Activity到堆栈 */
	public void addActivity(Activity activity)
	{
		if (mActivityStack == null)
		{
			mActivityStack = new Stack<Activity>();
		}
		mActivityStack.add(activity);
	}

	/** 获取栈顶Activity */
	public static Activity getTopActivity()
	{
		Activity activity = mActivityStack.lastElement();
		return activity;
	}

	/** 结束栈顶Activity */
	public void killTopActivity()
	{
		Activity activity = mActivityStack.lastElement();
		killActivity(activity);
	}

	/** 结束指定的Activity */
	public void killActivity(Activity activity)
	{
		if (activity != null)
		{
			mActivityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/** 结束指定类名的Activity */
	public void killActivity(Class<?> cls)
	{
		for (Activity activity : mActivityStack)
		{
			if (activity.getClass().equals(cls))
			{
				killActivity(activity);
			}
		}
	}

	/** 结束所有Activity */
	public void killAllActivity()
	{
		for (int i = 0, size = mActivityStack.size(); i < size; i++)
		{
			if (null != mActivityStack.get(i))
			{
				mActivityStack.get(i).finish();
			}
		}
		mActivityStack.clear();
	}

	/** 退出应用程序 */
	@SuppressWarnings("deprecation")
	public void AppExit(Context context)
	{
		try
		{
			killAllActivity();
			ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		}
		catch (Exception e)
		{
		}
	}

	/** 结束主Acitivity上面所有的Activity */
	public void killAllTopActivity()
	{
		Stack<Activity> mActivityStack = AppManager.mActivityStack;
		for (int i = mActivityStack.size() - 1; i > 0; i--)
		{
			killTopActivity();
		}
	}

	/** 结束除指定类名Acitivity外所有的Activity */
	public void killAllBeyondActivity(Class<?> cls)
	{
		for (Activity activity : mActivityStack)
		{
			if (!activity.getClass().equals(cls))
			{
				killActivity(activity);
			}
		}
	}

}
