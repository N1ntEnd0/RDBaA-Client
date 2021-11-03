package dto;

public class Music {
    private Long id;

    private String name;

    private String authorName;

    public Music(String name, String authorName) {
        this.name = name;
        this.authorName = authorName;
    }

    public Music(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

   @Override
    public String toString() {
        return this.authorName + " - " + this.name;
   }
}
