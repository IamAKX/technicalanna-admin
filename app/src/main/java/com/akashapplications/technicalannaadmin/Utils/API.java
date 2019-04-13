package com.akashapplications.technicalannaadmin.Utils;

public class API {
    public static final String BASE_URL = "https://us-central1-technical-anna.cloudfunctions.net/";

    // Booster API
    public static final String BOOSTER_ADD = BASE_URL + "boosterApp/v1/booster/add";
    public static final String BOOSTER_DETAIL = BASE_URL + "boosterApp/v1/booster/getBoosterDetail";
    public static final String BOOSTER_UPDATE = BASE_URL + "boosterApp/v1/booster/updateBoosterDetail";
    public static final String BOOSTER_DELETE = BASE_URL + "boosterApp/v1/booster/deleteBooster";
    public static final String ALL_BOOSTER = BASE_URL + "boosterApp/v1/booster/getAllBooster";

    // Subject-wise Exam API
    public static final String SUBJECT_EXAM_ADD = BASE_URL + "subjectExamsApp/v1/subjectexams/add";
    public static final String SUBJECT_EXAM_DELETE = BASE_URL + "subjectExamsApp/v1/subjectexams/delete";
    public static final String ALL_SUBJECT_EXAM = BASE_URL + "subjectExamsApp/v1/subjectexams/getAllExams";
    public static final String SUBJECT_EXAM_DETAIL = BASE_URL + "subjectExamsApp/v1/subjectexams/getExamDetails";

    // Full-length Exam API
    public static final String FULL_EXAM_ADD_NOTIFICATION = BASE_URL + "fullLengthExamsApp/v1/fullexam/addNotification";
    public static final String FULL_EXAM_ADD_SYLLABUS = BASE_URL + "fullLengthExamsApp/v1/fullexam/addSyllabus";
    public static final String FULL_EXAM_ADD_PREV_QUES_PAPER = BASE_URL + "fullLengthExamsApp/v1/fullexam/previousQuestionPaper";
    public static final String FULL_EXAM_ADD = BASE_URL + "fullLengthExamsApp/v1/fullexam/add";
    public static final String FULL_EXAM_EXAM_DETAIL = BASE_URL + "fullLengthExamsApp/v1/fullexam/getExamDetails";
    public static final String FULL_EXAM_DELETE = BASE_URL + "fullLengthExamsApp/v1/fullexam/delete";
    public static final String ALL_FULL_EXAM_ADD = BASE_URL + "fullLengthExamsApp/v1/fullexam/getAllExams";
    public static final String FULL_EXAM_CHECK_SUBSCRIBTION = BASE_URL + "fullLengthExamsApp/v1/fullexam/checkSubscribtion";
    public static final String FULL_EXAM_ADD_SUBSCRIBTION = BASE_URL + "fullLengthExamsApp/v1/fullexam/addSubscribtion";

}
