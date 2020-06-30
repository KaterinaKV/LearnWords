package app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class CatalogDto {

    @NonNull
    private long id;
    @NonNull
    private String name;

    private int countCard;
    @NonNull
    private UserDto userDto;
}
