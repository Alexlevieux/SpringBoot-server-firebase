package com.progresee.app.beans;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
	
	
	private String exerciseTitle;
	private String dateCreated;
	private Map<String, Object> finishedUsersList = new Hashtable<String, Object>();
	private String taskUid;
//	private String classroomId;
	private boolean archived;
	

}
