package beit.skn.pingmeuser;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

public class DashboardPagerAdapter extends PagerAdapter {

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub
		 ((ViewPager) arg0).removeView((View) arg2);
	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public Object instantiateItem(View collection, int position)
	{
		 LayoutInflater inflater = (LayoutInflater) collection.getContext()
         .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 int resId = 0;
 switch (position) {
 case 0:
     resId = R.layout.dashone;
     break;
 case 1:
     resId = R.layout.dashtwo;
     break;
 case 2:
     resId = R.layout.dashthree;
     break;

 }
 View view = inflater.inflate(resId, null);
 ((ViewPager)collection).addView(view, 0);
 return view;
 
	}

	@Override
	public CharSequence getPageTitle(int position) 
	{
		switch(position)
		{
		case 0:
			return "Settings";
			
		
		case 1:
			return "Summon";
		
		case 2:
			return "Ping";
		
		}
		return super.getPageTitle(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == ((View) arg1);

	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub

	}
}
