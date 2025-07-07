/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject4;

import java.time.LocalDateTime;
import java.util.*;

public class VisitLogRepository {
   private static List<VisitLog> visitLogList = new ArrayList<>();
   private static int idCounter = 1;

   public static VisitLog add(String studentName, String studentId, String studyProgram, String purpose, LocalDateTime visitTime) {
       VisitLog visitLog = new VisitLog(idCounter++, studentName, studentId, studyProgram, purpose, visitTime);
       visitLogList.add(visitLog);
       return visitLog;
   }

   public static List<VisitLog> findAll() {
       return visitLogList;
   }

   public static VisitLog findById(int id) {
       return visitLogList.stream().filter(p -> p.getId() == (id)).findFirst().orElse(null);
   }
    public static VisitLog update(int id, String studentName, String studentId, String studyProgram, String purpose, LocalDateTime visitTime) {
        VisitLog visitLog = findById(id);
           if (visitLog != null) {
               visitLog.setStudentName(studentName);
               visitLog.setStudentId(studentId);
               visitLog.setStudyProgram(studyProgram);
               visitLog.setPurpose(purpose);
               visitLog.setVisitTime(visitTime);
           }
           return visitLog;
       }

   public static boolean delete(int id) {
       return visitLogList.removeIf(p -> p.getId() == (id));
   }
}