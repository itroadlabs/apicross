package apicross.demo.myspace.ports.adapters.db;

import apicross.demo.myspace.domain.WorkFileReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface WorkFileReferenceDao extends JpaRepository<WorkFileReference, String> {
}
