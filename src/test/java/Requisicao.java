import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Requisicao {

    //###########################################################################
    //############## MUDE O LOGIN E SENHA ANTES DE FAZER CADA TESTE #############
    //###########################################################################
    User user = new User("fulano", "@Andre2710");

    //###########################################################################

    @BeforeEach
    public void configtest(){
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
    }

    @Test
    public void testePost01() {

        String userID = RestAssured.given().contentType(ContentType.JSON)
                .when()
                .body(user)
                .post("/Account/v1/User")
                .then()
                .extract().path("userID");

        //PRÓXIMO TESTE
        testePost02(userID);
    }

    @Test
    public void testePost02(String id) {

        String token = RestAssured.given().contentType(ContentType.JSON)
                .when()
                .body(user)
                .post("/Account/v1/GenerateToken")
                .then().extract().path("token");

        System.out.println("Token: "+token);

        //PRÓXIMO TESTE
        testePost03(id,token);
    }

    @Test
    public void testePost03(String userID, String token) {
        RestAssured.given().contentType(ContentType.JSON)
                .when()
                .body(user)
                .post("/Account/v1/Authorized")
                .then().statusCode(HttpStatus.SC_OK);

        //PRÓXIMO TESTE
        testePost04(userID, token);
    }

    @Test
    public void testePost04(String userID, String token) {
        Livro livro = new Livro("9781449325862");
        Livro livro2 = new Livro("9781449331818");

        ArrayList livros = new ArrayList();

        livros.add(livro);
        livros.add(livro2);

        InserirLivro books = new InserirLivro(userID, livros);

        Gson gson = new Gson();

        System.out.println(gson.toJson(books));
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .auth().preemptive().basic(user.getUserName(), user.getPassword())
                .when()
                .body(gson.toJson(books))
                .post("/BookStore/v1/Books")
                .then().statusCode(HttpStatus.SC_CREATED).log().all();

        //PRÓXIMO TESTE
        testeGet01(token, userID);

    }

    @Test
    public void testeGet01(String token, String idTeste){

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", token)
                .auth().preemptive().basic(user.getUserName(), user.getPassword())
                .when()
                .pathParam("UUID", idTeste)
                .get("/Account/v1/User/{UUID}")
                .then()
                .statusCode(HttpStatus.SC_OK).log().all();

    }

    //#######################################################################
    //############ TESTES QUE NÃO PRECISAM DE AUTENTICAÇÃO ##################
    //#######################################################################

    @Test
    public void testeGet02(){
        RestAssured.given().contentType(ContentType.JSON)
                .when()
                .get("/BookStore/v1/Books")
                .then().statusCode(HttpStatus.SC_OK).log().all();
    }

    @Test
    public void testeGet03(){
        RestAssured.given().contentType(ContentType.JSON)
                .when()
                .queryParam("ISBN","9781449331818")
                .get("/BookStore/v1/Book")
                .then()
                .statusCode(HttpStatus.SC_OK).log().all();

    }


    //#######################################################################
    //################# TESTES DELETE COM AUTENTICAÇÃO ######################
    //#######################################################################
    @Test
    public void testeDelete01(){

        //TOKEN GERADO NO testePost02
        String tokenTeste = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImZ1bGFubyIsInBhc3N3b3JkIjoiQEFuZHJlMjcxMCIsImlhdCI6MTY2NzY4MjY0Mn0.EWqwrQ9q1s0jXpF5tWUVvxzW1pG7IUijkrdUtqoe_co";
        //USUÁRIO DEVE SER O MESMO QUE FOI CRIADO NO testePost01
        User userTeste = new User("fulano", "@Andre2710");

        Map<String, Object> parametros = new HashMap<>();
        //ID EXTRAÍDO NO LOG DA REQUISIÇÃO DO testeGet01
        String idTeste = "93b9f133-0e3a-4267-ac95-351c3caaeb7f";
        //ISBN EXTRAÍDO NO LOG DA REQUISIÇÃO DO testeGet01
        String isbnTeste = "9781449325862";

        parametros.put("isbn",isbnTeste);
        parametros.put("userId", idTeste);

        Gson gson = new Gson();

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", tokenTeste)
                .auth().preemptive().basic(userTeste.getUserName(), userTeste.getPassword())
                .when()
                .body(gson.toJson(parametros))
                .delete("/BookStore/v1/Book")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT).log().all();
    }

    @Test
    public void testeDelete02(){

        //TOKEN GERADO NO testePost02
        String tokenTeste = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImZ1bGFubyIsInBhc3N3b3JkIjoiQEFuZHJlMjcxMCIsImlhdCI6MTY2NzY4MjY0Mn0.EWqwrQ9q1s0jXpF5tWUVvxzW1pG7IUijkrdUtqoe_co";
        //USUÁRIO DEVE SER O MESMO QUE FOI CRIADO NO testePost01
        User userTeste = new User("fulano", "@Andre2710");

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", tokenTeste)
                .auth().preemptive().basic(userTeste.getUserName(), userTeste.getPassword())
                .when()
                .queryParam("UserId","93b9f133-0e3a-4267-ac95-351c3caaeb7f")
                .delete("/BookStore/v1/Books")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT).log().all();
    }

    @Test
    public void testeDelete03(){

        //TOKEN GERADO NO testePost02
        String tokenTeste = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImZ1bGFubyIsInBhc3N3b3JkIjoiQEFuZHJlMjcxMCIsImlhdCI6MTY2NzY4MjY0Mn0.EWqwrQ9q1s0jXpF5tWUVvxzW1pG7IUijkrdUtqoe_co";
        //USUÁRIO DEVE SER O MESMO QUE FOI CRIADO NO testePost01
        User userTeste = new User("fulano", "@Andre2710");

        //ID EXTRAÍDO NO LOG DA REQUISIÇÃO DO testeGet01
        String idTeste = "93b9f133-0e3a-4267-ac95-351c3caaeb7f";

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", tokenTeste)
                .auth().preemptive().basic(userTeste.getUserName(), userTeste.getPassword())
                .when()
                .pathParam("UUID", idTeste)
                .delete("/Account/v1/User/{UUID}")
                .then().statusCode(HttpStatus.SC_NO_CONTENT).log().all();
    }



}
