package io.pivotal.timetracking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface TimeEntryRepository extends JpaRepository {
   List findByFeName(@Param("feName") String var1);

   List findByAccountName(@Param("accountName") String var1);
}
