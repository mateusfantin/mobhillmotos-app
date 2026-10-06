package br.com.mobhillmotos.api.adapter.out.persistence.moto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.mobhillmotos.api.domain.MotoType;

public interface MotoJpaRepository extends JpaRepository<MotoJpa, Long> {

	@Query("""
			select m from MotoJpa m
			where (:active is null or m.active = :active)
			  and (:type is null or m.type = :type)
			""")
	List<MotoJpa> search(@Param("active") Boolean active, @Param("type") MotoType type);
}
