package org.project.coWorks.repository;

import org.project.coWorks.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
	String findByPermissionName(String permissionName);
}
