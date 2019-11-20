//  SectionFragment
//
//Copyright (c) 2014 HelpStack (http://helpstack.io)
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package com.tenmiles.helpstack.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.activities.HSActivityManager;
import com.tenmiles.helpstack.fragments.SearchFragment.OnReportAnIssueClickListener;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.HSUtils;
import com.tenmiles.helpstack.logic.OnFetchedArraySuccessListener;
import com.tenmiles.helpstack.model.HSKBItem;

/**
 * 
 * Display all the articles in a section. 
 * 
 * @author Nalin Chhajer
 *
 */
public class SectionFragment extends HSFragmentParent {

	private static final int REQUEST_CODE_NEW_TICKET = HomeFragment.REQUEST_CODE_NEW_TICKET;

    public HSKBItem sectionItemToDisplay;

	private ListView mListView;
	private SectionAdapter mAdapter;
	private SearchFragment mSearchFragment;
    private HSSource gearSource;
	private HSKBItem[] fetchedKbItems;

	public SectionFragment() {
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.hs_fragment_section, container, false);

		// List View
		mListView = rootView.findViewById(R.id.sectionlistview);
		
		// Report an issue
		View report_an_issue_view = inflater.inflate(R.layout.hs_expandable_footer_report_issue, null);
        report_an_issue_view.findViewById(R.id.button1).setOnClickListener(reportIssueClickListener);
        mListView.addFooterView(report_an_issue_view);
		
        mAdapter = new SectionAdapter(this.fetchedKbItems);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(listItemClickListener);
		
        // Display Search
		mSearchFragment = new SearchFragment();
        HSFragmentManager.putFragmentInActivity(getHelpStackActivity(), R.id.search_container, mSearchFragment, "Search");
        mSearchFragment.setOnReportAnIssueClickListener(reportAnIssueLisener);
        setHasOptionsMenu(true);
        
        gearSource = HSSource.getInstance(getActivity());
		
        if (savedInstanceState == null) {
        	initializeView();
        }
        else {
            String json = savedInstanceState.getString("section_array");
            Gson gson = new Gson();
            fetchedKbItems = gson.fromJson(json, HSKBItem[].class);

        	mSearchFragment.setKBArticleList(fetchedKbItems);
        	refreshList();
        }
		
		return rootView;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// This is done to refresh the screen
		if (requestCode == REQUEST_CODE_NEW_TICKET) {
			if (resultCode == HSActivityManager.resultCode_sucess) {
				HSActivityManager.sendSuccessSignal(getActivity(), data);
			}
		}
	}
	
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        Gson gson = new Gson();
		outState.putSerializable("section_array", gson.toJson(fetchedKbItems));
	}

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.hs_search_menu, menu);
        
        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchFragment.addSearchViewInMenuItem(getActivity(), searchItem);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		gearSource.cancelOperation("SECTION_FAQ");
	}
	
	private void initializeView() {
        getHelpStackActivity().setProgressBarIndeterminateVisibility(true);
		
		gearSource.requestKBArticle("SECTION_FAQ", this.sectionItemToDisplay, new OnFetchedArraySuccessListener() {
			
			@Override
			public void onSuccess(Object[] successObject) {
				
				fetchedKbItems = (HSKBItem[])successObject;
				mSearchFragment.setKBArticleList(fetchedKbItems);
				refreshList();
				getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
				
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				HSUtils.showAlertDialog(getActivity(), getResources().getString(R.string.hs_error), getResources().getString(R.string.hs_error_fetching_articles));
			}
		});
	}
	
	protected OnClickListener reportIssueClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
            gearSource.launchCreateNewTicketScreen(SectionFragment.this, REQUEST_CODE_NEW_TICKET);
		}
	};
	
	private OnReportAnIssueClickListener reportAnIssueLisener = new OnReportAnIssueClickListener() {

		@Override
		public void startReportAnIssue() {
            mSearchFragment.setVisibility(false);
			gearSource.launchCreateNewTicketScreen(SectionFragment.this, REQUEST_CODE_NEW_TICKET);
		}
	};
	
	protected OnItemClickListener listItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			HSKBItem kbItemClicked = fetchedKbItems[position];
			articleClickedOnPosition(kbItemClicked);
		}
	};
	
	private void refreshList() {
		mAdapter.setKbArticles(fetchedKbItems);
	}
	
	protected void articleClickedOnPosition(HSKBItem kbItemClicked) {
		if(kbItemClicked.getArticleType() == HSKBItem.TYPE_ARTICLE) {
			HSActivityManager.startArticleActivity(this, kbItemClicked, REQUEST_CODE_NEW_TICKET);
		}
        else {
			HSActivityManager.startSectionActivity(this, kbItemClicked, REQUEST_CODE_NEW_TICKET);
		}
	}
	
	private class SectionAdapter extends BaseAdapter {

		HSKBItem[] kbItems;
		
		public SectionAdapter (HSKBItem[] kbItems) {
			this.kbItems = kbItems;
		}
		
		public void setKbArticles(HSKBItem[] fetchedKbItems) {
			this.kbItems = fetchedKbItems;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (kbItems == null) {
				return 0;
			}
			return this.kbItems.length;
		}

		@Override
		public Object getItem(int position) {
			return this.kbItems[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if(convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.hs_sectionlist_article, null);
				holder.title = convertView.findViewById(R.id.sectionlisttextview);
				convertView.setTag(holder);
			}
            else {
				holder = (ViewHolder)convertView.getTag();
			}
            holder.title.setText(((HSKBItem)getItem(position)).getSubject());
			
			return convertView;
		}
		
		private class ViewHolder {
			TextView title;
		}
	}
}
