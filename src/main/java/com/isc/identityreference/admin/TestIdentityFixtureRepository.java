package com.isc.identityreference.admin;
import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;
public interface TestIdentityFixtureRepository extends JpaRepository<TestIdentityFixtureEntity,String>{List<TestIdentityFixtureEntity> findAllByOrderByCreatedAtDesc();}