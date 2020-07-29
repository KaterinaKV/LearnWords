package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cards")
public class Card {

    @Id
    @SequenceGenerator(name = "cards_id", sequenceName = "seq_cards", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cards_id")
    private long id;

    @NonNull
    private String word;

    @NonNull
    private String translation;

    private String transcription;

    @ManyToOne
    @JoinColumn(name = "cardList")
    private Catalog catalog;

}
