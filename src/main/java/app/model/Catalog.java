package app.model;

import app.dto.CardDto;
import app.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "catalogs")
public class Catalog {
    @Id
    @SequenceGenerator(name = "catalogs_id", sequenceName = "seq_catalogs", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "catalogs_id")
    @NonNull
    private long id;
    @NonNull
    private String name;

    @OneToMany(mappedBy = "catalog", cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
    private List<Card> cardList;

    @NonNull
    @ManyToOne
    @JoinColumn(name="catalogList")
    private User user;

}
