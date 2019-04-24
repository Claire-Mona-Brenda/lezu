package com.konka.renting.tenant.findroom.roominfo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class RoomInfoThread implements OnItemClickListener, ViewPager.OnPageChangeListener {
	private Context mcontext;
	private ViewPager pager;
	private List<Topic> list_left;
	private List<Topic> list_bottom;
	public static final int left_ok = 1;
	public static final int right_ok = 2;
	public static final int dialogshow = 3;
	public static final int headerchange = 4;
	public static final int bottom_ok = 5;
	LinearLayout pointLayout;
	String cid;
	ImageAdapter adapterLeft;
	int header_size;
	int current_page = 0;
	boolean head_mark = true;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case left_ok:
				adapterLeft = new ImageAdapter(mcontext, list_left);
				pager.setAdapter(adapterLeft);
				initPointData(list_left.size());
				break;
			case bottom_ok:
//				initBottomData(bottom_view);
				break;
			case dialogshow:

				break;

			case headerchange:
				pager.setCurrentItem(current_page, true);
				pointChange(current_page);
				break;

			default:
				break;
			}
		};
	};

	public RoomInfoThread(Context context, ViewPager viewpager,
						  LinearLayout pointLayout) {
		mcontext = context;
		this.pager = viewpager;
		this.pointLayout = pointLayout;
		pager.setOnPageChangeListener(this);
		// pager.setOnItemClickListener(this);

	}
	String[]imgs;
	public void getData(String[]imgs) {
		this.imgs=imgs;
		new LeftThread().start();

	}

	public void initPointData(int size) {
		header_size = size;
		pointLayout.removeAllViews();
		for (int i = 0; i < size; i++) {
			PointImage p;
			if (i == 0) {
				p = new PointImage(mcontext);
				p.setChoosed(true);

			} else {
				p = new PointImage(mcontext);
				p.setChoosed(false);
			}
			p.setPadding(8, 0, 0, 0);
			p.setTag(i);
			pointLayout.addView(p);
		}
		new HeaderThread().start();

	}

	public void pointChange(int cp) {
		for (int i = 0; i < header_size; i++) {
			PointImage p = (PointImage) pointLayout.getChildAt(i);
			
			if ((Integer) (p.getTag()) == cp) {
				p.setChoosed(true);
			} else {
				p.setChoosed(false);
			}
		}

	}



	private void getLeftData() {
		try {
			list_left = new ArrayList<Topic>();
			for(String s:imgs){
				Topic topic=new Topic();
				topic.path=s;
				list_left.add(topic);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	class LeftThread extends Thread {
		@Override
		public void run() {
			Message msg = new Message();
			msg.what = dialogshow;
			handler.sendMessage(msg);
			getLeftData();

			Message msg2 = new Message();
			msg2.what = left_ok;
			handler.sendMessage(msg2);
			super.run();
		}
	}



	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// Intent intent = new Intent(mcontext, TopicActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// intent.putExtra("cid",list_left.get(arg2).cid);
		// intent.putExtra("name",list_left.get(arg2).name);
		// mcontext.toActivity(TopicActivity.class.getName(),
		// intent,RoomInfoActivity.class);

	}

	class HeaderThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (head_mark) {
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				current_page++;
				if (current_page >= header_size) {
					current_page = 0;
				}
				Message msg = new Message();
				msg.what = headerchange;
				handler.sendMessage(msg);
			}

		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		current_page=arg0;
		pointChange(current_page);
		
	}


}
