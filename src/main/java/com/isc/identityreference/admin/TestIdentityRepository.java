package com.isc.identityreference.admin;
import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;
public interface TestIdentityRepository extends JpaRepository<TestIdentityEntity,String>{List<TestIdentityEntity> findAllByOrderByCreatedAtDesc();}