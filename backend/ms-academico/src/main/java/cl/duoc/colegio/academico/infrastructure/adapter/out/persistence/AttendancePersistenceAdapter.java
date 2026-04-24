package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.application.port.out.AttendanceRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Attendance;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper.AttendancePersistenceMapper;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.AttendanceJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para Asistencia.
 */
@Component
public class AttendancePersistenceAdapter implements AttendanceRepositoryPort {

    private final AttendanceJpaRepository jpaRepository;
    private final AttendancePersistenceMapper mapper;

    public AttendancePersistenceAdapter(AttendanceJpaRepository jpaRepository,
                                        AttendancePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Attendance guardar(Attendance attendance) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(attendance)));
    }

    @Override
    public Optional<Attendance> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Attendance> buscarPorStudentId(Long studentId) {
        return jpaRepository.findByStudentId(studentId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Attendance> buscarPorStudentIdYFecha(Long studentId, LocalDate fecha) {
        return jpaRepository.findByStudentIdAndFecha(studentId, fecha).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
