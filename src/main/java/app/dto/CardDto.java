package app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {

    private long id;

    @NonNull
    private String word;

    @NonNull
    private String translation;

    private String transcription;

    @NonNull
    private CatalogDto catalogDto;

}
