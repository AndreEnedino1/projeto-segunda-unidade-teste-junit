import java.util.ArrayList;

public class InserirLivro {

    private String userId;
    private ArrayList collectionOfIsbns = new ArrayList<>();

    public InserirLivro(String userId, ArrayList livros) {
        this.userId = userId;
        this.collectionOfIsbns = livros;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList getLivro() {
        return collectionOfIsbns;
    }

    public void setLivro(ArrayList livro) {
        this.collectionOfIsbns.add(livro);
    }
}
