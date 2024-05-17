package br.com.mobsolution.events.repository;

import br.com.mobsolution.events.model.Event;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    static Specification<Event> byName(String name) {
        return (imp, cq, cb) -> cb.like(cb.lower(imp.get("name")),
                "%" + name.toLowerCase() + "%");
    }

    static Specification<Event> byStartDate(Date startDate) {
        return (imp, cq, cb) -> cb.greaterThanOrEqualTo(imp.get("startDate"), startDate);
    }

    static Specification<Event> byEndDate(Date endDate) {
        return (imp, cq, cb) -> cb.lessThanOrEqualTo(imp.get("endDate"), endDate);
    }
}
