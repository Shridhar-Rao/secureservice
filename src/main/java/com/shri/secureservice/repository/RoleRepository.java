package com.shri.secureservice.repository;

import com.shri.secureservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shri.secureservice.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    public Role findByRolename(String rolename);

    public Role deleteByRolename(String rolename);
}
