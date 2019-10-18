package com.progresee.app.beans;

import java.util.Date;
import java.util.List;

import com.google.cloud.firestore.FieldValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
	
	private String exerciseTitle;
	private Date dateCreated;
	private List<String> usrsFinishedList;

}
