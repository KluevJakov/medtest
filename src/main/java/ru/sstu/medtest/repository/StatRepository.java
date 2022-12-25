package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sstu.medtest.entity.Stat;


@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {
}
