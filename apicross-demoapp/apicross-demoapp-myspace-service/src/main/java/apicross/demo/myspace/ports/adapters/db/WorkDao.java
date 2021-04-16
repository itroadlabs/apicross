package apicross.demo.myspace.ports.adapters.db;

import apicross.demo.myspace.domain.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface WorkDao extends JpaRepository<Work, String> {
    @Query("SELECT w FROM Work w WHERE w.userId = :username")
    List<Work> findAllForUser(@Param("username") String username);

    @Query("SELECT w FROM Work w WHERE w.userId = :username AND w.id = :workId")
    Optional<Work> findForUser(@Param("username") String username, @Param("workId") String workId);
}
