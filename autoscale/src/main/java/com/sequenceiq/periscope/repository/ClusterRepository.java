package com.sequenceiq.periscope.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.sequenceiq.periscope.api.model.ClusterState;
import com.sequenceiq.periscope.domain.Cluster;

public interface ClusterRepository extends CrudRepository<Cluster, Long> {

    //@PostAuthorize("hasPermission(returnObject,'read')")
    Cluster findOne(@Param("id") Long id);

    Cluster find(@Param("id") Long id);

    List<Cluster> findAllByUser(@Param("id") String id);

    List<Cluster> findAllByState(@Param("state") ClusterState state);
}
