//package com.auth.AuthImpl.registraion.userServices;
//
//import com.auth.AuthImpl.registraion.dtos.fields.EmailField;
//import com.auth.AuthImpl.registraion.dtos.fields.FormBuilder;
//import com.auth.AuthImpl.registraion.dtos.fields.IsdField;
//import com.auth.AuthImpl.registraion.dtos.fields.MobileField;
//
//import java.util.List;
//
//public class Main {
//    public static void main(String[] args) {
//        try {
//            // Create a FormBuilder instance
//            FormBuilder formBuilder = new FormBuilder();
//
//            // Create fields
//            MobileField mobileField = new MobileField();
//            EmailField emailField = new EmailField();
//            IsdField isdCodeDropdownField = new IsdField(List.of("+1", "+44", "+91"));
//
//            // Add fields to the form
//            formBuilder.addField(mobileField);
//            formBuilder.addField(emailField);
//            formBuilder.addField(isdCodeDropdownField);
//
//            // Display the form fields
//            formBuilder.displayForm();
//
//            // Serialize the FormBuilder to JSON
//            String json = formBuilder.toJson();
//            System.out.println("Serialized JSON: " + json);
//
//            // Deserialize JSON back to FormBuilder
//            FormBuilder deserializedFormBuilder = FormBuilder.fromJson(json);
//            System.out.println("Deserialized FormBuilder: ");
//            deserializedFormBuilder.displayForm();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
//
