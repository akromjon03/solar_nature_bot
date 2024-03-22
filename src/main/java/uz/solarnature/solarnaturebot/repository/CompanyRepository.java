package uz.solarnature.solarnaturebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.solarnature.solarnaturebot.domain.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
