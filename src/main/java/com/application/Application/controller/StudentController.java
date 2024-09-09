package com.application.Application.controller;

import com.application.Application.Student;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StudentController {


    private List<Student> students=new ArrayList<>(
            List.of(new Student(1,"asdsa",33),
            (new Student(2,"fddg",33)),
            (new Student(3,"dsfsdf",33)
    )));

    @GetMapping("/students")
    public List<Student> getStudents(){
        return students;

    }

//    @GetMapping("/get-token")
//    public CsrfToken getToken(HttpServletRequest request) {
//        // Get the CSRF token from the request attribute
//        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//
//        // Check if the CSRF token is present
//        if (csrfToken == null) {
//            throw new RuntimeException("CSRF Token not found in request.");
//        }
//
//        return csrfToken;
//    }
    @GetMapping("/getCSRF")
    public CsrfToken getToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken == null) {
            throw new RuntimeException("CSRF Token not found in request.");
        }
        return csrfToken;
    }



    @PostMapping("/students")
    public Student addStudent(@RequestBody Student student){
        students.add(student);
        return student;
    }
}
