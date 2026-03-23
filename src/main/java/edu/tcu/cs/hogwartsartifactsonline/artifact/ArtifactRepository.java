package edu.tcu.cs.hogwartsartifactsonline.artifact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//optional
public interface ArtifactRepository extends JpaRepository<Artifact, String> {
}
