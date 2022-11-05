import java.util.ArrayList;

public class User {

    private String userName;
    private String password;

    private ArrayList<String> books = new ArrayList<>();

    public User(String nome, String senha) {
        this.userName = nome;
        this.password = senha;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBooks(String book){
        this.books.add(book);
    }
}
