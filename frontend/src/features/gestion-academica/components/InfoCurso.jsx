import { GraduationCap, Users, UserPlus, X } from 'lucide-react';

function InfoCurso({ curso, totalEstudiantes, mostrarPanel, onTogglePanel }) {
  return (
    <section className="listado-estudiantes__info-curso" aria-label="Información del curso">
      <div className="listado-estudiantes__info-item">
        <GraduationCap size={20} aria-hidden="true" className="listado-estudiantes__info-icono" />
        <div>
          <p className="listado-estudiantes__info-etiqueta">Curso</p>
          <p className="listado-estudiantes__info-valor">{curso.nombre}</p>
        </div>
      </div>
      <div className="listado-estudiantes__info-item">
        <div>
          <p className="listado-estudiantes__info-etiqueta">Año escolar</p>
          <p className="listado-estudiantes__info-valor">{curso.anio}</p>
        </div>
      </div>
      <div className="listado-estudiantes__info-item">
        <div>
          <p className="listado-estudiantes__info-etiqueta">Profesor Jefe</p>
          <p className="listado-estudiantes__info-valor">{curso.jefatura}</p>
        </div>
      </div>
      <div className="listado-estudiantes__info-item">
        <Users size={18} aria-hidden="true" className="listado-estudiantes__info-icono" />
        <div>
          <p className="listado-estudiantes__info-etiqueta">Total alumnos</p>
          <p className="listado-estudiantes__info-valor">{totalEstudiantes}</p>
        </div>
      </div>
      <div className="listado-estudiantes__info-item listado-estudiantes__info-item--accion">
        <button
          className="listado-estudiantes__btn-matricular"
          onClick={onTogglePanel}
          aria-expanded={mostrarPanel}
        >
          {mostrarPanel
            ? <><X size={15} aria-hidden="true" /> Cancelar</>
            : <><UserPlus size={15} aria-hidden="true" /> Matricular Alumno</>
          }
        </button>
      </div>
    </section>
  );
}

export default InfoCurso;
