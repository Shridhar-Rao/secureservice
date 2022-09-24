package com.shri.secureservice.repository;

import com.shri.secureservice.entity.Token;
import com.shri.secureservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer>{
	public Token findByAuthtoken(String authtoken);
	
	public Token deleteByUser(Integer id);

	@Query(value="select t from Token t where t.user.id=:id")
	public Token findByUser(Integer id);
}
