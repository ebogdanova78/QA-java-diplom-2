package tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static steps.UserSteps.createUser;
import static steps.UserSteps.loginUser;

public class LoginUserTests {

    public String EMAIL = RandomStringUtils.randomAlphabetic(8) + "@yandex.ru";
    public String PASSWORD = RandomStringUtils.randomAlphanumeric(8);
    public String NAME = "Name09124";
    protected String token;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Логин под существующим пользователем")
    public void loginUserTest() {
        //createUser(EMAIL, PASSWORD, NAME);
        token = UserSteps.createUser(EMAIL, PASSWORD, NAME).then().extract().path("accessToken");
        loginUser(EMAIL, PASSWORD)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Логин пользователя c неправильным логином")
    @Description("Логин некорректной учетной записи")
    public void loginUserWithIncorrectEmailTest() {
        token = UserSteps.createUser(EMAIL, PASSWORD, NAME).then().extract().path("accessToken");
        loginUser(EMAIL+"new", PASSWORD)
                .then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин пользователя c неправильным паролем")
    @Description("Логин некорректной учетной записи")
    public void loginUserWithIncorrectPasswordTest() {
        token = UserSteps.createUser(EMAIL, PASSWORD, NAME).then().extract().path("accessToken");
        loginUser(EMAIL, PASSWORD+"new")
                .then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @After
    @Description("Delete data")
    public void tearDown() {
        if (token != null)
            UserSteps.deleteUser(token);
    }
}
