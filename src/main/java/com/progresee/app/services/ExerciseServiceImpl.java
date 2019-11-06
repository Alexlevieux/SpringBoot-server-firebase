package com.progresee.app.services;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.progresee.app.beans.Exercise;
import com.progresee.app.services.dao.ExerciseService;
import com.progresee.app.utils.ResponseUtils;

@Service
public class ExerciseServiceImpl implements ExerciseService {

	private static final String EXERCISES = "exercises";
	@Autowired
	private UserServiceImpl userService;

	@Autowired
	Firestore firestore;

	@Autowired
	HttpServletResponse response;

	@PostConstruct
	public void initDB() {
		System.out.println("PostConstruct -----> ExerciseService");
	}

	@PreDestroy
	public void destroy() {
		System.out.println("PreDestroy -----> ExerciseService");
	}

	@Override
	public Map<String, Object> getExercise(String token, String classroomId, String taskId, String exerciseId) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		DocumentReference docRef = firestore.collection(EXERCISES).document(exerciseId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			if (documentSnapshot.exists()) {
				return documentSnapshot.getData();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		response.setStatus(ResponseUtils.BAD_REQUEST);
		return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.NOT_PART_OF_CLASSROOM,
				"/getExercise");
	}

	@Override
	public Map<String, Object> getFinishedUsers(String token, String classroomId, String exerciseId) {
		Map<String, String> usersInClassroom = new HashMap<>();
		Map<String, Object> finishedUsersList = new HashMap<>();
		Map<String, Object> finishedUsersTemp = new HashMap<>();
		Map<String, Object> finishedUsersToSave = new HashMap<>();
		usersInClassroom = userService.getUsersInClassroomNoToken(classroomId);
		System.out.println("usersinclassroom------>" + usersInClassroom);
		DocumentReference docRef = firestore.collection(EXERCISES).document(exerciseId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			if (documentSnapshot.exists()) {
				finishedUsersList = (Map<String, Object>) documentSnapshot.get("finishedUsersList");
				for (String email : usersInClassroom.values()) {
					finishedUsersTemp.put(email, "N/A");
				}
				if (finishedUsersList.size() > 0) {
					for (String email : finishedUsersList.keySet()) {
						if (finishedUsersTemp.containsKey(email)) {
							finishedUsersTemp.put(email, finishedUsersList.get(email));
						}
					}
				}
				finishedUsersToSave.put("finishedUsersList", finishedUsersTemp);
				ApiFuture<WriteResult> write = firestore.collection(EXERCISES).document(exerciseId)
						.set(finishedUsersToSave, SetOptions.merge());
				WriteResult writeResult = write.get();
				if (writeResult != null) {
					return finishedUsersTemp;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Object> initFinishedUsersList(String classroomId, String exerciseId) {
		System.out.println("checkedOwnership 9");
		Map<String, String> usersInClassroom = new HashMap<>();
		Map<String, Object> finishedUsersTemp = new HashMap<>();
		Map<String, Object> finishedUsersToSave = new HashMap<>();
		usersInClassroom = userService.getUsersInClassroomNoToken(classroomId);
		try {
			System.out.println("checkedOwnership 10");
			System.out.println("exist");
			for (String email : usersInClassroom.values()) {
				finishedUsersTemp.put(email, "N/A");
			}
			System.out.println("finishedUsersTemp--->" + finishedUsersTemp);
			finishedUsersToSave.put("finishedUsersList", finishedUsersTemp);
			ApiFuture<WriteResult> write = firestore.collection(EXERCISES).document(exerciseId).set(finishedUsersToSave,SetOptions.merge());
			WriteResult writeResult = write.get();
			if (writeResult != null) {
				System.out.println("yay");
				return finishedUsersTemp;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> getAllExercises(String token, String classroomId, String taskId) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		Query docRef = firestore.collection(EXERCISES).whereEqualTo("taskUid", taskId);
		try {
			ApiFuture<QuerySnapshot> documentReference = docRef.get();
			QuerySnapshot documentSnapshot = documentReference.get();
			List<Exercise> tempExercises = documentSnapshot.toObjects(Exercise.class);
			Map<String, Object> exercises = new HashMap<>();
			for (Exercise exercise : tempExercises) {
				exercises.put(exercise.getUid(), exercise);
			}
			return exercises;

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		response.setStatus(ResponseUtils.BAD_REQUEST);
		return ResponseUtils.generateErrorCode(ResponseUtils.BAD_REQUEST, ResponseUtils.NOT_PART_OF_CLASSROOM,
				"/getAllExercises");
	}

	@Override
	public Map<String, Object> createExercise(String token, String classroomId, String taskId, String description) {
		Map<String, Object> map = userService.findCurrentUser(token);
		Map<String, Object> checkMap = new HashMap<>();
		System.out.println("map -> " + map);
		String uid = (String) map.get("uid");
		if (userService.checkOwnerShip(classroomId, uid)) {
			System.out.println("checkedOwnership 1");
			Exercise exercise = new Exercise();
			exercise.setDateCreated(Calendar.getInstance().getTime().toString());
			exercise.setTaskUid(taskId);
			exercise.setFinishedUsersList(new Hashtable<String, Object>());
			String exerciseUid = UUID.randomUUID().toString().replace("-", "");
			exercise.setUid(exerciseUid);
			exercise.setExerciseTitle(description);
			System.out.println("checkedOwnership 2");
			ApiFuture<WriteResult> docRef = firestore.collection(EXERCISES).document(exerciseUid).set(exercise);
			System.out.println("checkedOwnership 3");
			try {
				System.out.println("checkedOwnership 4");
				WriteResult writeResult = docRef.get();
				System.out.println("checkedOwnership 5");
				if (writeResult != null) {
					System.out.println("checkedOwnership 6");
					checkMap = initFinishedUsersList(classroomId, exerciseUid);
					System.out.println("checkedOwnership 7");
					if (checkMap != null) {
						System.out.println("checkedOwnership 8");
						return getExerciseAfterRequest(exerciseUid);
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.OWNER, "/createExercise");
	}

	@Override
	public Map<String, Object> deleteExercise(String token, String classroomId, String taskId, String exerciseId) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		ApiFuture<WriteResult> docRef = firestore.collection(EXERCISES).document(exerciseId).delete();
		try {
			WriteResult writeResult = docRef.get();
			if (writeResult != null) {
				return ResponseUtils.generateSuccessString("Exercise has been deleted");
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.ERROR, "/deleteExercise");
	}

	@Override
	public Map<String, Object> updateExercise(String token, String classroomId, String taskId, Exercise exercise) {
		Map<String, Object> map = userService.findCurrentUser(token);
		System.out.println("map -> " + map);
		ApiFuture<WriteResult> docRef = firestore.collection(EXERCISES).document(exercise.getUid()).set(exercise);
		try {
			WriteResult writeResult = docRef.get();
			if (writeResult != null) {
				return getExerciseAfterRequest(exercise.getUid());
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.ERROR, "/updateExercise");
	}

	@Override
	public Map<String, Object> updateStatus(String token, String classroomId, String exerciseId) {
		Map<String, Object> map = userService.findCurrentUser(token);
		Map<String, Object> finishedUsersTemp = new HashMap<>();
		Map<String, Object> finishedUsersList = new HashMap<>();
		String email = (String) map.get("email");
		DocumentReference docRef = firestore.collection(EXERCISES).document(exerciseId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			if (documentSnapshot.exists()) {
				finishedUsersTemp = (Map<String, Object>) documentSnapshot.get("finishedUsersList");
					if (finishedUsersTemp.get(email).equals("N/A")) {
						finishedUsersTemp.put(email, Calendar.getInstance().getTime().toString());
					} else {
						finishedUsersTemp.put(email, "N/A");
					}
					System.out.println(finishedUsersTemp);
					finishedUsersList.put("finishedUsersList", finishedUsersTemp);
					ApiFuture<WriteResult> write = firestore.collection(EXERCISES).document(exerciseId)
							.set(finishedUsersList, SetOptions.merge());
					WriteResult writeResult = write.get();
					if (writeResult != null) {
						return getExerciseAfterRequest(exerciseId);
					}
				}
			

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		response.setStatus(ResponseUtils.FORBIDDEN);
		return ResponseUtils.generateErrorCode(ResponseUtils.FORBIDDEN, ResponseUtils.ERROR, "/updateExercise");
	}

	private Map<String, Object> getExerciseAfterRequest(String exerciseId) {
		DocumentReference docRef = firestore.collection(EXERCISES).document(exerciseId);
		try {
			ApiFuture<DocumentSnapshot> documentReference = docRef.get();
			DocumentSnapshot documentSnapshot = documentReference.get();
			Exercise exercise = documentSnapshot.toObject(Exercise.class);
			Map<String, Object> map = new HashMap<>();
			map.put(exerciseId, exercise);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
