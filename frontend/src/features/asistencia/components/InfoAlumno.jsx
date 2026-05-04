function InfoAlumno({ alumno, porcentaje }) {
  return (
    <div className="historial__alumno-info" aria-label="Datos del alumno">
      <div className="historial__alumno-dato">
        <span className="historial__alumno-etiqueta">Alumno</span>
        <span className="historial__alumno-valor">{alumno.nombre}</span>
      </div>
      <div className="historial__alumno-dato">
        <span className="historial__alumno-etiqueta">RUT</span>
        <span className="historial__alumno-valor">{alumno.rut}</span>
      </div>
      <div className="historial__alumno-dato">
        <span className="historial__alumno-etiqueta">Curso</span>
        <span className="historial__alumno-valor">{alumno.curso}</span>
      </div>
      {porcentaje !== null && (
        <div className="historial__alumno-dato">
          <span className="historial__alumno-etiqueta">% Asistencia</span>
          <span className={`historial__porcentaje ${porcentaje >= 75 ? 'historial__porcentaje--ok' : 'historial__porcentaje--critico'}`}>
            {porcentaje}%
          </span>
        </div>
      )}
    </div>
  );
}

export default InfoAlumno;
