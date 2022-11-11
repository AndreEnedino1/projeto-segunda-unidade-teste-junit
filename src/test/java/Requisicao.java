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
    User user = new User("ANDREENEDINO", "@Andre2710");

    //INICIALIZE A VARIÁVEL COM  O ID GERADO NO LOG DA REQUISIÇÃO testeCriaUsuario()
    String userID = "7311092a-ec5c-46be-9f1e-9e1ccde1f146";

    //INICIALIZE A VARIÁVEL COM  O TOKEN GERADO NO LOG DA REQUISIÇÃO testeGeraToken()
    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6IkFORFJFRU5FRElOTyIsInBhc3N3b3JkIjoiQEFuZHJlMjcxMCIsImlhdCI6MTY2ODEyODg2OH0.VWTOvb4NgJTHrUU3tiZKcIKKsTSQllQEcSqrqCe_B-Q";

    //###########################################################################
    //##########################    URL BASE   ##################################
    //###########################################################################
    @BeforeEach
    public void configtest(){
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
    }

    //###########################################################################
    //##########################    TESTE 01   ##################################
    //###########################################################################
    @Test
    public void testeCriaUsuario() throws Exception {

        RestAssured.given().contentType(ContentType.JSON)
                .when()
                .body(user)
                .post("/Account/v1/User")
                .then().statusCode(HttpStatus.SC_CREATED).log().all();

        /* TESTE EXTRAINDO DA REQUISIÇÃO
        String userID = RestAssured.given().contentType(ContentType.JSON)
                .when()
                .body(user)
                .post("/Account/v1/User")
                .then()
                .extract().path("userID");
         */
    }

    //###########################################################################
    //##########################    TESTE 02   ##################################
    //###########################################################################
    @Test
    public void testeGeraToken() {

        RestAssured.given().contentType(ContentType.JSON)
                .when()
                .body(user)
                .post("/Account/v1/GenerateToken")
                .then().statusCode(HttpStatus.SC_OK).log().all();
    }

    //###########################################################################
    //##########################    TESTE 03   ##################################
    //###########################################################################
    @Test
    public void testeAutoriza() {
        RestAssured.given().contentType(ContentType.JSON)
                .when()
                .body(user)
                .post("/Account/v1/Authorized")
                .then().statusCode(HttpStatus.SC_OK);

    }

    //###########################################################################
    //##########################    TESTE 04   ##################################
    //###########################################################################
    @Test
    public void testeGetUsuario(){

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", token)
                .auth().preemptive().basic(user.getUserName(), user.getPassword())
                .when()
                .pathParam("UUID", userID)
                .get("/Account/v1/User/{UUID}")
                .then()
                .statusCode(HttpStatus.SC_OK).log().all();
    }

    //###########################################################################
    //##########################    TESTE 05   ##################################
    //###########################################################################
    @Test
    public void testeGetLivros(){
        RestAssured.given().contentType(ContentType.JSON)
                .when()
                .get("/BookStore/v1/Books")
                .then().statusCode(HttpStatus.SC_OK).log().all();
    }

    //###########################################################################
    //##########################    TESTE 06   ##################################
    //###########################################################################
    @Test
    public void testeInsereLivroNoUsuario() {
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
    }

    //###########################################################################
    //##########################    TESTE 07   ##################################
    //###########################################################################
    @Test
    public void testeGetLivroPorId(){
        RestAssured.given().contentType(ContentType.JSON)
                .when()
                .queryParam("ISBN","9781449331818")
                .get("/BookStore/v1/Book")
                .then()
                .statusCode(HttpStatus.SC_OK).log().all();

    }

    //###########################################################################
    //##########################    TESTE 08   ##################################
    //###########################################################################
    @Test
    public void testeDeletaLivroDoUsuario(){

        Map<String, Object> parametros = new HashMap<>();

        //ISBN EXTRAÍDO NO LOG DA REQUISIÇÃO DO testeGetLivros()
        String isbnTeste = "9781449325862";

        parametros.put("isbn",isbnTeste);
        parametros.put("userId", userID);

        Gson gson = new Gson();

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", token)
                .auth().preemptive().basic(user.getUserName(), user.getPassword())
                .when()
                .body(gson.toJson(parametros))
                .delete("/BookStore/v1/Book")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT).log().all();
    }

    //###########################################################################
    //##########################    TESTE 09   ##################################
    //###########################################################################
    @Test
    public void testeDeletaTodosOsLivros(){

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", token)
                .auth().preemptive().basic(user.getUserName(), user.getPassword())
                .when()
                .queryParam("UserId",userID)
                .delete("/BookStore/v1/Books")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT).log().all();
    }

    //###########################################################################
    //##########################    TESTE 10   ##################################
    //###########################################################################
    @Test
    public void testeDeletaContaDoUsuario(){

        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", token)
                .auth().preemptive().basic(user.getUserName(), user.getPassword())
                .when()
                .pathParam("UUID", userID)
                .delete("/Account/v1/User/{UUID}")
                .then().statusCode(HttpStatus.SC_NO_CONTENT).log().all();
    }

}
