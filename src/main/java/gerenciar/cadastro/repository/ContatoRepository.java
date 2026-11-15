package gerenciar.cadastro.repository;

import gerenciar.cadastro.model.entity.Contato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long> {

    boolean existsByemail(String email);


    Page<Contato> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
