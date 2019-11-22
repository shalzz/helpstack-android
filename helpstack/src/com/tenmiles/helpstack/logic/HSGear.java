//  HSGear
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

/**
 * @author Nalin Chhajer
 *
 */
public abstract class HSGear {
	
	public HSGear() {
	}

    /**
     * Set this parameter, if gear is not implementing handling of Issues. Doing this, default email client will be open with given support Email Address.
     * Then there is no need to implement issues fetching related methods.
     *
     * Default:   it is considered that gear is gonna implement ticket fetching.
     *
     * @param companySupportEmailAddress Company Support Email Address
     */
	public void setNotImplementingTicketsFetching(String companySupportEmailAddress) {
		implementsTicketFetching = false;
		this.companySupportEmailAddress = companySupportEmailAddress;
	}

    /**
     *
     * @return Company support email address set in {@link #setNotImplementingTicketsFetching(java.lang.String) setNotImplementingTicketsFetching}
     */
    public String getCompanySupportEmailAddress() {
        return companySupportEmailAddress;
    }
    /**
     * Returns if gear have implemented Ticket Fetching. Modify this parameter using {@link #setNotImplementingTicketsFetching(java.lang.String) setNotImplementingTicketsFetching}
     *
     * Default:  true
     *
     */
    public boolean haveImplementedTicketFetching() {
        return implementsTicketFetching;
    }

    /**
     * Set this parameter, if gear is not implementing handling of FAQ. Doing this, FAQ will be fetched from article path.
     * Then there is no need to implement issues fetching related methods.
     *
     * Default:   it is considered that gear is gonna implement email fetching.
     *
     * @param articleResid Article Resource ID
     */
	public void setNotImplementingKBFetching (int articleResid) {
		this.articleResid = articleResid;
	}

    /**
     *
     * @return Local article id set in {@link #setNotImplementingKBFetching(int) setNotImplementingKBFetching}
     */
    public int getLocalArticleResourceId() {
        return articleResid;
    }

    /**
     *
     * @return maximum number of attachment gear can handle.
     * Default:  is 1
     */
	public int getNumberOfAttachmentGearCanHandle() {
		return numberOfAttachmentGearCanHandle;
	}

    ////////////////////////////////////////////////////
    /////////////   Private Variables   ///////////////
    ///////////////////////////////////////////////////

	private int numberOfAttachmentGearCanHandle = 1;
	
	// If this is true, we don't call kb article functions, will open email app is required.
	private boolean implementsTicketFetching = true;
	
	private int articleResid;
	
	private String companySupportEmailAddress;
}
