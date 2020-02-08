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
import pojos.Spartan;
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
        given()
    }

}
