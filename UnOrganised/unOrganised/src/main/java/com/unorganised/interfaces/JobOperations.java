package com.unorganised.interfaces;

import com.unorganised.util.Job;

public interface JobOperations {
	public void onJobRequested(Job job);
	public void onJobAccepted(Job job);
	public void onJobSelected(Job job);
	public void onJobDeleted(Job job);

}
