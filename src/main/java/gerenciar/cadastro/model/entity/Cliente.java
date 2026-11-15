package gerenciar.cadastro.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cliente")
@SQLDelete(sql = "UPDATE cliente SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Cliente extends Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @OneToMany(mappedBy = ("cliente"),cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<Contato> contatos =  new ArrayList<Contato>();
}
