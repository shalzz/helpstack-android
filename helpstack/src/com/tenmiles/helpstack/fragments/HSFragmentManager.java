//  HSFragmentManager
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

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tenmiles.helpstack.activities.HSActivityParent;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSUser;

/**
 * 
 * Contatins functions that help in creating fragment used in HelpStack.
 * 
 * @author Nalin Chhajer
 *
 */
public class HSFragmentManager {

	public static HomeFragment getHomeFragment() {
        return new HomeFragment();
	}

	public static ArticleFragment getArticleFragment(HSActivityParent activity, HSKBItem kbItem) {
		ArticleFragment sectionFragment = new ArticleFragment();
		sectionFragment.kbItem = kbItem;
		return sectionFragment;
	}

	public static void putFragmentInActivity(HSActivityParent activity, int resid, HSFragmentParent frag, String tag) {
		FragmentManager fragMgr = activity.getSupportFragmentManager();
		FragmentTransaction xact = fragMgr.beginTransaction();
		xact.replace(resid, frag, tag);
		xact.commit();	
	}
	
}
