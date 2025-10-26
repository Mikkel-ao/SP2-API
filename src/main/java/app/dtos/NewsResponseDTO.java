package app.dtos;

import lombok.Data;
import java.util.List;

@Data
public class NewsResponseDTO {
    private String status;
    private int totalResults;
    private List<NewsArticleDTO> articles;
}
