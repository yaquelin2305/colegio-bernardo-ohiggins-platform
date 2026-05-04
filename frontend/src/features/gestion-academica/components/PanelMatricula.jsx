import { UserPlus } from 'lucide-react';

function PanelMatricula({ curso, disponibles, alumnoSeleccionadoId, onAlumnoChange, onMatricular, onCancelar }) {
  return (
    <section className="listado-estudiantes__panel-matricula" aria-label="Matricular nuevo alumno">
      <h2 className="listado-estudiantes__panel-titulo">
        <UserPlus size={16} aria-hidden="true" />
        Matricular alumno en {curso.nombre}
      </h2>
      <form className="listado-estudiantes__form-matricula" onSubmit={onMatricular} noValidate>
        <div className="listado-estudiantes__form-campo">
          <label htmlFor="select-alumno-matricula" className="listado-estudiantes__form-label">
            Alumno disponible
          </label>
          {disponibles.length === 0 ? (
            <p className="listado-estudiantes__sin-disponibles">No hay alumnos disponibles para matricular.</p>
          ) : (
            <select
              id="select-alumno-matricula"
              className="listado-estudiantes__form-select"
              value={alumnoSeleccionadoId}
              onChange={onAlumnoChange}
              required
            >
              <option value="">— Selecciona un alumno —</option>
              {disponibles.map(d => (
                <option key={d.id} value={d.id}>
                  {d.nombre} {d.apellido} ({d.rut})
                </option>
              ))}
            </select>
          )}
        </div>
        {disponibles.length > 0 && (
          <div className="listado-estudiantes__form-acciones">
            <button type="submit" className="listado-estudiantes__btn-guardar-matricula">
              Guardar matrícula
            </button>
            <button
              type="button"
              className="listado-estudiantes__btn-cancelar-matricula"
              onClick={onCancelar}
            >
              Cancelar
            </button>
          </div>
        )}
      </form>
    </section>
  );
}

export default PanelMatricula;
