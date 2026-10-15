package models;

// Represents a printed document or book
public class Document extends Media {
    private static final long serialVersionUID = 1L;
    private int numberOfPages;

    public Document(String id, String title, String author, int publicationYear, String description, int numberOfPages) {
        super(id, title, author, publicationYear, description);
        this.numberOfPages = numberOfPages;
    }

    public int getNumberOfPages() { return numberOfPages; }

    @Override
    public void afficher() {
        System.out.println("Document: " + getTitle() + " by " + getAuthor() + " (" + numberOfPages + " pages)");
    }

    @Override
    public String getDisplayMedia() {
        return "Pages count: " + numberOfPages;
    }

    @Override
    public String getType() {
        return "Document";
    }
}
