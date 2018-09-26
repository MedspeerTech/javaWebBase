package medspeer.tech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import medspeer.tech.model.FileMeta;

@Repository
@Transactional
public interface FileMetaJpaRepository extends JpaRepository<FileMeta,Long>{

}
