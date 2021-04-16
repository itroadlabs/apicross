package apicross.demo.myspace.ports.adapters.db;

import apicross.demo.myspace.domain.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface CompetitionDao extends JpaRepository<Competition, String>, JpaSpecificationExecutor<Competition> {
    @Query("SELECT c FROM Competition c WHERE c.organizerUserId = :userId")
    List<Competition> findAllForUserId(@Param("userId") String userId);
}
