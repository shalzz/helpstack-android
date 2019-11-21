//  HSArticleReader
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

package com.tenmiles.helpstack.logic;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;

import com.tenmiles.helpstack.model.HSKBItem;

/**
 * 
 * @author Nalin Chhajer
 *
 */
public class HSArticleReader {

	private int articleResourceId;

	public HSArticleReader(int articlesResourceId) {
		this.articleResourceId = articlesResourceId;
	}
	
	public HSKBItem[] readArticlesFromResource(Context context) throws XmlPullParserException, IOException {
        ArrayList<HSKBItem> articles = new ArrayList<HSKBItem>();
        XmlPullParser xpp = context.getResources().getXml(articleResourceId);

        int eventType = xpp.getEventType();
        String subject = null;
        String text = null;
        while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
            String tagname = xpp.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagname.equalsIgnoreCase("article")) {
                        int attributeCount = xpp.getAttributeCount();
                        for (int i = 0; i < attributeCount; i++) {
                            String attrName = xpp.getAttributeName(i);
                            if (attrName.equals("subject")) {
                                subject = xpp.getAttributeValue(i);
                            }
                        }

                        assert subject != null : "Subject was not specified in xml for article @ index "+articles.size()+1;
                    }
                    break;

                case XmlPullParser.TEXT:
                    text = xpp.getText();
                    Log.d("Parser", text);
                    assert text != null : "Text was not specified in xml for article @ index "+articles.size()+1;
                    break;

                case XmlPullParser.END_TAG:
                    if (tagname.equalsIgnoreCase("article")) {
                        // add employee object to list
                        articles.add(new HSKBItem(null, subject, text));
                    }
                    break;

                default:
                    break;
            }

            eventType = xpp.next();
        }

		HSKBItem[] articleArray = new HSKBItem[0];
		articleArray = articles.toArray(articleArray);
		return articleArray;
	}
	
}
