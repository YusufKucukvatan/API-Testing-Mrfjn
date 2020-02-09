package selfStudy;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojos.Contact;
import pojos.Spartan;
import pojos.Student;
import utilities.ConfigurationReader;
import utilities.SpartanApiUtils;

import java.lang.reflect.Array;
import java.util.*;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

public class MySchool {
    @BeforeAll
    public static void setUp(){
        baseURI = ConfigurationReader.get("mySchoolURL");
    }

    @Test
    @DisplayName("Verify sorting hat")
    public void test1() {
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get("/student/all");

        JsonPath json = response.jsonPath();

        List<Map<String,?>> students = json.getList("students");
        System.out.println("students = " + students.size());

        for(Map<String,?> student : students) {
            //System.out.println(student);
        }

        String lastname = json.getString("students.lastName[0]");
        System.out.println("lastname = " + lastname);

        List<String> names = json.getList("lastName");
//        for (String name : names) {
            //System.out.println(name);
        //}

        int batch = json.getInt("students.batch[1]");
        System.out.println("batch = " + batch);

        Map<String , Object> contact5 = json.getMap("students[4].contact");
        //System.out.println("contact5 = " + contact5);

        String phone = json.getString("students[4].contact.phone");
        //System.out.println("phone = " + phone);

        int zipCode = json.getInt("students[7].company.address.zipCode");
        System.out.println("zipCode = " + zipCode);

        List<Student> studentsPOJO = json.getList("students");

        System.out.println(studentsPOJO.get(0));

        Contact contactPOJO = json.getObject("students[4].contact", Contact.class);
        System.out.println(contactPOJO);

        System.out.println(contactPOJO.getContactId());


        Student student = new Student();
        student.setAdmissionNo("jsagjhasd");


    }

}
